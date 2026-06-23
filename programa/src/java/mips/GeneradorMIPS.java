package mips;

import intermedio.Instruccion;
import intermedio.Operacion;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <strong>Nombre:</strong> GeneradorMIPS
 *
 * <p><strong>Objetivo:</strong> Traducir las instrucciones de código intermedio (tres direcciones) a
 * ensamblador MIPS ejecutable en SPIM/QtSpim: reserva memoria en {@code .data}, traduce cada
 * operación en {@code .text} y maneja registros, funciones, arreglos, flotantes y saltos.</p>
 *
 * <p><strong>Entrada:</strong> La lista de instrucciones intermedias ya validada.</p>
 *
 * <p><strong>Salida:</strong> Una lista de líneas de ensamblador MIPS.</p>
 *
 * <p><strong>Restricciones:</strong> Asume código intermedio correcto; reserva {@code $t6}–{@code $t9}
 * para conversiones y direccionamiento de arreglos.</p>
 */
public final class GeneradorMIPS {
    private final List<String> salida = new ArrayList<>();
    private final AdministradorRegistros registros = new AdministradorRegistros();
    private final Map<String, String> tipos = new LinkedHashMap<>();
    /** Relacion estable entre cada variable/temporal 3D y su etiqueta en .data. */
    private final Map<String, String> direcciones = new LinkedHashMap<>();
    private final Map<String, Integer> columnasArreglo = new LinkedHashMap<>();
    private final Map<String, String> cadenas = new LinkedHashMap<>();
    private final Map<String, String> flotantes = new LinkedHashMap<>();
    private final Map<String, Integer> parametrosFuncion = new LinkedHashMap<>();
    private final Map<String, String> retornosFuncion = new LinkedHashMap<>();
    private int contadorCadena;
    private int contadorFlotante;
    private int contadorEtiquetaInterna;
    private String funcionActual;
    private int indiceParametroFormal;
}

    