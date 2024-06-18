package com.ejercicio2.appinventario.model;

public class Categoria {
    String nombre, descripcion, fecha_creacion, estado, imagen_url;

    public Categoria(){

    }
    public Categoria(String nombre, String descripcion, String fecha_creacion, String estado, String imagen_url) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha_creacion = fecha_creacion;
        this.estado = estado;
        this.imagen_url = imagen_url;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getImagen_url() {
        return imagen_url;
    }

    public void setImagen_url(String imagen_url) {
        this.imagen_url = imagen_url;
    }
}
