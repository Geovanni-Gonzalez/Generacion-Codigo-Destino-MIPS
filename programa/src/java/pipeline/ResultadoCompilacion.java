package pipeline;

import intermedio.Instruccion;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import lexico.MiLexer;
import sintactico.Parser;

/**
 * <strong>Objetivo:</strong> Resultado inmutable de una corrida del pipeline.
 *
 * <p><strong>Entradas:</strong> Archivo fuente, lexer, parser y artefactos generados durante la compilacion.</p>
 *
 * <p><strong>Salidas:</strong> Resultado de compilacion y estado de aceptacion del programa fuente.</p>
 *
 * <p><strong>Restricciones:</strong> Debe coordinar fases sin duplicar la escritura de reportes de salida.</p>
 */
public class ResultadoCompilacion {
    private final Path fuente;
    private final MiLexer lexerTokens;
    private final Parser parser;
    private final boolean sintaxisCompleta;
    private final boolean aceptado;
    private final List<Instruccion> codigoIntermedio;

    /**
     * <strong>Objetivo:</strong> Crea el paquete de salida de una compilacion.
     *
     * <p><strong>Entradas:</strong> Path fuente, MiLexer lexerTokens, Parser parser, boolean sintaxisCompleta, boolean aceptado, List<Instruccion> codigoIntermedio</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de ResultadoCompilacion.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public ResultadoCompilacion(Path fuente, MiLexer lexerTokens, Parser parser,
                                boolean sintaxisCompleta, boolean aceptado,
                                List<Instruccion> codigoIntermedio) {
        this.fuente = fuente;
        this.lexerTokens = lexerTokens;
        this.parser = parser;
        this.sintaxisCompleta = sintaxisCompleta;
        this.aceptado = aceptado;
        this.codigoIntermedio = codigoIntermedio;
    }

    /**
     * <strong>Objetivo:</strong> Devuelve la ruta del archivo fuente procesado.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna Path.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public Path getFuente() {
        return fuente;
    }

    /**
     * <strong>Objetivo:</strong> Devuelve el lexer que conserva el reporte completo de tokens.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna MiLexer.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public MiLexer getLexerTokens() {
        return lexerTokens;
    }

    /**
     * <strong>Objetivo:</strong> Devuelve el parser con AST, tabla de simbolos y errores sintacticos.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna Parser.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public Parser getParser() {
        return parser;
    }

    /**
     * <strong>Objetivo:</strong> Indica si el parser finalizo sin una excepcion irrecuperable.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna boolean.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public boolean isSintaxisCompleta() {
        return sintaxisCompleta;
    }

    /**
     * <strong>Objetivo:</strong> Indica si el fuente supero analisis lexico, sintactico y semantico.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna boolean.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public boolean isAceptado() {
        return aceptado;
    }

    /**
     * <strong>Objetivo:</strong> Devuelve una vista de solo lectura del codigo intermedio generado.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna List<Instruccion>.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public List<Instruccion> getCodigoIntermedio() {
        return Collections.unmodifiableList(codigoIntermedio);
    }
}
