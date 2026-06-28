package pipeline;

/**
 * Nombre: CompiladorInternoException
 *
 * Objetivo: Coordinar fases del compilador o transportar resultados entre ellas.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class CompiladorInternoException extends RuntimeException {
    private final int linea;
    private final int columna;

    /**
     * Nombre: CompiladorInternoException
     *
     * Objetivo: Inicializar una instancia de CompiladorInternoException con los datos requeridos.
     *
     * Entrada: String mensaje.
     *
     * Salida: Nueva instancia de CompiladorInternoException.
     *
     * Restricciones: Ninguna.
     */
    public CompiladorInternoException(String mensaje) {
        this(mensaje, 0, 0);
    }

    /**
     * Nombre: CompiladorInternoException
     *
     * Objetivo: Inicializar una instancia de CompiladorInternoException con los datos requeridos.
     *
     * Entrada: String mensaje; Throwable causa.
     *
     * Salida: Nueva instancia de CompiladorInternoException.
     *
     * Restricciones: Ninguna.
     */
    public CompiladorInternoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.linea = 0;
        this.columna = 0;
    }

    /**
     * Nombre: CompiladorInternoException
     *
     * Objetivo: Inicializar una instancia de CompiladorInternoException con los datos requeridos.
     *
     * Entrada: String mensaje; int linea; int columna.
     *
     * Salida: Nueva instancia de CompiladorInternoException.
     *
     * Restricciones: Ninguna.
     */
    public CompiladorInternoException(String mensaje, int linea, int columna) {
        super(mensaje);
        this.linea = linea;
        this.columna = columna;
    }

    /**
     * Nombre: getLinea
     *
     * Objetivo: Obtener el valor de Linea almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo int.
     *
     * Restricciones: Ninguna.
     */
    public int getLinea() {
        return linea;
    }

    /**
     * Nombre: getColumna
     *
     * Objetivo: Obtener el valor de Columna almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo int.
     *
     * Restricciones: Ninguna.
     */
    public int getColumna() {
        return columna;
    }

    /** Indica si la excepción transporta una ubicación de fuente conocida. */
    /**
     * Nombre: tieneUbicacion
     *
     * Objetivo: Ejecutar la operacion tieneUbicacion definida por CompiladorInternoException.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    public boolean tieneUbicacion() {
        return linea > 0;
    }
}
