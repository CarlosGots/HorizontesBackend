package com.horizontes.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de manejar la conexión a la base de datos MySQL.
 * Todas las clases DAO usan esta clase para conectarse.
 */
public class Conexion {

    // Dirección de la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/horizontes_db?useSSL=false&serverTimezone=UTC";
    
    // Usuario de MySQL
    private static final String USUARIO = "root";
    
    // Contraseña de MySQL
    private static final String PASSWORD = "";

    /**
     * Crea y devuelve una conexión activa a la base de datos.
     * Se llama cada vez que un DAO necesita hacer una consulta.
     * @return Connection objeto de conexión listo para usar
     * @throws SQLException si no se puede conectar
     */
    public static Connection getConexion() throws SQLException {
        try {
            // Cargamos el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado: " + e.getMessage());
        }
    }
}