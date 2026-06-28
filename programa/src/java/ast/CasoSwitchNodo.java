package ast;

/**
 * Nombre: CasoSwitchNodo
 *
 * Objetivo: Representar CasoSwitchNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class CasoSwitchNodo extends Nodo {
    private final ExpresionNodo valor;
    private final BloqueNodo bloque;
    private final boolean defecto;
    /**
     * Nombre: CasoSwitchNodo
     *
     * Objetivo: Inicializar una instancia de CasoSwitchNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; ExpresionNodo valor; BloqueNodo bloque; boolean defecto.
     *
     * Salida: Nueva instancia de CasoSwitchNodo.
     *
     * Restricciones: Ninguna.
     */
    public CasoSwitchNodo(int linea, int columna, ExpresionNodo valor, BloqueNodo bloque, boolean defecto) {
        super(linea, columna);
        this.valor = valor;
        this.bloque = bloque;
        this.defecto = defecto;
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

    /**
     * Nombre: getBloque
     *
     * Objetivo: Obtener el valor de Bloque almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo BloqueNodo.
     *
     * Restricciones: Ninguna.
     */
    public BloqueNodo getBloque() {
        return bloque;
    }

    /**
     * Nombre: isDefecto
     *
     * Objetivo: Indicar si se cumple la condicion Defecto.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    public boolean isDefecto() {
        return defecto;
    }
}
