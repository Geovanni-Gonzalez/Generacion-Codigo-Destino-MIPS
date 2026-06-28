package pipeline;

import intermedio.Instruccion;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lexico.MiLexer;
import sintactico.Parser;

/**
 * Nombre: ResultadoCompilacion
 *
 * Objetivo: Coordinar fases del compilador o transportar resultados entre ellas.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
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
        this.codigoIntermedio = new ArrayList<>(codigoIntermedio);
        this.codigoMIPS = new ArrayList<>(codigoMIPS);
    }

    /**
     * Nombre: getFuente
     *
     * Objetivo: Obtener el valor de Fuente almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo Path.
     *
     * Restricciones: Ninguna.
     */
    public Path getFuente() {
        return fuente;
    }

    /**
     * Nombre: getLexerTokens
     *
     * Objetivo: Obtener el valor de LexerTokens almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo MiLexer.
     *
     * Restricciones: Ninguna.
     */
    public MiLexer getLexerTokens() {
        return lexerTokens;
    }

    /**
     * Nombre: getParser
     *
     * Objetivo: Obtener el valor de Parser almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo Parser.
     *
     * Restricciones: Ninguna.
     */
    public Parser getParser() {
        return parser;
    }

    /**
     * Nombre: isSintaxisCompleta
     *
     * Objetivo: Indicar si se cumple la condicion SintaxisCompleta.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    public boolean isSintaxisCompleta() {
        return sintaxisCompleta;
    }

    /**
     * Nombre: isAceptado
     *
     * Objetivo: Indicar si se cumple la condicion Aceptado.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    public boolean isAceptado() {
        return aceptado;
    }

    /**
     * Nombre: getCodigoIntermedio
     *
     * Objetivo: Obtener el valor de CodigoIntermedio almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo List<Instruccion>.
     *
     * Restricciones: Ninguna.
     */
    public List<Instruccion> getCodigoIntermedio() {
        return Collections.unmodifiableList(codigoIntermedio);
    }

    /**
     * Nombre: getCodigoMIPS
     *
     * Objetivo: Obtener el valor de CodigoMIPS almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo List<String>.
     *
     * Restricciones: Ninguna.
     */
    public List<String> getCodigoMIPS() {
        return Collections.unmodifiableList(codigoMIPS);
    }
}
