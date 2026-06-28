package intermedio;

/**
 * Nombre: Instruccion
 *
 * Objetivo: Representar, generar u optimizar instrucciones de codigo intermedio.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class Instruccion {
    public final Operacion op;
    public final String resultado;
    public final String op1;
    public final String op2;

    /**
     * Nombre: Instruccion
     *
     * Objetivo: Inicializar una instancia de Instruccion con los datos requeridos.
     *
     * Entrada: Operacion op; String resultado; String op1; String op2.
     *
     * Salida: Nueva instancia de Instruccion.
     *
     * Restricciones: Ninguna.
     */
    public Instruccion(Operacion op, String resultado, String op1, String op2) {
        this.op = op;
        this.resultado = resultado;
        this.op1 = op1;
        this.op2 = op2;
    }

    /**
     * Nombre: Instruccion
     *
     * Objetivo: Inicializar una instancia de Instruccion con los datos requeridos.
     *
     * Entrada: Operacion op; String resultado; String op1.
     *
     * Salida: Nueva instancia de Instruccion.
     *
     * Restricciones: Ninguna.
     */
    public Instruccion(Operacion op, String resultado, String op1) {
        this(op, resultado, op1, null);
    }

    /**
     * Nombre: Instruccion
     *
     * Objetivo: Inicializar una instancia de Instruccion con los datos requeridos.
     *
     * Entrada: Operacion op; String resultado.
     *
     * Salida: Nueva instancia de Instruccion.
     *
     * Restricciones: Ninguna.
     */
    public Instruccion(Operacion op, String resultado) {
        this(op, resultado, null, null);
    }

    /**
     * Nombre: getOp
     *
     * Objetivo: Obtener el valor de Op almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo Operacion.
     *
     * Restricciones: Ninguna.
     */
    public Operacion getOp() {
        return op;
    }

    /**
     * Nombre: getResultado
     *
     * Objetivo: Obtener el valor de Resultado almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String getResultado() {
        return resultado;
    }

    /**
     * Nombre: getOp1
     *
     * Objetivo: Obtener el valor de Op1 almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String getOp1() {
        return op1;
    }

    /**
     * Nombre: getOp2
     *
     * Objetivo: Obtener el valor de Op2 almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String getOp2() {
        return op2;
    }

    /**
     * Nombre: toString
     *
     * Objetivo: Ejecutar la operacion toString definida por Instruccion.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
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
     * Nombre: operandoUnico
     *
     * Objetivo: Ejecutar la operacion operandoUnico definida por Instruccion.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private String operandoUnico() {
        return op1 != null ? op1 : resultado;
    }

    /**
     * Nombre: formatearCall
     *
     * Objetivo: Convertir un valor interno a su representacion textual.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private String formatearCall() {
        String llamada = op2 == null ? "call " + op1 : "call " + op1 + ", " + op2;
        return resultado == null ? llamada : resultado + " = " + llamada;
    }

    /**
     * Nombre: simboloBinario
     *
     * Objetivo: Ejecutar la operacion simboloBinario definida por Instruccion.
     *
     * Entrada: Operacion op.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
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
     * Nombre: simboloUnario
     *
     * Objetivo: Ejecutar la operacion simboloUnario definida por Instruccion.
     *
     * Entrada: Operacion op.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
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
