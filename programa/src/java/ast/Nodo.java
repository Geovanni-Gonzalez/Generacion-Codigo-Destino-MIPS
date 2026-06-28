package ast;

/**
 * Nombre: Nodo
 *
 * Objetivo: Representar Nodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public abstract class Nodo {
    private final int linea;
    private final int columna;
    private TipoDato tipo;
    /**
     * Nombre: Nodo
     *
     * Objetivo: Inicializar una instancia de Nodo con los datos requeridos.
     *
     * Entrada: int linea; int columna.
     *
     * Salida: Nueva instancia de Nodo.
     *
     * Restricciones: Ninguna.
     */
    protected Nodo(int linea, int columna) {
        this(linea, columna, TipoDato.DESCONOCIDO);
    }
    /**
     * Nombre: Nodo
     *
     * Objetivo: Inicializar una instancia de Nodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; TipoDato tipo.
     *
     * Salida: Nueva instancia de Nodo.
     *
     * Restricciones: Ninguna.
     */
    protected Nodo(int linea, int columna, TipoDato tipo) {
        this.linea = linea;
        this.columna = columna;
        this.tipo = tipo;
    }
    /**
     * Nombre: getLinea
     *
     * Objetivo: Obtener el valor de Linea almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo int.
     *
     * Restricciones: Ninguna.
     */
    public int getLinea() {
        return linea;
    }

    /**
     * Nombre: getColumna
     *
     * Objetivo: Obtener el valor de Columna almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo int.
     *
     * Restricciones: Ninguna.
     */
    public int getColumna() {
        return columna;
    }

    /**
     * Nombre: getTipo
     *
     * Objetivo: Obtener el valor de Tipo almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo TipoDato.
     *
     * Restricciones: Ninguna.
     */
    public TipoDato getTipo() {
        return tipo;
    }

    /**
     * Nombre: setTipo
     *
     * Objetivo: Actualizar el valor de Tipo en la instancia.
     *
     * Entrada: TipoDato tipo.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void setTipo(TipoDato tipo) {
        this.tipo = tipo;
    }
}
