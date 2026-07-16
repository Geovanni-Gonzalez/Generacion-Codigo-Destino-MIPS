package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BloqueNodo extends Nodo {
    private final List<Nodo> instrucciones;
    public BloqueNodo(int linea, int columna, List<Nodo> instrucciones) {
        super(linea, columna, TipoDato.EMPTY);
        this.instrucciones = new ArrayList<>(instrucciones);
    }

    public List<Nodo> getInstrucciones() {
        return Collections.unmodifiableList(instrucciones);
    }
}
