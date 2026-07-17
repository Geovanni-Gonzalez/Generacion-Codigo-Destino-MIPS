package reporte;

import intermedio.Instruccion;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class EscritorReportes {
    private EscritorReportes() {
    }

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

    public static void escribirCodigoIntermedio(Path archivo, List<Instruccion> instrucciones) throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            for (Instruccion instruccion : instrucciones) {
                writer.write(instruccion.toString());
                writer.newLine();
            }
        }
    }

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
