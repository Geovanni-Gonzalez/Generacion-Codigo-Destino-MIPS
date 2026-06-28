package mips;

import intermedio.Operacion;
import java.util.Locale;

/**
 * Nombre: OperandosMIPS
 *
 * Objetivo: Analizar, traducir, emitir u optimizar codigo destino MIPS.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
final class OperandosMIPS {
    /**
     * Nombre: OperandosMIPS
     *
     * Objetivo: Inicializar una instancia de OperandosMIPS con los datos requeridos.
     *
     * Entrada: Ninguna.
     *
     * Salida: Nueva instancia de OperandosMIPS.
     *
     * Restricciones: Uso interno de la clase.
     */
    private OperandosMIPS() {
    }

    /**
     * Nombre: normalizarTipo
     *
     * Objetivo: Convertir la entrada a una forma canonica para procesamiento interno.
     *
     * Entrada: String tipo.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    static String normalizarTipo(String tipo) {
        return tipo == null ? "int" : tipo.toLowerCase(Locale.ROOT);
    }

    /**
     * Nombre: esFloat
     *
     * Objetivo: Indicar si se cumple la condicion Float.
     *
     * Entrada: String tipo.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    static boolean esFloat(String tipo) {
        return "float".equals(tipo);
    }

    /**
     * Nombre: esAritmetica
     *
     * Objetivo: Indicar si se cumple la condicion Aritmetica.
     *
     * Entrada: Operacion op.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    static boolean esAritmetica(Operacion op) {
        return op == Operacion.SUMA || op == Operacion.RESTA || op == Operacion.MULT
                || op == Operacion.DIV || op == Operacion.MOD || op == Operacion.POW;
    }

    /**
     * Nombre: esComparacion
     *
     * Objetivo: Indicar si se cumple la condicion Comparacion.
     *
     * Entrada: Operacion op.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    static boolean esComparacion(Operacion op) {
        return op == Operacion.IGUAL || op == Operacion.DISTINTO || op == Operacion.MENOR
                || op == Operacion.MAYOR || op == Operacion.MENOR_IGUAL
                || op == Operacion.MAYOR_IGUAL;
    }

    /**
     * Nombre: esCadena
     *
     * Objetivo: Indicar si se cumple la condicion Cadena.
     *
     * Entrada: String valor.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    static boolean esCadena(String valor) {
        return valor != null && valor.length() >= 2 && valor.startsWith("\"") && valor.endsWith("\"");
    }

    /**
     * Nombre: esChar
     *
     * Objetivo: Indicar si se cumple la condicion Char.
     *
     * Entrada: String valor.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    static boolean esChar(String valor) {
        return valor != null && valor.length() >= 3 && valor.startsWith("'") && valor.endsWith("'");
    }

    /**
     * Nombre: valorChar
     *
     * Objetivo: Extraer o convertir el valor representado por la entrada.
     *
     * Entrada: String valor.
     *
     * Salida: Valor de tipo char.
     *
     * Restricciones: Ninguna.
     */
    static char valorChar(String valor) {
        return valor.charAt(1);
    }

    /**
     * Nombre: esFloatLiteral
     *
     * Objetivo: Indicar si se cumple la condicion FloatLiteral.
     *
     * Entrada: String valor.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    static boolean esFloatLiteral(String valor) {
        return valor != null && (valor.matches("[0-9]+\\.[0-9]+")
                || valor.matches("[0-9]+/[1-9][0-9]*"));
    }

    /**
     * Nombre: esEnteroLiteral
     *
     * Objetivo: Indicar si se cumple la condicion EnteroLiteral.
     *
     * Entrada: String valor.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    static boolean esEnteroLiteral(String valor) {
        return valor != null && (valor.matches("[0-9]+") || valor.matches("[0-9]+e[1-9][0-9]*"));
    }

    /**
     * Nombre: esAccesoArreglo
     *
     * Objetivo: Indicar si se cumple la condicion AccesoArreglo.
     *
     * Entrada: String valor.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    static boolean esAccesoArreglo(String valor) {
        return valor != null && valor.matches("[A-Za-z_][A-Za-z0-9_]*\\[[^]]+\\]\\[[^]]+\\]");
    }

    /**
     * Nombre: valorEntero
     *
     * Objetivo: Extraer o convertir el valor representado por la entrada.
     *
     * Entrada: String valor.
     *
     * Salida: Valor de tipo int.
     *
     * Restricciones: Ninguna.
     */
    static int valorEntero(String valor) {
        if (valor.contains("e")) {
            String[] partes = valor.split("e", 2);
            double calculado = Double.parseDouble(partes[0]) * Math.pow(10, Integer.parseInt(partes[1]));
            return (int) calculado;
        }
        return Integer.parseInt(valor);
    }

    /**
     * Nombre: valorFloat
     *
     * Objetivo: Extraer o convertir el valor representado por la entrada.
     *
     * Entrada: String valor.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    static String valorFloat(String valor) {
        if (valor.contains("/")) {
            String[] partes = valor.split("/", 2);
            return String.valueOf(Double.parseDouble(partes[0]) / Double.parseDouble(partes[1]));
        }
        return valor;
    }

    /**
     * Nombre: parseEntero
     *
     * Objetivo: Ejecutar la operacion parseEntero definida por OperandosMIPS.
     *
     * Entrada: String valor; int defecto.
     *
     * Salida: Valor de tipo int.
     *
     * Restricciones: Ninguna.
     */
    static int parseEntero(String valor, int defecto) {
        try {
            return Integer.parseInt(valor);
        } catch (RuntimeException ex) {
            return defecto;
        }
    }
}
