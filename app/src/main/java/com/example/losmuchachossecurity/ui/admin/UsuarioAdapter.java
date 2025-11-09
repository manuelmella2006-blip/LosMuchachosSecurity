package com.example.losmuchachossecurity.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.model.Usuario;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Usuario> listaUsuarios;
    private OnUsuarioClickListener listener;

    public interface OnUsuarioClickListener {
        void onEditarClick(Usuario usuario);
        void onEliminarClick(Usuario usuario);
        void onCambiarRolClick(Usuario usuario);
    }

    public UsuarioAdapter(List<Usuario> listaUsuarios, OnUsuarioClickListener listener) {
        this.listaUsuarios = listaUsuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);
        holder.bind(usuario, listener);
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNombre;
        private TextView tvEmail;
        private TextView tvRol;
        private TextView tvFecha;
        private TextView tvEstado;
        private ImageButton btnEditar;
        private ImageButton btnEliminar;
        private ImageButton btnCambiarRol;
        private View indicadorRol;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRol = itemView.findViewById(R.id.tvRol);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnCambiarRol = itemView.findViewById(R.id.btnCambiarRol);
            indicadorRol = itemView.findViewById(R.id.indicadorRol);
        }

        public void bind(Usuario usuario, OnUsuarioClickListener listener) {
            tvNombre.setText(usuario.getNombre());
            tvEmail.setText(usuario.getEmail());

            // Configurar rol
            if (usuario.isAdmin()) {
                tvRol.setText("👨‍💼 ADMIN");
                tvRol.setTextColor(itemView.getContext().getColor(R.color.admin_color));
                indicadorRol.setBackgroundColor(itemView.getContext().getColor(R.color.admin_color));
            } else {
                tvRol.setText("👤 USUARIO");
                tvRol.setTextColor(itemView.getContext().getColor(R.color.usuario_color));
                indicadorRol.setBackgroundColor(itemView.getContext().getColor(R.color.usuario_color));
            }

            // Fecha de registro usando Timestamp de Firestore
            Timestamp timestamp = usuario.getFechaRegistro();
            if (timestamp != null) {
                Date fecha = timestamp.toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                tvFecha.setText("Registrado: " + sdf.format(fecha));
            } else {
                tvFecha.setText("Fecha no disponible");
            }

            // Estado
            if (usuario.isActivo()) {
                tvEstado.setText("✅ Activo");
                tvEstado.setTextColor(itemView.getContext().getColor(R.color.success));
            } else {
                tvEstado.setText("⛔ Inactivo");
                tvEstado.setTextColor(itemView.getContext().getColor(R.color.error));
            }

            // Listeners
            btnEditar.setOnClickListener(v -> listener.onEditarClick(usuario));
            btnEliminar.setOnClickListener(v -> listener.onEliminarClick(usuario));
            btnCambiarRol.setOnClickListener(v -> listener.onCambiarRolClick(usuario));
        }
    }
}