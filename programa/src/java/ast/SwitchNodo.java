package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nombre: SwitchNodo
 *
 * Objetivo: Representar SwitchNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class SwitchNodo extends SentenciaNodo {
    private final ExpresionNodo expresion;
    private final List<CasoSwitchNodo> casos;
    /**
     * Nombre: SwitchNodo
     *
     * Objetivo: Inicializar una instancia de SwitchNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; ExpresionNodo expresion; List<CasoSwitchNodo> casos.
     *
     * Salida: Nueva instancia de SwitchNodo.
     *
     * Restricciones: Ninguna.
     */
    public SwitchNodo(int linea, int columna, ExpresionNodo expresion, List<CasoSwitchNodo> casos) {
        super(linea, columna);
        this.expresion = expresion;
        this.casos = new ArrayList<>(casos);
    }
    /**
     * Nombre: getExpresion
     *
     * Objetivo: Obtener el valor de Expresion almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo ExpresionNodo.
     *
     * Restricciones: Ninguna.
     */
    public ExpresionNodo getExpresion() {
        return expresion;
    }

    /**
     * Nombre: getCasos
     *
     * Objetivo: Obtener el valor de Casos almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo List<CasoSwitchNodo>.
     *
     * Restricciones: Ninguna.
     */
    public List<CasoSwitchNodo> getCasos() {
        return Collections.unmodifiableList(casos);
    }
}
