package ast;

public class EntradaNodo extends SentenciaNodo {
    private final String destino;
    public EntradaNodo(int linea, int columna, String destino) {
        super(linea, columna);
        this.destino = destino;
    }

    public String getDestino() {
        return destino;
    }
}
