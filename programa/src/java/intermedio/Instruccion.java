package intermedio;

/**
 * <strong>Objetivo:</strong> Instruccion atomica del codigo intermedio.
 *
 * <p><strong>Entradas:</strong> AST validado, operaciones u operandos necesarios para representar codigo intermedio.</p>
 *
 * <p><strong>Salidas:</strong> Instrucciones, operaciones o texto de codigo intermedio.</p>
 *
 * <p><strong>Restricciones:</strong> Debe asumir programas ya aceptados y no reemplazar las validaciones semanticas.</p>
 */
public class Instruccion {
    public final Operacion op;
    public final String resultado;
    public final String op1;
    public final String op2;

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Operacion op, String resultado, String op1, String op2</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de Instruccion.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public Instruccion(Operacion op, String resultado, String op1, String op2) {
        this.op = op;
        this.resultado = resultado;
        this.op1 = op1;
        this.op2 = op2;
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Operacion op, String resultado, String op1</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de Instruccion.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public Instruccion(Operacion op, String resultado, String op1) {
        this(op, resultado, op1, null);
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Operacion op, String resultado</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de Instruccion.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public Instruccion(Operacion op, String resultado) {
        this(op, resultado, null, null);
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna Operacion.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public Operacion getOp() {
        return op;
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public String getResultado() {
        return resultado;
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public String getOp1() {
        return op1;
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public String getOp2() {
        return op2;
    }

    /** Formatea la instruccion como una linea de codigo intermedio. */
    @Override
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public String toString() {
        switch (op) {
            case DECL:
                return "declare " + op1 + " " + resultado;
            case DECL_ARRAY:
                return "declare " + op1 + " " + resultado + op2;
            case FORMAL_PARAM:
                return "parameter " + op1 + " " + resultado;
            case LOAD:
                return resultado + " = load " + op1;
            case STORE_ARRAY:
                return "store " + op1 + " -> " + resultado;
            case ASIG:
                return resultado + " = " + op1;
            case SUMA:
            case RESTA:
            case MULT:
            case DIV:
            case MOD:
            case POW:
            case AND:
            case OR:
            case IGUAL:
            case MENOR:
            case MAYOR:
            case MENOR_IGUAL:
            case MAYOR_IGUAL:
            case DISTINTO:
                return resultado + " = " + op1 + " " + simboloBinario(op) + " " + op2;
            case NEG:
            case NOT:
                return resultado + " = " + simboloUnario(op) + op1;
            case GOTO:
                return "goto " + resultado;
            case IF_FALSE:
                return "if_false " + op1 + " goto " + resultado;
            case PARAM:
                return "param " + operandoUnico();
            case CALL:
                return formatearCall();
            case PRINT:
                return "print " + operandoUnico();
            case READ:
                return "read " + operandoUnico();
            case RETURN:
                return op1 == null ? "return" : "return " + op1;
            case LABEL:
                return resultado + ":";
            case INICIO_FUNC:
                return "begin_function " + resultado;
            case FIN_FUNC:
                return resultado == null ? "end_function" : "end_function " + resultado;
            default:
                throw new IllegalStateException("Operacion no soportada: " + op);
        }
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private String operandoUnico() {
        return op1 != null ? op1 : resultado;
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private String formatearCall() {
        String llamada = op2 == null ? "call " + op1 : "call " + op1 + ", " + op2;
        return resultado == null ? llamada : resultado + " = " + llamada;
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Operacion op</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private static String simboloBinario(Operacion op) {
        switch (op) {
            case SUMA:
                return "+";
            case RESTA:
                return "-";
            case MULT:
                return "*";
            case DIV:
                return "/";
            case MOD:
                return "%";
            case POW:
                return "^";
            case AND:
                return "&&";
            case OR:
                return "||";
            case IGUAL:
                return "==";
            case MENOR:
                return "<";
            case MAYOR:
                return ">";
            case MENOR_IGUAL:
                return "<=";
            case MAYOR_IGUAL:
                return ">=";
            case DISTINTO:
                return "!=";
            default:
                throw new IllegalArgumentException("Operacion no binaria: " + op);
        }
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Operacion op</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private static String simboloUnario(Operacion op) {
        switch (op) {
            case NEG:
                return "-";
            case NOT:
                return "!";
            default:
                throw new IllegalArgumentException("Operacion no unaria: " + op);
        }
    }
}
