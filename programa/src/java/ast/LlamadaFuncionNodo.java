package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nombre: LlamadaFuncionNodo
 *
 * Objetivo: Representar LlamadaFuncionNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class LlamadaFuncionNodo extends ExpresionNodo {
    private final String nombre;
    private final List<ExpresionNodo> argumentos;
    /**
     * Nombre: LlamadaFuncionNodo
     *
     * Objetivo: Inicializar una instancia de LlamadaFuncionNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; String nombre; List<ExpresionNodo> argumentos.
     *
     * Salida: Nueva instancia de LlamadaFuncionNodo.
     *
     * Restricciones: Ninguna.
     */
    public LlamadaFuncionNodo(int linea, int columna, String nombre, List<ExpresionNodo> argumentos) {
        super(linea, columna);
        this.nombre = nombre;
        this.argumentos = new ArrayList<>(argumentos);
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
