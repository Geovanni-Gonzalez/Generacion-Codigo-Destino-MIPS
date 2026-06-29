package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nombre: ClaseNodo
 *
 * Objetivo: Representar la definicion de una clase (estructura y comportamiento) dentro del AST.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class ClaseNodo extends Nodo {
    private final String nombre;
    private final String nombrePadre;
    private final List<DeclaracionVariableNodo> campos;
    private final List<FuncionNodo> metodos;

    /**
     * Nombre: ClaseNodo
     *
     * Objetivo: Inicializar una instancia de ClaseNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; String nombre; String nombrePadre;
     *          List<DeclaracionVariableNodo> campos; List<FuncionNodo> metodos.
     *
     * Salida: Nueva instancia de ClaseNodo.
     *
     * Restricciones: nombrePadre puede ser null cuando la clase no hereda.
     */
    public ClaseNodo(int linea, int columna, String nombre, String nombrePadre,
                     List<DeclaracionVariableNodo> campos, List<FuncionNodo> metodos) {
        super(linea, columna, TipoDato.OBJETO);
        this.nombre = nombre;
        this.nombrePadre = nombrePadre;
        this.campos = new ArrayList<>(campos);
        this.metodos = new ArrayList<>(metodos);
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
     * Nombre: getNombrePadre
     *
     * Objetivo: Obtener el nombre de la clase padre, o null si la clase no hereda.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String getNombrePadre() {
        return nombrePadre;
    }

    /**
     * Nombre: getCampos
     *
     * Objetivo: Obtener el valor de Campos almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo List<DeclaracionVariableNodo>.
     *
     * Restricciones: Ninguna.
     */
    public List<DeclaracionVariableNodo> getCampos() {
        return Collections.unmodifiableList(campos);
    }

    /**
     * Nombre: getMetodos
     *
     * Objetivo: Obtener el valor de Metodos almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo List<FuncionNodo>.
     *
     * Restricciones: Ninguna.
     */
    public List<FuncionNodo> getMetodos() {
        return Collections.unmodifiableList(metodos);
    }
}
