package ast;

public class WhileNodo extends SentenciaNodo {
    private final ExpresionNodo condicion;
    private final BloqueNodo cuerpo;
    private final boolean doWhile;
    public WhileNodo(int linea, int columna, ExpresionNodo condicion, BloqueNodo cuerpo, boolean doWhile) {
        super(linea, columna);
        this.condicion = condicion;
        this.cuerpo = cuerpo;
        this.doWhile = doWhile;
    }
    public ExpresionNodo getCondicion() {
        return condicion;
    }

    public BloqueNodo getCuerpo() {
        return cuerpo;
    }

    public boolean isDoWhile() {
        return doWhile;
    }
}
