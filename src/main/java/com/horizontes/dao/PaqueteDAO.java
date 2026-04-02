package com.horizontes.dao;

import com.horizontes.db.Conexion;
import com.horizontes.models.Paquete;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaqueteDAO {

    public List<Paquete> listar() throws SQLException {
        List<Paquete> lista = new ArrayList<>();
        String sql = "SELECT p.*, d.nombre AS destino_nombre FROM paquete p " +
                     "JOIN destino d ON p.destino_id = d.id";
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Paquete> listarActivos() throws SQLException {
        List<Paquete> lista = new ArrayList<>();
        String sql = "SELECT p.*, d.nombre AS destino_nombre FROM paquete p " +
                     "JOIN destino d ON p.destino_id = d.id WHERE p.activo = TRUE";
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Paquete> listarPorDestino(int destinoId) throws SQLException {
        List<Paquete> lista = new ArrayList<>();
        String sql = "SELECT p.*, d.nombre AS destino_nombre FROM paquete p " +
                     "JOIN destino d ON p.destino_id = d.id " +
                     "WHERE p.destino_id = ? AND p.activo = TRUE";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, destinoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Paquete buscarPorId(int id) throws SQLException {
        String sql = "SELECT p.*, d.nombre AS destino_nombre FROM paquete p " +
                     "JOIN destino d ON p.destino_id = d.id WHERE p.id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public Paquete buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT p.*, d.nombre AS destino_nombre FROM paquete p " +
                     "JOIN destino d ON p.destino_id = d.id WHERE p.nombre = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public int insertar(Paquete p) throws SQLException {
        String sql = "INSERT INTO paquete (nombre, destino_id, duracion, descripcion, precio_venta, capacidad, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, TRUE)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getDestinoId());
            ps.setInt(3, p.getDuracion());
            ps.setString(4, p.getDescripcion());
            ps.setDouble(5, p.getPrecioVenta());
            ps.setInt(6, p.getCapacidad());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    public boolean actualizar(Paquete p) throws SQLException {
        String sql = "UPDATE paquete SET nombre=?, destino_id=?, duracion=?, descripcion=?, " +
                     "precio_venta=?, capacidad=? WHERE id=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getDestinoId());
            ps.setInt(3, p.getDuracion());
            ps.setString(4, p.getDescripcion());
            ps.setDouble(5, p.getPrecioVenta());
            ps.setInt(6, p.getCapacidad());
            ps.setInt(7, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean desactivar(int id) throws SQLException {
        String sql = "UPDATE paquete SET activo = FALSE WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Retorna cuántos pasajeros tiene el paquete en reservaciones futuras confirmadas/pendientes
    public int contarOcupacion(int paqueteId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(cantidad_pasajeros), 0) AS total FROM " +
                     "(SELECT COUNT(rp.cliente_id) AS cantidad_pasajeros FROM reservacion r " +
                     "JOIN reservacion_pasajero rp ON r.id = rp.reservacion_id " +
                     "WHERE r.paquete_id = ? AND r.fecha_viaje >= CURDATE() " +
                     "AND r.estado IN ('PENDIENTE','CONFIRMADA') " +
                     "GROUP BY r.id) AS sub";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, paqueteId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        }
        return 0;
    }

    private Paquete mapear(ResultSet rs) throws SQLException {
        Paquete p = new Paquete();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setDestinoId(rs.getInt("destino_id"));
        p.setDestinoNombre(rs.getString("destino_nombre"));
        p.setDuracion(rs.getInt("duracion"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecioVenta(rs.getDouble("precio_venta"));
        p.setCapacidad(rs.getInt("capacidad"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    }
}