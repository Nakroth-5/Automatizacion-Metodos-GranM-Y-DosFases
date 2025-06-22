package org.universidad.automatizacionmetodosgranmydosfases.metodos;

public class GranM extends SimplexBase {
    private static final double M_VALOR = 1E6;

    public GranM(double[] funcionObjetivo, double[][] restricciones,
                 double[] terminosIndependientes, char[] tipoRestricciones, boolean maximizar) {
        super();
        super.nroVariables = funcionObjetivo.length;
        super.nroRestricciones = restricciones.length;
        super.maximizar = maximizar;
        super.soluciónOptima = true;

        super.inicializarTableauBase(tipoRestricciones);
        construirTableau(funcionObjetivo, restricciones, terminosIndependientes, tipoRestricciones);
        super.guardarPaso("Tableau inicializado", "", "");

        actualizarZ();
        super.guardarPaso("Z actualizado", "", "");
    }

    private void construirTableau(double[] funcionObjetivo, double[][] restricciones,
                                  double[] terminosIndependientes, char[] tipoRestricciones) {

        // Configurar la función objetivo
        setValor(0, 0, maximizar ? 1 : -1);

        // Configurar coeficientes de la función objetivo
        for (VariableInfo var : variables) {
            if (var.esOriginal()) {
                int indiceOriginal = Integer.parseInt(var.getNombre().substring(1)) - 1;
                double coeficiente = maximizar ? -funcionObjetivo[indiceOriginal] : funcionObjetivo[indiceOriginal];
                setValor(0, var.getIndiceColumna(), coeficiente);
            } else if (var.esArtificial()) {
                // Las variables artificiales tienen coeficiente M_VALOR en Gran M
                setValor(0, var.getIndiceColumna(), M_VALOR);
            }
        }

        // Llenar las restricciones
        for (int i = 0; i < nroRestricciones; i++) {
            // Coeficientes de variables originales
            for (VariableInfo var : variables) {
                if (var.esOriginal()) {
                    int indiceOriginal = Integer.parseInt(var.getNombre().substring(1)) - 1;
                    setValor(i + 1, var.getIndiceColumna(), restricciones[i][indiceOriginal]);
                }
            }

            // Configurar variables de holgura, exceso y artificiales según el tipo de restricción
            configurarVariablesRestruccion(i, tipoRestricciones[i]);

            // Término independiente
            setValor(i + 1, nroColumnas - 1, terminosIndependientes[i]);
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
                        if (var.esExceso()) {
                            // Buscar la variable de exceso correspondiente
                            setValor(filaRestriccion + 1, var.getIndiceColumna(), -1);
                        } else if (var.esArtificial()) {
                            setValor(filaRestriccion + 1, var.getIndiceColumna(), 1);
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

        // Para restricciones ≥, necesitamos configurar tanto exceso como artificial
        if (tipoRestriccion == '≥') {
            for (VariableInfo var : variables) {
                if (var.esExceso() && !var.isEstaEnBase()) {
                    // Esta es la variable de exceso para esta restricción
                    setValor(filaRestriccion + 1, var.getIndiceColumna(), -1);
                    break;
                }
            }
        }
    }

    private void actualizarZ() {
        // Eliminar las variables artificiales de la función objetivo usando operaciones de fila
        for (VariableInfo var : variables) {
            if (var.esArtificial() && var.isEstaEnBase()) {
                int filaVariable = var.getFilaEnBase();
                double factorM = -M_VALOR;

                for (int k = 0; k < nroColumnas; k++) {
                    double valorActual = getValor(0, k);
                    double valorFila = getValor(filaVariable, k);
                    setValor(0, k, valorActual + factorM * valorFila);
                }
            }
        }
    }

    @Override
    public void resolver() {
        while (!esOptimo() && soluciónOptima) {
            iteraciónSimplex();
        }

        if (soluciónOptima) {
            if (hayVariablesArtificialesEnBase()) {
                super.guardarPaso("No tiene solución, hay artificiales en la base", "", "");
                soluciónOptima = false;
                return;
            } else {
                finalizarSolucion();
            }
        }
    }

    @Override
    protected int obtenerColumnaPivote() {
        return super.obtenerColumnaPivoteMaximizar();
    }

    @Override
    protected boolean esOptimo() {
        return !existenNegativosEnLaFunciónObjetivo();
    }

    @Override
    protected void finalizarSolucion() {
        super.finalizarSolucion();

        // Verificación adicional para Gran M
        boolean hayArtificialesConValor = variables.stream()
                .filter(VariableInfo::esArtificial)
                .filter(VariableInfo::isEstaEnBase)
                .anyMatch(var -> Math.abs(var.getValor()) > epsilon);

        if (hayArtificialesConValor) {
            soluciónOptima = false;
            guardarPaso("Solución no factible - variables artificiales con valor positivo", "", "");
        }

        mostrarEstadoVariables();
    }

    // Métodos adicionales para debugging y análisis


    public boolean tieneVariablesArtificialesActivas() {
        return variables.stream()
                .filter(VariableInfo::esArtificial)
                .filter(VariableInfo::isEstaEnBase)
                .anyMatch(var -> Math.abs(var.getValor()) > epsilon);
    }
}