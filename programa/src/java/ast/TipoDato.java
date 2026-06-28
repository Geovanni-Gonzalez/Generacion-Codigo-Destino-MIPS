package ast;

/**
 * Nombre: TipoDato
 *
 * Objetivo: Representar TipoDato dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public enum TipoDato {
    INT,
    FLOAT,
    BOOL,
    CHAR,
    STRING,
    VOID,
    ERROR,
    EMPTY,
    DESCONOCIDO;
    /**
     * Nombre: esNumerico
     *
     * Objetivo: Indicar si se cumple la condicion Numerico.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    public boolean esNumerico() {
        return this == INT || this == FLOAT;
    }

    /**
     * Nombre: esDeclarableVariable
     *
     * Objetivo: Indicar si se cumple la condicion DeclarableVariable.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    public boolean esDeclarableVariable() {
        return this == INT || this == FLOAT || this == BOOL || this == CHAR || this == STRING;
    }

    /**
     * Nombre: esCompatibleCon
     *
     * Objetivo: Indicar si se cumple la condicion CompatibleCon.
     *
     * Entrada: TipoDato otro.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    public boolean esCompatibleCon(TipoDato otro) {
        if (this == ERROR || otro == ERROR) {
            return true;
        }

        if (this == otro) {
            return true;
        }

        return esNumerico() && otro.esNumerico();
    }

    /** Imprime el tipo como texto del lenguaje en minuscula. */
    @Override
    /**
     * Nombre: toString
     *
     * Objetivo: Ejecutar la operacion toString definida por TipoDato.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String toString() {
        return name().toLowerCase();
    }
}
