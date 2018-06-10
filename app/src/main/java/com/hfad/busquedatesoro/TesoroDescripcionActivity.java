package com.hfad.busquedatesoro;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hfad.adapter.ComentarioRecyclerAdapter;
import com.hfad.modelo.Comentario;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.opencensus.stats.View;

public class TesoroDescripcionActivity extends AppCompatActivity {
    private TextView blog_user_name;
    private ImageView blog_image;
    private TextView blog_desc;
    private EditText comment_field;
    private ImageView comment_post_btn;

    private RecyclerView lista_comentario;
    private ComentarioRecyclerAdapter comentarioRecyclerAdapter;
    private List<Comentario> listaComentarios;

    private String tesoro_id;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tesoro_descripcion);

        // Obteniendo elementos visuales del layout
        blog_user_name = findViewById(R.id.blog_user_name);
        blog_image = findViewById(R.id.blog_image);
        blog_desc = findViewById(R.id.blog_desc);
        comment_field = findViewById(R.id.comment_field);
        comment_post_btn = findViewById(R.id.comment_post_btn);
        lista_comentario = findViewById(R.id.lista_comentario);

        // Obteniendo intento y llenando de datos
        Intent intent = getIntent();
        blog_user_name.setText(intent.getStringExtra("correo_usuario"));
        blog_desc.setText(intent.getStringExtra("tesoro_texto"));
        tesoro_id = intent.getStringExtra("tesoro_id");
        Picasso.with(getApplicationContext()).load(intent.getStringExtra("url_imagen")).into(blog_image);

        firebaseFirestore = FirebaseFirestore.getInstance();

        /*
         * Autenticacion de Firebase
         */
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            this.cerrarSesion();
            Intent loginIntent = new Intent(TesoroDescripcionActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        } else {
            mUser = mAuth.getCurrentUser();
            Toast.makeText(this, "Hallamos usuario logueado", Toast.LENGTH_SHORT).show();
        }

        comment_post_btn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                btn_enviar_mensaje();
            }
        });


        // Preparando recyclerView
        listaComentarios = new ArrayList<>();
        comentarioRecyclerAdapter = new ComentarioRecyclerAdapter(listaComentarios);
        lista_comentario.setHasFixedSize(true);
        lista_comentario.setLayoutManager(new LinearLayoutManager(this));
        lista_comentario.setAdapter(comentarioRecyclerAdapter);

        // Listener de comentarios en firebase
        firebaseFirestore.collection("tesoros/" + tesoro_id + "/comentarios").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){

                        if (doc.getType() == DocumentChange.Type.ADDED){
                            Comentario comentario = doc.getDocument().toObject(Comentario.class);
                            listaComentarios.add(comentario);
                            comentarioRecyclerAdapter.notifyDataSetChanged();
                        }

                    }
                }
            }
        });
    }

    public void btn_enviar_mensaje(){
        String texto = comment_field.getText().toString();

        if (!texto.isEmpty()){
            Map<String, Object> commentMap = new HashMap<>();
            commentMap.put("id_usuario_comentario", mUser.getUid());
            commentMap.put("email_usuario_comentario", mUser.getEmail());
            commentMap.put("timestamp", FieldValue.serverTimestamp());
            commentMap.put("texto_comentario", texto);

            firebaseFirestore.collection("tesoros/" + tesoro_id + "/comentarios").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (!task.isSuccessful()){
                        Toast.makeText(TesoroDescripcionActivity.this, "Error al publicar comentario, intentelo nuevamente", Toast.LENGTH_SHORT).show();
                    } else {
                        comment_field.setText("");
                        Toast.makeText(TesoroDescripcionActivity.this, "Comentario publicado!!", Toast.LENGTH_SHORT).show();
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
