package ast;

/**
 * Nombre: AccesoMiembroNodo
 *
 * Objetivo: Representar el acceso a un campo de un objeto ('objeto.campo') dentro del AST.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class AccesoMiembroNodo extends ExpresionNodo {
    private final ExpresionNodo objeto;
    private final String nombreCampo;

    /**
     * Nombre: AccesoMiembroNodo
     *
     * Objetivo: Inicializar una instancia de AccesoMiembroNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; ExpresionNodo objeto; String nombreCampo.
     *
     * Salida: Nueva instancia de AccesoMiembroNodo.
     *
     * Restricciones: objeto debe evaluar a un tipo OBJETO.
     */
    public AccesoMiembroNodo(int linea, int columna, ExpresionNodo objeto, String nombreCampo) {
        super(linea, columna);
        this.objeto = objeto;
        this.nombreCampo = nombreCampo;
    }

    /**
     * Nombre: getObjeto
     *
     * Objetivo: Obtener el valor de Objeto almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionNodo getObjeto() {
        return objeto;
    }

    /**
     * Nombre: getNombreCampo
     *
     * Objetivo: Obtener el valor de NombreCampo almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String getNombreCampo() {
        return nombreCampo;
    }
}
