package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GeneradorCodigo {
    private static final GeneradorCodigo INSTANCIA = new GeneradorCodigo();

    private int contadorTemporales;
    private int contadorEtiquetas;
    private final List<Instruccion> instrucciones;
    private GeneradorCodigo() {
        instrucciones = new ArrayList<>();
    }
    public static GeneradorCodigo getInstancia() {
        return INSTANCIA;
    }

    public String nuevoTemp() {
        return "_t" + contadorTemporales++;
    }

    public String nuevaEtiqueta() {
        return "_L" + contadorEtiquetas++;
    }

    public void emitir(Instruccion instruccion) {
        instrucciones.add(Objects.requireNonNull(instruccion, "instruccion"));
    }

    public List<Instruccion> getInstrucciones() {
        return Collections.unmodifiableList(instrucciones);
    }

    public void reiniciar() {
        contadorTemporales = 0;
        contadorEtiquetas = 0;
        instrucciones.clear();
    }
}
