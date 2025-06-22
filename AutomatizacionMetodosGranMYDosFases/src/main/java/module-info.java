module org.universidad.automatizacionmetodosgranmydosfases {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;

    opens org.universidad.automatizacionmetodosgranmydosfases to javafx.fxml;
    exports org.universidad.automatizacionmetodosgranmydosfases;
}