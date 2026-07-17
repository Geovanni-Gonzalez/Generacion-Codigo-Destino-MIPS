package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LlamadaFuncionNodo extends ExpresionNodo {
    private final String nombre;
    private final List<ExpresionNodo> argumentos;
    public LlamadaFuncionNodo(int linea, int columna, String nombre, List<ExpresionNodo> argumentos) {
        super(linea, columna);
        this.nombre = nombre;
        this.argumentos = new ArrayList<>(argumentos);
    }
    public String getNombre() {
        return nombre;
    }

    public List<ExpresionNodo> getArgumentos() {
        return Collections.unmodifiableList(argumentos);
    }
}
