package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <strong>Objetivo:</strong> Literal compuesto que contiene los valores iniciales de un arreglo 2D.
 *
 * <p><strong>Entradas:</strong> Datos sintacticos reconocidos por el parser, posiciones de fuente y subnodos relacionados.</p>
 *
 * <p><strong>Salidas:</strong> Nodos, valores o metadatos consultables por las fases semantica e intermedia.</p>
 *
 * <p><strong>Restricciones:</strong> No debe ejecutar validaciones globales ni escribir archivos; solo conserva estructura y metadatos.</p>
 */
public class InicializacionArregloNodo extends Nodo {
    private final List<List<ExpresionNodo>> filas;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, List<List<ExpresionNodo>> filas</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de InicializacionArregloNodo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
