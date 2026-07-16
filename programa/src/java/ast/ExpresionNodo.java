package ast;

public abstract class ExpresionNodo extends Nodo {
    protected ExpresionNodo(int linea, int columna) {
        super(linea, columna);
    }
    protected ExpresionNodo(int linea, int columna, TipoDato tipo) {
        super(linea, columna, tipo);
    }
}
