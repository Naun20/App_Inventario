package com.ejercicio2.appinventario.model;

import java.util.Map;

public class PermisoUsuario {
    private boolean clientes;
    private boolean tipoCliente;
    private boolean usuarios;
    private boolean proveedores;
    private boolean categorias;
    private boolean productos;
    private boolean compras;
    private boolean ventas;
    private boolean negocio;
    private boolean reporteVentas;
    private boolean ajuste;
    private boolean reporteCompras;

    // Getters y setters para cada permiso

    public boolean isClientes() { return clientes; }
    public void setClientes(boolean clientes) { this.clientes = clientes; }
    public boolean isTipoCliente() { return tipoCliente; }
    public void setTipoCliente(boolean tipoCliente) { this.tipoCliente = tipoCliente; }
    public boolean isUsuarios() { return usuarios; }
    public void setUsuarios(boolean usuarios) { this.usuarios = usuarios; }
    public boolean isProveedores() { return proveedores; }
    public void setProveedores(boolean proveedores) { this.proveedores = proveedores; }
    public boolean isCategorias() { return categorias; }
    public void setCategorias(boolean categorias) { this.categorias = categorias; }
    public boolean isProductos() { return productos; }
    public void setProductos(boolean productos) { this.productos = productos; }
    public boolean isCompras() { return compras; }
    public void setCompras(boolean compras) { this.compras = compras; }
    public boolean isVentas() { return ventas; }
    public void setVentas(boolean ventas) { this.ventas = ventas; }
    public boolean isNegocio() { return negocio; }
    public void setNegocio(boolean negocio) { this.negocio = negocio; }
    public boolean isReporteVentas() { return reporteVentas; }
    public void setReporteVentas(boolean reporteVentas) { this.reporteVentas = reporteVentas; }
    public boolean isAjuste() { return ajuste; }
    public void setAjuste(boolean ajuste) { this.ajuste = ajuste; }
    public boolean isReporteCompras() { return reporteCompras; }
    public void setReporteCompras(boolean reporteCompras) { this.reporteCompras = reporteCompras; }
}

