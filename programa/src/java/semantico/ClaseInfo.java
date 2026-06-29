package semantico;

import ast.TipoDato;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nombre: ClaseInfo
 *
 * Objetivo: Describir la estructura semantica de una clase (campos y, mas adelante, herencia y metodos).
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class ClaseInfo {
    private final String nombre;
    private final String padre;
    private final int linea;
    /** Campos propios de la clase, en orden de declaracion: nombre -> tipo. */
    private final Map<String, TipoDato> campos = new LinkedHashMap<>();

    /**
     * Nombre: ClaseInfo
     *
     * Objetivo: Inicializar una instancia de ClaseInfo con los datos requeridos.
     *
     * Entrada: String nombre; String padre; int linea.
     *
     * Salida: Nueva instancia de ClaseInfo.
     *
     * Restricciones: padre puede ser null cuando la clase no hereda.
     */
    public ClaseInfo(String nombre, String padre, int linea) {
        this.nombre = nombre;
        this.padre = padre;
        this.linea = linea;
    }

    /**
     * Nombre: agregarCampo
     *
     * Objetivo: Registrar un campo propio de la clase con su tipo.
     *
     * Entrada: String nombre; TipoDato tipo.
     *
     * Salida: Valor de tipo boolean (false si el campo ya existia).
     *
     * Restricciones: Ninguna.
     */
    public boolean agregarCampo(String nombre, TipoDato tipo) {
        if (campos.containsKey(nombre)) {
            return false;
        }
        campos.put(nombre, tipo);
        return true;
    }

    /**
     * Nombre: getNombre
     *
     * Objetivo: Obtener el nombre de la clase.
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
     * Nombre: getPadre
     *
     * Objetivo: Obtener el nombre de la clase padre, o null si no hereda.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String getPadre() {
        return padre;
    }

    /**
     * Nombre: getLinea
     *
     * Objetivo: Obtener la linea de declaracion de la clase.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo int.
     *
     * Restricciones: Ninguna.
     */
    public int getLinea() {
        return linea;
    }

    /**
     * Nombre: tieneCampo
     *
     * Objetivo: Indicar si la clase declara (de forma propia) el campo indicado.
     *
     * Entrada: String nombre.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    public boolean tieneCampo(String nombre) {
        return campos.containsKey(nombre);
    }

    /**
     * Nombre: tipoCampo
     *
     * Objetivo: Obtener el tipo de un campo propio de la clase.
     *
     * Entrada: String nombre.
     *
     * Salida: Valor de tipo TipoDato (null si el campo no existe).
     *
     * Restricciones: Ninguna.
     */
    public TipoDato tipoCampo(String nombre) {
        return campos.get(nombre);
    }
}
