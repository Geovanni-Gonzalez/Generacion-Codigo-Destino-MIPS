package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <strong>Objetivo:</strong> Nodo que agrupa una secuencia de instrucciones dentro de un alcance.
 *
 * <p><strong>Entradas:</strong> Datos sintacticos reconocidos por el parser, posiciones de fuente y subnodos relacionados.</p>
 *
 * <p><strong>Salidas:</strong> Nodos, valores o metadatos consultables por las fases semantica e intermedia.</p>
 *
 * <p><strong>Restricciones:</strong> No debe ejecutar validaciones globales ni escribir archivos; solo conserva estructura y metadatos.</p>
 */
public class BloqueNodo extends Nodo {
    private final List<Nodo> instrucciones;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, List<Nodo> instrucciones</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de BloqueNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public BloqueNodo(int linea, int columna, List<Nodo> instrucciones) {
        super(linea, columna, TipoDato.EMPTY);
        this.instrucciones = new ArrayList<>(instrucciones);
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna List<Nodo>.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public List<Nodo> getInstrucciones() {
        return Collections.unmodifiableList(instrucciones);
    }
}
