package com.example.losmuchachossecurity.model;

public class Plaza {

    private String id;
    private String numero;
    private boolean disponible;
    private String usuarioId;
    private Long fechaReserva;
    private String ubicacion;
    private String tipo; // normal, discapacitados, motos, etc.

    // Constructor vacío requerido para Firebase
    public Plaza() {
    }

    // Constructor completo
    public Plaza(String id, String numero, boolean disponible) {
        this.id = id;
        this.numero = numero;
        this.disponible = disponible;
    }

    // Constructor con todos los campos
    public Plaza(String id, String numero, boolean disponible, String usuarioId,
                 Long fechaReserva, String ubicacion, String tipo) {
        this.id = id;
        this.numero = numero;
        this.disponible = disponible;
        this.usuarioId = usuarioId;
        this.fechaReserva = fechaReserva;
        this.ubicacion = ubicacion;
        this.tipo = tipo;
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

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
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

    /**
     * Obtiene el estado de la plaza en formato legible
     */
    public String getEstadoTexto() {
        return disponible ? "Disponible" : "Ocupado";
    }

    /**
     * Verifica si la plaza está reservada por un usuario específico
     */
    public boolean estaReservadaPor(String userId) {
        return !disponible && usuarioId != null && usuarioId.equals(userId);
    }

    @Override
    public String toString() {
        return "Plaza{" +
                "id='" + id + '\'' +
                ", numero='" + numero + '\'' +
                ", disponible=" + disponible +
                ", usuarioId='" + usuarioId + '\'' +
                ", fechaReserva=" + fechaReserva +
                ", ubicacion='" + ubicacion + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}