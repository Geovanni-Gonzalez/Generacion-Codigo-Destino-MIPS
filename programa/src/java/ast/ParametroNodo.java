package ast;

/**
 * Nombre: ParametroNodo
 *
 * Objetivo: Representar ParametroNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class ParametroNodo extends Nodo {
    private final String nombre;
    /**
     * Nombre: ParametroNodo
     *
     * Objetivo: Inicializar una instancia de ParametroNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; String nombre; TipoDato tipo.
     *
     * Salida: Nueva instancia de ParametroNodo.
     *
     * Restricciones: Ninguna.
     */
    public ParametroNodo(int linea, int columna, String nombre, TipoDato tipo) {
        super(linea, columna, tipo);
        this.nombre = nombre;
    }

    /**
     * Nombre: getNombre
     *
     * Objetivo: Obtener el valor de Nombre almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String getNombre() {
        return nombre;
    }
}
