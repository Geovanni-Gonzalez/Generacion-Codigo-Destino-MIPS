package ast;

/**
 * <strong>Objetivo:</strong> Sentencia de ciclo while o do-while.
 *
 * <p><strong>Entradas:</strong> Datos sintacticos reconocidos por el parser, posiciones de fuente y subnodos relacionados.</p>
 *
 * <p><strong>Salidas:</strong> Nodos, valores o metadatos consultables por las fases semantica e intermedia.</p>
 *
 * <p><strong>Restricciones:</strong> No debe ejecutar validaciones globales ni escribir archivos; solo conserva estructura y metadatos.</p>
 */
public class WhileNodo extends SentenciaNodo {
    private final ExpresionNodo condicion;
    private final BloqueNodo cuerpo;
    private final boolean doWhile;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, ExpresionNodo condicion, BloqueNodo cuerpo, boolean doWhile</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de WhileNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public WhileNodo(int linea, int columna, ExpresionNodo condicion, BloqueNodo cuerpo, boolean doWhile) {
        super(linea, columna);
        this.condicion = condicion;
        this.cuerpo = cuerpo;
        this.doWhile = doWhile;
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
    public ExpresionNodo getCondicion() {
        return condicion;
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna BloqueNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public BloqueNodo getCuerpo() {
        return cuerpo;
    }

    /**
     * <strong>Objetivo:</strong> Consulta una condicion booleana del objeto.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna boolean.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public boolean isDoWhile() {
        return doWhile;
    }
}
