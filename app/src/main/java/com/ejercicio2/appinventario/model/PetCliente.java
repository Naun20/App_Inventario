package com.ejercicio2.appinventario.model;

public class PetCliente {
    String nombre, dni, direccion, genero, telefono, email, tipo, fechaCreacion, edad;

       public  PetCliente(){}

    public PetCliente(String nombre, String dni, String direccion, String genero, String telefono,
                      String email, String tipo, String fechaCreacion, String edad) {
        this.nombre = nombre;
        this.dni = dni;
        this.direccion = direccion;
        this.genero = genero;
        this.telefono = telefono;
        this.email = email;
        this.tipo = tipo;
        this.fechaCreacion = fechaCreacion;
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }


}
