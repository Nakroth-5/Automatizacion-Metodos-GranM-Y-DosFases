package org.universidad.automatizacionmetodosgranmydosfases.guardarPasos;

public class EstructuraParaGuardarPasos {
    private double[][] tablaPasos;
    private String variableEntrada;
    private String variableSalida;
    private String descripcionPaso;

    public EstructuraParaGuardarPasos(double[][] tablaPasos, String variableEntrada, String variableSalida,  String descripcionPaso) {
        this.tablaPasos = copiarTabla(tablaPasos);
        this.variableEntrada = variableEntrada;
        this.variableSalida = variableSalida;
        this.descripcionPaso = descripcionPaso;
    }

    private double[][] copiarTabla(double[][] tablaPasos) {
        double[][] copia = new double[tablaPasos.length][];
        for (int i = 0; i < tablaPasos.length; i++) {
            copia[i] = tablaPasos[i].clone();
        }
        return copia;
    }

    public double[][] getTablaPasos() {
        return tablaPasos;
    }

    public void setTablaPasos(double[][] tablaPasos) {
        this.tablaPasos = tablaPasos;
    }

    public String getVariableEntrada() {
        return variableEntrada;
    }

    public void setVariableEntrada(String variableEntrada) {
        this.variableEntrada = variableEntrada;
    }

    public String getVariableSalida() {
        return variableSalida;
    }

    public void setVariableSalida(String variableSalida) {
        this.variableSalida = variableSalida;
    }

    public String getDescripcionPaso() {
        return descripcionPaso;
    }

    public void setDescripcionPaso(String descripcionPaso) {
        this.descripcionPaso = descripcionPaso;
    }

}
