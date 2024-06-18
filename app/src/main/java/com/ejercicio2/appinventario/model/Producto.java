package com.ejercicio2.appinventario.model;

public class Producto {
    String nombre, codigoBarra, descripcion, marca, fechaCreacion, fechaVencimiento, estado, categoria, imagen_url;
    int stock;
    String precio;

    public Producto() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getImagen_url() {
        return imagen_url;
    }

    public void setImagen_url(String imagen_url) {
        this.imagen_url = imagen_url;
    }

    public Producto(String nombre, String codigoBarra, String descripcion, String marca, int stock, String precio, String fechaCreacion, String fechaVencimiento,
                    String estado, String categoria, String imagen_url) {
        this.nombre = nombre;
        this.codigoBarra = codigoBarra;
        this.descripcion = descripcion;
        this.marca = marca;
        this.stock = stock;
        this.precio = precio;
        this.fechaCreacion = fechaCreacion;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
        this.categoria = categoria;
        this.imagen_url = imagen_url;
    }
}
