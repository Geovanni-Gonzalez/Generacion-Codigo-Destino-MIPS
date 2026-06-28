package pipeline;

/**
 * Error controlado para fallos internos durante generacion de codigo.
 *
 * <p>Puede transportar la línea y columna del nodo de origen para que el pipeline reporte una
 * ubicación real en lugar de una posición fija. Una ubicación de {@code 0} indica «desconocida».</p>
 */
public class CompiladorInternoException extends RuntimeException {
    private final int linea;
    private final int columna;

    public CompiladorInternoException(String mensaje) {
        this(mensaje, 0, 0);
    }

    public CompiladorInternoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.linea = 0;
        this.columna = 0;
    }

    public CompiladorInternoException(String mensaje, int linea, int columna) {
        super(mensaje);
        this.linea = linea;
        this.columna = columna;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }

    /** Indica si la excepción transporta una ubicación de fuente conocida. */
    public boolean tieneUbicacion() {
        return linea > 0;
    }
}
