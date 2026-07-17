package ast;

public class AsignacionNodo extends SentenciaNodo {
    private final ExpresionNodo destino;
    private final ExpresionNodo valor;
    public AsignacionNodo(int linea, int columna, ExpresionNodo destino, ExpresionNodo valor) {
        super(linea, columna);
        this.destino = destino;
        this.valor = valor;
    }

    public ExpresionNodo getDestino() {
        return destino;
    }

    public ExpresionNodo getValor() {
        return valor;
    }
}
