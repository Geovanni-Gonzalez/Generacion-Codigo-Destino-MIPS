package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <strong>Objetivo:</strong> Raiz del AST: contiene todas las funciones y el procedimiento principal.
 *
 * <p><strong>Entradas:</strong> Datos sintacticos reconocidos por el parser, posiciones de fuente y subnodos relacionados.</p>
 *
 * <p><strong>Salidas:</strong> Nodos, valores o metadatos consultables por las fases semantica e intermedia.</p>
 *
 * <p><strong>Restricciones:</strong> No debe ejecutar validaciones globales ni escribir archivos; solo conserva estructura y metadatos.</p>
 */
public class ProgramaNodo extends Nodo {
    private final List<FuncionNodo> funciones;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, List<FuncionNodo> funciones</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de ProgramaNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public ProgramaNodo(int linea, int columna, List<FuncionNodo> funciones) {
        super(linea, columna, TipoDato.EMPTY);
        this.funciones = new ArrayList<>(funciones);
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna List<FuncionNodo>.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public List<FuncionNodo> getFunciones() {
        return Collections.unmodifiableList(funciones);
    }
}
