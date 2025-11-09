package com.example.losmuchachossecurity.model;

import com.google.firebase.Timestamp;

/**
 * 👤 Modelo de datos para un Usuario del sistema (Firestore + Timestamp flexible)
 */
public class Usuario {
    private String userId;
    private String nombre;
    private String email;
    private String rol; // "admin" o "usuario"
    private String telefono;
    private Timestamp fechaRegistro;
    private boolean activo;

    // Constructor vacío requerido por Firebase
    public Usuario() {
    }

    // Constructor completo
    public Usuario(String userId, String nombre, String email, String rol) {
        this.userId = userId;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.activo = true;
        this.fechaRegistro = Timestamp.now();
    }

    // Getters y Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * ✅ Setter flexible que acepta tanto Timestamp como Long
     */
    public void setFechaRegistro(Object fechaRegistro) {
        if (fechaRegistro instanceof Timestamp) {
            this.fechaRegistro = (Timestamp) fechaRegistro;
        } else if (fechaRegistro instanceof Long) {
            // Convertir Long (milisegundos) a Timestamp
            long millis = (Long) fechaRegistro;
            this.fechaRegistro = new Timestamp(millis / 1000, (int) ((millis % 1000) * 1000000));
        } else if (fechaRegistro == null) {
            // Si no existe fecha, usa la actual
            this.fechaRegistro = Timestamp.now();
        } else {
            // Tipo desconocido
            this.fechaRegistro = null;
        }
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // Métodos de utilidad
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(rol);
    }

    public boolean isUsuario() {
        return "usuario".equalsIgnoreCase(rol);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "userId='" + userId + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                ", activo=" + activo +
                ", fechaRegistro=" + (fechaRegistro != null ? fechaRegistro.toDate() : "null") +
                '}';
    }
}
