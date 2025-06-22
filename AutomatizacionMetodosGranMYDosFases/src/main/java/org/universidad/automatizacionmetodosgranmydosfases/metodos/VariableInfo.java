package org.universidad.automatizacionmetodosgranmydosfases.metodos;

public class VariableInfo {
    public enum TipoVariable {
        ORIGINAL,
        HOLGURA,
        EXCESO,
        ARTIFICIAL
    }

    private final int indiceColumna;
    private final TipoVariable tipo;
    private final String nombre;
    private boolean estaEnBase;
    private int filaEnBase;
    private double valor;

    public VariableInfo(int indiceColumna, TipoVariable tipo, String nombre) {
        this.indiceColumna = indiceColumna;
        this.tipo = tipo;
        this.nombre = nombre;
        this.estaEnBase = false;
        this.filaEnBase = -1;
        this.valor = 0.0;
    }

    public VariableInfo(int indiceColumna, TipoVariable tipo, String nombre, int filaEnBase) {
        this(indiceColumna, tipo, nombre);
        this.estaEnBase = true;
        this.filaEnBase = filaEnBase;
    }

    public int getIndiceColumna() { return indiceColumna; }
    public TipoVariable getTipo() { return tipo; }
    public String getNombre() { return nombre; }
    public boolean isEstaEnBase() { return estaEnBase; }
    public int getFilaEnBase() { return filaEnBase; }
    public double getValor() { return valor; }

    public void setEstaEnBase(boolean estaEnBase) {
        this.estaEnBase = estaEnBase;
        if (!estaEnBase) {
            this.filaEnBase = -1;
            this.valor = 0.0;
        }
    }

    public void setFilaEnBase(int filaEnBase) {
        this.filaEnBase = filaEnBase;
        this.estaEnBase = (filaEnBase >= 0);
    }

    public void setValor(double valor) { this.valor = valor; }

    public boolean esArtificial() { return tipo == TipoVariable.ARTIFICIAL; }
    public boolean esOriginal() { return tipo == TipoVariable.ORIGINAL; }
    public boolean esHolgura() { return tipo == TipoVariable.HOLGURA; }
    public boolean esExceso() { return tipo == TipoVariable.EXCESO; }

    public void sacarDeBase() {
        setEstaEnBase(false);
    }

    public void ponerEnBase(int fila, double valor) {
        setFilaEnBase(fila);
        setValor(valor);
    }

    @Override
    public String toString() {
        return String.format("%s (col:%d, tipo:%s, base:%s, fila:%d, valor:%.6f)",
                nombre, indiceColumna, tipo, estaEnBase ? "Sí" : "No", filaEnBase, valor);
    }
}