package semantico;

/**
 * Nombre: CategoriaSimb
 *
 * Objetivo: Validar reglas semanticas y administrar informacion de simbolos.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
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
