package semantico;

import ast.TipoDato;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nombre: Simbolo
 *
 * Objetivo: Validar reglas semanticas y administrar informacion de simbolos.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
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
    /** Nombre de la clase cuando el simbolo es un objeto; null en otro caso. */
    private String nombreClase;

    /**
     * Nombre: Simbolo
     *
     * Objetivo: Inicializar una instancia de Simbolo con los datos requeridos.
     *
     * Entrada: String nombre; TipoDato tipo; CategoriaSimb categoria; int linea.
     *
     * Salida: Nueva instancia de Simbolo.
     *
     * Restricciones: Ninguna.
     */
    public Simbolo(String nombre, TipoDato tipo, CategoriaSimb categoria, int linea) {
        this(nombre, tipo, categoria, linea, Collections.emptyList(), null, false);
    }

    /**
     * Nombre: Simbolo
     *
     * Objetivo: Inicializar una instancia de Simbolo con los datos requeridos.
     *
     * Entrada: String nombre; TipoDato tipo; CategoriaSimb categoria; int linea; boolean inicializado.
     *
     * Salida: Nueva instancia de Simbolo.
     *
     * Restricciones: Ninguna.
     */
    public Simbolo(String nombre, TipoDato tipo, CategoriaSimb categoria, int linea, boolean inicializado) {
        this(nombre, tipo, categoria, linea, Collections.emptyList(), null, inicializado);
    }

    /**
     * Nombre: Simbolo
     *
     * Objetivo: Inicializar una instancia de Simbolo con los datos requeridos.
     *
     * Entrada: String nombre; List<TipoDato> tiposParametros; TipoDato tipoRetorno; int linea.
     *
     * Salida: Nueva instancia de Simbolo.
     *
     * Restricciones: Ninguna.
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
     * <strong>Nombre:</strong> objeto
     *
     * <p><strong>Objetivo:</strong> Crear un símbolo que representa una variable de tipo objeto (instancia de clase).</p>
     *
     * <p><strong>Entrada:</strong> String nombre, String nombreClase, int linea, boolean inicializado.</p>
     *
     * <p><strong>Salida:</strong> Nueva instancia de Simbolo con categoría OBJETO.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public static Simbolo objeto(String nombre, String nombreClase, int linea, boolean inicializado) {
        Simbolo simbolo = new Simbolo(nombre, ast.TipoDato.OBJETO, CategoriaSimb.OBJETO, linea, inicializado);
        simbolo.nombreClase = nombreClase;
        return simbolo;
    }

    /**
     * Nombre: getNombre
     *
     * Objetivo: Obtener el valor de Nombre almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Nombre: getNombreClase
     *
     * Objetivo: Obtener el nombre de la clase asociada (solo para símbolos de objeto).
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String (null si no es objeto).
     *
     * Restricciones: Ninguna.
     */
    public String getNombreClase() {
        return nombreClase;
    }

    /**
     * Nombre: getTipo
     *
     * Objetivo: Obtener el valor de Tipo almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo TipoDato.
     *
     * Restricciones: Ninguna.
     */
    public TipoDato getTipo() {
        return tipo;
    }

    /**
     * Nombre: getCategoria
     *
     * Objetivo: Obtener el valor de Categoria almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo CategoriaSimb.
     *
     * Restricciones: Ninguna.
     */
    public CategoriaSimb getCategoria() {
        return categoria;
    }

    /**
     * Nombre: getLinea
     *
     * Objetivo: Obtener el valor de Linea almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo int.
     *
     * Restricciones: Ninguna.
     */
    public int getLinea() {
        return linea;
    }

    /**
     * Nombre: getTiposParametros
     *
     * Objetivo: Obtener el valor de TiposParametros almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo List<TipoDato>.
     *
     * Restricciones: Ninguna.
     */
    public List<TipoDato> getTiposParametros() {
        return Collections.unmodifiableList(tiposParametros);
    }

    /**
     * Nombre: getTipoRetorno
     *
     * Objetivo: Obtener el valor de TipoRetorno almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo TipoDato.
     *
     * Restricciones: Ninguna.
     */
    public TipoDato getTipoRetorno() {
        return tipoRetorno;
    }

    /**
     * Nombre: getFilasArreglo
     *
     * Objetivo: Obtener el valor de FilasArreglo almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo Integer.
     *
     * Restricciones: Ninguna.
     */
    public Integer getFilasArreglo() {
        return filasArreglo;
    }

    /**
     * Nombre: getColumnasArreglo
     *
     * Objetivo: Obtener el valor de ColumnasArreglo almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo Integer.
     *
     * Restricciones: Ninguna.
     */
    public Integer getColumnasArreglo() {
        return columnasArreglo;
    }

    /**
     * Nombre: isInicializado
     *
     * Objetivo: Indicar si se cumple la condicion Inicializado.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    public boolean isInicializado() {
        return inicializado;
    }

    /**
     * Nombre: setInicializado
     *
     * Objetivo: Actualizar el valor de Inicializado en la instancia.
     *
     * Entrada: boolean inicializado.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void setInicializado(boolean inicializado) {
        this.inicializado = inicializado;
    }

    /**
     * Nombre: agregarTipoParametro
     *
     * Objetivo: Ejecutar la operacion agregarTipoParametro definida por Simbolo.
     *
     * Entrada: TipoDato tipoParametro.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void agregarTipoParametro(TipoDato tipoParametro) {
        tiposParametros.add(tipoParametro);
    }
}
