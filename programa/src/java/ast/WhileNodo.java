package ast;

/**
 * Nombre: WhileNodo
 *
 * Objetivo: Representar WhileNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class WhileNodo extends SentenciaNodo {
    private final ExpresionNodo condicion;
    private final BloqueNodo cuerpo;
    private final boolean doWhile;
    /**
     * Nombre: WhileNodo
     *
     * Objetivo: Inicializar una instancia de WhileNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; ExpresionNodo condicion; BloqueNodo cuerpo; boolean doWhile.
     *
     * Salida: Nueva instancia de WhileNodo.
     *
     * Restricciones: Ninguna.
     */
    public WhileNodo(int linea, int columna, ExpresionNodo condicion, BloqueNodo cuerpo, boolean doWhile) {
        super(linea, columna);
        this.condicion = condicion;
        this.cuerpo = cuerpo;
        this.doWhile = doWhile;
    }
    /**
     * Nombre: getCondicion
     *
     * Objetivo: Obtener el valor de Condicion almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionNodo getCondicion() {
        return condicion;
    }

    /**
     * Nombre: getCuerpo
     *
     * Objetivo: Obtener el valor de Cuerpo almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo BloqueNodo.
     *
     * Restricciones: Ninguna.
     */
    public BloqueNodo getCuerpo() {
        return cuerpo;
    }

    /**
     * Nombre: isDoWhile
     *
     * Objetivo: Indicar si se cumple la condicion DoWhile.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    public boolean isDoWhile() {
        return doWhile;
    }
}
