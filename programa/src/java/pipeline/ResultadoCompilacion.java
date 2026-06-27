package pipeline;

import intermedio.Instruccion;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import lexico.MiLexer;
import sintactico.Parser;

/**
 * <strong>Nombre:</strong> ResultadoCompilacion
 *
 * <p><strong>Objetivo:</strong> Empaquetar de forma inmutable todo lo que produce una corrida del
 * compilador: la ruta fuente, el lexer y el parser usados, si fue aceptado y el código intermedio y MIPS.</p>
 *
 * <p><strong>Entrada:</strong> Los artefactos generados por el {@link Compilador}.</p>
 *
 * <p><strong>Salida:</strong> Objeto consultado por los escritores de reportes y de código.</p>
 *
 * <p><strong>Restricciones:</strong> Es inmutable; expone las listas como solo lectura.</p>
 */
public class ResultadoCompilacion {
    private final Path fuente;
    private final MiLexer lexerTokens;
    private final Parser parser;
    private final boolean sintaxisCompleta;
    private final boolean aceptado;
    private final List<Instruccion> codigoIntermedio;
    private final List<String> codigoMIPS;

    /**
     * <strong>Nombre:</strong> ResultadoCompilacion
     *
     * <p><strong>Objetivo:</strong> Reunir todos los artefactos de una compilación en un único resultado.</p>
     *
     * <p><strong>Entrada:</strong> Path fuente, MiLexer lexerTokens, Parser parser, boolean sintaxisCompleta, boolean aceptado, List&lt;Instruccion&gt; codigoIntermedio, List&lt;String&gt; codigoMIPS.</p>
     *
     * <p><strong>Salida:</strong> Nueva instancia de ResultadoCompilacion.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public ResultadoCompilacion(Path fuente, MiLexer lexerTokens, Parser parser,
                                boolean sintaxisCompleta, boolean aceptado,
                                List<Instruccion> codigoIntermedio, List<String> codigoMIPS) {
        this.fuente = fuente;
        this.lexerTokens = lexerTokens;
        this.parser = parser;
        this.sintaxisCompleta = sintaxisCompleta;
        this.aceptado = aceptado;
        this.codigoIntermedio = codigoIntermedio;
        this.codigoMIPS = codigoMIPS;
    }

    /**
     * <strong>Nombre:</strong> getFuente
     *
     * <p><strong>Objetivo:</strong> Devolver la ruta del archivo fuente procesado.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> Path del archivo fuente.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public Path getFuente() {
        return fuente;
    }

    /**
     * <strong>Nombre:</strong> getLexerTokens
     *
     * <p><strong>Objetivo:</strong> Devolver el lexer que conserva el reporte de tokens y los errores léxicos.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> MiLexer de la pasada de tokens.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public MiLexer getLexerTokens() {
        return lexerTokens;
    }

    /**
     * <strong>Nombre:</strong> getParser
     *
     * <p><strong>Objetivo:</strong> Devolver el parser con el AST, la tabla de símbolos y los errores sintácticos.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> Parser usado en la compilación.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public Parser getParser() {
        return parser;
    }

    /**
     * <strong>Nombre:</strong> isSintaxisCompleta
     *
     * <p><strong>Objetivo:</strong> Indicar si el parser terminó sin una excepción irrecuperable.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si el parseo concluyó.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public boolean isSintaxisCompleta() {
        return sintaxisCompleta;
    }

    /**
     * <strong>Nombre:</strong> isAceptado
     *
     * <p><strong>Objetivo:</strong> Indicar si el fuente superó los análisis léxico, sintáctico y semántico.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si no hubo errores.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public boolean isAceptado() {
        return aceptado;
    }

    /**
     * <strong>Nombre:</strong> getCodigoIntermedio
     *
     * <p><strong>Objetivo:</strong> Devolver el código intermedio generado.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> List&lt;Instruccion&gt; no modificable (vacía si no fue aceptado).</p>
     *
     * <p><strong>Restricciones:</strong> La lista no se puede modificar.</p>
     */
    public List<Instruccion> getCodigoIntermedio() {
        return Collections.unmodifiableList(codigoIntermedio);
    }

    /**
     * <strong>Nombre:</strong> getCodigoMIPS
     *
     * <p><strong>Objetivo:</strong> Devolver el programa MIPS generado.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> List&lt;String&gt; no modificable (vacía si no fue aceptado).</p>
     *
     * <p><strong>Restricciones:</strong> La lista no se puede modificar.</p>
     */
    public List<String> getCodigoMIPS() {
        return Collections.unmodifiableList(codigoMIPS);
    }
}
