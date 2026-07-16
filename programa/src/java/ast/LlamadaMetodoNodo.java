package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LlamadaMetodoNodo extends ExpresionNodo {
    private final ExpresionNodo objeto;
    private final String nombreMetodo;
    private final List<ExpresionNodo> argumentos;

    public LlamadaMetodoNodo(int linea, int columna, ExpresionNodo objeto, String nombreMetodo,
                             List<ExpresionNodo> argumentos) {
        super(linea, columna);
        this.objeto = objeto;
        this.nombreMetodo = nombreMetodo;
        this.argumentos = new ArrayList<>(argumentos);
    }

    public ExpresionNodo getObjeto() {
        return objeto;
    }

    public String getNombreMetodo() {
        return nombreMetodo;
    }

    public List<ExpresionNodo> getArgumentos() {
        return Collections.unmodifiableList(argumentos);
    }
}
