package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProgramaNodo extends Nodo {
    private final List<FuncionNodo> funciones;
    private final List<ClaseNodo> clases;
    public ProgramaNodo(int linea, int columna, List<FuncionNodo> funciones) {
        this(linea, columna, funciones, new ArrayList<ClaseNodo>());
    }

    public ProgramaNodo(int linea, int columna, List<FuncionNodo> funciones, List<ClaseNodo> clases) {
        super(linea, columna, TipoDato.EMPTY);
        this.funciones = new ArrayList<>(funciones);
        this.clases = new ArrayList<>(clases);
    }

    public List<FuncionNodo> getFunciones() {
        return Collections.unmodifiableList(funciones);
    }

    public List<ClaseNodo> getClases() {
        return Collections.unmodifiableList(clases);
    }
}
