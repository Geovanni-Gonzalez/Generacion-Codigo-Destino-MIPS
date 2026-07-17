package ast;

public enum TipoDato {
    INT,
    FLOAT,
    BOOL,
    CHAR,
    STRING,
    VOID,
    ERROR,
    EMPTY,
    /** Tipo de una instancia de clase; el nombre concreto de la clase se transporta aparte. */
    OBJETO,
    DESCONOCIDO;
    public boolean esNumerico() {
        return this == INT || this == FLOAT;
    }

    public boolean esDeclarableVariable() {
        return this == INT || this == FLOAT || this == BOOL || this == CHAR || this == STRING;
    }

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
    public String toString() {
        return name().toLowerCase();
    }
}
