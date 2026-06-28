package ast;

/**
 * Nombre: SentenciaNodo
 *
 * Objetivo: Representar SentenciaNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public abstract class SentenciaNodo extends Nodo {
    /**
     * Nombre: SentenciaNodo
     *
     * Objetivo: Inicializar una instancia de SentenciaNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna.
     *
     * Salida: Nueva instancia de SentenciaNodo.
     *
     * Restricciones: Ninguna.
     */
    protected SentenciaNodo(int linea, int columna) {
        super(linea, columna);
    }
    /**
     * Nombre: SentenciaNodo
     *
     * Objetivo: Inicializar una instancia de SentenciaNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; TipoDato tipo.
     *
     * Salida: Nueva instancia de SentenciaNodo.
     *
     * Restricciones: Ninguna.
     */
    protected SentenciaNodo(int linea, int columna, TipoDato tipo) {
        super(linea, columna, tipo);
    }
}
