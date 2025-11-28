package com.example.losmuchachossecurity.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewAssetLoader;

import com.example.losmuchachossecurity.R;

/**
 * 🎨 Activity para visualizar la maqueta 3D del estacionamiento
 * Usa WebView con model-viewer de Google y WebViewAssetLoader
 */
public class Maqueta3DActivity extends AppCompatActivity {

    private static final String TAG = "Maqueta3DActivity";

    private WebView webView;
    private ProgressBar progressBar;
    private Button btnVolver;
    private WebViewAssetLoader assetLoader;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maqueta3d);

        // Configurar status bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.ust_green_primary, null));

        // Inicializar UI
        webView = findViewById(R.id.webView3D);
        progressBar = findViewById(R.id.progressBar3D);
        btnVolver = findViewById(R.id.btnVolver3D);

        btnVolver.setOnClickListener(v -> finish());

        // Configurar WebViewAssetLoader
        assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        // Configurar WebView
        setupWebView();

        // Cargar visor 3D
        loadModel();
    }

    /**
     * ⚙️ Configura el WebView con los ajustes necesarios
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "Página cargada");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, "Error cargando página: " + description);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Maqueta3DActivity.this, "Error: " + description, Toast.LENGTH_LONG).show();
            }
        });

        // Habilitar consola de debugging
        WebView.setWebContentsDebuggingEnabled(true);
    }

    /**
     * 📦 Carga el modelo 3D en el WebView
     */
    private void loadModel() {
        progressBar.setVisibility(View.VISIBLE);

        // Crear HTML con model-viewer
        String html = createModelViewerHTML();

        // Cargar HTML en WebView con la URL base correcta
        webView.loadDataWithBaseURL(
                "https://appassets.androidplatform.net/assets/",
                html,
                "text/html",
                "UTF-8",
                null
        );
    }

    /**
     * 🌐 Crea el HTML con model-viewer de Google
     */
    private String createModelViewerHTML() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"es\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Maqueta 3D</title>\n" +
                "    <script type=\"module\" src=\"https://ajax.googleapis.com/ajax/libs/model-viewer/3.3.0/model-viewer.min.js\"></script>\n" +
                "    <style>\n" +
                "        body { \n" +
                "            margin: 0; \n" +
                "            padding: 0; \n" +
                "            background: #262B54; \n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "        model-viewer {\n" +
                "            width: 100%;\n" +
                "            height: 100vh;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <model-viewer\n" +
                "        src=\"https://appassets.androidplatform.net/assets/models/maqueta.glb\"\n" +
                "        alt=\"Maqueta del Estacionamiento Inteligente\"\n" +
                "        auto-rotate\n" +
                "        camera-controls\n" +
                "        shadow-intensity=\"1\"\n" +
                "        camera-orbit=\"45deg 75deg 2.5m\"\n" +
                "        environment-image=\"neutral\">\n" +
                "    </model-viewer>\n" +
                "</body>\n" +
                "</html>";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }
}
