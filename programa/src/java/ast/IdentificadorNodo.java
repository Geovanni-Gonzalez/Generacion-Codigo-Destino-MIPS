package ast;

public class IdentificadorNodo extends ExpresionNodo {
    private final String nombre;
    public IdentificadorNodo(int linea, int columna, String nombre) {
        super(linea, columna);
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
