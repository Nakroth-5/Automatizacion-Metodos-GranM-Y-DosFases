package org.universidad.automatizacionmetodosgranmydosfases.guardarPasos;

import java.util.ArrayList;
import java.util.List;

public class GuardarPasos {

    private List<EstructuraParaGuardarPasos> listaPasos;

    public GuardarPasos() {
        this.listaPasos = new ArrayList<>();
    }


    public void agregarPaso(double[][] tablaPasos, String variableEntrada, String variableSalida, String descripcionPaso) {
        listaPasos.add(new EstructuraParaGuardarPasos(tablaPasos, variableEntrada, variableSalida, descripcionPaso));
    }

    public List<EstructuraParaGuardarPasos> getListaPasos() {
        return listaPasos;
    }

    public void limpiarPasos(){
        listaPasos.clear();
    }

    public double[][] getTablaPaso(int indice){
        return listaPasos.get(indice).getTablaPasos();
    }
}
