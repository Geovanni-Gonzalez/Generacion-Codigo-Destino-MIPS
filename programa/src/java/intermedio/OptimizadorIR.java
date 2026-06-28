package intermedio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Nombre: OptimizadorIR
 *
 * Objetivo: Representar, generar u optimizar instrucciones de codigo intermedio.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public final class OptimizadorIR {

    private static final Pattern ENTERO = Pattern.compile("[0-9]+");

    /**
     * Nombre: optimizar
     *
     * Objetivo: Aplicar optimizaciones sobre el codigo recibido.
     *
     * Entrada: List<Instruccion> codigo.
     *
     * Salida: Valor de tipo List<Instruccion>.
     *
     * Restricciones: Ninguna.
     */
    public List<Instruccion> optimizar(List<Instruccion> codigo) {
        return eliminarTemporalesMuertos(plegarConstantes(codigo));
    }

    /**
     * Nombre: plegarConstantes
     *
     * Objetivo: Ejecutar la operacion plegarConstantes definida por OptimizadorIR.
     *
     * Entrada: List<Instruccion> codigo.
     *
     * Salida: Valor de tipo List<Instruccion>.
     *
     * Restricciones: Uso interno de la clase.
     */
    private List<Instruccion> plegarConstantes(List<Instruccion> codigo) {
        Map<String, String> constantes = new HashMap<>();
        List<Instruccion> resultado = new ArrayList<>(codigo.size());

        for (Instruccion i : codigo) {
            String op1 = sustituir(i.op1, constantes);
            String op2 = sustituir(i.op2, constantes);
            String res = (i.op == Operacion.PRINT || i.op == Operacion.PARAM)
                    ? sustituir(i.resultado, constantes) : i.resultado;

            Integer plegado = evaluar(i.op, op1, op2);
            if (plegado != null) {
                resultado.add(new Instruccion(Operacion.ASIG, i.resultado, String.valueOf(plegado)));
                if (esTemporal(i.resultado)) {
                    constantes.put(i.resultado, String.valueOf(plegado));
                }
                continue;
            }

            if (i.op == Operacion.ASIG && esEntero(op1) && esTemporal(i.resultado)) {
                constantes.put(i.resultado, op1);
            }
            resultado.add(new Instruccion(i.op, res, op1, op2));
        }
        return resultado;
    }

    /**
     * Nombre: sustituir
     *
     * Objetivo: Ejecutar la operacion sustituir definida por OptimizadorIR.
     *
     * Entrada: String operando; Map<String; String> constantes.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private String sustituir(String operando, Map<String, String> constantes) {
        return constantes.getOrDefault(operando, operando);
    }

    /**
     * Nombre: evaluar
     *
     * Objetivo: Calcular el tipo, valor o resultado auxiliar solicitado.
     *
     * Entrada: Operacion op; String op1; String op2.
     *
     * Salida: Valor de tipo Integer.
     *
     * Restricciones: Uso interno de la clase.
     */
    private Integer evaluar(Operacion op, String op1, String op2) {
        if (!esEntero(op1) || !esEntero(op2)) {
            return null;
        }
        int a = Integer.parseInt(op1);
        int b = Integer.parseInt(op2);
        int valor;
        switch (op) {
            case SUMA: valor = a + b; break;
            case RESTA: valor = a - b; break;
            case MULT: valor = a * b; break;
            case DIV: if (b == 0) { return null; } valor = a / b; break;
            case MOD: if (b == 0) { return null; } valor = a % b; break;
            case POW: valor = potencia(a, b); break;
            default: return null;
        }
        return valor >= 0 ? valor : null;
    }

    /** Potencia entera con la misma semántica que el bucle generado en MIPS (exponente &le; 0 da 1). */
    /**
     * Nombre: potencia
     *
     * Objetivo: Ejecutar la operacion potencia definida por OptimizadorIR.
     *
     * Entrada: int base; int exponente.
     *
     * Salida: Valor de tipo int.
     *
     * Restricciones: Uso interno de la clase.
     */
    private int potencia(int base, int exponente) {
        int acumulado = 1;
        for (int n = exponente; n > 0; n--) {
            acumulado *= base;
        }
        return acumulado;
    }

    /**
     * Nombre: eliminarTemporalesMuertos
     *
     * Objetivo: Ejecutar la operacion eliminarTemporalesMuertos definida por OptimizadorIR.
     *
     * Entrada: List<Instruccion> codigo.
     *
     * Salida: Valor de tipo List<Instruccion>.
     *
     * Restricciones: Uso interno de la clase.
     */
    private List<Instruccion> eliminarTemporalesMuertos(List<Instruccion> codigo) {
        Set<String> leidos = recolectarLecturas(codigo);
        List<Instruccion> resultado = new ArrayList<>(codigo.size());
        for (Instruccion i : codigo) {
            if (esDefinicionPura(i.op) && esTemporal(i.resultado) && !leidos.contains(i.resultado)) {
                continue;
            }
            resultado.add(i);
        }
        return resultado;
    }

    /** Conjunto de temporales que se leen en alguna instrucción (operandos y destinos que son lecturas). */
    /**
     * Nombre: recolectarLecturas
     *
     * Objetivo: Ejecutar la operacion recolectarLecturas definida por OptimizadorIR.
     *
     * Entrada: List<Instruccion> codigo.
     *
     * Salida: Valor de tipo Set<String>.
     *
     * Restricciones: Uso interno de la clase.
     */
    private Set<String> recolectarLecturas(List<Instruccion> codigo) {
        Set<String> temporales = new HashSet<>();
        for (Instruccion i : codigo) {
            agregarTemporales(i.op1, temporales);
            agregarTemporales(i.op2, temporales);
            // En estas operaciones el campo 'resultado' es una lectura (valor a imprimir/pasar) o
            // contiene índices de arreglo que sí se leen.
            if (i.op == Operacion.PRINT || i.op == Operacion.PARAM || i.op == Operacion.STORE_ARRAY) {
                agregarTemporales(i.resultado, temporales);
            }
        }
        return temporales;
    }

    /** Agrega cada temporal {@code _tN} que aparezca como token dentro del operando (incluye índices de arreglo). */
    /**
     * Nombre: agregarTemporales
     *
     * Objetivo: Ejecutar la operacion agregarTemporales definida por OptimizadorIR.
     *
     * Entrada: String operando; Set<String> destino.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void agregarTemporales(String operando, Set<String> destino) {
        if (operando == null) {
            return;
        }
        java.util.regex.Matcher m = Pattern.compile("_t[0-9]+").matcher(operando);
        while (m.find()) {
            destino.add(m.group());
        }
    }

    /**
     * Nombre: esDefinicionPura
     *
     * Objetivo: Indicar si se cumple la condicion DefinicionPura.
     *
     * Entrada: Operacion op.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Uso interno de la clase.
     */
    private boolean esDefinicionPura(Operacion op) {
        return op == Operacion.ASIG || op == Operacion.LOAD || op == Operacion.NEG
                || op == Operacion.NOT || esBinaria(op);
    }

    /**
     * Nombre: esBinaria
     *
     * Objetivo: Indicar si se cumple la condicion Binaria.
     *
     * Entrada: Operacion op.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Uso interno de la clase.
     */
    private boolean esBinaria(Operacion op) {
        switch (op) {
            case SUMA: case RESTA: case MULT: case DIV: case MOD: case POW:
            case AND: case OR: case IGUAL: case DISTINTO:
            case MENOR: case MAYOR: case MENOR_IGUAL: case MAYOR_IGUAL:
                return true;
            default:
                return false;
        }
    }

    /**
     * Nombre: esTemporal
     *
     * Objetivo: Indicar si se cumple la condicion Temporal.
     *
     * Entrada: String nombre.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Uso interno de la clase.
     */
    private boolean esTemporal(String nombre) {
        return nombre != null && nombre.startsWith("_t") && ENTERO.matcher(nombre.substring(2)).matches();
    }

    /**
     * Nombre: esEntero
     *
     * Objetivo: Indicar si se cumple la condicion Entero.
     *
     * Entrada: String valor.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Uso interno de la clase.
     */
    private boolean esEntero(String valor) {
        return valor != null && ENTERO.matcher(valor).matches();
    }
}
