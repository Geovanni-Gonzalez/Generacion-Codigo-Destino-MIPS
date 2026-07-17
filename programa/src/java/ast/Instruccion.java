package ast;

import java.util.Objects;

public class Instruccion {
    private final String texto;
    public Instruccion(String texto) {
        this.texto = Objects.requireNonNull(texto, "texto");
    }

    public String getTexto() {
        return texto;
    }

    /** Usa el texto como representacion imprimible de la instruccion. */
    @Override
    public String toString() {
        return texto;
    }
}
