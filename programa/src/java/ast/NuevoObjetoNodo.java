package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NuevoObjetoNodo extends ExpresionNodo {
    private final String nombreClase;
    private final List<ExpresionNodo> argumentos;

    public NuevoObjetoNodo(int linea, int columna, String nombreClase, List<ExpresionNodo> argumentos) {
        super(linea, columna);
        this.nombreClase = nombreClase;
        this.argumentos = new ArrayList<>(argumentos);
    }

    public String getNombreClase() {
        return nombreClase;
    }

    public List<ExpresionNodo> getArgumentos() {
        return Collections.unmodifiableList(argumentos);
    }
}
