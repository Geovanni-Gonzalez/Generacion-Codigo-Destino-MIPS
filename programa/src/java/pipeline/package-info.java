/**
 * Orquestacion del flujo completo del compilador.
 *
 * <p><strong>Objetivo:</strong> encapsular la secuencia lexer, parser,
 * analisis semantico y generacion de codigo intermedio para que la consola y
 * las pruebas reutilicen el mismo procedimiento.</p>
 *
 * <p><strong>Entradas:</strong> ruta del archivo fuente y lectores UTF-8
 * creados para las pasadas lexica y sintactica.</p>
 *
 * <p><strong>Salidas:</strong> {@code ResultadoCompilacion} con tokens,
 * parser, estado de aceptacion, errores acumulados y codigo intermedio cuando
 * corresponde.</p>
 *
 * <p><strong>Restricciones:</strong> no escribe archivos de reporte; conserva
 * una pasada lexica separada para no mezclar el consumo de tokens del parser
 * con el listado completo requerido por la documentacion de salida.</p>
 */
package pipeline;
