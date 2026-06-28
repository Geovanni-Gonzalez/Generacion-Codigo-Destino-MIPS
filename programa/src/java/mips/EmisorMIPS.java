package mips;

import java.util.ArrayList;
import java.util.List;

/**
 * Acumula las lineas del programa MIPS y aplica el formato comun de instrucciones.
 */
final class EmisorMIPS {
    private final List<String> lineas = new ArrayList<>();

    void limpiar() {
        lineas.clear();
    }

    void add(String linea) {
        lineas.add(linea);
    }

    void instruccion(String texto) {
        lineas.add("\t" + texto);
    }

    List<String> lineas() {
        return new ArrayList<>(lineas);
    }
}
