package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClaseNodo extends Nodo {
    private final String nombre;
    private final String nombrePadre;
    private final List<DeclaracionVariableNodo> campos;
    private final List<FuncionNodo> metodos;

    public ClaseNodo(int linea, int columna, String nombre, String nombrePadre,
                     List<DeclaracionVariableNodo> campos, List<FuncionNodo> metodos) {
        super(linea, columna, TipoDato.OBJETO);
        this.nombre = nombre;
        this.nombrePadre = nombrePadre;
        this.campos = new ArrayList<>(campos);
        this.metodos = new ArrayList<>(metodos);
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombrePadre() {
        return nombrePadre;
    }

    public List<DeclaracionVariableNodo> getCampos() {
        return Collections.unmodifiableList(campos);
    }

    public List<FuncionNodo> getMetodos() {
        return Collections.unmodifiableList(metodos);
    }
}
