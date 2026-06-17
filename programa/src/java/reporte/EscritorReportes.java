package reporte;

import intermedio.Instruccion;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * <strong>Objetivo:</strong> Genera los archivos de reporte producidos por la linea de comandos.
 *
 * <p><strong>Entradas:</strong> Resultados del analisis, errores, tokens, rutas de salida y metadatos de reporte.</p>
 *
 * <p><strong>Salidas:</strong> Mensajes formateados o archivos de reporte escritos en UTF-8.</p>
 *
 * <p><strong>Restricciones:</strong> No debe recalcular analisis; solo formatea o persiste informacion recibida.</p>
 */
public final class EscritorReportes {
    /**
     * <strong>Objetivo:</strong> Evita instancias de una clase utilitaria.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de EscritorReportes.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private EscritorReportes() {
    }

    /**
     * <strong>Objetivo:</strong> Escribe el listado completo de tokens reconocidos por el lexer.
     *
     * <p><strong>Entradas:</strong> Path archivo, List<TokenInfo> tokens</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public static void escribirTokens(Path archivo, List<TokenInfo> tokens) throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            writer.write("ID_TOKEN\tTOKEN\tLEXEMA\tLINEA\tCOLUMNA\tTABLA\tINFORMACION");
            writer.newLine();
            for (TokenInfo token : tokens) {
                writer.write(token.id + "\t" + token.nombre + "\t" + token.lexema + "\t"
                        + token.linea + "\t" + token.columna + "\t" + token.tabla + "\t"
                        + token.informacion);
                writer.newLine();
            }
        }
    }

    /**
     * <strong>Objetivo:</strong> Escribe una vista tabular de los simbolos lexicos recolectados.
     *
     * <p><strong>Entradas:</strong> Path archivo, List<TokenInfo> tokens</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public static void escribirTablaSimbolos(Path archivo, List<TokenInfo> tokens) throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            writer.write("TABLA\tLEXEMA\tTOKEN\tLINEA\tCOLUMNA\tINFORMACION");
            writer.newLine();
            for (TokenInfo token : tokens) {
                writer.write(token.tabla + "\t" + token.lexema + "\t" + token.nombre + "\t"
                        + token.linea + "\t" + token.columna + "\t" + token.informacion);
                writer.newLine();
            }
        }
    }

    /**
     * <strong>Objetivo:</strong> Agrupa errores lexicos, sintacticos y semanticos en un solo archivo.
     *
     * <p><strong>Entradas:</strong> Path archivo, List<String> erroresLexicos, List<String> erroresSintacticos, List<String> erroresSemanticos</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public static void escribirErrores(Path archivo, List<String> erroresLexicos,
                                       List<String> erroresSintacticos,
                                       List<String> erroresSemanticos) throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            escribirSeccionErrores(writer, "ERRORES LEXICOS", "Sin errores lexicos.", erroresLexicos);
            writer.newLine();
            escribirSeccionErrores(writer, "ERRORES SINTACTICOS", "Sin errores sintacticos.",
                    erroresSintacticos);
            writer.newLine();
            escribirSeccionErrores(writer, "ERRORES SEMANTICOS", "Sin errores semanticos.",
                    erroresSemanticos);
        }
    }

    /**
     * <strong>Objetivo:</strong> Escribe el veredicto global de aceptacion del programa fuente.
     *
     * <p><strong>Entradas:</strong> Path archivo, Path fuente, boolean aceptado</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public static void escribirResultado(Path archivo, Path fuente, boolean aceptado) throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            writer.write("Archivo fuente: " + fuente);
            writer.newLine();
            writer.write(aceptado
                    ? "El archivo fuente puede ser generado por la gramatica."
                    : "El archivo fuente NO puede ser generado por la gramatica.");
            writer.newLine();
        }
    }

    /**
     * <strong>Objetivo:</strong> Escribe instrucciones intermedias sin encabezado, util para reportes simples.
     *
     * <p><strong>Entradas:</strong> Path archivo, List<Instruccion> instrucciones</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public static void escribirCodigoIntermedio(Path archivo, List<Instruccion> instrucciones) throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            for (Instruccion instruccion : instrucciones) {
                writer.write(instruccion.toString());
                writer.newLine();
            }
        }
    }

    /**
     * <strong>Objetivo:</strong> Imprime una seccion de errores y un mensaje alterno cuando esta vacia.
     *
     * <p><strong>Entradas:</strong> BufferedWriter writer, String titulo, String mensajeVacio, List<String> errores</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private static void escribirSeccionErrores(BufferedWriter writer, String titulo,
                                               String mensajeVacio, List<String> errores) throws Exception {
        writer.write(titulo);
        writer.newLine();
        if (errores.isEmpty()) {
            writer.write(mensajeVacio);
            writer.newLine();
            return;
        }
        for (String error : errores) {
            writer.write(error);
            writer.newLine();
        }
    }
}
