package com.example.losmuchachossecurity.model;

import com.google.firebase.Timestamp;

public class Plaza {

    private String id;
    private String numero;
    private boolean ocupado;  // ✅ CAMBIADO de 'disponible' a 'ocupado' para coincidir con Firebase
    private String estado;    // ✅ NUEVO: "LIBRE" o "OCUPADO"
    private Double mean_diff; // ✅ NUEVO: para sensor
    private Timestamp ultima_actualizacion; // ✅ NUEVO

    // Campos adicionales opcionales
    private String usuarioId;
    private Long fechaReserva;
    private String ubicacion;
    private String tipo;

    // Constructor vacío requerido para Firebase
    public Plaza() {
    }

    // Constructor básico
    public Plaza(String id, String numero, boolean ocupado) {
        this.id = id;
        this.numero = numero;
        this.ocupado = ocupado;
        this.estado = ocupado ? "OCUPADO" : "LIBRE";
    }

    // Constructor completo
    public Plaza(String id, String numero, boolean ocupado, String estado,
                 Double mean_diff, Timestamp ultima_actualizacion) {
        this.id = id;
        this.numero = numero;
        this.ocupado = ocupado;
        this.estado = estado;
        this.mean_diff = mean_diff;
        this.ultima_actualizacion = ultima_actualizacion;
    }

    // ========================================
    // GETTERS Y SETTERS
    // ========================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    // ✅ IMPORTANTE: Mantener compatibilidad con código existente
    public boolean isDisponible() {
        return !ocupado; // Si NO está ocupado, está disponible
    }

    public void setDisponible(boolean disponible) {
        this.ocupado = !disponible;
        this.estado = disponible ? "LIBRE" : "OCUPADO";
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
        this.estado = ocupado ? "OCUPADO" : "LIBRE";
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
        this.ocupado = "OCUPADO".equals(estado);
    }

    public Double getMean_diff() {
        return mean_diff;
    }

    public void setMean_diff(Double mean_diff) {
        this.mean_diff = mean_diff;
    }

    public Timestamp getUltima_actualizacion() {
        return ultima_actualizacion;
    }

    public void setUltima_actualizacion(Timestamp ultima_actualizacion) {
        this.ultima_actualizacion = ultima_actualizacion;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(Long fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================

    public String getEstadoTexto() {
        return estado != null ? estado : (ocupado ? "OCUPADO" : "LIBRE");
    }

    public boolean estaReservadaPor(String userId) {
        return ocupado && usuarioId != null && usuarioId.equals(userId);
    }

    @Override
    public String toString() {
        return "Plaza{" +
                "id='" + id + '\'' +
                ", numero='" + numero + '\'' +
                ", ocupado=" + ocupado +
                ", estado='" + estado + '\'' +
                ", mean_diff=" + mean_diff +
                ", ultima_actualizacion=" + ultima_actualizacion +
                '}';
    }
}
