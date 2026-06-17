package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <strong>Objetivo:</strong> Expresion que representa una invocacion  con argumentos.
 *
 * <p><strong>Entradas:</strong> Datos sintacticos reconocidos por el parser, posiciones de fuente y subnodos relacionados.</p>
 *
 * <p><strong>Salidas:</strong> Nodos, valores o metadatos consultables por las fases semantica e intermedia.</p>
 *
 * <p><strong>Restricciones:</strong> No debe ejecutar validaciones globales ni escribir archivos; solo conserva estructura y metadatos.</p>
 */
public class LlamadaFuncionNodo extends ExpresionNodo {
    private final String nombre;
    private final List<ExpresionNodo> argumentos;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, String nombre, List<ExpresionNodo> argumentos</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de LlamadaFuncionNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public LlamadaFuncionNodo(int linea, int columna, String nombre, List<ExpresionNodo> argumentos) {
        super(linea, columna);
        this.nombre = nombre;
        this.argumentos = new ArrayList<>(argumentos);
    }
    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna List<ExpresionNodo>.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public List<ExpresionNodo> getArgumentos() {
        return Collections.unmodifiableList(argumentos);
    }
}
