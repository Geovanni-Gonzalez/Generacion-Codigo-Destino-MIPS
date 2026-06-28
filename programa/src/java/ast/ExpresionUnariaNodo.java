package ast;

/**
 * Nombre: ExpresionUnariaNodo
 *
 * Objetivo: Representar ExpresionUnariaNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class ExpresionUnariaNodo extends ExpresionNodo {
    private final String operador;
    private final ExpresionNodo expresion;
    /**
     * Nombre: ExpresionUnariaNodo
     *
     * Objetivo: Inicializar una instancia de ExpresionUnariaNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; String operador; ExpresionNodo expresion.
     *
     * Salida: Nueva instancia de ExpresionUnariaNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionUnariaNodo(int linea, int columna, String operador, ExpresionNodo expresion) {
        super(linea, columna);
        this.operador = operador;
        this.expresion = expresion;
    }
    /**
     * Nombre: getOperador
     *
     * Objetivo: Obtener el valor de Operador almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String getOperador() {
        return operador;
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
