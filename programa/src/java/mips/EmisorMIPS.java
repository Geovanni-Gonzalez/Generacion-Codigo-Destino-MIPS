package mips;

import java.util.ArrayList;
import java.util.List;

/**
 * Nombre: EmisorMIPS
 *
 * Objetivo: Analizar, traducir, emitir u optimizar codigo destino MIPS.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
final class EmisorMIPS {
    private final List<String> lineas = new ArrayList<>();

    /**
     * Nombre: limpiar
     *
     * Objetivo: Normalizar texto o limpiar estado interno.
     *
     * Entrada: Ninguna.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    void limpiar() {
        lineas.clear();
    }

    /**
     * Nombre: add
     *
     * Objetivo: Ejecutar la operacion add definida por EmisorMIPS.
     *
     * Entrada: String linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    void add(String linea) {
        lineas.add(linea);
    }

    /**
     * Nombre: instruccion
     *
     * Objetivo: Ejecutar la operacion instruccion definida por EmisorMIPS.
     *
     * Entrada: String texto.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    void instruccion(String texto) {
        lineas.add("\t" + texto);
    }

    /**
     * Nombre: lineas
     *
     * Objetivo: Ejecutar la operacion lineas definida por EmisorMIPS.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo List<String>.
     *
     * Restricciones: Ninguna.
     */
    List<String> lineas() {
        return new ArrayList<>(lineas);
    }
}
