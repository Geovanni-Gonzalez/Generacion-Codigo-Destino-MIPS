package ast;

/**
 * Nombre: SalidaNodo
 *
 * Objetivo: Representar SalidaNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class SalidaNodo extends SentenciaNodo {
    private final ExpresionNodo valor;
    /**
     * Nombre: SalidaNodo
     *
     * Objetivo: Inicializar una instancia de SalidaNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; ExpresionNodo valor.
     *
     * Salida: Nueva instancia de SalidaNodo.
     *
     * Restricciones: Ninguna.
     */
    public SalidaNodo(int linea, int columna, ExpresionNodo valor) {
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
