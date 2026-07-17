package ast;

public class ExpresionBinariaNodo extends ExpresionNodo {
    private final String operador;
    private final ExpresionNodo izquierda;
    private final ExpresionNodo derecha;
    public ExpresionBinariaNodo(int linea, int columna, String operador,
                                ExpresionNodo izquierda, ExpresionNodo derecha) {
        super(linea, columna);
        this.operador = operador;
        this.izquierda = izquierda;
        this.derecha = derecha;
    }
    public String getOperador() {
        return operador;
    }

    public ExpresionNodo getIzquierda() {
        return izquierda;
    }

    public ExpresionNodo getDerecha() {
        return derecha;
    }
}
