package ast;

/**
 * Nombre: IfNodo
 *
 * Objetivo: Representar IfNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class IfNodo extends SentenciaNodo {
    private final ExpresionNodo condicion;
    private final BloqueNodo bloqueEntonces;
    private final BloqueNodo bloqueSino;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, ExpresionNodo condicion, BloqueNodo bloqueEntonces, BloqueNodo bloqueSino</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de IfNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public IfNodo(int linea, int columna, ExpresionNodo condicion,
                  BloqueNodo bloqueEntonces, BloqueNodo bloqueSino) {
        super(linea, columna);
        this.condicion = condicion;
        this.bloqueEntonces = bloqueEntonces;
        this.bloqueSino = bloqueSino;
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
     * Nombre: getBloqueEntonces
     *
     * Objetivo: Obtener el valor de BloqueEntonces almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo BloqueNodo.
     *
     * Restricciones: Ninguna.
     */
    public BloqueNodo getBloqueEntonces() {
        return bloqueEntonces;
    }

    /**
     * Nombre: getBloqueSino
     *
     * Objetivo: Obtener el valor de BloqueSino almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo BloqueNodo.
     *
     * Restricciones: Ninguna.
     */
    public BloqueNodo getBloqueSino() {
        return bloqueSino;
    }
}
