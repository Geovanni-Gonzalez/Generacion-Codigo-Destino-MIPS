package pipeline;

import intermedio.GeneradorCodigoIntermedio;
import intermedio.Instruccion;
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
 * <strong>Objetivo:</strong> Fachada reutilizable del compilador.
 *
 * <p><strong>Entradas:</strong> Archivo fuente, lexer, parser y artefactos generados durante la compilacion.</p>
 *
 * <p><strong>Salidas:</strong> Resultado de compilacion y estado de aceptacion del programa fuente.</p>
 *
 * <p><strong>Restricciones:</strong> Debe coordinar fases sin duplicar la escritura de reportes de salida.</p>
 */
public class Compilador {
    /**
     * <strong>Objetivo:</strong> Compila un archivo fuente y devuelve todos los artefactos en memoria. El codigo intermedio se genera solamente si no existen errores lexicos, sintacticos ni semanticos.
     *
     * <p><strong>Entradas:</strong> Path fuente</p>
     *
     * <p><strong>Salidas:</strong> Retorna ResultadoCompilacion.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public ResultadoCompilacion compilar(Path fuente) throws Exception {
        validarFuente(fuente);
        MiLexer lexerTokens = crearLexer(fuente);
        consumirTokens(lexerTokens);

        MiLexer lexerParser = crearLexer(fuente);
        lexerParser.setImprimirErrores(false);
        Parser parser = new Parser(lexerParser);
        boolean sintaxisCompleta = true;
        try {
            parser.parse();
        } catch (Exception ex) {
            sintaxisCompleta = false;
            parser.erroresSintacticos.add(ReportadorErrores.reportarSintactico(0, 0,
                    "error fatal del parser: " + ex.getMessage()));
        }
 
        boolean aceptado = sintaxisCompleta
                && lexerTokens.getErroresLexicos().isEmpty()
                && parser.getNumErrores() == 0
                && parser.tablaSimbolos.getErroresSemanticos().isEmpty();

        List<Instruccion> codigoIntermedio = aceptado && parser.ast != null
                ? new GeneradorCodigoIntermedio().generar(parser.ast)
                : Collections.emptyList();

        return new ResultadoCompilacion(fuente, lexerTokens, parser, sintaxisCompleta,
                aceptado, codigoIntermedio);
    }

    /** Valida el archivo antes de iniciar las fases lexica y sintactica. */
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
     * <strong>Objetivo:</strong> Abre el archivo fuente como UTF-8 y crea un lexer nuevo para una pasada.
     *
     * <p><strong>Entradas:</strong> Path fuente</p>
     *
     * <p><strong>Salidas:</strong> Retorna MiLexer.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private MiLexer crearLexer(Path fuente) throws Exception {
        Reader reader = Files.newBufferedReader(fuente, StandardCharsets.UTF_8);
        return new MiLexer(reader);
    }

    /**
     * <strong>Objetivo:</strong> Recorre una pasada lexica completa para registrar tokens y errores. El parser usa otro lexer limpio, por eso esta pasada se consume hasta EOF sin construir AST.
     *
     * <p><strong>Entradas:</strong> MiLexer lexer</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void consumirTokens(MiLexer lexer) throws Exception {
        Symbol token;
        do {
            token = lexer.next_token();
        } while (token.sym != sym.EOF);
    }
}
