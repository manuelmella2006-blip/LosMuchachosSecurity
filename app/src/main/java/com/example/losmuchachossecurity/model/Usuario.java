package com.example.losmuchachossecurity.model;

public class Usuario {
    private String id;          // UID de Firebase Auth
    private String nombre;
    private String email;
    private String rol;         // "admin" o "cliente"

    public Usuario() {} // Requerido por Firestore

    public Usuario(String id, String nombre, String email, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}