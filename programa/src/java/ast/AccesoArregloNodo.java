package ast;

/**
 * Nombre: AccesoArregloNodo
 *
 * Objetivo: Representar AccesoArregloNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class AccesoArregloNodo extends ExpresionNodo {
    private final String nombre;
    private final ExpresionNodo fila;
    private final ExpresionNodo columnaIndice;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, String nombre, ExpresionNodo fila, ExpresionNodo columnaIndice</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de AccesoArregloNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public AccesoArregloNodo(int linea, int columna, String nombre,
                             ExpresionNodo fila, ExpresionNodo columnaIndice) {
        super(linea, columna);
        this.nombre = nombre;
        this.fila = fila;
        this.columnaIndice = columnaIndice;
    }

    /**
     * Nombre: getNombre
     *
     * Objetivo: Obtener el valor de Nombre almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Nombre: getFila
     *
     * Objetivo: Obtener el valor de Fila almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionNodo getFila() {
        return fila;
    }

    /**
     * Nombre: getColumnaIndice
     *
     * Objetivo: Obtener el valor de ColumnaIndice almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionNodo getColumnaIndice() {
        return columnaIndice;
    }
}
