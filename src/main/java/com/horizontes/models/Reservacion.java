package com.horizontes.models;

import java.util.List;

public class Reservacion {
    private int id;
    private String numero;
    private String fechaCreacion;
    private String fechaViaje;
    private int paqueteId;
    private String paqueteNombre;
    private int agenteId;
    private String agenteNombre;
    private double costoTotal;
    private String estado;
    private List<Cliente> pasajeros;

    public Reservacion() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getFechaViaje() { return fechaViaje; }
    public void setFechaViaje(String fechaViaje) { this.fechaViaje = fechaViaje; }

    public int getPaqueteId() { return paqueteId; }
    public void setPaqueteId(int paqueteId) { this.paqueteId = paqueteId; }

    public String getPaqueteNombre() { return paqueteNombre; }
    public void setPaqueteNombre(String paqueteNombre) { this.paqueteNombre = paqueteNombre; }

    public int getAgenteId() { return agenteId; }
    public void setAgenteId(int agenteId) { this.agenteId = agenteId; }

    public String getAgenteNombre() { return agenteNombre; }
    public void setAgenteNombre(String agenteNombre) { this.agenteNombre = agenteNombre; }

    public double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(double costoTotal) { this.costoTotal = costoTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<Cliente> getPasajeros() { return pasajeros; }
    public void setPasajeros(List<Cliente> pasajeros) { this.pasajeros = pasajeros; }
}