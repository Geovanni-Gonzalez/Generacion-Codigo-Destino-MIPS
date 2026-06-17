package semantico;

/**
 * <strong>Objetivo:</strong> Clasifica el rol de un simbolo dentro de la tabla de simbolos.
 *
 * <p><strong>Entradas:</strong> Simbolos, tipos, nodos y ubicaciones producidos por las fases previas.</p>
 *
 * <p><strong>Salidas:</strong> Estado semantico actualizado, simbolos resueltos o diagnosticos acumulados.</p>
 *
 * <p><strong>Restricciones:</strong> No debe generar codigo intermedio ni escribir reportes directamente.</p>
 */
public enum CategoriaSimb {
    /** Variable escalar declarada en un alcance. */
    VAR,
    /** Arreglo bidimensional declarado en un alcance. */
    ARREGLO,
    /** Parametro formal de una funcion. */
    PARAMETRO,
    /** Funcion de nivel superior. */
    FUNCION
}
