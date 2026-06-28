package ast;

/**
 * Nombre: ExpresionNodo
 *
 * Objetivo: Representar ExpresionNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public abstract class ExpresionNodo extends Nodo {
    /**
     * Nombre: ExpresionNodo
     *
     * Objetivo: Inicializar una instancia de ExpresionNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna.
     *
     * Salida: Nueva instancia de ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    protected ExpresionNodo(int linea, int columna) {
        super(linea, columna);
    }
    /**
     * Nombre: ExpresionNodo
     *
     * Objetivo: Inicializar una instancia de ExpresionNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; TipoDato tipo.
     *
     * Salida: Nueva instancia de ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    protected ExpresionNodo(int linea, int columna, TipoDato tipo) {
        super(linea, columna, tipo);
    }
}
