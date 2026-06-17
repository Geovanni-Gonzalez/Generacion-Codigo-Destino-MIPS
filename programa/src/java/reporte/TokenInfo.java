package reporte;

/**
 * <strong>Objetivo:</strong> Registro simple con la informacion que el lexer envia a los reportes.
 *
 * <p><strong>Entradas:</strong> Resultados del analisis, errores, tokens, rutas de salida y metadatos de reporte.</p>
 *
 * <p><strong>Salidas:</strong> Mensajes formateados o archivos de reporte escritos en UTF-8.</p>
 *
 * <p><strong>Restricciones:</strong> No debe recalcular analisis; solo formatea o persiste informacion recibida.</p>
 */
public class TokenInfo {
    /** Identificador numerico del token segun la tabla generada por CUP. */
    public final int id;
    /** Nombre simbolico del token. */
    public final String nombre;
    /** Texto original reconocido en el codigo fuente. */
    public final String lexema;
    /** Linea donde inicia el lexema. */
    public final int linea;
    /** Columna donde inicia el lexema. */
    public final int columna;
    /** Tabla o categoria de reporte a la que pertenece el token. */
    public final String tabla;
    /** Detalle adicional usado por la tabla de simbolos o literales. */
    public final String informacion;

    /**
     * <strong>Objetivo:</strong> Crea una fila inmutable para los reportes de tokens.
     *
     * <p><strong>Entradas:</strong> int id, String nombre, String lexema, int linea, int columna, String tabla, String informacion</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de TokenInfo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public TokenInfo(int id, String nombre, String lexema, int linea, int columna,
                     String tabla, String informacion) {
        this.id = id;
        this.nombre = nombre;
        this.lexema = lexema;
        this.linea = linea;
        this.columna = columna;
        this.tabla = tabla;
        this.informacion = informacion;
    }
}
