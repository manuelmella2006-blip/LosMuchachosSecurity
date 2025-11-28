package com.example.losmuchachossecurity.ui.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitoreoFragment extends Fragment {

    private static final String TAG = "MonitoreoFragment";

    private TextView tvDisponiblesAdmin, tvOcupadosAdmin;
    private CardView cardMaqueta;
    private RecyclerView recyclerViewPlazasAdmin;
    private WebView webViewCamera;
    private PlazaAdapter plazaAdapter;

    private FirebaseFirestore db;
    private ListenerRegistration plazasListener;

    // URLs de la cámara
    private static final String CAMERA_STREAM_URL =
            "https://cloud-stream-server.onrender.com/viewer";
    private static final String CAMERA_STREAM_FALLBACK = "http://192.168.67.180:81/stream";
    private static final String LOCALHOST_STREAM = "http://127.0.0.1:5000/video";
    private static final String TEST_STREAM_URL =
            "https://www.youtube.com/embed/jfKfPfyJRdk?autoplay=1";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_monitoreo, container, false);

        db = FirebaseConfig.getFirestore();
        initViews(view);
        setupCameraStream();
        setupRealtimeListener();

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
        webViewCamera = view.findViewById(R.id.webViewCamera);

        recyclerViewPlazasAdmin.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    // ==========================
    //  CÁMARA
    // ==========================

    private void cargarStreamEnWebView(String streamUrl) {
        WebSettings settings = webViewCamera.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webViewCamera.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        webViewCamera.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view,
                                      String url,
                                      Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "Cargando stream URL directa: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Stream cargado (URL directa): " + url);
            }

            @Override
            @SuppressWarnings("deprecation")
            public void onReceivedError(WebView view,
                                        int errorCode,
                                        String description,
                                        String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e(TAG, "Error WebView (" + errorCode + "): " + description);

                if (getContext() != null) {
                    if (streamUrl.equals(CAMERA_STREAM_URL)) {
                        Log.d(TAG, "Fallo ngrok, probando ESP32 directo...");
                        cargarStreamEnWebView(CAMERA_STREAM_FALLBACK);
                    } else if (streamUrl.equals(CAMERA_STREAM_FALLBACK)) {
                        Log.d(TAG, "Fallo ESP32, cargando stream de prueba YouTube...");
                        webViewCamera.loadUrl(TEST_STREAM_URL);
                        Toast.makeText(getContext(),
                                "Usando stream de prueba",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(),
                                "Error: " + description,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        webViewCamera.setWebChromeClient(new WebChromeClient());

        Map<String, String> headers = new HashMap<>();
        headers.put("ngrok-skip-browser-warning", "true");

        Log.d(TAG, "Cargando stream URL directa con header: " + streamUrl);
        webViewCamera.loadUrl(streamUrl, headers);
    }

    private void setupCameraStream() {
        try {
            Log.d(TAG, "Conectando a (ngrok) DIRECTO: " + CAMERA_STREAM_URL);
            cargarStreamEnWebView(CAMERA_STREAM_URL);
        } catch (Exception e) {
            Log.e(TAG, "Error inicial al cargar stream", e);
            cargarStreamEnWebView(CAMERA_STREAM_FALLBACK);
        }
    }

    // ==========================
    //  LISTENER DE PLAZAS
    // ==========================

    private void setupRealtimeListener() {
        plazasListener = db.collection("plazas")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error al escuchar plazas: ", error);
                        Toast.makeText(getContext(),
                                "Error al cargar plazas",
                                Toast.LENGTH_SHORT).show();
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

                        // Solo mostrar las primeras 4 plazas
                        List<Plaza> plazasLimitadas = new ArrayList<>();
                        int limite = Math.min(4, plazas.size());
                        for (int i = 0; i < limite; i++) {
                            plazasLimitadas.add(plazas.get(i));
                        }

                        actualizarEstadisticas(plazasLimitadas);
                        actualizarRecyclerView(plazasLimitadas);
                        Log.d(TAG, "Plazas actualizadas: " + plazasLimitadas.size());
                    } else {
                        tvDisponiblesAdmin.setText("0");
                        tvOcupadosAdmin.setText("0");
                    }
                });
    }

    private void actualizarEstadisticas(List<Plaza> plazas) {
        int disponibles = 0;
        int ocupados = 0;

        for (Plaza plaza : plazas) {
            if (!plaza.isOcupado()) {
                disponibles++;
            } else {
                ocupados++;
            }
        }

        tvDisponiblesAdmin.setText(String.valueOf(disponibles));
        tvOcupadosAdmin.setText(String.valueOf(ocupados));
    }

    private void actualizarRecyclerView(List<Plaza> plazas) {
        if (plazaAdapter == null) {
            plazaAdapter = new PlazaAdapter(plazas);
            plazaAdapter.setPermitirCambioEstado(true); // Habilita cambiar estado tocando la card
            recyclerViewPlazasAdmin.setAdapter(plazaAdapter);
        } else {
            plazaAdapter.actualizarPlazas(plazas);
        }
    }

    // ==========================
    //  CICLO DE VIDA
    // ==========================

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (plazasListener != null) {
            plazasListener.remove();
        }
        if (webViewCamera != null) {
            webViewCamera.stopLoading();
            webViewCamera.loadUrl("about:blank");
            webViewCamera.destroy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webViewCamera != null) {
            webViewCamera.onResume();
            webViewCamera.reload();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webViewCamera != null) {
            webViewCamera.onPause();
        }
    }
}
