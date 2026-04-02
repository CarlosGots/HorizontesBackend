package com.horizontes.dao;

import com.horizontes.db.Conexion;
import com.horizontes.models.Cancelacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CancelacionDAO {

    public boolean insertar(Cancelacion c) throws SQLException {
        String sql = "INSERT INTO cancelacion (reservacion_id, fecha_cancelacion, monto_reembolso, porcentaje_reembolso) " +
                     "VALUES (?, CURDATE(), ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getReservacionId());
            ps.setDouble(2, c.getMontoReembolso());
            ps.setInt(3, c.getPorcentajeReembolso());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Cancelacion> listarPorIntervalo(String fechaInicio, String fechaFin) throws SQLException {
        List<Cancelacion> lista = new ArrayList<>();
        String sql = "SELECT c.*, r.numero AS reservacion_numero FROM cancelacion c " +
                     "JOIN reservacion r ON c.reservacion_id = r.id " +
                     "WHERE (? IS NULL OR c.fecha_cancelacion >= ?) " +
                     "AND (? IS NULL OR c.fecha_cancelacion <= ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaInicio);
            ps.setString(3, fechaFin);
            ps.setString(4, fechaFin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Cancelacion mapear(ResultSet rs) throws SQLException {
        Cancelacion c = new Cancelacion();
        c.setId(rs.getInt("id"));
        c.setReservacionId(rs.getInt("reservacion_id"));
        c.setReservacionNumero(rs.getString("reservacion_numero"));
        c.setFechaCancelacion(rs.getString("fecha_cancelacion"));
        c.setMontoReembolso(rs.getDouble("monto_reembolso"));
        c.setPorcentajeReembolso(rs.getInt("porcentaje_reembolso"));
        return c;
    }
}