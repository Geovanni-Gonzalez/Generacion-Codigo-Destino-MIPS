package ast;

public abstract class Nodo {
    private final int linea;
    private final int columna;
    private TipoDato tipo;
    protected Nodo(int linea, int columna) {
        this(linea, columna, TipoDato.DESCONOCIDO);
    }
    protected Nodo(int linea, int columna, TipoDato tipo) {
        this.linea = linea;
        this.columna = columna;
        this.tipo = tipo;
    }
    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }

    public TipoDato getTipo() {
        return tipo;
    }

    public void setTipo(TipoDato tipo) {
        this.tipo = tipo;
    }
}
