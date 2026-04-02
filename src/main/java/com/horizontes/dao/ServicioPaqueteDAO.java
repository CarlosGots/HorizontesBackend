package com.horizontes.dao;

import com.horizontes.db.Conexion;
import com.horizontes.models.ServicioPaquete;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioPaqueteDAO {

    public List<ServicioPaquete> listarPorPaquete(int paqueteId) throws SQLException {
        List<ServicioPaquete> lista = new ArrayList<>();
        String sql = "SELECT sp.*, p.nombre AS proveedor_nombre " +
                     "FROM servicio_paquete sp " +
                     "JOIN proveedor p ON sp.proveedor_id = p.id " +
                     "WHERE sp.paquete_id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, paqueteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public boolean insertar(ServicioPaquete sp) throws SQLException {
        String sql = "INSERT INTO servicio_paquete (paquete_id, proveedor_id, descripcion, costo) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sp.getPaqueteId());
            ps.setInt(2, sp.getProveedorId());
            ps.setString(3, sp.getDescripcion());
            ps.setDouble(4, sp.getCosto());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarPorPaquete(int paqueteId) throws SQLException {
        String sql = "DELETE FROM servicio_paquete WHERE paquete_id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, paqueteId);
            return ps.executeUpdate() > 0;
        }
    }

    private ServicioPaquete mapear(ResultSet rs) throws SQLException {
        ServicioPaquete sp = new ServicioPaquete();
        sp.setId(rs.getInt("id"));
        sp.setPaqueteId(rs.getInt("paquete_id"));
        sp.setProveedorId(rs.getInt("proveedor_id"));
        sp.setProveedorNombre(rs.getString("proveedor_nombre"));
        sp.setDescripcion(rs.getString("descripcion"));
        sp.setCosto(rs.getDouble("costo"));
        return sp;
    }
}