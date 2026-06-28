package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nombre: BloqueNodo
 *
 * Objetivo: Representar BloqueNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class BloqueNodo extends Nodo {
    private final List<Nodo> instrucciones;
    /**
     * Nombre: BloqueNodo
     *
     * Objetivo: Inicializar una instancia de BloqueNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; List<Nodo> instrucciones.
     *
     * Salida: Nueva instancia de BloqueNodo.
     *
     * Restricciones: Ninguna.
     */
    public BloqueNodo(int linea, int columna, List<Nodo> instrucciones) {
        super(linea, columna, TipoDato.EMPTY);
        this.instrucciones = new ArrayList<>(instrucciones);
    }

    /**
     * Nombre: getInstrucciones
     *
     * Objetivo: Obtener el valor de Instrucciones almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo List<Nodo>.
     *
     * Restricciones: Ninguna.
     */
    public List<Nodo> getInstrucciones() {
        return Collections.unmodifiableList(instrucciones);
    }
}
