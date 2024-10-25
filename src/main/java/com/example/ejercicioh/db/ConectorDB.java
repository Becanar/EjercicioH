package com.example.ejercicioh.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase que establece la conexión a la base de datos MySQL.
 * Proporciona métodos para obtener la conexión y cerrarla.
 */
public class ConectorDB {
    private final Connection connection; ///< Conexión a la base de datos.

    /**
     * Constructor de la clase ConectorDB.
     * Establece la conexión a la base de datos con las credenciales especificadas.
     *
     * @throws SQLException Si hay un error al establecer la conexión a la base de datos.
     */
    public ConectorDB() throws SQLException {
        Properties connConfig = new Properties();
        connConfig.setProperty("user", "admin"); ///< Usuario de la base de datos.
        connConfig.setProperty("password", "1234"); ///< Contraseña del usuario.

        // Establece la conexión a la base de datos
        connection = DriverManager.getConnection("jdbc:mysql://localhost:33066/personas?serverTimezone=Europe/Madrid", connConfig);
        connection.setAutoCommit(true); // Activa el auto-commit para la conexión.

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        /*
        System.out.println();
        System.out.println("--- Datos de conexión ------------------------------------------");
        System.out.printf("Base de datos: %s%n", databaseMetaData.getDatabaseProductName());
        System.out.printf("  Versión: %s%n", databaseMetaData.getDatabaseProductVersion());
        System.out.printf("Driver: %s%n", databaseMetaData.getDriverName());
        System.out.printf("  Versión: %s%n", databaseMetaData.getDriverVersion());
        System.out.println("----------------------------------------------------------------");
        System.out.println();
        connection.setAutoCommit(true);
        */
    }

    /**
     * Obtiene la conexión a la base de datos.
     *
     * @return La conexión a la base de datos.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Cierra la conexión a la base de datos.
     *
     * @throws SQLException Si hay un error al cerrar la conexión.
     */
    public Connection closeConexion() throws SQLException {
        connection.close(); // Cierra la conexión.
        return connection; // Retorna la conexión cerrada (aunque ya no será útil).
    }

    /*public static void main(String[] args) throws SQLException {
        ConectorDB c = new ConectorDB();
        c.getConnection(); // Obtiene la conexión a la base de datos.
    }*/
}
