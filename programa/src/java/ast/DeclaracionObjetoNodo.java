package ast;

/**
 * Nombre: DeclaracionObjetoNodo
 *
 * Objetivo: Representar la declaracion de una variable de tipo objeto ('Clase ~ id <- new ...').
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class DeclaracionObjetoNodo extends SentenciaNodo {
    private final String nombre;
    private final String nombreClase;
    private final ExpresionNodo inicializador;

    /**
     * Nombre: DeclaracionObjetoNodo
     *
     * Objetivo: Inicializar una instancia de DeclaracionObjetoNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; String nombre; String nombreClase; ExpresionNodo inicializador.
     *
     * Salida: Nueva instancia de DeclaracionObjetoNodo.
     *
     * Restricciones: inicializador puede ser null cuando se declara sin instanciar.
     */
    public DeclaracionObjetoNodo(int linea, int columna, String nombre, String nombreClase,
                                 ExpresionNodo inicializador) {
        super(linea, columna, TipoDato.OBJETO);
        this.nombre = nombre;
        this.nombreClase = nombreClase;
        this.inicializador = inicializador;
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
     * Nombre: getNombreClase
     *
     * Objetivo: Obtener el valor de NombreClase almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String getNombreClase() {
        return nombreClase;
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
}
