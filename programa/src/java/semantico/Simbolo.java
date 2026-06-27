package semantico;

import ast.TipoDato;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <strong>Nombre:</strong> Simbolo
 *
 * <p><strong>Objetivo:</strong> Representar una entrada de la tabla de símbolos: una variable, un
 * arreglo, un parámetro o una función, con su tipo, categoría y datos asociados.</p>
 *
 * <p><strong>Entrada:</strong> Nombre, tipo, categoría, línea y, según el caso, parámetros,
 * tipo de retorno o dimensiones.</p>
 *
 * <p><strong>Salida:</strong> Objeto consultable por el análisis semántico.</p>
 *
 * <p><strong>Restricciones:</strong> Solo el estado de inicialización y los parámetros pueden cambiar tras crearse.</p>
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
     * <strong>Nombre:</strong> Simbolo
     *
     * <p><strong>Objetivo:</strong> Crear un símbolo simple (variable o parámetro) sin inicializar.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, TipoDato tipo, CategoriaSimb categoria, int linea.</p>
     *
     * <p><strong>Salida:</strong> Nueva instancia de Simbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public Simbolo(String nombre, TipoDato tipo, CategoriaSimb categoria, int linea) {
        this(nombre, tipo, categoria, linea, Collections.emptyList(), null, false);
    }

    /**
     * <strong>Nombre:</strong> Simbolo
     *
     * <p><strong>Objetivo:</strong> Crear un símbolo simple indicando si ya está inicializado.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, TipoDato tipo, CategoriaSimb categoria, int linea, boolean inicializado.</p>
     *
     * <p><strong>Salida:</strong> Nueva instancia de Simbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public Simbolo(String nombre, TipoDato tipo, CategoriaSimb categoria, int linea, boolean inicializado) {
        this(nombre, tipo, categoria, linea, Collections.emptyList(), null, inicializado);
    }

    /**
     * <strong>Nombre:</strong> Simbolo
     *
     * <p><strong>Objetivo:</strong> Crear un símbolo de función con sus tipos de parámetros y su tipo de retorno.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, List&lt;TipoDato&gt; tiposParametros, TipoDato tipoRetorno, int linea.</p>
     *
     * <p><strong>Salida:</strong> Nueva instancia de Simbolo (categoría FUNCION).</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public Simbolo(String nombre, List<TipoDato> tiposParametros, TipoDato tipoRetorno, int linea) {
        this(nombre, tipoRetorno, CategoriaSimb.FUNCION, linea, tiposParametros, tipoRetorno, true);
    }

    /**
     * <strong>Nombre:</strong> Simbolo
     *
     * <p><strong>Objetivo:</strong> Crear un símbolo indicando explícitamente parámetros, retorno e inicialización.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, TipoDato tipo, CategoriaSimb categoria, int linea, List&lt;TipoDato&gt; tiposParametros, TipoDato tipoRetorno, boolean inicializado.</p>
     *
     * <p><strong>Salida:</strong> Nueva instancia de Simbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public Simbolo(String nombre, TipoDato tipo, CategoriaSimb categoria, int linea,
                   List<TipoDato> tiposParametros, TipoDato tipoRetorno, boolean inicializado) {
        this(nombre, tipo, categoria, linea, tiposParametros, tipoRetorno, inicializado, null, null);
    }

    /**
     * <strong>Nombre:</strong> Simbolo
     *
     * <p><strong>Objetivo:</strong> Crear un símbolo de arreglo conservando sus dimensiones estáticas conocidas.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, TipoDato tipo, CategoriaSimb categoria, int linea, boolean inicializado, Integer filasArreglo, Integer columnasArreglo.</p>
     *
     * <p><strong>Salida:</strong> Nueva instancia de Simbolo (con dimensiones).</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public Simbolo(String nombre, TipoDato tipo, CategoriaSimb categoria, int linea,
                   boolean inicializado, Integer filasArreglo, Integer columnasArreglo) {
        this(nombre, tipo, categoria, linea, Collections.emptyList(), null, inicializado,
                filasArreglo, columnasArreglo);
    }

    /**
     * <strong>Nombre:</strong> Simbolo
     *
     * <p><strong>Objetivo:</strong> Constructor base que reúne todos los campos posibles de un símbolo.</p>
     *
     * <p><strong>Entrada:</strong> Nombre, tipo, categoría, línea, tipos de parámetros, tipo de retorno, inicialización y dimensiones.</p>
     *
     * <p><strong>Salida:</strong> Nueva instancia de Simbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Es privado; lo usan los demás constructores.</p>
     */
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
     * <strong>Nombre:</strong> getNombre
     *
     * <p><strong>Objetivo:</strong> Devolver el nombre del símbolo.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> String con el nombre.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * <strong>Nombre:</strong> getTipo
     *
     * <p><strong>Objetivo:</strong> Devolver el tipo de dato del símbolo (para funciones, su tipo de retorno).</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> TipoDato del símbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public TipoDato getTipo() {
        return tipo;
    }

    /**
     * <strong>Nombre:</strong> getCategoria
     *
     * <p><strong>Objetivo:</strong> Devolver la categoría del símbolo: variable, arreglo, parámetro o función.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> CategoriaSimb del símbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public CategoriaSimb getCategoria() {
        return categoria;
    }

    /**
     * <strong>Nombre:</strong> getLinea
     *
     * <p><strong>Objetivo:</strong> Devolver la línea donde se declaró el símbolo.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> int con el número de línea.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public int getLinea() {
        return linea;
    }

    /**
     * <strong>Nombre:</strong> getTiposParametros
     *
     * <p><strong>Objetivo:</strong> Devolver los tipos de los parámetros (solo en funciones).</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> List&lt;TipoDato&gt; no modificable.</p>
     *
     * <p><strong>Restricciones:</strong> La lista no se puede modificar.</p>
     */
    public List<TipoDato> getTiposParametros() {
        return Collections.unmodifiableList(tiposParametros);
    }

    /**
     * <strong>Nombre:</strong> getTipoRetorno
     *
     * <p><strong>Objetivo:</strong> Devolver el tipo de retorno si el símbolo es una función.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> TipoDato del retorno, o {@code null} si no es función.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public TipoDato getTipoRetorno() {
        return tipoRetorno;
    }

    /**
     * <strong>Nombre:</strong> getFilasArreglo
     *
     * <p><strong>Objetivo:</strong> Devolver el número de filas si es un arreglo.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> Integer con las filas, o {@code null} si no es arreglo.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public Integer getFilasArreglo() {
        return filasArreglo;
    }

    /**
     * <strong>Nombre:</strong> getColumnasArreglo
     *
     * <p><strong>Objetivo:</strong> Devolver el número de columnas si es un arreglo.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> Integer con las columnas, o {@code null} si no es arreglo.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public Integer getColumnasArreglo() {
        return columnasArreglo;
    }

    /**
     * <strong>Nombre:</strong> isInicializado
     *
     * <p><strong>Objetivo:</strong> Indicar si al símbolo ya se le asignó un valor.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si está inicializado.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public boolean isInicializado() {
        return inicializado;
    }

    /**
     * <strong>Nombre:</strong> setInicializado
     *
     * <p><strong>Objetivo:</strong> Marcar el símbolo como inicializado o no.</p>
     *
     * <p><strong>Entrada:</strong> boolean inicializado.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void setInicializado(boolean inicializado) {
        this.inicializado = inicializado;
    }

    /**
     * <strong>Nombre:</strong> agregarTipoParametro
     *
     * <p><strong>Objetivo:</strong> Agregar el tipo de un parámetro a la firma de la función.</p>
     *
     * <p><strong>Entrada:</strong> TipoDato tipoParametro.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void agregarTipoParametro(TipoDato tipoParametro) {
        tiposParametros.add(tipoParametro);
    }
}
