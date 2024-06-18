package com.ejercicio2.appinventario.model;

import java.io.Serializable;

public class Negocio implements Serializable {
    private String numerodoc;
    private String nom_empresa;
    private String dire_empresa;
    private String descripcion;
    private String telefono;
    private String email;
    private String impuesto;
    private String cai;
    private String mensaje;
    private String imagenlogo;

    // Constructor vacío necesario para Firestore
    public Negocio() {}

    // Constructor completo
    public Negocio(String numerodoc, String nom_empresa, String dire_empresa, String descripcion,
                   String telefono, String email, String impuesto, String cai,
                   String mensaje, String imagenlogo) {
        this.numerodoc = numerodoc;
        this.nom_empresa = nom_empresa;
        this.dire_empresa = dire_empresa;
        this.descripcion = descripcion;
        this.telefono = telefono;
        this.email = email;
        this.impuesto = impuesto;
        this.cai = cai;
        this.mensaje = mensaje;
        this.imagenlogo = imagenlogo;
    }
    // Getters y setters
    public String getNumerodoc() { return numerodoc; }
    public void setNumerodoc(String numerodoc) { this.numerodoc = numerodoc; }

    public String getNom_empresa() { return nom_empresa; }
    public void setNom_empresa(String nom_empresa) { this.nom_empresa = nom_empresa; }

    public String getDire_empresa() { return dire_empresa; }
    public void setDire_empresa(String dire_empresa) { this.dire_empresa = dire_empresa; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getImpuesto() { return impuesto; }
    public void setImpuesto(String impuesto) { this.impuesto = impuesto; }

    public String getCai() { return cai; }
    public void setCai(String cai) { this.cai = cai; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getImagenlogo() { return imagenlogo; }
    public void setImagenlogo(String imagenlogo) { this.imagenlogo = imagenlogo; }
}
