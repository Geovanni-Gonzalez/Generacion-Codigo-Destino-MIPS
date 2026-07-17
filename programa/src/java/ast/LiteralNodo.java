package ast;

public class LiteralNodo extends ExpresionNodo {
    private final Object valor;
    public LiteralNodo(int linea, int columna, Object valor, TipoDato tipo) {
        super(linea, columna, tipo);
        this.valor = valor;
    }

    public Object getValor() {
        return valor;
    }
}
