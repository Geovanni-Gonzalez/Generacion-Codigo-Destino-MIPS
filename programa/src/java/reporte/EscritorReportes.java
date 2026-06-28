package reporte;

import intermedio.Instruccion;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Nombre: EscritorReportes
 *
 * Objetivo: Formatear o escribir reportes y artefactos generados por el compilador.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public final class EscritorReportes {
    /**
     * Nombre: EscritorReportes
     *
     * Objetivo: Inicializar una instancia de EscritorReportes con los datos requeridos.
     *
     * Entrada: Ninguna.
     *
     * Salida: Nueva instancia de EscritorReportes.
     *
     * Restricciones: Uso interno de la clase.
     */
    private EscritorReportes() {
    }

    /**
     * Nombre: escribirTokens
     *
     * Objetivo: Indicar si se cumple la condicion cribirTokens.
     *
     * Entrada: Path archivo; List<TokenInfo> tokens.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Puede propagar Exception.
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
     * Nombre: escribirTablaSimbolos
     *
     * Objetivo: Indicar si se cumple la condicion cribirTablaSimbolos.
     *
     * Entrada: Path archivo; List<TokenInfo> tokens.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Puede propagar Exception.
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
     * Nombre: escribirResultado
     *
     * Objetivo: Indicar si se cumple la condicion cribirResultado.
     *
     * Entrada: Path archivo; Path fuente; boolean aceptado.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Puede propagar Exception.
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
     * Nombre: escribirCodigoIntermedio
     *
     * Objetivo: Indicar si se cumple la condicion cribirCodigoIntermedio.
     *
     * Entrada: Path archivo; List<Instruccion> instrucciones.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Puede propagar Exception.
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
