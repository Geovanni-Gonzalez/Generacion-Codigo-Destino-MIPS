package semantico;

/**
 * <strong>Nombre:</strong> CategoriaSimb
 *
 * <p><strong>Objetivo:</strong> Clasificar el rol de un símbolo dentro de la tabla de símbolos:
 * distingue variables, arreglos, parámetros y funciones.</p>
 *
 * <p><strong>Entrada:</strong> Ninguna; son constantes fijas del enum.</p>
 *
 * <p><strong>Salida:</strong> Valores de categoría usados por la tabla de símbolos.</p>
 *
 * <p><strong>Restricciones:</strong> Ninguna.</p>
 */
public enum CategoriaSimb {
    /** Variable escalar declarada en un alcance. */
    VAR,
    /** Arreglo bidimensional declarado en un alcance. */
    ARREGLO,
    /** Parámetro formal de una función. */
    PARAMETRO,
    /** Función de nivel superior. */
    FUNCION
}
