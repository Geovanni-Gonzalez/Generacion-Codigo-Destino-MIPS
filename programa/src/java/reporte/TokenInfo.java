package reporte;

public class TokenInfo {
    /** Identificador numérico del token según la tabla generada por CUP. */
    public final int id;
    /** Nombre simbólico del token. */
    public final String nombre;
    /** Texto original reconocido en el código fuente. */
    public final String lexema;
    /** Línea donde inicia el lexema. */
    public final int linea;
    /** Columna donde inicia el lexema. */
    public final int columna;
    /** Tabla o categoría de reporte a la que pertenece el token. */
    public final String tabla;
    /** Detalle adicional usado por la tabla de símbolos o literales. */
    public final String informacion;

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
