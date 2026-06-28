package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nombre: InicializacionArregloNodo
 *
 * Objetivo: Representar InicializacionArregloNodo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class InicializacionArregloNodo extends Nodo {
    private final List<List<ExpresionNodo>> filas;
    /**
     * Nombre: InicializacionArregloNodo
     *
     * Objetivo: Inicializar una instancia de InicializacionArregloNodo con los datos requeridos.
     *
     * Entrada: int linea; int columna; List<List<ExpresionNodo>> filas.
     *
     * Salida: Nueva instancia de InicializacionArregloNodo.
     *
     * Restricciones: Ninguna.
     */
    public InicializacionArregloNodo(int linea, int columna, List<List<ExpresionNodo>> filas) {
        super(linea, columna);
        this.filas = new ArrayList<>();
        for (List<ExpresionNodo> fila : filas) {
            this.filas.add(new ArrayList<>(fila));
        }
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna List<List<ExpresionNodo>>.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public List<List<ExpresionNodo>> getFilas() {
        List<List<ExpresionNodo>> copia = new ArrayList<>();
        for (List<ExpresionNodo> fila : filas) {
            copia.add(Collections.unmodifiableList(fila));
        }
        return Collections.unmodifiableList(copia);
    }
}
