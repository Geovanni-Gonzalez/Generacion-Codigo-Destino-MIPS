package mips;

import java.util.Map;

/**
 * Nombre: EmisorDatosMIPS
 *
 * Objetivo: Analizar, traducir, emitir u optimizar codigo destino MIPS.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
final class EmisorDatosMIPS {
    void emitir(EmisorMIPS salida, Map<String, String> tipos, Map<String, String> direcciones,
                Map<String, Integer> columnasArreglo, Map<String, Integer> dimensionesDeclaradas,
                Map<String, String> cadenas, Map<String, String> flotantes) {
        salida.add(".data");
        for (Map.Entry<String, String> entrada : tipos.entrySet()) {
            String etiqueta = direccionDato(direcciones, entrada.getKey());
            if (columnasArreglo.containsKey(entrada.getKey())) {
                salida.add(etiqueta + ": .space " + espacioArreglo(dimensionesDeclaradas, entrada.getKey()));
            } else if (OperandosMIPS.esFloat(entrada.getValue())) {
                salida.add(etiqueta + ": .float 0.0");
            } else {
                salida.add(etiqueta + ": .word 0");
            }
        }
        for (Map.Entry<String, String> entrada : cadenas.entrySet()) {
            salida.add(entrada.getValue() + ": .asciiz " + entrada.getKey());
        }
        for (Map.Entry<String, String> entrada : flotantes.entrySet()) {
            salida.add(entrada.getValue() + ": .float " + OperandosMIPS.valorFloat(entrada.getKey()));
        }
        salida.add("");
    }

    /**
     * Nombre: espacioArreglo
     *
     * Objetivo: Indicar si se cumple la condicion pacioArreglo.
     *
     * Entrada: Map<String; Integer> dimensionesDeclaradas; String clave.
     *
     * Salida: Valor de tipo int.
     *
     * Restricciones: Uso interno de la clase.
     */
    private static int espacioArreglo(Map<String, Integer> dimensionesDeclaradas, String clave) {
        return dimensionesDeclaradas.getOrDefault(clave, 1) * 4;
    }

    /**
     * Nombre: direccionDato
     *
     * Objetivo: Ejecutar la operacion direccionDato definida por EmisorDatosMIPS.
     *
     * Entrada: Map<String; String> direcciones; String clave.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private static String direccionDato(Map<String, String> direcciones, String clave) {
        String direccion = direcciones.get(clave);
        if (direccion == null) {
            throw new IllegalStateException("No se reservo memoria MIPS para " + clave);
        }
        return direccion;
    }
}
