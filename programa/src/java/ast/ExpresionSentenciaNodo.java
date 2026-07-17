package ast;

public class ExpresionSentenciaNodo extends SentenciaNodo {
    private final ExpresionNodo expresion;
    public ExpresionSentenciaNodo(int linea, int columna, ExpresionNodo expresion) {
        super(linea, columna);
        this.expresion = expresion;
    }

    public ExpresionNodo getExpresion() {
        return expresion;
    }
}
