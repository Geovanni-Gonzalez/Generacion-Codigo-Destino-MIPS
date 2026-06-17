package ast;

/**
 * <strong>Objetivo:</strong> Estructura auxiliar usada por la gramatica para conservar el sufijo de una.
 *
 * <p><strong>Entradas:</strong> Datos sintacticos reconocidos por el parser, posiciones de fuente y subnodos relacionados.</p>
 *
 * <p><strong>Salidas:</strong> Nodos, valores o metadatos consultables por las fases semantica e intermedia.</p>
 *
 * <p><strong>Restricciones:</strong> No debe ejecutar validaciones globales ni escribir archivos; solo conserva estructura y metadatos.</p>
 */
public class DeclaracionSufijo {
    public final ExpresionNodo inicializador;
    public final ExpresionNodo filas;
    public final ExpresionNodo columnas;
    public final InicializacionArregloNodo inicializacionArreglo;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> ExpresionNodo inicializador</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de DeclaracionSufijo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
