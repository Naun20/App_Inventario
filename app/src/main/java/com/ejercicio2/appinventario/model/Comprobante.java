package com.ejercicio2.appinventario.model;

public class Comprobante {
    private String documento;
    private String serie;
    private String numero;

    public Comprobante(String documento, String serie, String numero) {
        this.documento = documento;
        this.serie = serie;
        this.numero = numero;
    }

    public String getDocumento() {
        return documento;
    }

    public String getSerie() {
        return serie;
    }

    public String getNumero() {
        return numero;
    }
}
