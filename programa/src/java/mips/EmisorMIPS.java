package mips;

import java.util.ArrayList;
import java.util.List;

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
