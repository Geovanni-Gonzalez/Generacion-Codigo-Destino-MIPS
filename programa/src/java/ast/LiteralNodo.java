package ast;

/**
 * Nombre: LiteralNodo
 *
 * Objetivo: Representar LiteralNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class LiteralNodo extends ExpresionNodo {
    private final Object valor;
    /**
     * Nombre: LiteralNodo
     *
     * Objetivo: Inicializar una instancia de LiteralNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; Object valor; TipoDato tipo.
     *
     * Salida: Nueva instancia de LiteralNodo.
     *
     * Restricciones: Ninguna.
     */
    public LiteralNodo(int linea, int columna, Object valor, TipoDato tipo) {
        super(linea, columna, tipo);
        this.valor = valor;
    }

    /**
     * Nombre: getValor
     *
     * Objetivo: Obtener el valor de Valor almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo Object.
     *
     * Restricciones: Ninguna.
     */
    public Object getValor() {
        return valor;
    }
}
