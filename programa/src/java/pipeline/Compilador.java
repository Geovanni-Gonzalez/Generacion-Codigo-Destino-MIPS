package pipeline;

import intermedio.GeneradorCodigoIntermedio;
import intermedio.Instruccion;
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
        try (Reader reader = abrirFuente(fuente)) {
            lexerTokens = new MiLexer(reader);
            consumirTokens(lexerTokens);
        }

        Parser parser;
        boolean sintaxisCompleta = true;
        try (Reader reader = abrirFuente(fuente)) {
            MiLexer lexerParser = new MiLexer(reader);
            lexerParser.setImprimirErrores(false);
            parser = new Parser(lexerParser);
            try {
                parser.parse();
            } catch (Exception ex) {
                sintaxisCompleta = false;
                parser.erroresSintacticos.add(ReportadorErrores.reportarSintactico(0, 0,
                        "error fatal del parser: " + ex.getMessage()));
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
                codigoMIPS = new GeneradorMIPS().generarCodigo(codigoIntermedio);
            } catch (CompiladorInternoException ex) {
                aceptado = false;
                parser.tablaSimbolos.getErroresSemanticos().add(ReportadorErrores.semantico(1, 1,
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
     * <p><strong>Objetivo:</strong> Recorrer una pasada léxica completa hasta el fin de archivo para
     * registrar tokens y errores léxicos. El parser usa otro lexer limpio, por eso esta pasada se
     * consume aparte sin construir el AST.</p>
     *
     * <p><strong>Entrada:</strong> MiLexer lexer.</p>
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
