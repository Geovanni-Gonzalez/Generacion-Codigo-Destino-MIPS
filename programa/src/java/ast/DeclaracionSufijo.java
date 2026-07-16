package ast;

public class DeclaracionSufijo {
    public final ExpresionNodo inicializador;
    public final ExpresionNodo filas;
    public final ExpresionNodo columnas;
    public final InicializacionArregloNodo inicializacionArreglo;
    public DeclaracionSufijo(ExpresionNodo inicializador) {
        this.inicializador = inicializador;
        this.filas = null;
        this.columnas = null;
        this.inicializacionArreglo = null;
    }

    public DeclaracionSufijo(ExpresionNodo filas, ExpresionNodo columnas,
                             InicializacionArregloNodo inicializacionArreglo) {
        this.inicializador = null;
        this.filas = filas;
        this.columnas = columnas;
        this.inicializacionArreglo = inicializacionArreglo;
    }

    public boolean esArreglo() {
        return filas != null || columnas != null;
    }
}
