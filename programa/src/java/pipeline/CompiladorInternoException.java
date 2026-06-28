package pipeline;

/**
 * Error controlado para fallos internos durante generacion de codigo.
 */
public class CompiladorInternoException extends RuntimeException {
    public CompiladorInternoException(String mensaje) {
        super(mensaje);
    }

    public CompiladorInternoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
