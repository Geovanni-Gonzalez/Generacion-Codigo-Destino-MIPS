package ast;

public class CasoSwitchNodo extends Nodo {
    private final ExpresionNodo valor;
    private final BloqueNodo bloque;
    private final boolean defecto;
    public CasoSwitchNodo(int linea, int columna, ExpresionNodo valor, BloqueNodo bloque, boolean defecto) {
        super(linea, columna);
        this.valor = valor;
        this.bloque = bloque;
        this.defecto = defecto;
    }

    public ExpresionNodo getValor() {
        return valor;
    }

    public BloqueNodo getBloque() {
        return bloque;
    }

    public boolean isDefecto() {
        return defecto;
    }
}
