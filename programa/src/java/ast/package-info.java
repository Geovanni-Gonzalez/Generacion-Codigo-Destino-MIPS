/**
 * Modelo interno del arbol sintactico abstracto (AST).
 *
 * <p><strong>Objetivo:</strong> representar en memoria las construcciones del
 * lenguaje fuente despues del analisis sintactico, conservando ubicacion,
 * tipo semantico y relaciones entre expresiones, sentencias, funciones y
 * bloques.</p>
 *
 * <p><strong>Entradas:</strong> acciones semanticas del parser CUP que crean
 * nodos con lexemas, tipos, linea, columna y subnodos ya reconocidos.</p>
 *
 * <p><strong>Salidas:</strong> objetos {@code Nodo} y sus especializaciones,
 * consumidos por el analizador semantico y por el generador de codigo
 * intermedio.</p>
 *
 * <p><strong>Restricciones:</strong> los nodos no deben ejecutar validaciones
 * globales ni escribir reportes; solo almacenan estructura y metadatos para
 * que las fases posteriores trabajen con una representacion estable.</p>
 */
package ast;
