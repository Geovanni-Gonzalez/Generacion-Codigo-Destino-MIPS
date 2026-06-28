package mips;

import intermedio.Operacion;
import java.util.Locale;

/**
 * Operaciones puras sobre tipos, literales y formas de operandos usados por el generador MIPS.
 */
final class OperandosMIPS {
    private OperandosMIPS() {
    }

    static String normalizarTipo(String tipo) {
        return tipo == null ? "int" : tipo.toLowerCase(Locale.ROOT);
    }

    static boolean esFloat(String tipo) {
        return "float".equals(tipo);
    }

    static boolean esAritmetica(Operacion op) {
        return op == Operacion.SUMA || op == Operacion.RESTA || op == Operacion.MULT
                || op == Operacion.DIV || op == Operacion.MOD || op == Operacion.POW;
    }

    static boolean esComparacion(Operacion op) {
        return op == Operacion.IGUAL || op == Operacion.DISTINTO || op == Operacion.MENOR
                || op == Operacion.MAYOR || op == Operacion.MENOR_IGUAL
                || op == Operacion.MAYOR_IGUAL;
    }

    static boolean esCadena(String valor) {
        return valor != null && valor.length() >= 2 && valor.startsWith("\"") && valor.endsWith("\"");
    }

    static boolean esChar(String valor) {
        return valor != null && valor.length() >= 3 && valor.startsWith("'") && valor.endsWith("'");
    }

    static char valorChar(String valor) {
        return valor.charAt(1);
    }

    static boolean esFloatLiteral(String valor) {
        return valor != null && (valor.matches("[0-9]+\\.[0-9]+")
                || valor.matches("[0-9]+/[1-9][0-9]*"));
    }

    static boolean esEnteroLiteral(String valor) {
        return valor != null && (valor.matches("[0-9]+") || valor.matches("[0-9]+e[1-9][0-9]*"));
    }

    static boolean esAccesoArreglo(String valor) {
        return valor != null && valor.matches("[A-Za-z_][A-Za-z0-9_]*\\[[^]]+\\]\\[[^]]+\\]");
    }

    static int valorEntero(String valor) {
        if (valor.contains("e")) {
            String[] partes = valor.split("e", 2);
            double calculado = Double.parseDouble(partes[0]) * Math.pow(10, Integer.parseInt(partes[1]));
            return (int) calculado;
        }
        return Integer.parseInt(valor);
    }

    static String valorFloat(String valor) {
        if (valor.contains("/")) {
            String[] partes = valor.split("/", 2);
            return String.valueOf(Double.parseDouble(partes[0]) / Double.parseDouble(partes[1]));
        }
        return valor;
    }

    static int parseEntero(String valor, int defecto) {
        try {
            return Integer.parseInt(valor);
        } catch (RuntimeException ex) {
            return defecto;
        }
    }
}
