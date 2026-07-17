import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import pipeline.Compilador;
import pipeline.ResultadoCompilacion;
import reporte.EscritorCodigo;
import reporte.EscritorMIPS;
import reporte.EscritorReportes;

public class Main {
    private static final String DIR_SALIDA_POR_DEFECTO = "salida";
    private static final String REPORTE_TOKENS = "tokens_report.txt";
    private static final String REPORTE_TABLA_SIMBOLOS = "tabla_simbolos.txt";
    private static final String REPORTE_ERRORES = "errores_report.txt";
    private static final String REPORTE_SINTACTICO = "resultado_sintactico.txt";

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

        try {
            Path salida = args.length > 1
                    ? Paths.get(args[1]).toAbsolutePath().normalize()
                    : Paths.get(DIR_SALIDA_POR_DEFECTO).toAbsolutePath().normalize();
            Files.createDirectories(salida);

            ResultadoCompilacion resultado = new Compilador().compilar(fuente);

            EscritorReportes.escribirTokens(salida.resolve(REPORTE_TOKENS),
                    resultado.getLexerTokens().getTokens());
            EscritorReportes.escribirTablaSimbolos(salida.resolve(REPORTE_TABLA_SIMBOLOS),
                    resultado.getLexerTokens().getTokens());
            EscritorReportes.escribirErrores(salida.resolve(REPORTE_ERRORES),
                    resultado.getLexerTokens().getErroresLexicos(),
                    resultado.getParser().erroresSintacticos,
                    resultado.getParser().tablaSimbolos.getErroresSemanticos());
            EscritorReportes.escribirResultado(salida.resolve(REPORTE_SINTACTICO),
                    fuente, resultado.isAceptado());
            Path codigoIntermedio = EscritorCodigo.escribir(salida, resultado);
            Path codigoMIPS = EscritorMIPS.escribir(salida, resultado);

            System.out.println("Archivo analizado: " + fuente);
            System.out.println(resultado.isAceptado()
                    ? "El archivo fuente puede ser generado por la gramatica."
                    : "El archivo fuente NO puede ser generado por la gramatica.");
            System.out.println("Reporte de tokens: " + salida.resolve(REPORTE_TOKENS));
            System.out.println("Tabla de simbolos: " + salida.resolve(REPORTE_TABLA_SIMBOLOS));
            System.out.println("Reporte de errores: " + salida.resolve(REPORTE_ERRORES));
            System.out.println("Resultado sintactico: " + salida.resolve(REPORTE_SINTACTICO));
            System.out.println(resultado.isAceptado()
                    ? "Codigo intermedio: " + codigoIntermedio
                    : "Codigo intermedio no generado por errores de analisis.");
            System.out.println(resultado.isAceptado()
                    ? "Codigo MIPS: " + codigoMIPS
                    : "Codigo MIPS no generado por errores de analisis.");
        } catch (InvalidPathException ex) {
            System.err.println("La ruta del directorio de salida no es valida: " + ex.getInput());
        } catch (IOException ex) {
            System.err.println("Error de entrada/salida: " + ex.getMessage());
        } catch (RuntimeException ex) {
            System.err.println("Error interno durante la compilacion: " + ex.getMessage());
            if (Boolean.getBoolean("compilador.debug")) {
                ex.printStackTrace(System.err);
            }
        } catch (Exception ex) {
            System.err.println("No se pudo procesar el archivo fuente: " + ex.getMessage());
            if (Boolean.getBoolean("compilador.debug")) {
                ex.printStackTrace(System.err);
            }
        }
    }
}
