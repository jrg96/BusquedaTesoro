package com.hfad.busquedatesoro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TesoroCrearActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tesoro_crear);

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
