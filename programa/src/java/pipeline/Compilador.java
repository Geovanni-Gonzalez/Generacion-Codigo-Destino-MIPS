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

public class Compilador {
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

    private Reader abrirFuente(Path fuente) throws IOException {
        return Files.newBufferedReader(fuente, StandardCharsets.UTF_8);
    }

    private void consumirTokens(MiLexer lexer) throws Exception {
        Symbol token;
        do {
            token = lexer.next_token();
        } while (token.sym != sym.EOF);
    }
}
