package reporte;

import intermedio.Instruccion;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * <strong>Nombre:</strong> EscritorReportes
 *
 * <p><strong>Objetivo:</strong> Generar los archivos de reporte de la línea de comandos: tokens,
 * tabla de símbolos, errores, resultado sintáctico y código intermedio.</p>
 *
 * <p><strong>Entrada:</strong> Rutas de salida y los datos ya calculados por las fases de análisis.</p>
 *
 * <p><strong>Salida:</strong> Archivos de texto escritos en UTF-8.</p>
 *
 * <p><strong>Restricciones:</strong> Clase utilitaria; solo formatea y persiste, no analiza.</p>
 */
public final class EscritorReportes {
    private EscritorReportes() {
    }

    /**
     * <strong>Nombre:</strong> escribirTokens
     *
     * <p><strong>Objetivo:</strong> Escribir la lista completa de tokens reconocidos por el lexer, en formato de tabla.</p>
     *
     * <p><strong>Entrada:</strong> Path archivo, List&lt;TokenInfo&gt; tokens.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> escribirTablaSimbolos
     *
     * <p><strong>Objetivo:</strong> Escribir una vista tabular de los símbolos léxicos recolectados.</p>
     *
     * <p><strong>Entrada:</strong> Path archivo, List&lt;TokenInfo&gt; tokens.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> escribirErrores
     *
     * <p><strong>Objetivo:</strong> Escribir en un solo archivo los errores léxicos, sintácticos y
     * semánticos, cada grupo en su sección y precedidos por una línea de resumen con los conteos.</p>
     *
     * <p><strong>Entrada:</strong> Path archivo, List&lt;String&gt; erroresLexicos, List&lt;String&gt; erroresSintacticos, List&lt;String&gt; erroresSemanticos.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public static void escribirErrores(Path archivo, List<String> erroresLexicos,
                                       List<String> erroresSintacticos,
                                       List<String> erroresSemanticos) throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            int total = erroresLexicos.size() + erroresSintacticos.size() + erroresSemanticos.size();
            writer.write("RESUMEN: " + total + " error(es) encontrado(s) [lexicos: "
                    + erroresLexicos.size() + ", sintacticos: " + erroresSintacticos.size()
                    + ", semanticos: " + erroresSemanticos.size() + "]");
            writer.newLine();
            writer.newLine();
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
     * <strong>Nombre:</strong> escribirResultado
     *
     * <p><strong>Objetivo:</strong> Escribir el veredicto global de aceptación del programa fuente.</p>
     *
     * <p><strong>Entrada:</strong> Path archivo, Path fuente, boolean aceptado.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> escribirCodigoIntermedio
     *
     * <p><strong>Objetivo:</strong> Escribir instrucciones intermedias sin encabezado, una por línea.</p>
     *
     * <p><strong>Entrada:</strong> Path archivo, List&lt;Instruccion&gt; instrucciones.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> escribirSeccionErrores
     *
     * <p><strong>Objetivo:</strong> Escribir una sección de errores numerada, con su título y conteo;
     * si la lista está vacía, escribe en su lugar el mensaje alterno indicado.</p>
     *
     * <p><strong>Entrada:</strong> BufferedWriter writer, String titulo, String mensajeVacio, List&lt;String&gt; errores.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static void escribirSeccionErrores(BufferedWriter writer, String titulo,
                                               String mensajeVacio, List<String> errores) throws Exception {
        writer.write(titulo + " (" + errores.size() + ")");
        writer.newLine();
        if (errores.isEmpty()) {
            writer.write(mensajeVacio);
            writer.newLine();
            return;
        }
        for (int i = 0; i < errores.size(); i++) {
            writer.write((i + 1) + ". " + errores.get(i));
            writer.newLine();
        }
    }
}
