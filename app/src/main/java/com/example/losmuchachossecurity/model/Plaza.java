package com.example.losmuchachossecurity.model;

public class Plaza {
    private String id;        // ID del documento en Firestore
    private int numero;       // Número visible en la maqueta
    private boolean ocupada;  // True = ocupada, False = libre
    private String usuarioId; // UID del usuario que la ocupa (opcional)
    private String sensorId;  // ID del sensor físico en Arduino

    public Plaza() {}

    public Plaza(String id, int numero, boolean ocupada, String usuarioId, String sensorId) {
        this.id = id;
        this.numero = numero;
        this.ocupada = ocupada;
        this.usuarioId = usuarioId;
        this.sensorId = sensorId;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public boolean isOcupada() { return ocupada; }
    public void setOcupada(boolean ocupada) { this.ocupada = ocupada; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
}