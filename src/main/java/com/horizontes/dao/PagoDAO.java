package com.horizontes.dao;

import com.horizontes.db.Conexion;
import com.horizontes.models.Pago;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    public List<Pago> listarPorReservacion(int reservacionId) throws SQLException {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT p.*, r.numero AS reservacion_numero FROM pago p " +
                     "JOIN reservacion r ON p.reservacion_id = r.id " +
                     "WHERE p.reservacion_id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reservacionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public boolean insertar(Pago p) throws SQLException {
        String sql = "INSERT INTO pago (reservacion_id, monto, metodo, fecha) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getReservacionId());
            ps.setDouble(2, p.getMonto());
            ps.setInt(3, p.getMetodo());
            ps.setString(4, p.getFecha());
            return ps.executeUpdate() > 0;
        }
    }

    private Pago mapear(ResultSet rs) throws SQLException {
        Pago p = new Pago();
        p.setId(rs.getInt("id"));
        p.setReservacionId(rs.getInt("reservacion_id"));
        p.setReservacionNumero(rs.getString("reservacion_numero"));
        p.setMonto(rs.getDouble("monto"));
        p.setMetodo(rs.getInt("metodo"));
        p.setFecha(rs.getString("fecha"));
        return p;
    }
}
