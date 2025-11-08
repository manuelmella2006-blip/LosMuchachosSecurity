package com.example.losmuchachossecurity.model;

public class ControlEvento {

    private String id;
    private String tipo; // "entrada" o "salida"
    private String accion; // "abrir" o "cerrar"
    private String userId;
    private Long timestamp;
    private String nombreUsuario;

    // Constructor vacío requerido para Firebase
    public ControlEvento() {
    }

    // Constructor básico
    public ControlEvento(String id, String tipo, String accion, String userId, Long timestamp) {
        this.id = id;
        this.tipo = tipo;
        this.accion = accion;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    // Constructor completo
    public ControlEvento(String id, String tipo, String accion, String userId,
                         Long timestamp, String nombreUsuario) {
        this.id = id;
        this.tipo = tipo;
        this.accion = accion;
        this.userId = userId;
        this.timestamp = timestamp;
        this.nombreUsuario = nombreUsuario;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    // Métodos auxiliares
    public String getDescripcion() {
        return "Barrera de " + tipo + " " + accion + "da";
    }

    @Override
    public String toString() {
        return "ControlEvento{" +
                "id='" + id + '\'' +
                ", tipo='" + tipo + '\'' +
                ", accion='" + accion + '\'' +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                '}';
    }
}
