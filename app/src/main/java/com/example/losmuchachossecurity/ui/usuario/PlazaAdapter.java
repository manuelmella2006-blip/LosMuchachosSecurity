package com.example.losmuchachossecurity.ui.usuario;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.data.PlazaRepository;
import com.example.losmuchachossecurity.model.Plaza;

import java.util.List;

public class PlazaAdapter extends RecyclerView.Adapter<PlazaAdapter.PlazaViewHolder> {

    private static final String TAG = "PlazaAdapter";

    private List<Plaza> plazas;
    private final PlazaRepository plazaRepository;
    private boolean permitirCambioEstado = false;

    public PlazaAdapter(List<Plaza> plazas) {
        this.plazas = plazas;
        this.plazaRepository = new PlazaRepository();
    }

    /**
     * Habilita/deshabilita el cambio de estado al tocar las plazas.
     * Se activa desde MonitoreoFragment.
     */
    public void setPermitirCambioEstado(boolean permitir) {
        this.permitirCambioEstado = permitir;
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
        holder.bind(plaza, permitirCambioEstado, plazaRepository);
    }

    @Override
    public int getItemCount() {
        return plazas != null ? plazas.size() : 0;
    }

    public void actualizarPlazas(List<Plaza> nuevasPlazas) {
        this.plazas = nuevasPlazas;
        notifyDataSetChanged();
    }

    static class PlazaViewHolder extends RecyclerView.ViewHolder {

        private final CardView layoutPlaza;
        private final TextView tvNumeroPlaza;
        private final TextView tvEstadoPlaza;

        public PlazaViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutPlaza = (CardView) itemView;
            tvNumeroPlaza = itemView.findViewById(R.id.tvNumeroPlaza);
            tvEstadoPlaza = itemView.findViewById(R.id.tvEstadoPlaza);
        }

        public void bind(Plaza plaza,
                         boolean permitirCambio,
                         PlazaRepository repository) {

            tvNumeroPlaza.setText(plaza.getNumero());

            // Estado visual
            if (!plaza.isOcupado()) {
                tvEstadoPlaza.setText("Libre");
                tvEstadoPlaza.setTextColor(Color.WHITE);
                layoutPlaza.setCardBackgroundColor(Color.parseColor("#28A745")); // Verde
            } else {
                tvEstadoPlaza.setText("Ocupado");
                tvEstadoPlaza.setTextColor(Color.WHITE);
                layoutPlaza.setCardBackgroundColor(Color.parseColor("#952929")); // Rojo
            }

            if (permitirCambio) {
                layoutPlaza.setOnClickListener(v -> {
                    boolean nuevoOcupado = !plaza.isOcupado();

                    Log.d(TAG,
                            "Cambiando plaza " + plaza.getNumero() +
                                    " de " + (plaza.isOcupado() ? "OCUPADO" : "LIBRE") +
                                    " a " + (nuevoOcupado ? "OCUPADO" : "LIBRE"));

                    repository.cambiarEstadoPlazaManual(
                            plaza.getId(),
                            nuevoOcupado,
                            new PlazaRepository.ReservaCallback() {
                                @Override
                                public void onReservaExitosa() {
                                    Toast.makeText(
                                            v.getContext(),
                                            "Plaza " + plaza.getNumero() +
                                                    " cambiada a " + (nuevoOcupado ? "OCUPADO" : "LIBRE"),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }

                                @Override
                                public void onError(String mensaje) {
                                    Toast.makeText(
                                            v.getContext(),
                                            "Error: " + mensaje,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    Log.e(TAG, "Error al actualizar estado: " + mensaje);
                                }
                            }
                    );
                });

                layoutPlaza.setClickable(true);
                layoutPlaza.setFocusable(true);
                layoutPlaza.setForeground(
                        itemView.getContext().getDrawable(
                                android.R.drawable.list_selector_background));

            } else {
                layoutPlaza.setOnClickListener(null);
                layoutPlaza.setClickable(false);
                layoutPlaza.setFocusable(false);
                layoutPlaza.setForeground(null);
            }
        }
    }
}
