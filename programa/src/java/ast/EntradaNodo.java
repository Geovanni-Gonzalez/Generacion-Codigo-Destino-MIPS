package ast;

/**
 * Nombre: EntradaNodo
 *
 * Objetivo: Representar EntradaNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class EntradaNodo extends SentenciaNodo {
    private final String destino;
    /**
     * Nombre: EntradaNodo
     *
     * Objetivo: Inicializar una instancia de EntradaNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; String destino.
     *
     * Salida: Nueva instancia de EntradaNodo.
     *
     * Restricciones: Ninguna.
     */
    public EntradaNodo(int linea, int columna, String destino) {
        super(linea, columna);
        this.destino = destino;
    }

    /**
     * Nombre: getDestino
     *
     * Objetivo: Obtener el valor de Destino almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String getDestino() {
        return destino;
    }
}
