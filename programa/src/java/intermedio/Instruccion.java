package intermedio;

public class Instruccion {
    public final Operacion op;
    public final String resultado;
    public final String op1;
    public final String op2;

    public Instruccion(Operacion op, String resultado, String op1, String op2) {
        this.op = op;
        this.resultado = resultado;
        this.op1 = op1;
        this.op2 = op2;
    }

    public Instruccion(Operacion op, String resultado, String op1) {
        this(op, resultado, op1, null);
    }

    public Instruccion(Operacion op, String resultado) {
        this(op, resultado, null, null);
    }

    public Operacion getOp() {
        return op;
    }

    public String getResultado() {
        return resultado;
    }

    public String getOp1() {
        return op1;
    }

    public String getOp2() {
        return op2;
    }

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
            case NEW:
                return resultado + " = new " + op1 + ", " + op2; // t = new Clase, <bytes>
            case LOAD_FIELD:
                return resultado + " = field " + op1 + " @" + op2; // t = field obj @offset:tipo
            case STORE_FIELD:
                return "field " + resultado + " @" + op2 + " = " + op1; // field obj @offset:tipo = valor
            default:
                throw new IllegalStateException("Operacion no soportada: " + op);
        }
    }

    private String operandoUnico() {
        return op1 != null ? op1 : resultado;
    }

    private String formatearCall() {
        String llamada = op2 == null ? "call " + op1 : "call " + op1 + ", " + op2;
        return resultado == null ? llamada : resultado + " = " + llamada;
    }

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
