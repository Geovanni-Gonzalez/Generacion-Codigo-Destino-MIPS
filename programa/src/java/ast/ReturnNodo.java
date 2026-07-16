package ast;

public class ReturnNodo extends SentenciaNodo {
    private final ExpresionNodo valor;
    public ReturnNodo(int linea, int columna, ExpresionNodo valor) {
        super(linea, columna);
        this.valor = valor;
    }

    public ExpresionNodo getValor() {
        return valor;
    }
}
