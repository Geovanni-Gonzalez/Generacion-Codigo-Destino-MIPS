package semantico;

public enum CategoriaSimb {
    /** Variable escalar declarada en un alcance. */
    VAR,
    /** Arreglo bidimensional declarado en un alcance. */
    ARREGLO,
    /** Parámetro formal de una función. */
    PARAMETRO,
    /** Función de nivel superior. */
    FUNCION,
    /** Definición de una clase de nivel superior. */
    CLASE,
    /** Variable cuyo tipo es una instancia de clase (objeto). */
    OBJETO
}
