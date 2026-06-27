package intermedio;

/**
 * <strong>Nombre:</strong> Instruccion (intermedio)
 *
 * <p><strong>Objetivo:</strong> Representar una instrucción del código de tres direcciones, con una
 * {@link Operacion} y hasta tres operandos de texto ({@code resultado}, {@code op1}, {@code op2}).</p>
 *
 * <p><strong>Entrada:</strong> La operación y los operandos que correspondan.</p>
 *
 * <p><strong>Salida:</strong> Objeto inmutable que {@link #toString()} imprime como una línea legible.</p>
 *
 * <p><strong>Restricciones:</strong> No todos los operandos se usan en cada operación.</p>
 */
public class Instruccion {
    public final Operacion op;
    public final String resultado;
    public final String op1;
    public final String op2;

    /**
     * <strong>Nombre:</strong> Instruccion
     *
     * <p><strong>Objetivo:</strong> Crear una instrucción con sus tres operandos.</p>
     *
     * <p><strong>Entrada:</strong> Operacion op, String resultado, String op1, String op2.</p>
     *
     * <p><strong>Salida:</strong> Nueva instancia de Instruccion.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public Instruccion(Operacion op, String resultado, String op1, String op2) {
        this.op = op;
        this.resultado = resultado;
        this.op1 = op1;
        this.op2 = op2;
    }

    /**
     * <strong>Nombre:</strong> Instruccion
     *
     * <p><strong>Objetivo:</strong> Crear una instrucción con resultado y un operando ({@code op2 = null}).</p>
     *
     * <p><strong>Entrada:</strong> Operacion op, String resultado, String op1.</p>
     *
     * <p><strong>Salida:</strong> Nueva instancia de Instruccion.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public Instruccion(Operacion op, String resultado, String op1) {
        this(op, resultado, op1, null);
    }

    /**
     * <strong>Nombre:</strong> Instruccion
     *
     * <p><strong>Objetivo:</strong> Crear una instrucción con un único operando en {@code resultado}.</p>
     *
     * <p><strong>Entrada:</strong> Operacion op, String resultado.</p>
     *
     * <p><strong>Salida:</strong> Nueva instancia de Instruccion.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public Instruccion(Operacion op, String resultado) {
        this(op, resultado, null, null);
    }

    /**
     * <strong>Nombre:</strong> getOp
     *
     * <p><strong>Objetivo:</strong> Devolver la operación que realiza la instrucción.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> Operacion de la instrucción.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public Operacion getOp() {
        return op;
    }

    /**
     * <strong>Nombre:</strong> getResultado
     *
     * <p><strong>Objetivo:</strong> Devolver el operando de resultado (destino, etiqueta u operando único).</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> String con el resultado, o {@code null}.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public String getResultado() {
        return resultado;
    }

    /**
     * <strong>Nombre:</strong> getOp1
     *
     * <p><strong>Objetivo:</strong> Devolver el primer operando.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> String con op1, o {@code null} si no aplica.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public String getOp1() {
        return op1;
    }

    /**
     * <strong>Nombre:</strong> getOp2
     *
     * <p><strong>Objetivo:</strong> Devolver el segundo operando.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> String con op2, o {@code null} si no aplica.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public String getOp2() {
        return op2;
    }

    /**
     * <strong>Nombre:</strong> toString
     *
     * <p><strong>Objetivo:</strong> Dar formato a la instrucción como una línea de código intermedio.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> String con la instrucción legible.</p>
     *
     * <p><strong>Restricciones:</strong> Lanza excepción si la operación no está soportada.</p>
     */
    @Override
    public String toString() {
        switch (op) {
            case DECL:
                return "declare " + op1 + " " + resultado; // declare <tipo> <variable> : declare int x
            case DECL_ARRAY:
                return "declare " + op1 + " " + resultado + op2; // declare <tipo> <variable>[<tamaño>] : declare int arr[10]
            case FORMAL_PARAM:
                return "parameter " + op1 + " " + resultado; // parameter <tipo> <variable> : parameter int x
            case LOAD:
                return resultado + " = load " + op1;    // <variable> = load <variable> : x = load y
            case STORE_ARRAY:
                return "store " + op1 + " -> " + resultado; // store <variable> -> <variable> : store x -> y
            case ASIG:
                return resultado + " = " + op1; // <variable> = <variable> : x = y
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
     * <strong>Nombre:</strong> operandoUnico
     *
     * <p><strong>Objetivo:</strong> Devolver el operando de las operaciones de un solo argumento
     * (en {@code op1} o, si falta, en {@code resultado}).</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> String con el operando único.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private String operandoUnico() {
        return op1 != null ? op1 : resultado;
    }

    /**
     * <strong>Nombre:</strong> formatearCall
     *
     * <p><strong>Objetivo:</strong> Dar formato a una llamada, con o sin variable que recibe el valor retornado.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> String con la llamada formateada.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private String formatearCall() {
        String llamada = op2 == null ? "call " + op1 : "call " + op1 + ", " + op2;
        return resultado == null ? llamada : resultado + " = " + llamada;
    }

    /**
     * <strong>Nombre:</strong> simboloBinario
     *
     * <p><strong>Objetivo:</strong> Traducir una operación binaria a su símbolo ({@code +}, {@code <}, {@code ==}, ...).</p>
     *
     * <p><strong>Entrada:</strong> Operacion op.</p>
     *
     * <p><strong>Salida:</strong> String con el símbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Lanza excepción si la operación no es binaria.</p>
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
     * <strong>Nombre:</strong> simboloUnario
     *
     * <p><strong>Objetivo:</strong> Traducir una operación unaria a su símbolo ({@code -} para NEG, {@code !} para NOT).</p>
     *
     * <p><strong>Entrada:</strong> Operacion op.</p>
     *
     * <p><strong>Salida:</strong> String con el símbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Lanza excepción si la operación no es unaria.</p>
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
