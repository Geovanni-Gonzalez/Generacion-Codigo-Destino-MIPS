package ast;

/**
 * Nombre: IdentificadorNodo
 *
 * Objetivo: Representar IdentificadorNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class IdentificadorNodo extends ExpresionNodo {
    private final String nombre;
    /**
     * Nombre: IdentificadorNodo
     *
     * Objetivo: Inicializar una instancia de IdentificadorNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; String nombre.
     *
     * Salida: Nueva instancia de IdentificadorNodo.
     *
     * Restricciones: Ninguna.
     */
    public IdentificadorNodo(int linea, int columna, String nombre) {
        super(linea, columna);
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
