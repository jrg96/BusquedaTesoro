package com.hfad.busquedatesoro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TesoroCrearActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ImageView imgTesoro;
    private EditText txtTextoTesoro;
    private Button btnPublicar;

    private Uri mAttachmentUri;
    private static final String SELECT_PICTURE = "Seleccionar imagen";
    private static final String TYPE_IMAGE = "image/*";
    private static final int PICK_IMAGE = 1;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    private String latitud;
    private String longitud;

    final int FOTOGRAFIA = 654;//Codigo de uso de camara
    Uri file;//El archivo a crear

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.activity_tesoro_crear);

        // Obteniendo datos del intent principal (latitud y longitud)
        Intent intent = getIntent();
        latitud = intent.getStringExtra("latitud");
        longitud = intent.getStringExtra("longitud");

        // Obteniendo controles del layout
        imgTesoro = findViewById(R.id.imgTesoro);
        txtTextoTesoro = findViewById(R.id.txtTextoTesoro);
        btnPublicar = findViewById(R.id.btnPublicarTesoro);

        // Inicializando referencias
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        /*
         * Autenticacion de Firebase
         */
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            this.cerrarSesion();
            Intent loginIntent = new Intent(TesoroCrearActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        } else {
            mUser = mAuth.getCurrentUser();
            Toast.makeText(this, "Hallamos usuario logueado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==FOTOGRAFIA){
            if(resultCode == RESULT_OK){
                imgTesoro.setImageURI(file); // Aqui pone la imagen en la ImageView despues de una fotografia;
            }
            else{
                Toast.makeText(getApplicationContext(),"fotografia No tomada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void btn_seleccionar_imagen(View v){
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo =new File("/storage/sdcard0/Pictures/"+String.valueOf(Calendar.getInstance().getTimeInMillis())+".jpg");
        file=Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(intent,FOTOGRAFIA);
    }

    public void btn_publicar_tesoro(View v){
        btnPublicar.setEnabled(false);
        mAttachmentUri = file;
        if (!txtTextoTesoro.getText().toString().isEmpty() || (mAttachmentUri != null)){

            final String texto = txtTextoTesoro.getText().toString();

            Random r = new Random();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
            String timestamp = df.format(calendar.getTime()) + (r.nextInt(1500 - 0) + 0) +  (r.nextInt(1800 - 0) + 0);

            // Agregando imagen a storage de imagenes
            final StorageReference rutaArch = storageReference.child("tesoro_imagenes").child(timestamp + ".jpg");
            rutaArch.putFile(mAttachmentUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        rutaArch.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Si llegamos hasta aqui, significa que la imagen fue subida con exito
                                Map<String, Object> postMap = new HashMap<>();
                                postMap.put("latitud", Double.parseDouble(latitud));
                                postMap.put("longitud", Double.parseDouble(longitud));
                                postMap.put("url_imagen", uri.toString());
                                postMap.put("tesoro_texto", texto);
                                postMap.put("id_usuario", mUser.getUid());
                                postMap.put("correo_usuario", mUser.getEmail());
                                postMap.put("timestamp", FieldValue.serverTimestamp());

                                firebaseFirestore.collection("tesoros").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(TesoroCrearActivity.this, "Tesoro publicado con exito", Toast.LENGTH_SHORT).show();
                                        } else{
                                            Toast.makeText(TesoroCrearActivity.this, "Error al publicar tesoro", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(TesoroCrearActivity.this, "Error: no se pudo subir archivo", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void cerrarSesion(){
        // Cerrando datos de sesion
        try {
            LoginManager.getInstance().logOut();
        } catch (Exception e){
        }

        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception e){
        }
    }
}