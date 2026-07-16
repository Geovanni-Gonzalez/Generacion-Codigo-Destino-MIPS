package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwitchNodo extends SentenciaNodo {
    private final ExpresionNodo expresion;
    private final List<CasoSwitchNodo> casos;
    public SwitchNodo(int linea, int columna, ExpresionNodo expresion, List<CasoSwitchNodo> casos) {
        super(linea, columna);
        this.expresion = expresion;
        this.casos = new ArrayList<>(casos);
    }
    public ExpresionNodo getExpresion() {
        return expresion;
    }

    public List<CasoSwitchNodo> getCasos() {
        return Collections.unmodifiableList(casos);
    }
}
