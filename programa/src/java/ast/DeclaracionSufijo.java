package ast;

/**
 * Nombre: DeclaracionSufijo
 *
 * Objetivo: Representar DeclaracionSufijo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class DeclaracionSufijo {
    public final ExpresionNodo inicializador;
    public final ExpresionNodo filas;
    public final ExpresionNodo columnas;
    public final InicializacionArregloNodo inicializacionArreglo;
    /**
     * Nombre: DeclaracionSufijo
     *
     * Objetivo: Inicializar una instancia de DeclaracionSufijo con los datos requeridos.
     *
     * Entrada: ExpresionNodo inicializador.
     *
     * Salida: Nueva instancia de DeclaracionSufijo.
     *
     * Restricciones: Ninguna.
     */
    public DeclaracionSufijo(ExpresionNodo inicializador) {
        this.inicializador = inicializador;
        this.filas = null;
        this.columnas = null;
        this.inicializacionArreglo = null;
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> ExpresionNodo filas, ExpresionNodo columnas, InicializacionArregloNodo inicializacionArreglo</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de DeclaracionSufijo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public DeclaracionSufijo(ExpresionNodo filas, ExpresionNodo columnas,
                             InicializacionArregloNodo inicializacionArreglo) {
        this.inicializador = null;
        this.filas = filas;
        this.columnas = columnas;
        this.inicializacionArreglo = inicializacionArreglo;
    }

    /**
     * Nombre: esArreglo
     *
     * Objetivo: Indicar si se cumple la condicion Arreglo.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    public boolean esArreglo() {
        return filas != null || columnas != null;
    }
}
