package ast;

public class AccesoArregloNodo extends ExpresionNodo {
    private final String nombre;
    private final ExpresionNodo fila;
    private final ExpresionNodo columnaIndice;
    public AccesoArregloNodo(int linea, int columna, String nombre,
                             ExpresionNodo fila, ExpresionNodo columnaIndice) {
        super(linea, columna);
        this.nombre = nombre;
        this.fila = fila;
        this.columnaIndice = columnaIndice;
    }

    public String getNombre() {
        return nombre;
    }

    public ExpresionNodo getFila() {
        return fila;
    }

    public ExpresionNodo getColumnaIndice() {
        return columnaIndice;
    }
}
