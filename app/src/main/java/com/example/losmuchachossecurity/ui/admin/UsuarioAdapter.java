package com.example.losmuchachossecurity.ui.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.model.Usuario;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter para mostrar la lista de usuarios en el RecyclerView
 */
public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Usuario> listaUsuarios;
    private Context context;
    private OnEditarListener onEditarListener;
    private OnEliminarListener onEliminarListener;

    // Interfaces para callbacks
    public interface OnEditarListener {
        void onEditar(Usuario usuario);
    }

    public interface OnEliminarListener {
        void onEliminar(Usuario usuario);
    }

    public UsuarioAdapter(List<Usuario> listaUsuarios, Context context) {
        this.listaUsuarios = listaUsuarios;
        this.context = context;
    }

    public void setOnEditarListener(OnEditarListener listener) {
        this.onEditarListener = listener;
    }

    public void setOnEliminarListener(OnEliminarListener listener) {
        this.onEliminarListener = listener;
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
        holder.bind(usuario);
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    /**
     * ViewHolder para cada item de usuario
     */
    class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAvatar, tvNombre, tvEmail, tvRol, tvEstado;
        private ImageButton btnEditar, btnEliminar;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatarUsuario);
            tvNombre = itemView.findViewById(R.id.tvNombreUsuario);
            tvEmail = itemView.findViewById(R.id.tvEmailUsuario);
            tvRol = itemView.findViewById(R.id.tvRolUsuario);
            tvEstado = itemView.findViewById(R.id.tvEstadoUsuario);
            btnEditar = itemView.findViewById(R.id.btnEditarUsuario);
            btnEliminar = itemView.findViewById(R.id.btnEliminarUsuario);
        }

        public void bind(Usuario usuario) {
            // Avatar (primera letra del nombre)
            if (usuario.getNombre() != null && !usuario.getNombre().isEmpty()) {
                String inicial = usuario.getNombre().substring(0, 1).toUpperCase();
                tvAvatar.setText(inicial);
            } else {
                tvAvatar.setText("?");
            }

            // Información básica
            tvNombre.setText(usuario.getNombre());
            tvEmail.setText(usuario.getEmail());

            // Rol con color
            if (usuario.isAdmin()) {
                tvRol.setText("Admin");
                tvRol.setBackgroundColor(ContextCompat.getColor(context, R.color.ust_green_primary));
            } else {
                tvRol.setText("Usuario");
                tvRol.setBackgroundColor(ContextCompat.getColor(context, R.color.info));
            }

            // Estado con color
            if (usuario.isActivo()) {
                tvEstado.setText("Activo");
                tvEstado.setBackgroundColor(ContextCompat.getColor(context, R.color.success));
                tvEstado.setVisibility(View.VISIBLE);
            } else {
                tvEstado.setText("Inactivo");
                tvEstado.setBackgroundColor(ContextCompat.getColor(context, R.color.error));
                tvEstado.setVisibility(View.VISIBLE);
            }

            // Botón Editar
            btnEditar.setOnClickListener(v -> {
                if (onEditarListener != null) {
                    onEditarListener.onEditar(usuario);
                }
            });

            // Botón Eliminar
            btnEliminar.setOnClickListener(v -> {
                if (onEliminarListener != null) {
                    onEliminarListener.onEliminar(usuario);
                }
            });
        }
    }

    /**
     * Actualiza la lista de usuarios
     */
    public void actualizarLista(List<Usuario> nuevaLista) {
        this.listaUsuarios.clear();
        this.listaUsuarios.addAll(nuevaLista);
        notifyDataSetChanged();
    }
}