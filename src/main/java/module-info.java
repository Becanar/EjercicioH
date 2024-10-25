module com.example.ejercicioh {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ejercicioh to javafx.fxml;
    exports com.example.ejercicioh.model;
    opens com.example.ejercicioh.model to javafx.fxml;
    exports com.example.ejercicioh.app;
    opens com.example.ejercicioh.app to javafx.fxml;
}