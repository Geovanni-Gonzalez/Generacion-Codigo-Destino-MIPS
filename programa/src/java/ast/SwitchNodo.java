package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <strong>Objetivo:</strong> Sentencia switch con expresion de seleccion y lista de casos.
 *
 * <p><strong>Entradas:</strong> Datos sintacticos reconocidos por el parser, posiciones de fuente y subnodos relacionados.</p>
 *
 * <p><strong>Salidas:</strong> Nodos, valores o metadatos consultables por las fases semantica e intermedia.</p>
 *
 * <p><strong>Restricciones:</strong> No debe ejecutar validaciones globales ni escribir archivos; solo conserva estructura y metadatos.</p>
 */
public class SwitchNodo extends SentenciaNodo {
    private final ExpresionNodo expresion;
    private final List<CasoSwitchNodo> casos;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, ExpresionNodo expresion, List<CasoSwitchNodo> casos</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de SwitchNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public SwitchNodo(int linea, int columna, ExpresionNodo expresion, List<CasoSwitchNodo> casos) {
        super(linea, columna);
        this.expresion = expresion;
        this.casos = new ArrayList<>(casos);
    }
    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna ExpresionNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public ExpresionNodo getExpresion() {
        return expresion;
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna List<CasoSwitchNodo>.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public List<CasoSwitchNodo> getCasos() {
        return Collections.unmodifiableList(casos);
    }
}
