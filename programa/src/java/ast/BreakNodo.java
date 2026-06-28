package ast;

/**
 * Nombre: BreakNodo
 *
 * Objetivo: Representar BreakNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class BreakNodo extends SentenciaNodo {
    /**
     * Nombre: BreakNodo
     *
     * Objetivo: Inicializar una instancia de BreakNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna.
     *
     * Salida: Nueva instancia de BreakNodo.
     *
     * Restricciones: Ninguna.
     */
    public BreakNodo(int linea, int columna) {
        super(linea, columna, TipoDato.EMPTY);
    }
}
