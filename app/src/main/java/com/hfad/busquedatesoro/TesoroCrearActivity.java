package com.hfad.busquedatesoro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tesoro_crear);

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
        if (requestCode == PICK_IMAGE && data != null) {
            mAttachmentUri = data.getData();
            imgTesoro.setImageURI(mAttachmentUri);
        }
    }

    public void btn_seleccionar_imagen(View v){
        Intent intent = new Intent();
        intent.setType(TYPE_IMAGE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, SELECT_PICTURE), PICK_IMAGE);
    }

    public void btn_publicar_tesoro(View v){
        btnPublicar.setEnabled(false);

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
                                postMap.put("url_imagen", uri.toString());
                                postMap.put("tesoro_texto", texto);
                                postMap.put("id_usuario", mUser.getUid());

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
