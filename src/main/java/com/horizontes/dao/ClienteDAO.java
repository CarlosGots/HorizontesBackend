package com.horizontes.dao;

import com.horizontes.db.Conexion;
import com.horizontes.models.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public List<Cliente> listar() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente";
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Cliente buscarPorDpi(String dpi) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE dpi = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dpi);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public boolean insertar(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (dpi, nombre, fecha_nacimiento, telefono, email, nacionalidad) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getDpi());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getFechaNacimiento());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getNacionalidad());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nombre=?, fecha_nacimiento=?, telefono=?, email=?, nacionalidad=? WHERE id=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getFechaNacimiento());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getNacionalidad());
            ps.setInt(6, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setDpi(rs.getString("dpi"));
        c.setNombre(rs.getString("nombre"));
        c.setFechaNacimiento(rs.getString("fecha_nacimiento"));
        c.setTelefono(rs.getString("telefono"));
        c.setEmail(rs.getString("email"));
        c.setNacionalidad(rs.getString("nacionalidad"));
        return c;
    }
}