package pipeline;

import intermedio.GeneradorCodigoIntermedio;
import intermedio.Instruccion;
import intermedio.OptimizadorIR;
import mips.GeneradorMIPS;
import java.io.Reader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java_cup.runtime.Symbol;
import lexico.MiLexer;
import reporte.ReportadorErrores;
import sintactico.Parser;
import sintactico.sym;

/**
 * <strong>Nombre:</strong> Compilador
 *
 * <p><strong>Objetivo:</strong> Coordinar todas las fases del compilador en orden, desde el archivo
 * fuente hasta el código MIPS: validar el archivo, recorrer el lexer, ejecutar el parser (que hace
 * el análisis sintáctico y, dirigido por sintaxis, el semántico) y, solo si no hubo errores, generar
 * el código intermedio y el MIPS.</p>
 *
 * <p><strong>Entrada:</strong> La ruta del archivo fuente a compilar.</p>
 *
 * <p><strong>Salida:</strong> Un {@link ResultadoCompilacion} con todos los artefactos en memoria.</p>
 *
 * <p><strong>Restricciones:</strong> No escribe archivos; de eso se encarga la capa de reportes.</p>
 */
public class Compilador {
    /**
     * <strong>Nombre:</strong> compilar
     *
     * <p><strong>Objetivo:</strong> Compilar un archivo fuente y devolver todos sus artefactos en memoria.
     * El código intermedio y el MIPS solo se generan si no hay errores léxicos, sintácticos ni semánticos.</p>
     *
     * <p><strong>Entrada:</strong> Path fuente.</p>
     *
     * <p><strong>Salida:</strong> ResultadoCompilacion con tokens, AST, errores y código generado.</p>
     *
     * <p><strong>Restricciones:</strong> Lanza excepción si el archivo no es válido o legible.</p>
     */
    public ResultadoCompilacion compilar(Path fuente) throws Exception {
        validarFuente(fuente);

        MiLexer lexerTokens;
        Parser parser;
        boolean sintaxisCompleta = true;
        try (Reader reader = abrirFuente(fuente)) {
            lexerTokens = new MiLexer(reader);
            parser = new Parser(lexerTokens);
            try {
                parser.parse();
            } catch (Exception ex) {
                sintaxisCompleta = false;
                parser.erroresSintacticos.add(ReportadorErrores.reportarSintactico(0, 0,
                        "error fatal del parser: " + ex.getMessage()));
                // El parser se detuvo antes del fin de archivo: consumir el resto del flujo
                // léxico para que el reporte de tokens y los errores léxicos queden completos.
                consumirTokens(lexerTokens);
            }
        }

        boolean aceptado = sintaxisCompleta
                && lexerTokens.getErroresLexicos().isEmpty()
                && parser.getNumErrores() == 0
                && parser.tablaSimbolos.getErroresSemanticos().isEmpty();

        List<Instruccion> codigoIntermedio = Collections.emptyList();
        List<String> codigoMIPS = Collections.emptyList();
        if (aceptado && parser.ast != null) {
            try {
                codigoIntermedio = new GeneradorCodigoIntermedio().generar(parser.ast);
                codigoIntermedio = new OptimizadorIR().optimizar(codigoIntermedio);
                codigoMIPS = new GeneradorMIPS().generarCodigo(codigoIntermedio);
            } catch (CompiladorInternoException ex) {
                aceptado = false;
                int linea = ex.tieneUbicacion() ? ex.getLinea() : 1;
                int columna = ex.tieneUbicacion() ? ex.getColumna() : 1;
                parser.tablaSimbolos.getErroresSemanticos().add(ReportadorErrores.semantico(linea, columna,
                        "error interno de generacion: " + ex.getMessage()));
                codigoIntermedio = Collections.emptyList();
                codigoMIPS = Collections.emptyList();
            }
        }

        return new ResultadoCompilacion(fuente, lexerTokens, parser, sintaxisCompleta,
                aceptado, codigoIntermedio, codigoMIPS);
    }

    /**
     * <strong>Nombre:</strong> validarFuente
     *
     * <p><strong>Objetivo:</strong> Comprobar que el archivo exista, sea regular y se pueda leer.</p>
     *
     * <p><strong>Entrada:</strong> Path fuente.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Lanza IOException si alguna comprobación falla.</p>
     */
    private void validarFuente(Path fuente) throws IOException {
        if (fuente == null) {
            throw new IOException("No se proporciono una ruta de archivo fuente.");
        }
        if (!Files.exists(fuente)) {
            throw new IOException("No existe el archivo fuente: " + fuente);
        }
        if (!Files.isRegularFile(fuente)) {
            throw new IOException("La ruta no corresponde a un archivo regular: " + fuente);
        }
        if (!Files.isReadable(fuente)) {
            throw new IOException("No se puede leer el archivo fuente: " + fuente);
        }
    }

    /**
     * <strong>Nombre:</strong> abrirFuente
     *
     * <p><strong>Objetivo:</strong> Abrir el archivo como UTF-8 para una pasada del lexer.</p>
     *
     * <p><strong>Entrada:</strong> Path fuente.</p>
     *
     * <p><strong>Salida:</strong> Reader listo para recorrer el archivo.</p>
     *
     * <p><strong>Restricciones:</strong> El llamador debe cerrarlo.</p>
     */
    private Reader abrirFuente(Path fuente) throws IOException {
        return Files.newBufferedReader(fuente, StandardCharsets.UTF_8);
    }

    /**
     * <strong>Nombre:</strong> consumirTokens
     *
     * <p><strong>Objetivo:</strong> Drenar el resto del flujo léxico hasta el fin de archivo. Como el
     * lexer registra cada token y error léxico al ser leído, el parser ya recolecta los tokens conforme
     * avanza; esta pasada solo completa el reporte cuando el parser se detiene antes del EOF por un
     * error fatal.</p>
     *
     * <p><strong>Entrada:</strong> MiLexer lexer (el mismo que usó el parser).</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void consumirTokens(MiLexer lexer) throws Exception {
        Symbol token;
        do {
            token = lexer.next_token();
        } while (token.sym != sym.EOF);
    }
}
