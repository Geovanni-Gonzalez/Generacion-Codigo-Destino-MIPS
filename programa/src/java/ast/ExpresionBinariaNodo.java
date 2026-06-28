package ast;

/**
 * Nombre: ExpresionBinariaNodo
 *
 * Objetivo: Representar ExpresionBinariaNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class ExpresionBinariaNodo extends ExpresionNodo {
    private final String operador;
    private final ExpresionNodo izquierda;
    private final ExpresionNodo derecha;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, String operador, ExpresionNodo izquierda, ExpresionNodo derecha</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de ExpresionBinariaNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public ExpresionBinariaNodo(int linea, int columna, String operador,
                                ExpresionNodo izquierda, ExpresionNodo derecha) {
        super(linea, columna);
        this.operador = operador;
        this.izquierda = izquierda;
        this.derecha = derecha;
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
     * Nombre: getIzquierda
     *
     * Objetivo: Obtener el valor de Izquierda almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionNodo getIzquierda() {
        return izquierda;
    }

    /**
     * Nombre: getDerecha
     *
     * Objetivo: Obtener el valor de Derecha almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionNodo getDerecha() {
        return derecha;
    }
}
