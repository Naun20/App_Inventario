package com.ejercicio2.appinventario.model;

import java.util.Map;

public class PetUsuario {
    String id; // Nuevo campo para almacenar el ID único del usuario
    String nombre, email, telefono, usuario, password, tipo_usuario, imagen_url;
    Map<String, String> permisos;

    public PetUsuario() {}

    public PetUsuario(String id, String nombre, String email, String telefono, String usuario, String password, String tipo_usuario,
                      String imagen_url, Map<String, String> permisos) {
        this.id = id; // Establecer el ID del usuario
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.usuario = usuario;
        this.password = password;
        this.tipo_usuario = tipo_usuario;
        this.imagen_url = imagen_url;
        this.permisos = permisos;
    }

    // Getters y setters para todos los campos incluyendo permisos

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(String tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }

    public String getImagen_url() {
        return imagen_url;
    }

    public void setImagen_url(String imagen_url) {
        this.imagen_url = imagen_url;
    }

    public Map<String, String> getPermisos() {
        return permisos;
    }

    public void setPermisos(Map<String, String> permisos) {
        this.permisos = permisos;
    }
}
