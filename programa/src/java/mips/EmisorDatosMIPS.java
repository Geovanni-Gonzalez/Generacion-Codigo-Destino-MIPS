package mips;

import java.util.Map;

/**
 * Emite la seccion .data a partir del analisis previo del codigo intermedio.
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

    private static int espacioArreglo(Map<String, Integer> dimensionesDeclaradas, String clave) {
        return dimensionesDeclaradas.getOrDefault(clave, 1) * 4;
    }

    private static String direccionDato(Map<String, String> direcciones, String clave) {
        String direccion = direcciones.get(clave);
        if (direccion == null) {
            throw new IllegalStateException("No se reservo memoria MIPS para " + clave);
        }
        return direccion;
    }
}
