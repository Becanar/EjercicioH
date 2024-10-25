package com.example.ejercicioh.dao;

import com.example.ejercicioh.db.ConectorDB;
import com.example.ejercicioh.model.Persona;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonaDao {

    public static ObservableList<Persona> cargarPersonas() {
        ConectorDB cn;
        ObservableList<Persona> lst = FXCollections.observableArrayList();
        try{
            cn = new ConectorDB();

            String consulta = "SELECT id,nombre,apellidos,edad FROM Persona";
            PreparedStatement ps = cn.getConnection().prepareStatement(consulta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellidos = rs.getString("apellidos");
                int edad = rs.getInt("edad");
                Persona p = new Persona(id,nombre,apellidos,edad);
                lst.add(p);

            }
            rs.close();
            cn.closeConexion();
        }catch (
                SQLException e) {
            System.out.println(e.getMessage());
        }
        return lst;
    }

    public static boolean modificarPersona(Persona p, Persona pN) {
        ConectorDB cn;
        PreparedStatement ps;

        try {
            cn = new ConectorDB();

            String consulta = "UPDATE Persona SET nombre = ?,apellidos = ?,edad = ? WHERE id = ?";
            ps = cn.getConnection().prepareStatement(consulta);

            ps.setString(1, pN.getNombre());
            ps.setString(2, pN.getApellidos());
            ps.setInt(3, pN.getEdad());
            ps.setInt(4, p.getId());
            int result = ps.executeUpdate();
            ps.close();
            cn.closeConexion();
            return result > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;

        }

    }

    public  static int insertarPersona(Persona p) {
        ConectorDB cn;
        PreparedStatement ps;

        try {
            cn = new ConectorDB();
            String sql = "INSERT INTO Persona (nombre,apellidos,edad) VALUES (?,?,?) ";
            ps = cn.getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getApellidos());
            ps.setInt(3, p.getEdad());

            int result = ps.executeUpdate();
            if (result > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    ps.close();
                    cn.closeConexion();
                    return id;
                }
            }
            ps.close();
            cn.closeConexion();
            return -1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;

        }

    }

    public  static boolean eliminarPersona(Persona p){

        ConectorDB cn;
        PreparedStatement ps;
        try {
            cn = new ConectorDB();
            String sql = "DELETE FROM Persona WHERE (id = ?)";
            ps = cn.getConnection().prepareStatement(sql);
            ps.setInt(1, p.getId());
            int filasAfectadas = ps.executeUpdate();
            ps.close();
            cn.closeConexion();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}