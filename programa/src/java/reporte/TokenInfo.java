package reporte;

/**
 * Nombre: TokenInfo
 *
 * Objetivo: Formatear o escribir reportes y artefactos generados por el compilador.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
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

    /**
     * <strong>Nombre:</strong> TokenInfo
     *
     * <p><strong>Objetivo:</strong> Crear una fila de reporte con todos los datos del token.</p>
     *
     * <p><strong>Entrada:</strong> int id, String nombre, String lexema, int linea, int columna, String tabla, String informacion.</p>
     *
     * <p><strong>Salida:</strong> Nueva instancia de TokenInfo.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
