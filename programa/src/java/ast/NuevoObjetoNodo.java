package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nombre: NuevoObjetoNodo
 *
 * Objetivo: Representar la instanciacion de un objeto ('new Clase<|args|>') dentro del AST.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class NuevoObjetoNodo extends ExpresionNodo {
    private final String nombreClase;
    private final List<ExpresionNodo> argumentos;

    /**
     * Nombre: NuevoObjetoNodo
     *
     * Objetivo: Inicializar una instancia de NuevoObjetoNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; String nombreClase; List<ExpresionNodo> argumentos.
     *
     * Salida: Nueva instancia de NuevoObjetoNodo.
     *
     * Restricciones: Ninguna.
     */
    public NuevoObjetoNodo(int linea, int columna, String nombreClase, List<ExpresionNodo> argumentos) {
        super(linea, columna);
        this.nombreClase = nombreClase;
        this.argumentos = new ArrayList<>(argumentos);
    }

    /**
     * Nombre: getNombreClase
     *
     * Objetivo: Obtener el valor de NombreClase almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String getNombreClase() {
        return nombreClase;
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
