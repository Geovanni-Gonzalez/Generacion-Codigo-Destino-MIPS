package semantico;

import ast.TipoDato;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <strong>Objetivo:</strong> Entrada individual de la tabla de simbolos.
 *
 * <p><strong>Entradas:</strong> Simbolos, tipos, nodos y ubicaciones producidos por las fases previas.</p>
 *
 * <p><strong>Salidas:</strong> Estado semantico actualizado, simbolos resueltos o diagnosticos acumulados.</p>
 *
 * <p><strong>Restricciones:</strong> No debe generar codigo intermedio ni escribir reportes directamente.</p>
 */
public class Simbolo {
    private final String nombre;
    private final TipoDato tipo;
    private final CategoriaSimb categoria;
    private final int linea;
    private final List<TipoDato> tiposParametros;
    private final TipoDato tipoRetorno;
    private final Integer filasArreglo;
    private final Integer columnasArreglo;
    private boolean inicializado;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, TipoDato tipo, CategoriaSimb categoria, int linea</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de Simbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public Simbolo(String nombre, TipoDato tipo, CategoriaSimb categoria, int linea) {
        this(nombre, tipo, categoria, linea, Collections.emptyList(), null, false);
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, TipoDato tipo, CategoriaSimb categoria, int linea, boolean inicializado</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de Simbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public Simbolo(String nombre, TipoDato tipo, CategoriaSimb categoria, int linea, boolean inicializado) {
        this(nombre, tipo, categoria, linea, Collections.emptyList(), null, inicializado);
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, List<TipoDato> tiposParametros, TipoDato tipoRetorno, int linea</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de Simbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public Simbolo(String nombre, List<TipoDato> tiposParametros, TipoDato tipoRetorno, int linea) {
        this(nombre, tipoRetorno, CategoriaSimb.FUNCION, linea, tiposParametros, tipoRetorno, true);
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, TipoDato tipo, CategoriaSimb categoria, int linea, List<TipoDato> tiposParametros, TipoDato tipoRetorno, boolean inicializado</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de Simbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public Simbolo(String nombre, TipoDato tipo, CategoriaSimb categoria, int linea,
                   List<TipoDato> tiposParametros, TipoDato tipoRetorno, boolean inicializado) {
            this(nombre, tipo, categoria, linea, tiposParametros, tipoRetorno, inicializado, null, null);
    }

    /** Construye un simbolo de arreglo conservando sus dimensiones estaticas conocidas. */
    public Simbolo(String nombre, TipoDato tipo, CategoriaSimb categoria, int linea,
                   boolean inicializado, Integer filasArreglo, Integer columnasArreglo) {
        this(nombre, tipo, categoria, linea, Collections.emptyList(), null, inicializado,
                filasArreglo, columnasArreglo);
    }

    private Simbolo(String nombre, TipoDato tipo, CategoriaSimb categoria, int linea,
                    List<TipoDato> tiposParametros, TipoDato tipoRetorno, boolean inicializado,
                    Integer filasArreglo, Integer columnasArreglo) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.categoria = categoria;
        this.linea = linea;
        this.tiposParametros = new ArrayList<>(tiposParametros);
        this.tipoRetorno = tipoRetorno;
        this.filasArreglo = filasArreglo;
        this.columnasArreglo = columnasArreglo;
        this.inicializado = inicializado;
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
    public TipoDato getTipo() {
        return tipo;
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna CategoriaSimb.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public CategoriaSimb getCategoria() {
        return categoria;
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna int.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public int getLinea() {
        return linea;
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna List<TipoDato>.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public List<TipoDato> getTiposParametros() {
        return Collections.unmodifiableList(tiposParametros);
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
 * <p><strong>Entradas:</strong> Sin parametros.</p>
 * <p><strong>Salidas:</strong> Retorna Integer.</p>
 * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
 */
     public Integer getFilasArreglo() {
        return filasArreglo;
    }
/**
 * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
 * <p><strong>Entradas:</strong> Sin parametros.</p>
 * <p><strong>Salidas:</strong> Retorna Integer.</p>
 * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
 */ 
    public Integer getColumnasArreglo() {
        return columnasArreglo;
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
    public boolean isInicializado() {
        return inicializado;
    }

    /**
     * <strong>Objetivo:</strong> Actualiza el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> boolean inicializado</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void setInicializado(boolean inicializado) {
        this.inicializado = inicializado;
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> TipoDato tipoParametro</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void agregarTipoParametro(TipoDato tipoParametro) {
        tiposParametros.add(tipoParametro);
    }
}
