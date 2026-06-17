/**
 * Analisis semantico y administracion de simbolos del compilador.
 *
 * <p><strong>Objetivo:</strong> validar reglas que no dependen solo de la
 * gramatica, como declaracion previa, alcances, tipos compatibles, uso de
 * arreglos, llamadas a funciones, retornos y existencia del metodo principal.</p>
 *
 * <p><strong>Entradas:</strong> nodos del AST, simbolos detectados por el
 * parser, tipos declarados, expresiones evaluables y ubicaciones de fuente
 * para diagnosticos.</p>
 *
 * <p><strong>Salidas:</strong> tabla de simbolos actualizada, tipos calculados
 * en expresiones y lista de errores semanticos formateados para los reportes.</p>
 *
 * <p><strong>Restricciones:</strong> no genera codigo intermedio ni modifica
 * archivos; cuando encuentra un error retorna tipos centinela como
 * {@code TipoDato.ERROR} para reducir cascadas de diagnosticos derivados.</p>
 */
package semantico;
