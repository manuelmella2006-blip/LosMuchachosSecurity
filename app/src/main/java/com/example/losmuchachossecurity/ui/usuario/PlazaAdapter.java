package com.example.losmuchachossecurity.ui.usuario;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
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

        private LinearLayout layoutPlaza;
        private ImageView ivIconoPlaza;
        private TextView tvNumeroPlaza;
        private TextView tvEstadoPlaza;

        public PlazaViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutPlaza = itemView.findViewById(R.id.layoutPlaza);
            ivIconoPlaza = itemView.findViewById(R.id.ivIconoPlaza);
            tvNumeroPlaza = itemView.findViewById(R.id.tvNumeroPlaza);
            tvEstadoPlaza = itemView.findViewById(R.id.tvEstadoPlaza);
        }

        public void bind(Plaza plaza) {
            tvNumeroPlaza.setText(plaza.getNumero());

            if (plaza.isDisponible()) {
                tvEstadoPlaza.setText("Disponible");
                tvEstadoPlaza.setTextColor(Color.parseColor("#4CAF50"));
                layoutPlaza.setBackgroundColor(Color.parseColor("#E8F5E9"));
                ivIconoPlaza.setColorFilter(Color.parseColor("#4CAF50"));
            } else {
                tvEstadoPlaza.setText("Ocupado");
                tvEstadoPlaza.setTextColor(Color.parseColor("#F44336"));
                layoutPlaza.setBackgroundColor(Color.parseColor("#FFEBEE"));
                ivIconoPlaza.setColorFilter(Color.parseColor("#F44336"));
            }
        }
    }
}