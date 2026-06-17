package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <strong>Objetivo:</strong> Declaracion completa de una funcion o del procedimiento principal.
 *
 * <p><strong>Entradas:</strong> Datos sintacticos reconocidos por el parser, posiciones de fuente y subnodos relacionados.</p>
 *
 * <p><strong>Salidas:</strong> Nodos, valores o metadatos consultables por las fases semantica e intermedia.</p>
 *
 * <p><strong>Restricciones:</strong> No debe ejecutar validaciones globales ni escribir archivos; solo conserva estructura y metadatos.</p>
 */
public class FuncionNodo extends Nodo {
    private final String nombre;
    private final TipoDato tipoRetorno;
    private final List<ParametroNodo> parametros;
    private final BloqueNodo cuerpo;
    private final boolean principal;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, String nombre, TipoDato tipoRetorno, List<ParametroNodo> parametros, BloqueNodo cuerpo, boolean principal</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada Nodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public FuncionNodo(int linea, int columna, String nombre, TipoDato tipoRetorno,
                       List<ParametroNodo> parametros, BloqueNodo cuerpo, boolean principal) {
        super(linea, columna, tipoRetorno);
        this.nombre = nombre;
        this.tipoRetorno = tipoRetorno;
        this.parametros = new ArrayList<>(parametros);
        this.cuerpo = cuerpo;
        this.principal = principal;
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
     * <p><strong>Salidas:</strong> Retorna TipoDato.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public TipoDato getTipoRetorno() {
        return tipoRetorno;
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna List<ParametroNodo>.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public List<ParametroNodo> getParametros() {
        return Collections.unmodifiableList(parametros);
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna BloqueNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public BloqueNodo getCuerpo() {
        return cuerpo;
    }

    /**
     * <strong>Objetivo:</strong> Consulta una condicion booleana del objeto.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna boolean.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public boolean isPrincipal() {
        return principal;
    }
}
