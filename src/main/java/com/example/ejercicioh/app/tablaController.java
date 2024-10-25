package com.example.ejercicioh.app;

import com.example.ejercicioh.dao.PersonaDao;
import com.example.ejercicioh.model.Persona;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

public class tablaController {

    @FXML
    private Button btAgregar;
    @FXML
    private Button btEliminar;
    @FXML
    private Button btModificar;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TableColumn<Persona, String> columnaApellidos;
    @FXML
    private TableColumn<Persona, Integer> columnaEdad;
    @FXML
    private TableColumn<Persona, String> columnaNombre;
    @FXML
    private TableView<Persona> tablaVista;

    private ObservableList<Persona> personas = FXCollections.observableArrayList();
    private FilteredList<Persona> filtro;

    private TextField txtNombre;
    private TextField txtApellidos;
    private TextField txtEdad;
    private Button btnGuardar;
    private Button btnCancelar;
    private Stage modal;

    public void initialize() {
        columnaNombre.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getNombre()));
        columnaApellidos.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getApellidos()));
        columnaEdad.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getEdad()));

        personas = PersonaDao.cargarPersonas();
        filtro = new FilteredList<>(personas);
        tablaVista.setItems(filtro);

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> filtrar(null));
    }

    @FXML
    void agregarPersona(ActionEvent event) {
        mostrarVentanaDatos((Stage) btAgregar.getScene().getWindow(), false);
        btnGuardar.setOnAction(actionEvent -> {
            guardar(false);
            filtro.setPredicate(null);
            tablaVista.getSelectionModel().clearSelection();
        });
        btnCancelar.setOnAction(actionEvent -> cancelar());
    }

    public void mostrarVentanaDatos(Stage ventanaPrincipal, boolean esModif) {
        modal = new Stage();
        modal.setResizable(false);
        modal.initOwner(ventanaPrincipal);
        modal.initModality(Modality.WINDOW_MODAL);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label lblNombre = new Label("Nombre");
        txtNombre = new TextField(esModif ? tablaVista.getSelectionModel().getSelectedItem().getNombre() : "");
        gridPane.add(lblNombre, 0, 0);
        gridPane.add(txtNombre, 1, 0);

        Label lblApellidos = new Label("Apellidos");
        txtApellidos = new TextField(esModif ? tablaVista.getSelectionModel().getSelectedItem().getApellidos() : "");
        gridPane.add(lblApellidos, 0, 1);
        gridPane.add(txtApellidos, 1, 1);

        Label lblEdad = new Label("Edad");
        txtEdad = new TextField(esModif ? String.valueOf(tablaVista.getSelectionModel().getSelectedItem().getEdad()) : "");
        gridPane.add(lblEdad, 0, 2);
        gridPane.add(txtEdad, 1, 2);

        btnGuardar = new Button("Guardar");
        btnCancelar = new Button("Cancelar");
        FlowPane flowPane = new FlowPane(btnGuardar, btnCancelar);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setHgap(20);
        gridPane.add(flowPane, 0, 3, 2, 1);

        Scene scene = new Scene(gridPane, 300, 150);
        modal.setScene(scene);
        modal.setResizable(false);
        modal.setTitle(esModif ? "Editar Persona" : "Nueva Persona");
        modal.show();
    }

    public void guardar(boolean esModificar) {
        if (valido()) {
            String nombre = txtNombre.getText();
            String apellidos = txtApellidos.getText();
            int edad = Integer.parseInt(txtEdad.getText());

            Persona nuevaPersona = new Persona(nombre, apellidos, edad);

            boolean existe = false;
            for (Persona persona : personas) {
                if (persona.equals(nuevaPersona)) {
                    if (esModificar && persona.equals(tablaVista.getSelectionModel().getSelectedItem())) {
                        continue;
                    }
                    existe = true;
                    break;
                }
            }

            if (existe) {
                ArrayList<String> errores = new ArrayList<>();
                errores.add("La persona ya existe.");
                mostrarAlertError(errores);
                return;
            }
            if (esModificar) {
                Persona personaSeleccionada = tablaVista.getSelectionModel().getSelectedItem();
                Persona personaModificada = new Persona(personaSeleccionada.getId(), nombre, apellidos, edad);

                if (PersonaDao.modificarPersona(personaSeleccionada, personaModificada)) {
                    int index = personas.indexOf(personaSeleccionada);
                    if (index >= 0) {
                        personas.set(index, personaModificada);
                        mostrarVentanaModificado();
                    }
                }
            } else {
                int idGenerado = PersonaDao.insertarPersona(nuevaPersona);
                if (idGenerado != -1) {
                    nuevaPersona.setId(idGenerado);
                    personas.add(nuevaPersona);
                    mostrarVentanaAgregado();
                }
            }

            modal.close();
            txtBuscar.setText("");
            filtro.setPredicate(null);
        }
    }

    private boolean valido() {
        boolean error = false;
        ArrayList<String> errores = new ArrayList<>();
        if (txtNombre.getText().equals("")) {
            errores.add("El campo Nombre es obligatorio.");
            error = true;
        }
        if (txtApellidos.getText().equals("")) {
            errores.add("El campo Apellidos es obligatorio.");
            error = true;
        }
        try {
            Integer.parseInt(txtEdad.getText());
        } catch (NumberFormatException e) {
            errores.add("El campo Edad debe ser numérico.");
            error = true;
        }
        if (error) {
            mostrarAlertError(errores);
            return false;
        }
        return true;
    }

    @FXML
    void eliminar(ActionEvent event) {
        Persona p = tablaVista.getSelectionModel().getSelectedItem();
        if (p == null) {
            ArrayList<String> lst = new ArrayList<>();
            lst.add("No has seleccionado ninguna persona.");
            mostrarAlertError(lst);
        } else {
            if (PersonaDao.eliminarPersona(p)) {
                personas.remove(p);
                filtro.setPredicate(null);
                mostrarVentanaEliminado();
                tablaVista.getSelectionModel().clearSelection();
                txtBuscar.setText("");
            }
        }
    }

    @FXML
    void modificar(ActionEvent event) {
        Persona p = tablaVista.getSelectionModel().getSelectedItem();
        if (p == null) {
            ArrayList<String> lst = new ArrayList<>();
            lst.add("No has seleccionado ninguna persona.");
            mostrarAlertError(lst);
        } else {
            mostrarVentanaDatos((Stage) btModificar.getScene().getWindow(), true);
            btnGuardar.setOnAction(actionEvent -> {
                guardar(true);
                tablaVista.getSelectionModel().clearSelection();
            });
            btnCancelar.setOnAction(actionEvent -> cancelar());
        }
    }

    @FXML
    void filtrar(ActionEvent event) {
        if (txtBuscar.getText().isEmpty()) {
            tablaVista.setItems(personas);
        } else {
            String textoBusqueda = txtBuscar.getText().toLowerCase();
            filtro.setPredicate(persona ->
                    persona.getNombre().toLowerCase().startsWith(textoBusqueda)
            );
            tablaVista.setItems(filtro);
        }
    }

    private void mostrarAlertError(ArrayList<String> lst) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(btAgregar.getScene().getWindow());
        alert.setHeaderText(null);
        alert.setTitle("Error");
        String error = String.join("\n", lst);
        alert.setContentText(error);
        alert.showAndWait();
    }

    private void mostrarVentanaAgregado() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.initOwner(btAgregar.getScene().getWindow());
        alerta.setHeaderText(null);
        alerta.setTitle("Info");
        alerta.setContentText("Persona agregada correctamente.");
        alerta.showAndWait();
    }

    private void mostrarVentanaModificado() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.initOwner(btAgregar.getScene().getWindow());
        alerta.setHeaderText(null);
        alerta.setTitle("Info");
        alerta.setContentText("Persona modificada correctamente.");
        alerta.showAndWait();
    }

    private void mostrarVentanaEliminado() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.initOwner(btAgregar.getScene().getWindow());
        alerta.setHeaderText(null);
        alerta.setTitle("Info");
        alerta.setContentText("Persona eliminada correctamente.");
        alerta.showAndWait();
    }

    private void cancelar() {
        modal.close();
    }
}
