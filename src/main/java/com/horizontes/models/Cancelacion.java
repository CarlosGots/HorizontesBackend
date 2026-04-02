package com.horizontes.models;

public class Cancelacion {
    private int id;
    private int reservacionId;
    private String reservacionNumero;
    private String fechaCancelacion;
    private double montoReembolso;
    private int porcentajeReembolso;

    public Cancelacion() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReservacionId() { return reservacionId; }
    public void setReservacionId(int reservacionId) { this.reservacionId = reservacionId; }

    public String getReservacionNumero() { return reservacionNumero; }
    public void setReservacionNumero(String reservacionNumero) { this.reservacionNumero = reservacionNumero; }

    public String getFechaCancelacion() { return fechaCancelacion; }
    public void setFechaCancelacion(String fechaCancelacion) { this.fechaCancelacion = fechaCancelacion; }

    public double getMontoReembolso() { return montoReembolso; }
    public void setMontoReembolso(double montoReembolso) { this.montoReembolso = montoReembolso; }

    public int getPorcentajeReembolso() { return porcentajeReembolso; }
    public void setPorcentajeReembolso(int porcentajeReembolso) { this.porcentajeReembolso = porcentajeReembolso; }
}