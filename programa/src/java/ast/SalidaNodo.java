package ast;

public class SalidaNodo extends SentenciaNodo {
    private final ExpresionNodo valor;
    public SalidaNodo(int linea, int columna, ExpresionNodo valor) {
        super(linea, columna);
        this.valor = valor;
    }

    public ExpresionNodo getValor() {
        return valor;
    }
}
