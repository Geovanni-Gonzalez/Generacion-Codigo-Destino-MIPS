package ast;

/**
 * Nombre: ExpresionSentenciaNodo
 *
 * Objetivo: Representar ExpresionSentenciaNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class ExpresionSentenciaNodo extends SentenciaNodo {
    private final ExpresionNodo expresion;
    /**
     * Nombre: ExpresionSentenciaNodo
     *
     * Objetivo: Inicializar una instancia de ExpresionSentenciaNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; ExpresionNodo expresion.
     *
     * Salida: Nueva instancia de ExpresionSentenciaNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionSentenciaNodo(int linea, int columna, ExpresionNodo expresion) {
        super(linea, columna);
        this.expresion = expresion;
    }

    /**
     * Nombre: getExpresion
     *
     * Objetivo: Obtener el valor de Expresion almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionNodo getExpresion() {
        return expresion;
    }
}
