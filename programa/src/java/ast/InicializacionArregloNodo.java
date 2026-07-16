package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InicializacionArregloNodo extends Nodo {
    private final List<List<ExpresionNodo>> filas;
    public InicializacionArregloNodo(int linea, int columna, List<List<ExpresionNodo>> filas) {
        super(linea, columna);
        this.filas = new ArrayList<>();
        for (List<ExpresionNodo> fila : filas) {
            this.filas.add(new ArrayList<>(fila));
        }
    }

    public List<List<ExpresionNodo>> getFilas() {
        List<List<ExpresionNodo>> copia = new ArrayList<>();
        for (List<ExpresionNodo> fila : filas) {
            copia.add(Collections.unmodifiableList(fila));
        }
        return Collections.unmodifiableList(copia);
    }
}
