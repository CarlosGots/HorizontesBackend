package com.horizontes.dao;

import com.horizontes.db.Conexion;
import com.horizontes.models.Proveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    public List<Proveedor> listar() throws SQLException {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM proveedor";
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Proveedor buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM proveedor WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public Proveedor buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM proveedor WHERE nombre = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public boolean insertar(Proveedor p) throws SQLException {
        String sql = "INSERT INTO proveedor (nombre, tipo, pais, contacto) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getTipo());
            ps.setString(3, p.getPais());
            ps.setString(4, p.getContacto());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Proveedor p) throws SQLException {
        String sql = "UPDATE proveedor SET nombre=?, tipo=?, pais=?, contacto=? WHERE id=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getTipo());
            ps.setString(3, p.getPais());
            ps.setString(4, p.getContacto());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    private Proveedor mapear(ResultSet rs) throws SQLException {
        Proveedor p = new Proveedor();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setTipo(rs.getInt("tipo"));
        p.setPais(rs.getString("pais"));
        p.setContacto(rs.getString("contacto"));
        return p;
    }
}