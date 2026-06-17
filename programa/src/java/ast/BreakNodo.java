package ast;

/**
 * <strong>Objetivo:</strong> Sentencia de interrupcion de flujo dentro de estructuras como switch.
 *
 * <p><strong>Entradas:</strong> Datos sintacticos reconocidos por el parser, posiciones de fuente y subnodos relacionados.</p>
 *
 * <p><strong>Salidas:</strong> Nodos, valores o metadatos consultables por las fases semantica e intermedia.</p>
 *
 * <p><strong>Restricciones:</strong> No debe ejecutar validaciones globales ni escribir archivos; solo conserva estructura y metadatos.</p>
 */
public class BreakNodo extends SentenciaNodo {
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de BreakNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public BreakNodo(int linea, int columna) {
        super(linea, columna, TipoDato.EMPTY);
    }
}
