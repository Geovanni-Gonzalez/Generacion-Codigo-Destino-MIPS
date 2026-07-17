package ast;

public class DeclaracionObjetoNodo extends SentenciaNodo {
    private final String nombre;
    private final String nombreClase;
    private final ExpresionNodo inicializador;

    public DeclaracionObjetoNodo(int linea, int columna, String nombre, String nombreClase,
                                 ExpresionNodo inicializador) {
        super(linea, columna, TipoDato.OBJETO);
        this.nombre = nombre;
        this.nombreClase = nombreClase;
        this.inicializador = inicializador;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombreClase() {
        return nombreClase;
    }

    public ExpresionNodo getInicializador() {
        return inicializador;
    }
}
