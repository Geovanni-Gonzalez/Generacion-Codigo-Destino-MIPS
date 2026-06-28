package ast;

/**
 * Nombre: DeclaracionVariableNodo
 *
 * Objetivo: Representar DeclaracionVariableNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class DeclaracionVariableNodo extends SentenciaNodo {
    private final String nombre;
    private final ExpresionNodo inicializador;
    private final ExpresionNodo filas;
    private final ExpresionNodo columnas;
    private final InicializacionArregloNodo inicializacionArreglo;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, String nombre, TipoDato tipoDeclarado, ExpresionNodo inicializador</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de DeclaracionVariableNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public DeclaracionVariableNodo(int linea, int columna, String nombre, TipoDato tipoDeclarado,
                                   ExpresionNodo inicializador) {
        this(linea, columna, nombre, tipoDeclarado, inicializador, null, null, null);
    }
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, String nombre, TipoDato tipoDeclarado, ExpresionNodo filas, ExpresionNodo columnas, InicializacionArregloNodo inicializacionArreglo</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de DeclaracionVariableNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public DeclaracionVariableNodo(int linea, int columna, String nombre, TipoDato tipoDeclarado,
                                   ExpresionNodo filas, ExpresionNodo columnas,
                                   InicializacionArregloNodo inicializacionArreglo) {
        this(linea, columna, nombre, tipoDeclarado, null, filas, columnas, inicializacionArreglo);
    }
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, String nombre, TipoDato tipoDeclarado, ExpresionNodo inicializador, ExpresionNodo filas, ExpresionNodo columnas, InicializacionArregloNodo inicializacionArreglo</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de DeclaracionVariableNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private DeclaracionVariableNodo(int linea, int columna, String nombre, TipoDato tipoDeclarado,
                                    ExpresionNodo inicializador, ExpresionNodo filas,
                                    ExpresionNodo columnas,
                                    InicializacionArregloNodo inicializacionArreglo) {
        super(linea, columna, tipoDeclarado);
        this.nombre = nombre;
        this.inicializador = inicializador;
        this.filas = filas;
        this.columnas = columnas;
        this.inicializacionArreglo = inicializacionArreglo;
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
     * Nombre: getInicializador
     *
     * Objetivo: Obtener el valor de Inicializador almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionNodo getInicializador() {
        return inicializador;
    }

    /**
     * Nombre: getFilas
     *
     * Objetivo: Obtener el valor de Filas almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionNodo getFilas() {
        return filas;
    }

    /**
     * Nombre: getColumnas
     *
     * Objetivo: Obtener el valor de Columnas almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionNodo getColumnas() {
        return columnas;
    }

    /**
     * Nombre: getInicializacionArreglo
     *
     * Objetivo: Obtener el valor de InicializacionArreglo almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo InicializacionArregloNodo.
     *
     * Restricciones: Ninguna.
     */
    public InicializacionArregloNodo getInicializacionArreglo() {
        return inicializacionArreglo;
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
