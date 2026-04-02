package com.horizontes.models;

public class Pago {
    private int id;
    private int reservacionId;
    private String reservacionNumero;
    private double monto;
    private int metodo;
    private String fecha;

    public Pago() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReservacionId() { return reservacionId; }
    public void setReservacionId(int reservacionId) { this.reservacionId = reservacionId; }

    public String getReservacionNumero() { return reservacionNumero; }
    public void setReservacionNumero(String reservacionNumero) { this.reservacionNumero = reservacionNumero; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public int getMetodo() { return metodo; }
    public void setMetodo(int metodo) { this.metodo = metodo; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}