package com.horizontes.dao;

import com.horizontes.db.Conexion;
import com.horizontes.models.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario login(String nombre, String password) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE nombre = ? AND password = ? AND activo = TRUE";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public boolean insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (nombre, password, tipo, activo) VALUES (?, ?, ?, TRUE)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getPassword());
            ps.setInt(3, u.getTipo());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET tipo = ?, activo = ? WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, u.getTipo());
            ps.setBoolean(2, u.isActivo());
            ps.setInt(3, u.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean desactivar(int id) throws SQLException {
        String sql = "UPDATE usuario SET activo = FALSE WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existeNombre(String nombre) throws SQLException {
        String sql = "SELECT id FROM usuario WHERE nombre = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNombre(rs.getString("nombre"));
        u.setPassword(rs.getString("password"));
        u.setTipo(rs.getInt("tipo"));
        u.setActivo(rs.getBoolean("activo"));
        return u;
    }
}
