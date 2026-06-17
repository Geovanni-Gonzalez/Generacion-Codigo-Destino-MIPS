package ast;

/**
 * <strong>Objetivo:</strong> Sentencia que declara una variable escalar o un arreglo.
 *
 * <p><strong>Entradas:</strong> Datos sintacticos reconocidos por el parser, posiciones de fuente y subnodos relacionados.</p>
 *
 * <p><strong>Salidas:</strong> Nodos, valores o metadatos consultables por las fases semantica e intermedia.</p>
 *
 * <p><strong>Restricciones:</strong> No debe ejecutar validaciones globales ni escribir archivos; solo conserva estructura y metadatos.</p>
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
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna ExpresionNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public ExpresionNodo getInicializador() {
        return inicializador;
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna ExpresionNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public ExpresionNodo getFilas() {
        return filas;
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna ExpresionNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public ExpresionNodo getColumnas() {
        return columnas;
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna InicializacionArregloNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public InicializacionArregloNodo getInicializacionArreglo() {
        return inicializacionArreglo;
    }

    /**
     * <strong>Objetivo:</strong> Consulta una condicion booleana del objeto.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna boolean.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public boolean esArreglo() {
        return filas != null || columnas != null;
    }
}
