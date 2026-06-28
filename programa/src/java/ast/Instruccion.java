package ast;

import java.util.Objects;

/**
 * Nombre: Instruccion
 *
 * Objetivo: Representar Instruccion dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class Instruccion {
    private final String texto;
    /**
     * Nombre: Instruccion
     *
     * Objetivo: Inicializar una instancia de Instruccion con los datos requeridos.
     *
     * Entrada: String texto.
     *
     * Salida: Nueva instancia de Instruccion.
     *
     * Restricciones: Ninguna.
     */
    public Instruccion(String texto) {
        this.texto = Objects.requireNonNull(texto, "texto");
    }

    /**
     * Nombre: getTexto
     *
     * Objetivo: Obtener el valor de Texto almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String getTexto() {
        return texto;
    }

    /** Usa el texto como representacion imprimible de la instruccion. */
    @Override
    /**
     * Nombre: toString
     *
     * Objetivo: Ejecutar la operacion toString definida por Instruccion.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String toString() {
        return texto;
    }
}
