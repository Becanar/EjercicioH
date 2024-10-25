package com.example.ejercicioh.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConectorDB {
    private final Connection connection;

    public ConectorDB() throws SQLException {

        Properties connConfig = new Properties();
        connConfig.setProperty("user", "admin");
        connConfig.setProperty("password", "1234");

        connection = DriverManager.getConnection("jdbc:mysql://localhost:33066/personas?serverTimezone=Europe/Madrid", connConfig);
        connection.setAutoCommit(true);
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
        connection.setAutoCommit(true);*/
    }

    public Connection getConnection() {
        return connection;
    }

    public Connection closeConexion() throws SQLException{
        connection.close();
        return connection;
    }

    /*public static void main(String[] args) throws SQLException {
        ConectorDB c=new ConectorDB();
        c.getConnection();
    }*/

}