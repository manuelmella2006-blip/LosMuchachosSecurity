package com.example.losmuchachossecurity.ui.admin;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.model.Registro;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder> {

    private List<Registro> registros;
    private SimpleDateFormat dateFormat;

    public RegistroAdapter(List<Registro> registros) {
        this.registros = registros;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public RegistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_registro, parent, false);
        return new RegistroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroViewHolder holder, int position) {
        Registro registro = registros.get(position);
        holder.bind(registro, dateFormat);
    }

    @Override
    public int getItemCount() {
        return registros.size();
    }

    public void actualizarRegistros(List<Registro> nuevosRegistros) {
        this.registros = nuevosRegistros;
        notifyDataSetChanged();
    }

    static class RegistroViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTipoRegistro;
        private TextView tvDetalleRegistro;
        private TextView tvFechaRegistro;
        private TextView tvUsuarioRegistro;
        private View indicadorColor;

        public RegistroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipoRegistro = itemView.findViewById(R.id.tvTipoRegistro);
            tvDetalleRegistro = itemView.findViewById(R.id.tvDetalleRegistro);
            tvFechaRegistro = itemView.findViewById(R.id.tvFechaRegistro);
            tvUsuarioRegistro = itemView.findViewById(R.id.tvUsuarioRegistro);
            indicadorColor = itemView.findViewById(R.id.indicadorColor);
        }

        public void bind(Registro registro, SimpleDateFormat dateFormat) {
            // Tipo de registro
            tvTipoRegistro.setText(registro.getTipo().toUpperCase());

            // Detalle
            StringBuilder detalle = new StringBuilder();
            if (registro.getPlazaNumero() != null) {
                detalle.append("Plaza: ").append(registro.getPlazaNumero());
            }
            if (registro.getDetalles() != null) {
                if (detalle.length() > 0) detalle.append(" - ");
                detalle.append(registro.getDetalles());
            }
            tvDetalleRegistro.setText(detalle.toString());

            // Fecha
            if (registro.getTimestamp() != null) {
                String fecha = dateFormat.format(new Date(registro.getTimestamp()));
                tvFechaRegistro.setText(fecha);
            }

            // Usuario
            if (registro.getNombreUsuario() != null) {
                tvUsuarioRegistro.setText("Usuario: " + registro.getNombreUsuario());
            } else {
                tvUsuarioRegistro.setText("Usuario: " + registro.getUserId());
            }

            // Color según tipo
            int color = getColorForTipo(registro.getTipo());
            indicadorColor.setBackgroundColor(color);
            tvTipoRegistro.setTextColor(color);
        }

        private int getColorForTipo(String tipo) {
            switch (tipo.toLowerCase()) {
                case "entrada":
                    return Color.parseColor("#4CAF50"); // Verde
                case "salida":
                    return Color.parseColor("#F44336"); // Rojo
                case "reserva":
                    return Color.parseColor("#2196F3"); // Azul
                case "liberacion":
                    return Color.parseColor("#FF9800"); // Naranja
                default:
                    return Color.parseColor("#9E9E9E"); // Gris
            }
        }
    }
}