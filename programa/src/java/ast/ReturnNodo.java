package ast;

/**
 * Nombre: ReturnNodo
 *
 * Objetivo: Representar ReturnNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class ReturnNodo extends SentenciaNodo {
    private final ExpresionNodo valor;
    /**
     * Nombre: ReturnNodo
     *
     * Objetivo: Inicializar una instancia de ReturnNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; ExpresionNodo valor.
     *
     * Salida: Nueva instancia de ReturnNodo.
     *
     * Restricciones: Ninguna.
     */
    public ReturnNodo(int linea, int columna, ExpresionNodo valor) {
        super(linea, columna);
        this.valor = valor;
    }

    /**
     * Nombre: getValor
     *
     * Objetivo: Obtener el valor de Valor almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionNodo getValor() {
        return valor;
    }
}
