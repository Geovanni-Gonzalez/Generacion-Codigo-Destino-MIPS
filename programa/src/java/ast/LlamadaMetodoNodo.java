package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nombre: LlamadaMetodoNodo
 *
 * Objetivo: Representar la invocacion de un metodo sobre un objeto ('objeto.metodo<|args|>').
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class LlamadaMetodoNodo extends ExpresionNodo {
    private final ExpresionNodo objeto;
    private final String nombreMetodo;
    private final List<ExpresionNodo> argumentos;

    /**
     * Nombre: LlamadaMetodoNodo
     *
     * Objetivo: Inicializar una instancia de LlamadaMetodoNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; ExpresionNodo objeto; String nombreMetodo;
     *          List<ExpresionNodo> argumentos.
     *
     * Salida: Nueva instancia de LlamadaMetodoNodo.
     *
     * Restricciones: objeto debe evaluar a un tipo OBJETO.
     */
    public LlamadaMetodoNodo(int linea, int columna, ExpresionNodo objeto, String nombreMetodo,
                             List<ExpresionNodo> argumentos) {
        super(linea, columna);
        this.objeto = objeto;
        this.nombreMetodo = nombreMetodo;
        this.argumentos = new ArrayList<>(argumentos);
    }

    /**
     * Nombre: getObjeto
     *
     * Objetivo: Obtener el valor de Objeto almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionNodo getObjeto() {
        return objeto;
    }

    /**
     * Nombre: getNombreMetodo
     *
     * Objetivo: Obtener el valor de NombreMetodo almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String getNombreMetodo() {
        return nombreMetodo;
    }

    /**
     * Nombre: getArgumentos
     *
     * Objetivo: Obtener el valor de Argumentos almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo List<ExpresionNodo>.
     *
     * Restricciones: Ninguna.
     */
    public List<ExpresionNodo> getArgumentos() {
        return Collections.unmodifiableList(argumentos);
    }
}
