package com.horizontes.dao;

import com.horizontes.db.Conexion;
import com.horizontes.models.Destino;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DestinoDAO {

    public List<Destino> listar() throws SQLException {
        List<Destino> lista = new ArrayList<>();
        String sql = "SELECT * FROM destino";
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Destino buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM destino WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public Destino buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM destino WHERE nombre = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public boolean insertar(Destino d) throws SQLException {
        String sql = "INSERT INTO destino (nombre, pais, descripcion, clima, imagen_url) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getNombre());
            ps.setString(2, d.getPais());
            ps.setString(3, d.getDescripcion());
            ps.setString(4, d.getClima());
            ps.setString(5, d.getImagenUrl());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Destino d) throws SQLException {
        String sql = "UPDATE destino SET nombre=?, pais=?, descripcion=?, clima=?, imagen_url=? WHERE id=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getNombre());
            ps.setString(2, d.getPais());
            ps.setString(3, d.getDescripcion());
            ps.setString(4, d.getClima());
            ps.setString(5, d.getImagenUrl());
            ps.setInt(6, d.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM destino WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Destino mapear(ResultSet rs) throws SQLException {
        Destino d = new Destino();
        d.setId(rs.getInt("id"));
        d.setNombre(rs.getString("nombre"));
        d.setPais(rs.getString("pais"));
        d.setDescripcion(rs.getString("descripcion"));
        d.setClima(rs.getString("clima"));
        d.setImagenUrl(rs.getString("imagen_url"));
        return d;
    }
}