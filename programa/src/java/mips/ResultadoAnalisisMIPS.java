package mips;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nombre: ResultadoAnalisisMIPS
 *
 * Objetivo: Analizar, traducir, emitir u optimizar codigo destino MIPS.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
final class ResultadoAnalisisMIPS {
    final Map<String, String> tipos = new LinkedHashMap<>();
    final Map<String, String> direcciones = new LinkedHashMap<>();
    final Map<String, Integer> columnasArreglo = new LinkedHashMap<>();
    final Map<String, String> cadenas = new LinkedHashMap<>();
    final Map<String, String> flotantes = new LinkedHashMap<>();
    final Map<String, Integer> parametrosFuncion = new LinkedHashMap<>();
    final Map<String, String> retornosFuncion = new LinkedHashMap<>();
    final Map<String, Integer> dimensionesDeclaradas = new LinkedHashMap<>();
}
