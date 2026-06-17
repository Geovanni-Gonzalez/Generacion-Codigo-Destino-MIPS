package ast;

/**
 * <strong>Objetivo:</strong> Clase base para todos los nodos que producen un valor.
 *
 * <p><strong>Entradas:</strong> Datos sintacticos reconocidos por el parser, posiciones de fuente y subnodos relacionados.</p>
 *
 * <p><strong>Salidas:</strong> Nodos, valores o metadatos consultables por las fases semantica e intermedia.</p>
 *
 * <p><strong>Restricciones:</strong> No debe ejecutar validaciones globales ni escribir archivos; solo conserva estructura y metadatos.</p>
 */
public abstract class ExpresionNodo extends Nodo {
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de ExpresionNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    protected ExpresionNodo(int linea, int columna) {
        super(linea, columna);
    }
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, TipoDato tipo</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de ExpresionNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    protected ExpresionNodo(int linea, int columna, TipoDato tipo) {
        super(linea, columna, tipo);
    }
}
