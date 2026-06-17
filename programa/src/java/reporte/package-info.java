/**
 * Escritura de reportes y artefactos de salida.
 *
 * <p><strong>Objetivo:</strong> convertir los resultados internos de la
 * compilacion en archivos legibles: tokens, tabla de simbolos, errores,
 * resultado sintactico y codigo intermedio.</p>
 *
 * <p><strong>Entradas:</strong> rutas de destino, tokens recolectados, errores
 * lexicos/sintacticos/semanticos, resultado global e instrucciones
 * intermedias.</p>
 *
 * <p><strong>Salidas:</strong> archivos de texto en UTF-8 dentro del directorio
 * de salida indicado por la linea de comandos.</p>
 *
 * <p><strong>Restricciones:</strong> esta capa no recalcula analisis ni decide
 * reglas del lenguaje; solo formatea datos ya producidos y elimina el
 * {@code .ic} anterior cuando el programa actual fue rechazado.</p>
 */
package reporte;
