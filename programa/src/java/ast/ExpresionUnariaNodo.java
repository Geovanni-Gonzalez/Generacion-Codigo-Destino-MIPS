package ast;

public class ExpresionUnariaNodo extends ExpresionNodo {
    private final String operador;
    private final ExpresionNodo expresion;
    public ExpresionUnariaNodo(int linea, int columna, String operador, ExpresionNodo expresion) {
        super(linea, columna);
        this.operador = operador;
        this.expresion = expresion;
    }
    public String getOperador() {
        return operador;
    }

    public ExpresionNodo getExpresion() {
        return expresion;
    }
}
