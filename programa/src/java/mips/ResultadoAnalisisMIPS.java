package mips;

import java.util.LinkedHashMap;
import java.util.Map;

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
