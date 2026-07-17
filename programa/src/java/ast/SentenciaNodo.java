package ast;

public abstract class SentenciaNodo extends Nodo {
    protected SentenciaNodo(int linea, int columna) {
        super(linea, columna);
    }
    protected SentenciaNodo(int linea, int columna, TipoDato tipo) {
        super(linea, columna, tipo);
    }
}
