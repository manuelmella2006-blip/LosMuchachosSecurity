package com.example.losmuchachossecurity.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.data.PlazaRepository;
import com.example.losmuchachossecurity.model.Plaza;
import com.example.losmuchachossecurity.ui.Maqueta3DActivity;

import android.content.Intent;

import java.util.List;

public class MonitoreoFragment extends Fragment {

    private TextView tvDisponiblesAdmin, tvOcupadosAdmin;
    private CardView cardMaqueta;
    private PlazaRepository plazaRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_monitoreo, container, false);

        plazaRepository = new PlazaRepository();
        initViews(view);
        cargarMonitoreo();

        // Abrir maqueta 3D
        if (cardMaqueta != null) {
            cardMaqueta.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), Maqueta3DActivity.class);
                startActivity(intent);
            });
        }

        // ----------- 🔥 CÁMARA ESP32-CAM -----------
        WebView camView = view.findViewById(R.id.webcamView);

        WebSettings ws = camView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);

        camView.setWebViewClient(new WebViewClient());
        camView.loadUrl("http://192.168.137.95:81/stream");

        return view;
    }

    private void initViews(View view) {
        tvDisponiblesAdmin = view.findViewById(R.id.tvDisponiblesAdmin);
        tvOcupadosAdmin = view.findViewById(R.id.tvOcupadosAdmin);
        cardMaqueta = view.findViewById(R.id.cardMaqueta);

        // ❗️IMPORTANTE: ya NO existe recyclerViewPlazasAdmin en el XML
    }

    private void cargarMonitoreo() {
        plazaRepository.obtenerPlazas(new PlazaRepository.PlazaCallback() {
            @Override
            public void onPlazasObtenidas(List<Plaza> plazas) {
                if (plazas != null && !plazas.isEmpty()) {
                    actualizarEstadisticas(plazas);
                } else {
                    tvDisponiblesAdmin.setText("0");
                    tvOcupadosAdmin.setText("0");
                }
            }

            @Override
            public void onError(String mensaje) {
                tvDisponiblesAdmin.setText("0");
                tvOcupadosAdmin.setText("0");
            }
        });
    }

    private void actualizarEstadisticas(List<Plaza> plazas) {
        int disponibles = 0;
        int ocupados = 0;

        for (Plaza p : plazas) {
            if (p.isDisponible()) {
                disponibles++;
            } else {
                ocupados++;
            }
        }

        tvDisponiblesAdmin.setText(String.valueOf(disponibles));
        tvOcupadosAdmin.setText(String.valueOf(ocupados));
    }
}
