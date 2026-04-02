package com.horizontes.dao;

import com.horizontes.db.Conexion;
import com.horizontes.models.Cliente;
import com.horizontes.models.Reservacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservacionDAO {

    public List<Reservacion> listar() throws SQLException {
        List<Reservacion> lista = new ArrayList<>();
        String sql = "SELECT r.*, p.nombre AS paquete_nombre, u.nombre AS agente_nombre " +
                     "FROM reservacion r " +
                     "JOIN paquete p ON r.paquete_id = p.id " +
                     "JOIN usuario u ON r.agente_id = u.id " +
                     "ORDER BY r.fecha_creacion DESC";
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Reservacion> listarDelDia() throws SQLException {
        List<Reservacion> lista = new ArrayList<>();
        String sql = "SELECT r.*, p.nombre AS paquete_nombre, u.nombre AS agente_nombre " +
                     "FROM reservacion r " +
                     "JOIN paquete p ON r.paquete_id = p.id " +
                     "JOIN usuario u ON r.agente_id = u.id " +
                     "WHERE r.fecha_creacion = CURDATE()";
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Reservacion> listarPorCliente(int clienteId) throws SQLException {
        List<Reservacion> lista = new ArrayList<>();
        String sql = "SELECT r.*, p.nombre AS paquete_nombre, u.nombre AS agente_nombre " +
                     "FROM reservacion r " +
                     "JOIN paquete p ON r.paquete_id = p.id " +
                     "JOIN usuario u ON r.agente_id = u.id " +
                     "JOIN reservacion_pasajero rp ON r.id = rp.reservacion_id " +
                     "WHERE rp.cliente_id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Reservacion buscarPorNumero(String numero) throws SQLException {
        String sql = "SELECT r.*, p.nombre AS paquete_nombre, u.nombre AS agente_nombre " +
                     "FROM reservacion r " +
                     "JOIN paquete p ON r.paquete_id = p.id " +
                     "JOIN usuario u ON r.agente_id = u.id " +
                     "WHERE r.numero = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, numero);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public Reservacion buscarPorId(int id) throws SQLException {
        String sql = "SELECT r.*, p.nombre AS paquete_nombre, u.nombre AS agente_nombre " +
                     "FROM reservacion r " +
                     "JOIN paquete p ON r.paquete_id = p.id " +
                     "JOIN usuario u ON r.agente_id = u.id " +
                     "WHERE r.id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public int insertar(Reservacion r) throws SQLException {
        String numero = generarNumero();
        String sql = "INSERT INTO reservacion (numero, fecha_creacion, fecha_viaje, paquete_id, agente_id, costo_total, estado) " +
                     "VALUES (?, CURDATE(), ?, ?, ?, ?, 'PENDIENTE')";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, numero);
            ps.setString(2, r.getFechaViaje());
            ps.setInt(3, r.getPaqueteId());
            ps.setInt(4, r.getAgenteId());
            ps.setDouble(5, r.getCostoTotal());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    public boolean agregarPasajero(int reservacionId, int clienteId) throws SQLException {
        String sql = "INSERT INTO reservacion_pasajero (reservacion_id, cliente_id) VALUES (?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reservacionId);
            ps.setInt(2, clienteId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Cliente> listarPasajeros(int reservacionId) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT c.* FROM cliente c " +
                     "JOIN reservacion_pasajero rp ON c.id = rp.cliente_id " +
                     "WHERE rp.reservacion_id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reservacionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setDpi(rs.getString("dpi"));
                c.setNombre(rs.getString("nombre"));
                c.setFechaNacimiento(rs.getString("fecha_nacimiento"));
                c.setTelefono(rs.getString("telefono"));
                c.setEmail(rs.getString("email"));
                c.setNacionalidad(rs.getString("nacionalidad"));
                lista.add(c);
            }
        }
        return lista;
    }

    public boolean actualizarEstado(int id, String estado) throws SQLException {
        String sql = "UPDATE reservacion SET estado = ? WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public double getTotalPagado(int reservacionId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(monto), 0) AS total FROM pago WHERE reservacion_id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reservacionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total");
        }
        return 0;
    }

    private String generarNumero() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM reservacion";
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                int siguiente = rs.getInt("total") + 1;
                return String.format("RES-%05d", siguiente);
            }
        }
        return "RES-00001";
    }

    private Reservacion mapear(ResultSet rs) throws SQLException {
        Reservacion r = new Reservacion();
        r.setId(rs.getInt("id"));
        r.setNumero(rs.getString("numero"));
        r.setFechaCreacion(rs.getString("fecha_creacion"));
        r.setFechaViaje(rs.getString("fecha_viaje"));
        r.setPaqueteId(rs.getInt("paquete_id"));
        r.setPaqueteNombre(rs.getString("paquete_nombre"));
        r.setAgenteId(rs.getInt("agente_id"));
        r.setAgenteNombre(rs.getString("agente_nombre"));
        r.setCostoTotal(rs.getDouble("costo_total"));
        r.setEstado(rs.getString("estado"));
        return r;
    }
}