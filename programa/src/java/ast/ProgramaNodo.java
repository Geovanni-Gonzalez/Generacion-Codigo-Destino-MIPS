package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nombre: ProgramaNodo
 *
 * Objetivo: Representar ProgramaNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class ProgramaNodo extends Nodo {
    private final List<FuncionNodo> funciones;
    /**
     * Nombre: ProgramaNodo
     *
     * Objetivo: Inicializar una instancia de ProgramaNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; List<FuncionNodo> funciones.
     *
     * Salida: Nueva instancia de ProgramaNodo.
     *
     * Restricciones: Ninguna.
     */
    public ProgramaNodo(int linea, int columna, List<FuncionNodo> funciones) {
        super(linea, columna, TipoDato.EMPTY);
        this.funciones = new ArrayList<>(funciones);
    }

    /**
     * Nombre: getFunciones
     *
     * Objetivo: Obtener el valor de Funciones almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo List<FuncionNodo>.
     *
     * Restricciones: Ninguna.
     */
    public List<FuncionNodo> getFunciones() {
        return Collections.unmodifiableList(funciones);
    }
}
