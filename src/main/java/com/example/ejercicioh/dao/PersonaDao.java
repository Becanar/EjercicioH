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
        ObservableList<Persona> listadoDePersonas= FXCollections.observableArrayList();

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
                Persona mp = new Persona(id,nombre,apellidos,edad);
                listadoDePersonas.add(mp);

            }
            rs.close();
            cn.closeConexion();
        }catch (
                SQLException e) {
            System.out.println(e.getMessage());
        }
        return listadoDePersonas;
    }

    public static boolean modificarPersona(Persona p, Persona pNueva) {
        ConectorDB cn;
        PreparedStatement ps;

        try {
            cn = new ConectorDB();

            String consulta = "UPDATE Persona SET nombre = ?,apellidos = ?,edad = ? WHERE id = ?";
            ps = cn.getConnection().prepareStatement(consulta);

            ps.setString(1, pNueva.getNombre());
            ps.setString(2, pNueva.getApellidos());
            ps.setInt(3, pNueva.getEdad());
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

    public  static int insertarPersona(Persona persona) {
        ConectorDB cn;
        PreparedStatement ps;

        try {
            cn = new ConectorDB();


            String consulta = "INSERT INTO Persona (nombre,apellidos,edad) VALUES (?,?,?) ";
            ps = cn.getConnection().prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setString(1, persona.getNombre());
            ps.setString(2, persona.getApellidos());
            ps.setInt(3, persona.getEdad());

            int filasAfectadas = ps.executeUpdate();
            //if (pstmt != null)

            //if (cn != null)
            System.out.println("Nueva entrada en  persona");
            if (filasAfectadas > 0) {
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
               /* Alertas alertaError = new Alertas();
                alertaError.mostrarError("No he podido cargar el listado de dnis");
                alertaError.mostrarError(e.getMessage());*/
            System.out.println(e.getMessage());
            return -1;

        }

    }

    public  static boolean eliminarPersona(Persona personaAEliminar){

        ConectorDB cn;
        PreparedStatement ps;
        try {
            cn = new ConectorDB();
            //DELETE FROM `DNI`.`dni` WHERE (`dni` = 'asdasd');
            String consulta = "DELETE FROM Persona WHERE (id = ?)";
            ps = cn.getConnection().prepareStatement(consulta);
            ps.setInt(1, personaAEliminar.getId());
            int filasAfectadas = ps.executeUpdate();
            ps.close();
            cn.closeConexion();
            System.out.println("Eliminado con éxito");
            return filasAfectadas > 0;

        } catch (SQLException e) {
                /*
                Alertas alertaError = new Alertas();
                alertaError.mostrarError("No he podido borrar ese registro");
                alertaError.mostrarError(e.getMessage());*/
            System.out.println(e.getMessage());
            return false;
        }
    }

}