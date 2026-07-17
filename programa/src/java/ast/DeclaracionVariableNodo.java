package ast;

public class DeclaracionVariableNodo extends SentenciaNodo {
    private final String nombre;
    private final ExpresionNodo inicializador;
    private final ExpresionNodo filas;
    private final ExpresionNodo columnas;
    private final InicializacionArregloNodo inicializacionArreglo;
    public DeclaracionVariableNodo(int linea, int columna, String nombre, TipoDato tipoDeclarado,
                                   ExpresionNodo inicializador) {
        this(linea, columna, nombre, tipoDeclarado, inicializador, null, null, null);
    }
    public DeclaracionVariableNodo(int linea, int columna, String nombre, TipoDato tipoDeclarado,
                                   ExpresionNodo filas, ExpresionNodo columnas,
                                   InicializacionArregloNodo inicializacionArreglo) {
        this(linea, columna, nombre, tipoDeclarado, null, filas, columnas, inicializacionArreglo);
    }
    private DeclaracionVariableNodo(int linea, int columna, String nombre, TipoDato tipoDeclarado,
                                    ExpresionNodo inicializador, ExpresionNodo filas,
                                    ExpresionNodo columnas,
                                    InicializacionArregloNodo inicializacionArreglo) {
        super(linea, columna, tipoDeclarado);
        this.nombre = nombre;
        this.inicializador = inicializador;
        this.filas = filas;
        this.columnas = columnas;
        this.inicializacionArreglo = inicializacionArreglo;
    }
    public String getNombre() {
        return nombre;
    }

    public ExpresionNodo getInicializador() {
        return inicializador;
    }

    public ExpresionNodo getFilas() {
        return filas;
    }

    public ExpresionNodo getColumnas() {
        return columnas;
    }

    public InicializacionArregloNodo getInicializacionArreglo() {
        return inicializacionArreglo;
    }

    public boolean esArreglo() {
        return filas != null || columnas != null;
    }
}
