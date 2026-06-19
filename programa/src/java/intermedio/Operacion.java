package intermedio;

/**
 * <strong>Objetivo:</strong> Operaciones soportadas por el codigo de tres direcciones del proyecto.
 *
 * <p><strong>Entradas:</strong> AST validado, operaciones u operandos necesarios para representar codigo intermedio.</p>
 *
 * <p><strong>Salidas:</strong> Instrucciones, operaciones o texto de codigo intermedio.</p>
 *
 * <p><strong>Restricciones:</strong> Debe asumir programas ya aceptados y no reemplazar las validaciones semanticas.</p>
 */
public enum Operacion {
    /** Declaracion de variable escalar. */
    DECL,
    /** Declaracion de arreglo bidimensional. */
    DECL_ARRAY,
    /** Declaracion de parametro formal. */
    FORMAL_PARAM,
    /** Carga de una variable o celda hacia un temporal. */
    LOAD,
    /** Escritura explicita de una celda de arreglo. */
    STORE_ARRAY,
    /** Asignacion simple: destino = valor. */
    ASIG,
    /** Suma binaria. */
    SUMA,
    /** Resta binaria. */
    RESTA,
    /** Multiplicacion binaria. */
    MULT,
    /** Division binaria. */
    DIV,
    /** Modulo binario. */
    MOD,
    /** Potencia binaria. */
    POW,
    /** Negacion aritmetica unaria. */
    NEG,
    /** Conjuncion logica. */
    AND,
    /** Disyuncion logica. */
    OR,
    /** Negacion logica unaria. */
    NOT,
    /** Comparacion de igualdad. */
    IGUAL,
    /** Comparacion menor que. */
    MENOR,
    /** Comparacion mayor que. */
    MAYOR,
    /** Comparacion menor o igual. */
    MENOR_IGUAL,
    /** Comparacion mayor o igual. */
    MAYOR_IGUAL,
    /** Comparacion de desigualdad. */
    DISTINTO,
    /** Salto incondicional. */
    GOTO,
    /** Salto condicional cuando la condicion es falsa. */
    IF_FALSE,
    /** Paso de parametro previo a llamada. */
    PARAM,
    /** Llamada a funcion. */
    CALL,
    /** Impresion de salida. */
    PRINT,
    /** Lectura de entrada. */
    READ,
    /** Retorno . */
    RETURN,
    /** Etiqueta de salto. */
    LABEL,
    /** Marcador de inicio . */
    INICIO_FUNC,
    /** Marcador de fin . */
    FIN_FUNC
}
