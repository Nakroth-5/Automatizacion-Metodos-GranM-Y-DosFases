package org.universidad.automatizacionmetodosgranmydosfases.metodos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DosFases extends SimplexBase {
    private double[] funcionObjetivoOriginal;

    private Map<Integer, Integer> indiceSolucion;
    private Map<Integer, Integer> idxParaEliminarMDeLaBase;

    private boolean maximizarOriginal;

    public DosFases(double[] funcionObjetivo, double[][] restricciones,
                    double[] terminosIndependientes, char[] tipoRestricciones, boolean maximizar) {
        super();
        super.nroVariables = funcionObjetivo.length;
        super.nroRestricciones = restricciones.length;
        super.maximizar = maximizar;
        maximizarOriginal = maximizar;

        this.indiceSolucion = new HashMap<>();
        this.idxParaEliminarMDeLaBase = new HashMap<>();

        guardarZOriginal(funcionObjetivo);
        super.inicializarTableauBase(tipoRestricciones);
        construirTableau(funcionObjetivo, restricciones, terminosIndependientes, tipoRestricciones);
        super.guardarPaso("Tableau inicializado", "", "");

        eliminarArtificialesDeBase();
        super.guardarPaso("Artificiales eliminados de la base", "", "");
    }

    private void guardarZOriginal(double[] funcionObjetivo) {
        this.funcionObjetivoOriginal = new double[nroVariables + 1];
        funcionObjetivoOriginal[0] = maximizar ? 1 : -1;
        for (int i = 0; i < nroVariables; i++) {
            funcionObjetivoOriginal[i + 1] = maximizar ? -funcionObjetivo[i] : funcionObjetivo[i];
        }
    }

    private void construirTableau(double[] funcionObjetivo, double[][] restricciones,
                                  double[] terminosIndependientes, char[] tipoRestricciones) {

        setValor(0, 0, 1);

        for (VariableInfo var : variables) {
            if (var.esArtificial()) {
                setValor(0, var.getIndiceColumna(), -1);
            }
        }

        for (int i = 0; i < nroRestricciones; i++) {
            for (VariableInfo var : variables) {
                if (var.esOriginal()) {
                    int indiceOriginal = Integer.parseInt(var.getNombre().substring(1)) - 1;
                    setValor(i + 1, var.getIndiceColumna(), restricciones[i][indiceOriginal]);
                }
            }

            configurarVariablesRestruccion(i, tipoRestricciones[i]);

            setValor(i + 1, nroColumnas - 1, terminosIndependientes[i]);
        }

        inicializarMapsIndices();
    }

    private void inicializarMapsIndices() {
        for (VariableInfo var : variables) {
            if (var.isEstaEnBase()) {
                indiceSolucion.put(var.getIndiceColumna(), var.getFilaEnBase());

                if (var.esArtificial()) {
                    idxParaEliminarMDeLaBase.put(var.getFilaEnBase(), var.getIndiceColumna());
                }
            }
        }
    }

    private void configurarVariablesRestruccion(int filaRestriccion, char tipoRestriccion) {
        for (VariableInfo var : variables) {
            if (!var.isEstaEnBase()) continue;

            if (var.getFilaEnBase() == filaRestriccion + 1) {
                switch (tipoRestriccion) {
                    case '≤':
                        if (var.esHolgura()) {
                            setValor(filaRestriccion + 1, var.getIndiceColumna(), 1);
                        }
                        break;

                    case '≥':
                        if (var.esArtificial()) {
                            setValor(filaRestriccion + 1, var.getIndiceColumna(), 1);
                            // También configurar la variable de exceso correspondiente
                            configurarVariableExceso(filaRestriccion);
                        }
                        break;

                    case '=':
                        if (var.esArtificial()) {
                            setValor(filaRestriccion + 1, var.getIndiceColumna(), 1);
                        }
                        break;
                }
            }
        }
    }

    private void configurarVariableExceso(int filaRestriccion) {
        for (VariableInfo var : variables) {
            if (var.esExceso() && !var.isEstaEnBase()) {
                setValor(filaRestriccion + 1, var.getIndiceColumna(), -1);
                break;
            }
        }
    }

    private void eliminarArtificialesDeBase() {
        // Eliminar variables artificiales de la función objetivo usando operaciones de fila
        List<VariableInfo> artificiales = obtenerVariablesPorTipo(VariableInfo.TipoVariable.ARTIFICIAL);

        for (VariableInfo var : artificiales) {
            if (var.isEstaEnBase()) {
                int filaVariable = var.getFilaEnBase();

                for (int k = 0; k < nroColumnas; k++) {
                    double valorActual = getValor(0, k);
                    double valorFila = getValor(filaVariable, k);
                    setValor(0, k, valorActual + valorFila);
                }
            }
        }
    }

    @Override
    public void resolver() {
        resolverFaseUno();
        super.guardarPaso("Fase 1 terminada con exito", "", " ");
        resolverFaseDos();
    }

    private void resolverFaseUno() {
        maximizar = true;
        for (int i = 0; i < nroColumnas; i++) {
            setValor(0, i, getValor(0, i) * -1);
        }

        while (!esOptimo() && soluciónOptima) {
            iteraciónSimplex();
        }

        if (!soluciónOptima) {
            super.guardarPaso("Fase 1: Problema no factible", "", "");
            return;
        }

        if (getValor(0, nroColumnas - 1) < -epsilon) {
            super.guardarPaso("Fase 1: Problema no factible - variables artificiales > 0", "", "");
            soluciónOptima = false;
            return;
        }
    }

    private void resolverFaseDos() {
        solucion.clear();

        actualizarBaseParaFaseDos();
        super.guardarPaso("Base actualizada para la fase dos", "", " ");

        while (!esOptimo() && soluciónOptima) {
            iteraciónSimplex();
        }

        if (soluciónOptima) {
            if (hayVariablesArtificialesEnBase()) {
                super.guardarPaso("No tiene solución, hay artifiales en la base", "", " ");
                soluciónOptima = false;
                return;
            } else {
                finalizarSolucion();
            }
        }
    }

    private void actualizarBaseParaFaseDos() {
        // Restaurar función objetivo original
        for (int i = 0; i < funcionObjetivoOriginal.length && i < nroColumnas; i++) {
            setValor(0, i, funcionObjetivoOriginal[i]);
        }

        // Crear nueva base sin variables artificiales
        Map<Integer, Integer> nuevaBase = new HashMap<>();

        indiceSolucion.forEach((columna, fila) -> {
            if (!idxParaEliminarMDeLaBase.containsValue(columna)) {
                nuevaBase.put(columna, fila);
            }
        });

        indiceSolucion = nuevaBase;

        // Eliminar columnas de variables artificiales
        idxParaEliminarMDeLaBase.forEach((fila, columna) -> {
            for (int k = 0; k < nroFilas; k++) {
                setValor(k, columna, 0);
            }
            // Actualizar VariableInfo
            VariableInfo var = obtenerVariablePorColumna(columna);
            if (var != null && var.esArtificial()) {
                var.sacarDeBase();
            }
        });

        // Realizar pivoteo para mantener forma canónica
        indiceSolucion.forEach((columna, fila) -> {
            realizarPivoteoFase2(columna, fila);
        });

        // Actualizar información de variables para Fase 2
        actualizarVariablesParaFase2();
    }

    private void actualizarVariablesParaFase2() {
        // Actualizar el estado de las variables basadas en indiceSolucion
        for (VariableInfo var : variables) {
            if (indiceSolucion.containsKey(var.getIndiceColumna())) {
                int fila = indiceSolucion.get(var.getIndiceColumna());
                double valor = getValor(fila, nroColumnas - 1);
                var.ponerEnBase(fila, valor);
            } else if (!var.esArtificial()) {
                var.sacarDeBase();
            }
        }
    }

    protected void realizarPivoteoFase2(int columnaPivote, int filaPivote) {
        double coeficienteZ = getValor(0, columnaPivote);
        if (Math.abs(coeficienteZ) < epsilon) return;

        double valorPivote = getValor(filaPivote, columnaPivote);
        if (Math.abs(valorPivote) < epsilon) return;

        // Eliminación de Gauss solo para la fila Z (fila 0)
        double factor = (-1) * coeficienteZ / valorPivote;
        for (int j = 0; j < nroColumnas; j++) {
            setValor(0, j, redondear(getValor(0, j) + factor * getValor(filaPivote, j)));
        }
    }

    @Override
    protected void actualizarBaseDeVariables(VariableInfo varEntrante, VariableInfo varSaliente, int filaPivote) {
        super.actualizarBaseDeVariables(varEntrante, varSaliente, filaPivote);

        if (varSaliente != null && indiceSolucion.containsKey(varSaliente.getIndiceColumna())) {
            indiceSolucion.remove(varSaliente.getIndiceColumna());
        }

        if (varEntrante != null) {
            indiceSolucion.put(varEntrante.getIndiceColumna(), filaPivote);
        }
    }

    @Override
    protected void finalizarSolucion() {
        super.guardarPaso("Solución óptima encontrada", "", "");
        actualizarValoresVariables();
        solucion.clear();
        maximizar = maximizarOriginal;

        indiceSolucion.forEach((columna, fila) -> {
            // Encontrar la variable correspondiente a esta columna
            VariableInfo var = obtenerVariablePorColumna(columna);
            if (var != null && var.esOriginal()) {
                double valor = redondear(getValor(fila, nroColumnas - 1));
                solucion.put(var.getNombre(), valor);
            }
        });

        for (int i = 1; i <= nroVariables; i++) {
            String nombreVar = "x" + i;
            if (!solucion.containsKey(nombreVar)) {
                solucion.put(nombreVar, 0.0);
            }
        }

        double valorZ = getValor(0, nroColumnas - 1);
        solucion.put("z", maximizar ? valorZ : valorZ * (-1));
        mostrarEstadoVariables();
    }

    @Override
    protected int obtenerColumnaPivote() {
        return super.obtenerColumnaPivoteMaximizar();
    }

    @Override
    protected boolean esOptimo() {
        return !existenNegativosEnLaFunciónObjetivo();
    }

}