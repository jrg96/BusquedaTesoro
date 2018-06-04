package com.hfad.busquedatesoro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TesoroCrearActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ImageView imgTesoro;
    private EditText txtTextoTesoro;

    private Uri mAttachmentUri;
    private static final String SELECT_PICTURE = "Seleccionar imagen";
    private static final String TYPE_IMAGE = "image/*";
    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tesoro_crear);

        // Obteniendo controles del layout
        imgTesoro = findViewById(R.id.imgTesoro);
        txtTextoTesoro = findViewById(R.id.txtTextoTesoro);


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
        }
    }

    public void btn_seleccionar_imagen(View v){
        Intent intent = new Intent();
        intent.setType(TYPE_IMAGE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, SELECT_PICTURE), PICK_IMAGE);
    }

    public void btn_publicar_tesoro(View v){

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
