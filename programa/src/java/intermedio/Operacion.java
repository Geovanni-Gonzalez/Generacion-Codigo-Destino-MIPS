package intermedio;

/**
 * <strong>Nombre:</strong> Operacion
 *
 * <p><strong>Objetivo:</strong> Enumerar las operaciones que puede representar una instrucción
 * del código de tres direcciones (declarar, operar, saltar, llamar, imprimir, etc.).</p>
 *
 * <p><strong>Entrada:</strong> Ninguna; son constantes fijas del enum.</p>
 *
 * <p><strong>Salida:</strong> Valores que identifican qué hace cada instrucción intermedia.</p>
 *
 * <p><strong>Restricciones:</strong> Ninguna.</p>
 */
public enum Operacion {
    /** Declaración de variable escalar. */
    DECL,
    /** Declaración de arreglo bidimensional. */
    DECL_ARRAY,
    /** Declaración de parámetro formal. */
    FORMAL_PARAM,
    /** Carga de una variable o celda hacia un temporal. */
    LOAD,
    /** Escritura explícita de una celda de arreglo. */
    STORE_ARRAY,
    /** Asignación simple: destino = valor. */
    ASIG,
    /** Suma binaria. */
    SUMA,
    /** Resta binaria. */
    RESTA,
    /** Multiplicación binaria. */
    MULT,
    /** División binaria. */
    DIV,
    /** Módulo binario. */
    MOD,
    /** Potencia binaria. */
    POW,
    /** Negación aritmética unaria. */
    NEG,
    /** Conjunción lógica. */
    AND,
    /** Disyunción lógica. */
    OR,
    /** Negación lógica unaria. */
    NOT,
    /** Comparación de igualdad. */
    IGUAL,
    /** Comparación menor que. */
    MENOR,
    /** Comparación mayor que. */
    MAYOR,
    /** Comparación menor o igual. */
    MENOR_IGUAL,
    /** Comparación mayor o igual. */
    MAYOR_IGUAL,
    /** Comparación de desigualdad. */
    DISTINTO,
    /** Salto incondicional. */
    GOTO,
    /** Salto condicional cuando la condición es falsa. */
    IF_FALSE,
    /** Paso de parámetro previo a una llamada. */
    PARAM,
    /** Llamada a función. */
    CALL,
    /** Impresión de salida. */
    PRINT,
    /** Lectura de entrada. */
    READ,
    /** Retorno de una función. */
    RETURN,
    /** Etiqueta de salto. */
    LABEL,
    /** Marcador de inicio de función. */
    INICIO_FUNC,
    /** Marcador de fin de función. */
    FIN_FUNC
}
