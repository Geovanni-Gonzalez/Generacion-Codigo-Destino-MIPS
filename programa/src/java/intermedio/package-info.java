/**
 * Generacion y representacion del codigo intermedio.
 *
 * <p><strong>Objetivo:</strong> traducir un AST aceptado a instrucciones de
 * tres direcciones con temporales, etiquetas, saltos, llamadas y operaciones
 * aritmeticas o logicas.</p>
 *
 * <p><strong>Entradas:</strong> {@code ProgramaNodo} validado por las fases
 * lexica, sintactica y semantica; cada expresion debe tener una forma
 * estructural reconocible por el generador.</p>
 *
 * <p><strong>Salidas:</strong> lista ordenada de {@code Instruccion}, lista
 * para ser escrita luego en el archivo {@code .ic}.</p>
 *
 * <p><strong>Restricciones:</strong> esta fase asume que no hay errores
 * semanticos pendientes; no debe aceptar programas rechazados ni volver a
 * validar tipos, solo emitir la representacion intermedia.</p>
 */
package intermedio;
