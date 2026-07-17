package pipeline;

import intermedio.Instruccion;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lexico.MiLexer;
import sintactico.Parser;

public class ResultadoCompilacion {
    private final Path fuente;
    private final MiLexer lexerTokens;
    private final Parser parser;
    private final boolean sintaxisCompleta;
    private final boolean aceptado;
    private final List<Instruccion> codigoIntermedio;
    private final List<String> codigoMIPS;

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

    public Path getFuente() {
        return fuente;
    }

    public MiLexer getLexerTokens() {
        return lexerTokens;
    }

    public Parser getParser() {
        return parser;
    }

    public boolean isSintaxisCompleta() {
        return sintaxisCompleta;
    }

    public boolean isAceptado() {
        return aceptado;
    }

    public List<Instruccion> getCodigoIntermedio() {
        return Collections.unmodifiableList(codigoIntermedio);
    }

    public List<String> getCodigoMIPS() {
        return Collections.unmodifiableList(codigoMIPS);
    }
}
