package ast;

/**
 * Nombre: AsignacionNodo
 *
 * Objetivo: Representar AsignacionNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class AsignacionNodo extends SentenciaNodo {
    private final ExpresionNodo destino;
    private final ExpresionNodo valor;
    /**
     * Nombre: AsignacionNodo
     *
     * Objetivo: Inicializar una instancia de AsignacionNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; ExpresionNodo destino; ExpresionNodo valor.
     *
     * Salida: Nueva instancia de AsignacionNodo.
     *
     * Restricciones: Ninguna.
     */
    public AsignacionNodo(int linea, int columna, ExpresionNodo destino, ExpresionNodo valor) {
        super(linea, columna);
        this.destino = destino;
        this.valor = valor;
    }

    /**
     * Nombre: getDestino
     *
     * Objetivo: Obtener el valor de Destino almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionNodo getDestino() {
        return destino;
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
