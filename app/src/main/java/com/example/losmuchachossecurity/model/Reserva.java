package com.example.losmuchachossecurity.model;

import com.google.firebase.Timestamp;

/**
 * Modelo Reserva
 * Representa una reserva de estacionamiento realizada por un usuario
 */
public class Reserva {

    // Campos principales
    private String id;                  // ID único de la reserva (generado por Firestore)
    private String usuarioId;           // UID del usuario que reserva
    private String usuarioNombre;       // Nombre del usuario
    private String usuarioEmail;        // Email del usuario
    private int plazaNumero;            // Número de plaza reservada
    private Timestamp fechaReserva;     // Fecha de la reserva
    private Timestamp horaInicio;       // Hora de inicio de la reserva
    private Timestamp horaFin;          // Hora de fin de la reserva
    private String estado;              // Estado: "activa", "completada", "cancelada"
    private Timestamp fechaCreacion;    // Timestamp de creación

    // Constructores

    /**
     * Constructor vacío requerido por Firestore
     */
    public Reserva() {
        // Constructor vacío necesario para deserialización de Firestore
    }

    /**
     * Constructor completo
     */
    public Reserva(String id, String usuarioId, String usuarioNombre, String usuarioEmail,
                   int plazaNumero, Timestamp fechaReserva, Timestamp horaInicio,
                   Timestamp horaFin, String estado, Timestamp fechaCreacion) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.usuarioEmail = usuarioEmail;
        this.plazaNumero = plazaNumero;
        this.fechaReserva = fechaReserva;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Constructor simplificado (para creación rápida)
     */
    public Reserva(String usuarioId, String usuarioNombre, String usuarioEmail,
                   int plazaNumero, Timestamp fechaReserva, Timestamp horaInicio,
                   Timestamp horaFin) {
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.usuarioEmail = usuarioEmail;
        this.plazaNumero = plazaNumero;
        this.fechaReserva = fechaReserva;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = "activa";  // Estado por defecto
        this.fechaCreacion = Timestamp.now();
    }

    // Getters y Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }

    public int getPlazaNumero() {
        return plazaNumero;
    }

    public void setPlazaNumero(int plazaNumero) {
        this.plazaNumero = plazaNumero;
    }

    public Timestamp getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(Timestamp fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public Timestamp getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Timestamp horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Timestamp getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(Timestamp horaFin) {
        this.horaFin = horaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // Métodos auxiliares

    /**
     * Verifica si la reserva está activa
     */
    public boolean isActiva() {
        return "activa".equals(estado);
    }

    /**
     * Verifica si la reserva está completada
     */
    public boolean isCompletada() {
        return "completada".equals(estado);
    }

    /**
     * Verifica si la reserva está cancelada
     */
    public boolean isCancelada() {
        return "cancelada".equals(estado);
    }

    /**
     * Marca la reserva como completada
     */
    public void marcarCompletada() {
        this.estado = "completada";
    }

    /**
     * Marca la reserva como cancelada
     */
    public void marcarCancelada() {
        this.estado = "cancelada";
    }

    // toString para debugging

    @Override
    public String toString() {
        return "Reserva{" +
                "id='" + id + '\'' +
                ", usuarioNombre='" + usuarioNombre + '\'' +
                ", plazaNumero=" + plazaNumero +
                ", estado='" + estado + '\'' +
                ", fechaReserva=" + fechaReserva +
                '}';
    }
}