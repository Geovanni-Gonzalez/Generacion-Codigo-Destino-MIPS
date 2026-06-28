package intermedio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * <strong>Nombre:</strong> OptimizadorIR
 *
 * <p><strong>Objetivo:</strong> Mejorar el código de tres direcciones antes de generar MIPS mediante
 * dos optimizaciones conservadoras:</p>
 * <ol>
 *   <li><b>Plegado y propagación de constantes</b> sobre temporales: evalúa en tiempo de compilación
 *       las operaciones aritméticas enteras cuyos dos operandos son literales (por ejemplo
 *       {@code _t0 = 5 * 2} pasa a {@code _t0 = 10}) y propaga ese valor a los usos posteriores del
 *       temporal.</li>
 *   <li><b>Eliminación de temporales muertos</b>: descarta las definiciones de temporales que, tras
 *       la propagación, ya no se leen en ninguna instrucción.</li>
 * </ol>
 *
 * <p><strong>Entrada:</strong> Lista de {@link Instruccion} generada por {@link GeneradorCodigoIntermedio}.</p>
 *
 * <p><strong>Salida:</strong> Una nueva lista equivalente y, en general, más corta.</p>
 *
 * <p><strong>Restricciones:</strong> Solo pliega enteros no negativos (para no emitir literales que el
 * traductor MIPS no sabe leer) y nunca elimina instrucciones con efectos (llamadas, lecturas, saltos).
 * Los temporales son de asignación única, por lo que la propagación es segura entre bloques.</p>
 */
public final class OptimizadorIR {

    private static final Pattern ENTERO = Pattern.compile("[0-9]+");

    /**
     * <strong>Nombre:</strong> optimizar
     *
     * <p><strong>Objetivo:</strong> Aplicar el plegado de constantes seguido de la eliminación de
     * temporales muertos.</p>
     *
     * <p><strong>Entrada:</strong> List&lt;Instruccion&gt; codigo.</p>
     *
     * <p><strong>Salida:</strong> Nueva lista optimizada.</p>
     *
     * <p><strong>Restricciones:</strong> No modifica la lista de entrada.</p>
     */
    public List<Instruccion> optimizar(List<Instruccion> codigo) {
        return eliminarTemporalesMuertos(plegarConstantes(codigo));
    }

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

    private String sustituir(String operando, Map<String, String> constantes) {
        return constantes.getOrDefault(operando, operando);
    }

    /**
     * Evalúa una operación binaria entera con dos literales y devuelve el resultado, o {@code null} si
     * no se puede o no se debe plegar (operación no aritmética, operandos no enteros, división por cero
     * o resultado negativo que el traductor MIPS no representaría como literal).
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
    private int potencia(int base, int exponente) {
        int acumulado = 1;
        for (int n = exponente; n > 0; n--) {
            acumulado *= base;
        }
        return acumulado;
    }

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
    private void agregarTemporales(String operando, Set<String> destino) {
        if (operando == null) {
            return;
        }
        java.util.regex.Matcher m = Pattern.compile("_t[0-9]+").matcher(operando);
        while (m.find()) {
            destino.add(m.group());
        }
    }

    private boolean esDefinicionPura(Operacion op) {
        return op == Operacion.ASIG || op == Operacion.LOAD || op == Operacion.NEG
                || op == Operacion.NOT || esBinaria(op);
    }

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

    private boolean esTemporal(String nombre) {
        return nombre != null && nombre.startsWith("_t") && ENTERO.matcher(nombre.substring(2)).matches();
    }

    private boolean esEntero(String valor) {
        return valor != null && ENTERO.matcher(valor).matches();
    }
}
