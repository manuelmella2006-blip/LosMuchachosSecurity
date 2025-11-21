package com.example.losmuchachossecurity.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.data.FirebaseConfig;
import com.example.losmuchachossecurity.model.Plaza;
import com.example.losmuchachossecurity.ui.Maqueta3DActivity;
import com.example.losmuchachossecurity.ui.usuario.PlazaAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class MonitoreoFragment extends Fragment {

    private static final String TAG = "MonitoreoFragment";

    private TextView tvDisponiblesAdmin, tvOcupadosAdmin;
    private CardView cardMaqueta;
    private RecyclerView recyclerViewPlazasAdmin;
    private PlazaAdapter plazaAdapter;

    private FirebaseFirestore db;
    private ListenerRegistration plazasListener; // ✅ Para detener el listener cuando se destruya el fragment

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitoreo, container, false);

        db = FirebaseConfig.getFirestore();
        initViews(view);
        setupRealtimeListener(); // ✅ Configurar listener en tiempo real

        // Listener para ir a Maqueta 3D
        if (cardMaqueta != null) {
            cardMaqueta.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), Maqueta3DActivity.class);
                startActivity(intent);
            });
        }

        return view;
    }

    private void initViews(View view) {
        tvDisponiblesAdmin = view.findViewById(R.id.tvDisponiblesAdmin);
        tvOcupadosAdmin = view.findViewById(R.id.tvOcupadosAdmin);
        cardMaqueta = view.findViewById(R.id.cardMaqueta);
        recyclerViewPlazasAdmin = view.findViewById(R.id.recyclerViewPlazasAdmin);

        // ✅ Grid de 2x2 para 4 plazas
        recyclerViewPlazasAdmin.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    /**
     * ✅ TIEMPO REAL: Escucha cambios en Firebase automáticamente
     */
    private void setupRealtimeListener() {
        plazasListener = db.collection("plazas")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error al escuchar plazas: ", error);
                        Toast.makeText(getContext(), "Error al cargar plazas", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<Plaza> plazas = new ArrayList<>();

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            Plaza plaza = doc.toObject(Plaza.class);
                            if (plaza != null) {
                                plaza.setId(doc.getId());
                                plazas.add(plaza);
                            }
                        }

                        // ✅ Limitar a solo 4 plazas
                        List<Plaza> plazasLimitadas = new ArrayList<>();
                        int limite = Math.min(4, plazas.size());
                        for (int i = 0; i < limite; i++) {
                            plazasLimitadas.add(plazas.get(i));
                        }

                        // ✅ Actualizar UI
                        actualizarEstadisticas(plazasLimitadas);
                        actualizarRecyclerView(plazasLimitadas);

                        Log.d(TAG, "Plazas actualizadas en tiempo real: " + plazasLimitadas.size());
                    } else {
                        tvDisponiblesAdmin.setText("0");
                        tvOcupadosAdmin.setText("0");
                    }
                });
    }

    /**
     * ✅ Actualiza las estadísticas (contador de disponibles/ocupados)
     */
    private void actualizarEstadisticas(List<Plaza> plazas) {
        int disponibles = 0;
        int ocupados = 0;

        for (Plaza plaza : plazas) {
            if (!plaza.isOcupado()) { // Si NO está ocupado = disponible
                disponibles++;
            } else {
                ocupados++;
            }
        }

        tvDisponiblesAdmin.setText(String.valueOf(disponibles));
        tvOcupadosAdmin.setText(String.valueOf(ocupados));
    }

    /**
     * ✅ Actualiza el RecyclerView con las plazas
     */
    private void actualizarRecyclerView(List<Plaza> plazas) {
        if (plazaAdapter == null) {
            plazaAdapter = new PlazaAdapter(plazas);
            recyclerViewPlazasAdmin.setAdapter(plazaAdapter);
        } else {
            plazaAdapter.actualizarPlazas(plazas);
        }
    }

    /**
     * ✅ IMPORTANTE: Detener el listener cuando se destruye el fragment
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (plazasListener != null) {
            plazasListener.remove();
            Log.d(TAG, "Listener de plazas detenido");
        }
    }
}
