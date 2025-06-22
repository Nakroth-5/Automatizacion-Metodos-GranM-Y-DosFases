package org.universidad.automatizacionmetodosgranmydosfases.metodos;

import org.universidad.automatizacionmetodosgranmydosfases.guardarPasos.GuardarPasos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SimplexBase {
    protected double[][] M;
    protected int nroFilas, nroColumnas, nroVariables, nroRestricciones;
    protected boolean maximizar, soluciónOptima;
    int nroColunaHolgura, nroColunaExceso, nroColunaArtificial;

    // Nueva estructura usando VariableInfo
    protected List<VariableInfo> variables;
    protected Map<String, VariableInfo> variablesPorNombre;

    public Map<String, Double> solucion;
    protected GuardarPasos historialDePasos;

    public static double epsilon = 1e-10;
    public static byte decimales = 6;

    protected SimplexBase() {
        this.soluciónOptima = true;
        this.solucion = new HashMap<>();
        this.historialDePasos = new GuardarPasos();
        this.variables = new ArrayList<>();
        this.variablesPorNombre = new HashMap<>();
        this.maximizar = true;
        this.nroVariables = 0;
        this.nroRestricciones = 0;
        this.nroFilas = 0;
        this.nroColumnas = 0;
    }

    protected void inicializarTableauBase(char[] tipoRestricciones) {
        contarVariablesPorTipo(tipoRestricciones);
        this.nroFilas = this.nroRestricciones + 1;
        this.nroColumnas = 1 + getTotalVariables() + 1; // Z + variables + RHS
        this.M = new double[this.nroFilas][this.nroColumnas];
    }

    private void contarVariablesPorTipo(char[] tipoRestricciones) {
        variables.clear();
        variablesPorNombre.clear();

        int indiceColumna = 1; // Columna 0 es para Z

        // Variables originales
        for (int i = 0; i < nroVariables; i++) {
            String nombre = "x" + (i + 1);
            VariableInfo var = new VariableInfo(indiceColumna++, VariableInfo.TipoVariable.ORIGINAL, nombre);
            variables.add(var);
            variablesPorNombre.put(nombre, var);
        }

        // Variables de holgura, exceso y artificiales
        int contadorHolgura = 1, contadorExceso = 1, contadorArtificial = 1;

        for (int i = 0; i < tipoRestricciones.length; i++) {
            switch (tipoRestricciones[i]) {
                case '≤':
                    String nombreHolgura = "s" + contadorHolgura++;
                    VariableInfo varHolgura = new VariableInfo(indiceColumna++,
                            VariableInfo.TipoVariable.HOLGURA, nombreHolgura, i + 1);
                    variables.add(varHolgura);
                    variablesPorNombre.put(nombreHolgura, varHolgura);
                    break;

                case '≥':
                    String nombreExceso = "e" + contadorExceso++;
                    VariableInfo varExceso = new VariableInfo(indiceColumna++,
                            VariableInfo.TipoVariable.EXCESO, nombreExceso);
                    variables.add(varExceso);
                    variablesPorNombre.put(nombreExceso, varExceso);

                    String nombreArtificial1 = "a" + contadorArtificial++;
                    VariableInfo varArtificial1 = new VariableInfo(indiceColumna++,
                            VariableInfo.TipoVariable.ARTIFICIAL, nombreArtificial1, i + 1);
                    variables.add(varArtificial1);
                    variablesPorNombre.put(nombreArtificial1, varArtificial1);
                    break;

                case '=':
                    String nombreArtificial2 = "a" + contadorArtificial++;
                    VariableInfo varArtificial2 = new VariableInfo(indiceColumna++,
                            VariableInfo.TipoVariable.ARTIFICIAL, nombreArtificial2, i + 1);
                    variables.add(varArtificial2);
                    variablesPorNombre.put(nombreArtificial2, varArtificial2);
                    break;
            }
        }
    }

    protected void setValor(int fila, int columna, double valor) {
        validarRango(fila, columna);
        this.M[fila][columna] = valor;
    }

    protected double getValor(int fila, int columna) {
        validarRango(fila, columna);
        return this.M[fila][columna];
    }

    private void validarRango(int f, int c) {
        if (f < 0 || f >= nroFilas) {
            throw new IllegalArgumentException(String.format(
                    "Fuera de rango f %d (rango válido: 0-%d)", f, nroFilas-1));
        }
        if (c < 0 || c >= nroColumnas) {
            throw new IllegalArgumentException(String.format(
                    "Fuera de rango c %d (rango válido: 0-%d)", c, nroColumnas-1));
        }
    }

    protected boolean existenNegativosEnLaFunciónObjetivo() {
        for (int j = 1; j < nroColumnas - 1; j++) {
            if (getValor(0, j) < 0) return true;
        }
        return false;
    }

    public abstract void resolver();

    protected void iteraciónSimplex() {
        int columnaPivote = obtenerColumnaPivote();
        if (columnaPivote == 0) {
            manejarProblemaNoAcotado("no se puede resolver el problema");
            return;
        }

        int filaPivote = obtenerFilaPivote(columnaPivote);
        if (filaPivote == 0) {
            manejarProblemaNoAcotado("no se puede resolver el problema");
            return;
        }

        // Actualizar información de variables antes del pivoteo
        VariableInfo varEntrante = obtenerVariablePorColumna(columnaPivote);
        VariableInfo varSaliente = obtenerVariableEnBase(filaPivote);

        realizarPivoteo(columnaPivote, filaPivote);
        actualizarBaseDeVariables(varEntrante, varSaliente, filaPivote);
    }

    protected void actualizarBaseDeVariables(VariableInfo varEntrante, VariableInfo varSaliente, int filaPivote) {
        if (varSaliente != null) {
            varSaliente.sacarDeBase();
        }

        if (varEntrante != null) {
            double valor = getValor(filaPivote, nroColumnas - 1);
            varEntrante.ponerEnBase(filaPivote, valor);
        }

        guardarPaso("Eliminación de la columna pivote",
                varEntrante != null ? varEntrante.getNombre() : "Variable desconocida",
                varSaliente != null ? varSaliente.getNombre() : "Variable desconocida");
    }

    protected void realizarPivoteo(int columnaPivote, int filaPivote) {
        double valorPivote = getValor(filaPivote, columnaPivote);

        if (Math.abs(valorPivote - 1.0) > epsilon) {
            normalizarFilaPivote(filaPivote, valorPivote);
            VariableInfo varEntrante = obtenerVariablePorColumna(columnaPivote);
            guardarPaso("Normalización de la fila pivote",
                    varEntrante != null ? varEntrante.getNombre() : "Variable desconocida",
                    "Fila " + filaPivote);
        }

        // Eliminación de Gauss
        for (int i = 0; i < nroFilas; i++) {
            if (i == filaPivote) continue;
            double factor = (-1) * getValor(i, columnaPivote);
            for (int j = 0; j < nroColumnas; j++) {
                setValor(i, j, redondear(getValor(i, j) + factor * getValor(filaPivote, j)));
            }
        }
    }

    protected void normalizarFilaPivote(int filaPivote, double valorPivote) {
        double inversoMultiplicador = 1.0 / valorPivote;
        for (int j = 1; j < nroColumnas; j++) {
            this.M[filaPivote][j] *= inversoMultiplicador;
        }
    }

    protected abstract int obtenerColumnaPivote();

    protected int obtenerColumnaPivoteMaximizar() {
        double maxNegativo = 0;
        int columnaPivote = 0;

        for (int j = 1; j < nroColumnas - 1; j++) {
            if (getValor(0, j) < maxNegativo) {
                maxNegativo = getValor(0, j);
                columnaPivote = j;
            }
        }
        return columnaPivote;
    }

    protected int obtenerFilaPivote(int columnaPivote) {
        double minimoActual = Double.POSITIVE_INFINITY;
        int filaPivote = 0;

        for (int i = 1; i < nroFilas; i++) {
            if (getValor(i, columnaPivote) <= 0) continue;

            double razón = getValor(i, nroColumnas - 1) / getValor(i, columnaPivote);
            if (razón < minimoActual) {
                minimoActual = razón;
                filaPivote = i;
            }
        }
        return filaPivote;
    }

    protected abstract boolean esOptimo();

    protected boolean hayVariablesArtificialesEnBase() {
        return variables.stream()
                .filter(VariableInfo::esArtificial)
                .filter(VariableInfo::isEstaEnBase)
                .anyMatch(var -> Math.abs(var.getValor()) > epsilon);
    }

    protected void manejarProblemaNoAcotado(String tipo) {
        guardarPaso("Problema no acotado " + tipo, "", "");
        soluciónOptima = false;
    }

    protected void finalizarSolucion() {
        guardarPaso("Solución óptima encontrada", "", "");

        // Actualizar valores de todas las variables
        actualizarValoresVariables();

        // Construir solución final
        variables.stream()
                .filter(VariableInfo::esOriginal)
                .forEach(var -> solucion.put(var.getNombre(), var.getValor()));

        double valorZ = getValor(0, nroColumnas - 1);
        solucion.put("z", maximizar ? valorZ : -valorZ);
    }

    protected void actualizarValoresVariables() {
        // Reiniciar valores
        variables.forEach(var -> {
            if (!var.isEstaEnBase()) {
                var.setValor(0.0);
            }
        });

        // Actualizar valores de variables en base
        variables.stream()
                .filter(VariableInfo::isEstaEnBase)
                .forEach(var -> {
                    double valor = getValor(var.getFilaEnBase(), nroColumnas - 1);
                    var.setValor(redondear(valor));
                });
    }

    // Métodos de utilidad para trabajar con VariableInfo
    protected VariableInfo obtenerVariablePorColumna(int columna) {
        return variables.stream()
                .filter(var -> var.getIndiceColumna() == columna)
                .findFirst()
                .orElse(null);
    }

    protected VariableInfo obtenerVariableEnBase(int fila) {
        return variables.stream()
                .filter(VariableInfo::isEstaEnBase)
                .filter(var -> var.getFilaEnBase() == fila)
                .findFirst()
                .orElse(null);
    }

    public List<VariableInfo> obtenerVariablesPorTipo(VariableInfo.TipoVariable tipo) {
        return variables.stream()
                .filter(var -> var.getTipo() == tipo)
                .toList();
    }

    protected int getTotalVariables() {
        return variables.size();
    }

    public int getVarHolguras() {
        return (int) variables.stream().filter(VariableInfo::esHolgura).count();
    }

    public int getVarExceso() {
        return (int) variables.stream().filter(VariableInfo::esExceso).count();
    }

    protected int getVarArtificiales() {
        return (int) variables.stream().filter(VariableInfo::esArtificial).count();
    }

    protected void guardarPaso(String descripcion, String variableEntrada, String variableSalida) {
        historialDePasos.agregarPaso(M, variableEntrada, variableSalida, descripcion);
    }

    public static double redondear(double valor) {
        if (Math.abs(valor) < epsilon) {
            return 0.0;
        }
        BigDecimal bd = BigDecimal.valueOf(valor);
        bd = bd.setScale(decimales, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    protected void mostrarEstadoVariables() {
        System.out.println("=== Estado de Variables ===");
        for (VariableInfo var : variables) {
            System.out.println(var.toString());
        }
        System.out.println("===========================");
    }

    // Getters y Setters
    public double[][] getM() { return M; }
    public void setM(double[][] m) { M = m; }
    public int getNroFilas() { return nroFilas; }
    public void setNroFilas(int nroFilas) { this.nroFilas = nroFilas; }
    public int getNroColumnas() { return nroColumnas; }
    public void setNroColumnas(int nroColumnas) { this.nroColumnas = nroColumnas; }
    public int getNroVariables() { return nroVariables; }
    public void setNroVariables(int nroVariables) { this.nroVariables = nroVariables; }
    public int getNroRestricciones() { return nroRestricciones; }
    public void setNroRestricciones(int nroRestricciones) { this.nroRestricciones = nroRestricciones; }
    public boolean isSolucionEncontrada() { return soluciónOptima; }
    public double obtenerValorFuncionObjetivo() { return getValor(0, nroColumnas - 1); }
    public boolean isMaximizar() { return maximizar; }

    // Métodos para acceder a las variables
    public List<VariableInfo> getVariables() { return new ArrayList<>(variables); }
    public VariableInfo getVariablePorNombre(String nombre) { return variablesPorNombre.get(nombre); }

    // Métodos heredados para compatibilidad
    public GuardarPasos getHistorialDePasos() { return historialDePasos; }
    public int cantPasos() { return historialDePasos.getListaPasos().size(); }
    public String getDescripcionPaso(int i) { return historialDePasos.getListaPasos().get(i).getDescripcionPaso(); }
    public String getVariableEntradaPaso(int i) { return historialDePasos.getListaPasos().get(i).getVariableEntrada(); }
    public String getVariableSalidaPaso(int i) { return historialDePasos.getListaPasos().get(i).getVariableSalida(); }
    public void limpiarPasos() { historialDePasos.limpiarPasos(); }
    public double[][] getTablaPaso(int indice) { return historialDePasos.getTablaPaso(indice); }
}