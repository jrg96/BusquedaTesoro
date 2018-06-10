package com.hfad.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hfad.busquedatesoro.R;
import com.hfad.modelo.Comentario;

import org.w3c.dom.Text;

import java.util.List;

public class ComentarioRecyclerAdapter extends RecyclerView.Adapter<ComentarioRecyclerAdapter.ViewHolder>{
    public List<Comentario> listaComentarios;
    public Context context;

    public ComentarioRecyclerAdapter(List<Comentario> lista){
        this.listaComentarios = lista;
    }


    @Override
    public ComentarioRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_coment_item, parent, false);
        context = parent.getContext();
        return new ComentarioRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ComentarioRecyclerAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        String texto_comentario = listaComentarios.get(position).getTexto_comentario();
        holder.setTexto_Comentario(texto_comentario);

        String email = listaComentarios.get(position).getEmail_usuario_comentario();
        holder.setEmailUsuario(email);
    }

    @Override
    public int getItemCount() {
        if(listaComentarios != null) {
            return listaComentarios.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView comment_message;
        private TextView comment_username;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTexto_Comentario(String message){

            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);
        }

        public void setEmailUsuario(String email){
            comment_username = mView.findViewById(R.id.comment_username);
            comment_username.setText(email);
        }

    }
}
