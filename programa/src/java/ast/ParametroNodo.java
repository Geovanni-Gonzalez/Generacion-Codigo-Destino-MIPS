package ast;

public class ParametroNodo extends Nodo {
    private final String nombre;
    public ParametroNodo(int linea, int columna, String nombre, TipoDato tipo) {
        super(linea, columna, tipo);
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
