package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <strong>Objetivo:</strong> Generador historico/simple de temporales, etiquetas e instrucciones AST.
 *
 * <p><strong>Entradas:</strong> Datos sintacticos reconocidos por el parser, posiciones de fuente y subnodos relacionados.</p>
 *
 * <p><strong>Salidas:</strong> Nodos, valores o metadatos consultables por las fases semantica e intermedia.</p>
 *
 * <p><strong>Restricciones:</strong> No debe ejecutar validaciones globales ni escribir archivos; solo conserva estructura y metadatos.</p>
 */
public final class GeneradorCodigo {
    /**
     * <strong>Objetivo:</strong> Mantener la unica instancia compartida del
     * generador historico de codigo.
     *
     * <p><strong>Entradas:</strong> Inicializacion estatica de la clase.</p>
     *
     * <p><strong>Salidas:</strong> Instancia reutilizable devuelta por
     * {@code getInstancia}.</p>
     *
     * <p><strong>Restricciones:</strong> Debe usarse como singleton y reiniciarse
     * explicitamente antes de reutilizar sus acumuladores.</p>
     */
    private static final GeneradorCodigo INSTANCIA = new GeneradorCodigo();

    private int contadorTemporales;
    private int contadorEtiquetas;
    private final List<Instruccion> instrucciones;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de GeneradorCodigo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private GeneradorCodigo() {
        instrucciones = new ArrayList<>();
    }
    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna GeneradorCodigo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public static GeneradorCodigo getInstancia() {
        return INSTANCIA;
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public String nuevoTemp() {
        return "_t" + contadorTemporales++;
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public String nuevaEtiqueta() {
        return "_L" + contadorEtiquetas++;
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Instruccion instruccion</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void emitir(Instruccion instruccion) {
        instrucciones.add(Objects.requireNonNull(instruccion, "instruccion"));
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna List<Instruccion>.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public List<Instruccion> getInstrucciones() {
        return Collections.unmodifiableList(instrucciones);
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reiniciar() {
        contadorTemporales = 0;
        contadorEtiquetas = 0;
        instrucciones.clear();
    }
}
