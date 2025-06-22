package org.universidad.automatizacionmetodosgranmydosfases;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.universidad.automatizacionmetodosgranmydosfases.metodos.SimplexBase;
import org.universidad.automatizacionmetodosgranmydosfases.metodos.VariableInfo;

import java.util.List;

import static org.universidad.automatizacionmetodosgranmydosfases.HelloController.crearGridPane;

public class WindowSolucion {
    SimplexBase simplex;

    private Stage stage;
    private VBox mainContainer;
    private GridPane gridPane;
    private HBox btnContainer;
    private Label labelPaso;
    private Button btnAnterior;
    private Button btnSiguiente;
    private GridPane gridPaneSolucion;
    private int pasoActual;
    private int fila;

    public WindowSolucion(SimplexBase simplex) {
        this.simplex = simplex;

        stage = new Stage();
        mainContainer = new VBox(10);
        mainContainer.getStyleClass().add("main-container");

        gridPane = crearGridPane();
        btnContainer = new HBox(10);
        btnContainer.getStyleClass().add("btn-container");

        labelPaso = new Label();
        labelPaso.getStyleClass().add("label");
        btnAnterior = new Button("Anterior");
        btnSiguiente = new Button("Siguiente");
        btnAnterior.setOnAction(e -> mostrarPasoAnterior());
        btnSiguiente.setOnAction(e -> mostrarPasoSiguiente());

        gridPaneSolucion = crearGridPane();

        btnContainer.getChildren().addAll(btnAnterior, labelPaso, btnSiguiente);
        mainContainer.getChildren().addAll(gridPane, btnContainer, gridPaneSolucion);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("scroll-pane");

        Scene scene = new Scene(mainContainer);
        stage.setScene(scene);
        stage.setTitle("Proceso del Método Simplex");
    }

    public void mostrar() {
        mostrarPaso(pasoActual);
        if (simplex.isSolucionEncontrada())
            mostrarSolucionFinal();
        stage.show();
    }

    private void mostrarPaso(int pasoActual) {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();

        double[][] tableau = simplex.getTablaPaso(pasoActual);
        String descripcionPaso = simplex.getDescripcionPaso(pasoActual);
        String varEntrada = simplex.getVariableEntradaPaso(pasoActual);
        String varSalida = simplex.getVariableSalidaPaso(pasoActual);

        dimencinarCeldas(tableau);

        // Mostrar descripción del paso
        Label labelDescripcionPaso = new Label(descripcionPaso);
        labelDescripcionPaso.getStyleClass().add("label");
        gridPane.add(labelDescripcionPaso, 0, 0, tableau[0].length + 1, 1);

        // Mostrar variables de entrada/salida si existen
        if (!varEntrada.isEmpty() || !varSalida.isEmpty()) {
            Label labelVariables = new Label("Entrada: " + varEntrada + ", Salida: " + varSalida);
            labelVariables.getStyleClass().add("label");
            gridPane.add(labelVariables, 0, 1, tableau[0].length + 1, 1);
        }

        // Mostrar encabezados de columnas usando información de variables
        mostrarEncabezadosColumnas(tableau[0].length);

        // Mostrar RHS
        Label labelRHS = new Label("RHS");
        gridPane.add(labelRHS, tableau[0].length, 2);

        // Mostrar filas del tableau con etiquetas de variables en base
        mostrarFilasTableau(tableau);

        labelPaso.setText("Paso " + (pasoActual + 1) + " de " + simplex.cantPasos());
        btnAnterior.setDisable(pasoActual == 0);
        btnSiguiente.setDisable(pasoActual == simplex.cantPasos() - 1);
    }

    private void mostrarEncabezadosColumnas(int nroColumnas) {
        List<VariableInfo> variables = simplex.getVariables();

        // Columna 0: Etiqueta para las filas (vacía en el encabezado)
        Label labelVacio = new Label("");
        gridPane.add(labelVacio, 0, 2);

        // Columna 1: Siempre es Z (función objetivo)
        Label labelZ = new Label("Z");
        labelZ.getStyleClass().add("column-header");
        gridPane.add(labelZ, 1, 2);

        // Columnas 2 en adelante: Variables según su orden en el tableau
        for (int j = 1; j < nroColumnas - 1; j++) {
            String nombreColumna = "";

            for (VariableInfo var : variables) {
                if (var.getIndiceColumna() == j) {
                    nombreColumna = obtenerNombreVisualVariable(var);
                    break;
                }
            }

            Label labelColumna = new Label(nombreColumna);
            labelColumna.getStyleClass().add("column-header");

            // Aplicar estilo especial si es una variable artificial
            boolean esArtificial = false;
            for (VariableInfo var : variables) {
                if (var.getIndiceColumna() == j && var.esArtificial()) {
                    labelColumna.getStyleClass().add("artificial-variable");
                    esArtificial = true;
                    break;
                }
            }

            gridPane.add(labelColumna, j + 1, 2);
        }
    }
    private void mostrarFilasTableau(double[][] tableau) {
        List<VariableInfo> variables = simplex.getVariables();

        for (int i = 0; i < tableau.length; i++) {
            // Etiqueta de fila
            String labelFilaText;
            if (i == 0) {
                labelFilaText = "Z";
            } else {
                // Buscar qué variable está en base en esta fila
                labelFilaText = "R" + i; // Default
                for (VariableInfo var : variables) {
                    if (var.isEstaEnBase() && var.getFilaEnBase() == i) {
                        labelFilaText = obtenerNombreVisualVariable(var);
                        break;
                    }
                }
            }

            Label labelFila = new Label(labelFilaText);
            labelFila.getStyleClass().add("row-label");
            gridPane.add(labelFila, 0, i + 3);

            // Valores de la fila
            for (int j = 0; j < tableau[i].length; j++) {
                String valor = String.format("%.2f", tableau[i][j]);
                TextField labelValor = new TextField(valor.equals("-0.00") ? "0.00" : valor);
                labelValor.setEditable(false);

                // Aplicar estilo especial si es una variable artificial
                if (j > 0) { // No aplicar a la columna Z
                    for (VariableInfo var : variables) {
                        if (var.getIndiceColumna() == j && var.esArtificial()) {
                            labelValor.getStyleClass().add("artificial-variable");
                            break;
                        }
                    }
                }

                gridPane.add(labelValor, j + 1, i + 3);
            }
        }
    }

    private String obtenerNombreVisualVariable(VariableInfo var) {
        switch (var.getTipo()) {
            case ORIGINAL:
                String numero = var.getNombre().substring(1);
                return "X" + numero;
            case HOLGURA:
                String numeroH = var.getNombre().substring(1);
                return "S" + numeroH;
            case EXCESO:
                String numeroE = var.getNombre().substring(1);
                return "E" + numeroE;
            case ARTIFICIAL:
                String numeroA = var.getNombre().substring(1);
                return "A" + numeroA;
            default:
                return var.getNombre().toUpperCase();
        }
    }

    private void dimencinarCeldas(double[][] tableau) {
        // Configurar columnas basándose en el número real de columnas del tableau
        int totalColumnas = tableau[0].length + 2; // +2 para etiqueta de fila y RHS

        for (int i = 0; i < totalColumnas; i++) {
            ColumnConstraints columna = new ColumnConstraints();
            columna.setMinWidth(80);
            columna.setPrefWidth(80);
            columna.setMaxWidth(80);
            gridPane.getColumnConstraints().add(columna);
        }
    }

    private void mostrarSolucionFinal() {
        gridPaneSolucion.getChildren().clear();
        gridPaneSolucion.getColumnConstraints().clear();

        // Configurar columnas para la solución
        for (int i = 0; i < 3; i++) {
            ColumnConstraints columna = new ColumnConstraints();
            columna.setMinWidth(100);
            columna.setPrefWidth(100);
            gridPaneSolucion.getColumnConstraints().add(columna);
        }

        Label titulo = new Label("Solución Óptima");
        titulo.getStyleClass().add("title-label");
        gridPaneSolucion.add(titulo, 0, 0, 3, 1);

        fila = 1;

        // Mostrar variables originales
        List<VariableInfo> variablesOriginales = simplex.obtenerVariablesPorTipo(VariableInfo.TipoVariable.ORIGINAL);
        mostrarResultadoDeLasVariables(variablesOriginales, "Variables de Decisión:", "(Original)");

        // Mostrar variables de holgura en base (si tienen valor > 0)
        List<VariableInfo> variablesHolgura = simplex.obtenerVariablesPorTipo(VariableInfo.TipoVariable.HOLGURA);
        List<VariableInfo> holguraEnBase = variablesHolgura.stream()
                .filter(var -> var.isEstaEnBase() && Math.abs(var.getValor()) > SimplexBase.epsilon)
                .toList();

        mostrarResultadoDeLasVariables(holguraEnBase, "Variables de Holgura:", "(Holgura)");

        // Mostrar variables de exceso en base (si tienen valor > 0)
        List<VariableInfo> variablesExceso = simplex.obtenerVariablesPorTipo(VariableInfo.TipoVariable.EXCESO);
        List<VariableInfo> excesoEnBase = variablesExceso.stream()
                .filter(var -> var.isEstaEnBase() && Math.abs(var.getValor()) > SimplexBase.epsilon)
                .toList();
        mostrarResultadoDeLasVariables(excesoEnBase, "Variables de Exceso:", "(Exceso)");

        // Mostrar variables artificiales en base (si tienen valor > 0)
        List<VariableInfo> variablesArtificial = simplex.obtenerVariablesPorTipo(VariableInfo.TipoVariable.ARTIFICIAL);
        List<VariableInfo> artificialEnBase = variablesArtificial.stream()
                .filter(var -> var.isEstaEnBase() && Math.abs(var.getValor()) > SimplexBase.epsilon)
                .toList();
        mostrarResultadoDeLasVariables(artificialEnBase, "Variables Artificial:", "(Artificial)");

        // Mostrar valor de la función objetivo
        fila++; // Espacio
        Label labelZ = new Label("Función Objetivo (Z):");
        labelZ.getStyleClass().add("objective-label");
        Label labelValorZ = new Label(String.format("%.2f", simplex.solucion.get("z")));
        labelValorZ.getStyleClass().add("objective-value");
        Label tipoProblema = new Label(simplex.isMaximizar() ? "(Maximizar)" : "(Minimizar)");
        tipoProblema.getStyleClass().add("tipo-variable");

        gridPaneSolucion.add(labelZ, 0, fila);
        gridPaneSolucion.add(labelValorZ, 1, fila);
        gridPaneSolucion.add(tipoProblema, 2, fila);
    }

    private void mostrarResultadoDeLasVariables(List<VariableInfo> variables, String subtitulo, String tipo) {
        if (!variables.isEmpty()) {
            Label subtituloOriginales = new Label(subtitulo);
            subtituloOriginales.getStyleClass().add("subtitle-label");
            gridPaneSolucion.add(subtituloOriginales, 0, fila++, 3, 1);

            for (VariableInfo var : variables) {
                double valor = var.getValor();

                Label txtVar = new Label(obtenerNombreVisualVariable(var) + ":");
                Label txtValor = new Label(String.format("%.2f", valor));
                Label txtTipo = new Label(tipo);
                txtTipo.getStyleClass().add("tipo-variable");

                gridPaneSolucion.add(txtVar, 0, fila);
                gridPaneSolucion.add(txtValor, 1, fila);
                gridPaneSolucion.add(txtTipo, 2, fila);
                fila++;
            }
        }
    }

    private void mostrarPasoAnterior() {
        if (pasoActual > 0) {
            pasoActual--;
            mostrarPaso(pasoActual);
        }
    }

    private void mostrarPasoSiguiente() {
        if (pasoActual < simplex.cantPasos() - 1) {
            pasoActual++;
            mostrarPaso(pasoActual);
        }
    }
}