package com.ejercicio2.appinventario.model;

public class TipoCliente {
    private String id;
    private String tipo;
    private double descuento;

    // Constructor vacío requerido por Firestore
    public TipoCliente() {}

    // Constructor
    public TipoCliente(String id, String tipo, double descuento) {
        this.id = id;
        this.tipo = tipo;
        this.descuento = descuento;
    }

    // Getters y setters
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

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }
}
