package org.universidad.automatizacionmetodosgranmydosfases;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.universidad.automatizacionmetodosgranmydosfases.metodos.DosFases;
import org.universidad.automatizacionmetodosgranmydosfases.metodos.GranM;
import org.universidad.automatizacionmetodosgranmydosfases.metodos.SimplexBase;

import java.util.ArrayList;
import java.util.List;

public class HelloController {
    SimplexBase simplex;

    private double[] funcionObjetivo;
    private double[][] restricciones;
    private double[] terminosIndependientes;
    private char[] tipoRestricciones;

    private ComboBox<Character> comboBoxTipoRestricciones;
    private List<ComboBox<Character>> operadoresRestricciones;
    private ComboBox<String> comboBoxMetodo;
    private ComboBox<String> comboBoxTipoObjetivo;

    private int pasoActual;

    @FXML private Spinner<Integer> spinnerVariables;
    @FXML private Spinner<Integer> spinnerRestricciones;

    @FXML private VBox containerFuncionObjetivo;
    @FXML private VBox containerRestricciones;

    @FXML private ScrollPane scrollFuncionObjetivo;
    @FXML private ScrollPane scrollRestricciones;

    @FXML private List<TextField> textFieldsFuncionObjetivo;
    @FXML private List<List<TextField>> textFieldsRestricciones;
    @FXML private List<TextField> textFieldsTerminosIndependientes;


    public void initialize() {
        SpinnerValueFactory<Integer> spinnerVariablesValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 100, 1);
        spinnerVariables.setValueFactory(spinnerVariablesValueFactory);

        SpinnerValueFactory<Integer> spinnerRestriccionesValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 1);
        spinnerRestricciones.setValueFactory(spinnerRestriccionesValueFactory);
    }

    @FXML
    public void generarModelo(ActionEvent actionEvent) {
        textFieldsFuncionObjetivo = new ArrayList<>();
        textFieldsRestricciones = new ArrayList<>();
        textFieldsTerminosIndependientes = new ArrayList<>();

        limpiar(actionEvent);

        gridPaneFuncionObjetivo();

        gridPaneResticciones();

    }

    private void gridPaneFuncionObjetivo() {
        int nroVariables = spinnerVariables.getValue();

        GridPane gridFuncionObjetivo = crearGridPane();

        Label label = new Label("Función objetivo (Coeficientes): ");

        comboBoxMetodo = new ComboBox<>();
        comboBoxMetodo.getItems().addAll("Gran M", "Dos Fases");
        comboBoxMetodo.setValue("Gran M");

        comboBoxTipoObjetivo = new ComboBox<>();
        comboBoxTipoObjetivo.getItems().addAll("Minimizar", "Maximizar");
        comboBoxTipoObjetivo.setValue("Minimizar");

        gridFuncionObjetivo.add(label, 0, 0, nroVariables, 1);
        gridFuncionObjetivo.add(comboBoxMetodo, 2, 0, 1, 1);
        gridFuncionObjetivo.add(comboBoxTipoObjetivo, 3, 0, 1, 1);

        for (int i = 0; i < nroVariables; i++) {
            Label labelFuncionObjetivo = new Label("x" + (i + 1));
            TextField textFieldCoeficientes = new TextField();
            textFieldCoeficientes.setPromptText("Coef. X" + (i + 1));

            gridFuncionObjetivo.add(labelFuncionObjetivo, i, 1);
            gridFuncionObjetivo.add(textFieldCoeficientes, i, 2);
            textFieldsFuncionObjetivo.add(textFieldCoeficientes);
        }

        containerFuncionObjetivo.getChildren().add(gridFuncionObjetivo);
    }

    private void gridPaneResticciones() {
        int nroRestricciones = spinnerRestricciones.getValue();
        int nroVariables = spinnerVariables.getValue();

        operadoresRestricciones = new ArrayList<>();

        for (int r = 0; r < nroRestricciones; r++) {
            GridPane gridRestriccion = crearGridPane();

            Label lblTituloRestriccion = new Label("Restricción " + (r + 1) + ":");
            gridRestriccion.add(lblTituloRestriccion, 0, 0, nroVariables + 2, 1);

            List<TextField> camposRestriccion = new ArrayList<>();

            for (int v = 0; v < nroVariables; v++) {
                Label lblVariable = new Label("X" + (v + 1) + ":");
                TextField txtCoeficiente = new TextField();
                txtCoeficiente.setPromptText("Coef. X" + (v + 1));

                gridRestriccion.add(lblVariable, v, 1);
                gridRestriccion.add(txtCoeficiente, v, 2);
                camposRestriccion.add(txtCoeficiente);
            }

            comboBoxTipoRestricciones = new ComboBox<>();
            comboBoxTipoRestricciones.getItems().addAll('≤', '≥', '=');
            comboBoxTipoRestricciones.setValue('≤');
            gridRestriccion.add(comboBoxTipoRestricciones, nroVariables, 2);
            operadoresRestricciones.add(comboBoxTipoRestricciones);

            // Campo para el término independiente de la restricción
            TextField txtTerminoIndependiente = new TextField();
            txtTerminoIndependiente.setPromptText("Valor");
            gridRestriccion.add(new Label("Término:"), nroVariables+ 1, 1);
            gridRestriccion.add(txtTerminoIndependiente, nroVariables + 1, 2);
            textFieldsTerminosIndependientes.add(txtTerminoIndependiente);

            textFieldsRestricciones.add(camposRestriccion);
            containerRestricciones.getChildren().add(gridRestriccion);
        }
        GridPane gridNoNegatividad = crearGridPane();

        Label lblTituloNoNeg = new Label("Restricciones de no negatividad:");
        gridNoNegatividad.add(lblTituloNoNeg, 0, 0, nroVariables, 1);

        for (int i = 0; i < nroVariables; i++) {
            Label lblRestriccion = new Label("X" + (i + 1) + "≥ 0");
            gridNoNegatividad.add(lblRestriccion, i, 1);
        }
        containerRestricciones.getChildren().add(gridNoNegatividad);

        Button btnResolver = new Button("Resolver Problema");
        btnResolver.setOnAction(this::resolverProblema);
        btnResolver.getStyleClass().add("button-limpiar");
        containerRestricciones.getChildren().add(btnResolver);
    }

    @FXML
    private void resolverProblema(ActionEvent actionEvent) {
        try {
            int numVariables = spinnerVariables.getValue();
            int numRestricciones = spinnerRestricciones.getValue();

            funcionObjetivo = new double[numVariables];
            for (int i = 0; i < numVariables; i++) {
                funcionObjetivo[i] = Double.parseDouble(textFieldsFuncionObjetivo.get(i).getText());
            }

            // Capturar coeficientes de las restricciones y términos independientes
            restricciones = new double[numRestricciones][numVariables];
            terminosIndependientes = new double[numRestricciones];
            tipoRestricciones = new char[numRestricciones];
            boolean todasRestriccionesSonMenorIgual = true;

            for (int r = 0; r < numRestricciones; r++) {
                for (int v = 0; v < numVariables; v++) {
                    restricciones[r][v] = Double.parseDouble(textFieldsRestricciones.get(r).get(v).getText());
                }
                terminosIndependientes[r] = Double.parseDouble(textFieldsTerminosIndependientes.get(r).getText());
                tipoRestricciones[r] = operadoresRestricciones.get(r).getValue();
                if (tipoRestricciones[r] != '≤')
                    todasRestriccionesSonMenorIgual = false;
            }

            // Crear instancia del resolvedor y ejecutar el método Simplex
            boolean maximizar;
            if (comboBoxTipoObjetivo.getValue().equals("Maximizar"))
                maximizar = true;
            else
                maximizar = false;

            String metodo = comboBoxMetodo.getValue();

            if (metodo.equals("Gran M")) {
                simplex = new GranM(funcionObjetivo, restricciones, terminosIndependientes, tipoRestricciones, maximizar);
            } else if (metodo.equals("Dos Fases")) {
                simplex = new DosFases(funcionObjetivo, restricciones, terminosIndependientes, tipoRestricciones, maximizar);
            }


           simplex.resolver();

            WindowSolucion window = new WindowSolucion(simplex);
            window.mostrar();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Por favor ingrese valores numéricos válidos en todos los campos.");
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error al resolver el problema: " + e.getMessage());
        }
    }

    public static GridPane crearGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.getStyleClass().add("grid-pane");
        return gridPane;
    }

    @FXML
    public void limpiar(ActionEvent actionEvent) {
        containerFuncionObjetivo.getChildren().clear();
        containerRestricciones.getChildren().clear();
        textFieldsFuncionObjetivo.clear();
        textFieldsRestricciones.clear();
        textFieldsTerminosIndependientes.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void Salir(ActionEvent actionEvent) {
        System.exit(0);
    }
}