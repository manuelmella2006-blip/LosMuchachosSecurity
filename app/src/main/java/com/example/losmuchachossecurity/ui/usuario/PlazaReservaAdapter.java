package com.example.losmuchachossecurity.ui.usuario;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.model.Plaza;
import java.util.List;

public class PlazaReservaAdapter extends RecyclerView.Adapter<PlazaReservaAdapter.ReservaViewHolder> {

    private List<Plaza> plazas;
    private OnReservaClickListener listener;

    public interface OnReservaClickListener {
        void onReservaClick(Plaza plaza);
    }

    public PlazaReservaAdapter(List<Plaza> plazas, OnReservaClickListener listener) {
        this.plazas = plazas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plaza_reserva, parent, false);
        return new ReservaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        Plaza plaza = plazas.get(position);
        holder.bind(plaza, listener);
    }

    @Override
    public int getItemCount() {
        return plazas.size();
    }

    public void actualizarPlazas(List<Plaza> nuevasPlazas) {
        this.plazas = nuevasPlazas;
        notifyDataSetChanged();
    }

    static class ReservaViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNumeroPlazaReserva;
        private TextView tvUbicacionPlaza;
        private Button btnReservarPlaza;

        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroPlazaReserva = itemView.findViewById(R.id.tvNumeroPlazaReserva);
            tvUbicacionPlaza = itemView.findViewById(R.id.tvUbicacionPlaza);
            btnReservarPlaza = itemView.findViewById(R.id.btnReservarPlaza);
        }

        public void bind(Plaza plaza, OnReservaClickListener listener) {
            tvNumeroPlazaReserva.setText("Plaza " + plaza.getNumero());
            tvUbicacionPlaza.setText("Nivel 1 - Sección A"); // Puedes modificar esto según tus datos

            btnReservarPlaza.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReservaClick(plaza);
                }
            });
        }
    }
}