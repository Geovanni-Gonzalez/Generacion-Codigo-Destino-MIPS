package ast;

/**
 * <strong>Objetivo:</strong> Expresion que representa el uso de un identificador escalar.
 *
 * <p><strong>Entradas:</strong> Datos sintacticos reconocidos por el parser, posiciones de fuente y subnodos relacionados.</p>
 *
 * <p><strong>Salidas:</strong> Nodos, valores o metadatos consultables por las fases semantica e intermedia.</p>
 *
 * <p><strong>Restricciones:</strong> No debe ejecutar validaciones globales ni escribir archivos; solo conserva estructura y metadatos.</p>
 */
public class IdentificadorNodo extends ExpresionNodo {
    private final String nombre;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, String nombre</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de IdentificadorNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public IdentificadorNodo(int linea, int columna, String nombre) {
        super(linea, columna);
        this.nombre = nombre;
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public String getNombre() {
        return nombre;
    }
}
