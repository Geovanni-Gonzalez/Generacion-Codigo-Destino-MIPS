package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nombre: FuncionNodo
 *
 * Objetivo: Representar FuncionNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
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
     * Nombre: getParametros
     *
     * Objetivo: Obtener el valor de Parametros almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo List<ParametroNodo>.
     *
     * Restricciones: Ninguna.
     */
    public List<ParametroNodo> getParametros() {
        return Collections.unmodifiableList(parametros);
    }

    /**
     * Nombre: getCuerpo
     *
     * Objetivo: Obtener el valor de Cuerpo almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo BloqueNodo.
     *
     * Restricciones: Ninguna.
     */
    public BloqueNodo getCuerpo() {
        return cuerpo;
    }

    /**
     * Nombre: isPrincipal
     *
     * Objetivo: Indicar si se cumple la condicion Principal.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    public boolean isPrincipal() {
        return principal;
    }
}
