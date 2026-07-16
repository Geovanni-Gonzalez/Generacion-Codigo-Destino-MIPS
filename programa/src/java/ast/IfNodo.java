package ast;

public class IfNodo extends SentenciaNodo {
    private final ExpresionNodo condicion;
    private final BloqueNodo bloqueEntonces;
    private final BloqueNodo bloqueSino;
    public IfNodo(int linea, int columna, ExpresionNodo condicion,
                  BloqueNodo bloqueEntonces, BloqueNodo bloqueSino) {
        super(linea, columna);
        this.condicion = condicion;
        this.bloqueEntonces = bloqueEntonces;
        this.bloqueSino = bloqueSino;
    }
    public ExpresionNodo getCondicion() {
        return condicion;
    }

    public BloqueNodo getBloqueEntonces() {
        return bloqueEntonces;
    }

    public BloqueNodo getBloqueSino() {
        return bloqueSino;
    }
}
