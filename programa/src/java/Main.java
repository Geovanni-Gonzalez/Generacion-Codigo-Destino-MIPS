import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import pipeline.Compilador;
import pipeline.ResultadoCompilacion;
import reporte.EscritorCodigo;
import reporte.EscritorMIPS;
import reporte.EscritorReportes;

/**
 * <strong>Nombre:</strong> Main
 *
 * <p><strong>Objetivo:</strong> Ser el punto de entrada del compilador desde la línea de comandos:
 * ejecuta el {@link Compilador} sobre el archivo recibido y escribe en disco los reportes, el código
 * intermedio ({@code .ic}) y el código MIPS ({@code .asm}).</p>
 *
 * <p><strong>Entrada:</strong> Argumentos de consola: archivo fuente y, opcionalmente, directorio de salida.</p>
 *
 * <p><strong>Salida:</strong> Archivos de reporte y de código en el directorio de salida; mensajes en consola.</p>
 *
 * <p><strong>Restricciones:</strong> Valida el archivo antes de compilar y maneja rutas inválidas.</p>
 */
public class Main {
    /**
     * <strong>Nombre:</strong> main
     *
     * <p><strong>Objetivo:</strong> Ejecutar el compilador sobre el archivo recibido y persistir sus resultados.</p>
     *
     * <p><strong>Entrada:</strong> String[] args; {@code args[0]} = archivo fuente, {@code args[1]} (opcional) = directorio de salida.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor; produce archivos y mensajes de consola.</p>
     *
     * <p><strong>Restricciones:</strong> Si falta el argumento o la ruta es inválida, muestra un mensaje y termina.</p>
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java -jar target/proyecto-compiladores-1.0-SNAPSHOT.jar <archivo_fuente> [directorio_salida]");
            return;
        }

        Path fuente;
        try {
            fuente = Paths.get(args[0]).toAbsolutePath().normalize();
        } catch (InvalidPathException ex) {
            System.err.println("La ruta del archivo fuente no es valida: " + ex.getInput());
            return;
        }

        if (!Files.exists(fuente)) {
            System.err.println("No existe el archivo fuente: " + fuente);
            return;
        }
        if (!Files.isRegularFile(fuente)) {
            System.err.println("La ruta no corresponde a un archivo regular: " + fuente);
            return;
        }
        if (!Files.isReadable(fuente)) {
            System.err.println("No se puede leer el archivo fuente: " + fuente);
            return;
        }

        try {
            Path salida = args.length > 1
                    ? Paths.get(args[1]).toAbsolutePath().normalize()
                    : Paths.get("salida").toAbsolutePath().normalize();
            Files.createDirectories(salida);

            ResultadoCompilacion resultado = new Compilador().compilar(fuente);

            EscritorReportes.escribirTokens(salida.resolve("tokens_report.txt"),
                    resultado.getLexerTokens().getTokens());
            EscritorReportes.escribirTablaSimbolos(salida.resolve("tabla_simbolos.txt"),
                    resultado.getLexerTokens().getTokens());
            EscritorReportes.escribirErrores(salida.resolve("errores_report.txt"),
                    resultado.getLexerTokens().getErroresLexicos(),
                    resultado.getParser().erroresSintacticos,
                    resultado.getParser().tablaSimbolos.getErroresSemanticos());
            EscritorReportes.escribirResultado(salida.resolve("resultado_sintactico.txt"),
                    fuente, resultado.isAceptado());
            Path codigoIntermedio = EscritorCodigo.escribir(salida, resultado);
            Path codigoMIPS = EscritorMIPS.escribir(salida, resultado);

            System.out.println("Archivo analizado: " + fuente);
            System.out.println(resultado.isAceptado()
                    ? "El archivo fuente puede ser generado por la gramatica."
                    : "El archivo fuente NO puede ser generado por la gramatica.");
            System.out.println("Reporte de tokens: " + salida.resolve("tokens_report.txt"));
            System.out.println("Tabla de simbolos: " + salida.resolve("tabla_simbolos.txt"));
            System.out.println("Reporte de errores: " + salida.resolve("errores_report.txt"));
            System.out.println("Resultado sintactico: " + salida.resolve("resultado_sintactico.txt"));
            System.out.println(resultado.isAceptado()
                    ? "Codigo intermedio: " + codigoIntermedio
                    : "Codigo intermedio no generado por errores de analisis.");
            System.out.println(resultado.isAceptado()
                    ? "Codigo MIPS: " + codigoMIPS
                    : "Codigo MIPS no generado por errores de analisis.");
        } catch (InvalidPathException ex) {
            System.err.println("La ruta del directorio de salida no es valida: " + ex.getInput());
        } catch (Exception ex) {
            System.err.println("No se pudo procesar el archivo fuente: " + ex.getMessage());
        }
    }
}
