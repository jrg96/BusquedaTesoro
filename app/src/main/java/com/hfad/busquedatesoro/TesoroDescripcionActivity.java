package com.hfad.busquedatesoro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class TesoroDescripcionActivity extends AppCompatActivity {
    private TextView blog_user_name;
    private ImageView blog_image;
    private TextView blog_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tesoro_descripcion);

        // Obteniendo elementos visuales del layout
        blog_user_name = findViewById(R.id.blog_user_name);
        blog_image = findViewById(R.id.blog_image);
        blog_desc = findViewById(R.id.blog_desc);

        // Obteniendo intento y llenando de datos
        Intent intent = getIntent();
        blog_user_name.setText(intent.getStringExtra("correo_usuario"));
        blog_desc.setText(intent.getStringExtra("tesoro_texto"));

        Picasso.with(getApplicationContext()).load(intent.getStringExtra("url_imagen")).into(blog_image);
    }
}
