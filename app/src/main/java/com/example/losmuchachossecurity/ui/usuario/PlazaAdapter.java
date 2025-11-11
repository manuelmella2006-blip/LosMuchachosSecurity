package com.example.losmuchachossecurity.ui.usuario;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.model.Plaza;

import java.util.List;

public class PlazaAdapter extends RecyclerView.Adapter<PlazaAdapter.PlazaViewHolder> {

    private List<Plaza> plazas;

    public PlazaAdapter(List<Plaza> plazas) {
        this.plazas = plazas;
    }

    @NonNull
    @Override
    public PlazaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plaza, parent, false);
        return new PlazaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlazaViewHolder holder, int position) {
        Plaza plaza = plazas.get(position);
        holder.bind(plaza);
    }

    @Override
    public int getItemCount() {
        return plazas.size();
    }

    public void actualizarPlazas(List<Plaza> nuevasPlazas) {
        this.plazas = nuevasPlazas;
        notifyDataSetChanged();
    }

    static class PlazaViewHolder extends RecyclerView.ViewHolder {

        // ✅ CORREGIDO: Solo los IDs que existen en item_plaza.xml nuevo
        private CardView layoutPlaza; // El card es el root ahora
        private TextView tvNumeroPlaza;
        private TextView tvEstadoPlaza;

        public PlazaViewHolder(@NonNull View itemView) {
            super(itemView);
            // ✅ layoutPlaza ahora es el CardView root
            layoutPlaza = (CardView) itemView;
            tvNumeroPlaza = itemView.findViewById(R.id.tvNumeroPlaza);
            tvEstadoPlaza = itemView.findViewById(R.id.tvEstadoPlaza);
        }

        public void bind(Plaza plaza) {
            tvNumeroPlaza.setText(plaza.getNumero());

            // ✅ IMPORTANTE: Usar los colores de Los Muchachos Security
            if (plaza.isDisponible()) {
                tvEstadoPlaza.setText("Libre");
                tvEstadoPlaza.setTextColor(Color.WHITE);
                // Verde éxito
                layoutPlaza.setCardBackgroundColor(Color.parseColor("#28A745"));
            } else {
                tvEstadoPlaza.setText("Ocupado");
                tvEstadoPlaza.setTextColor(Color.WHITE);
                // Rojo oscuro error
                layoutPlaza.setCardBackgroundColor(Color.parseColor("#952929"));
            }
        }
    }
}