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


/**
     * <strong>Nombre:</strong> generarCodigo
     *
     * <p><strong>Objetivo:</strong> Generar un programa MIPS completo a partir del código intermedio validado.</p>
     *
     * <p><strong>Entrada:</strong> List&lt;Instruccion&gt; codigoIntermedio.</p>
     *
     * <p><strong>Salida:</strong> List&lt;String&gt; con las líneas de ensamblador MIPS.</p>
     *
     * <p><strong>Restricciones:</strong> Reinicia el estado interno en cada llamada.</p>
     */
    public List<String> generarCodigo(List<Instruccion> codigoIntermedio) {
        reiniciar();
    //    analizar(codigoIntermedio);
      //  emitirDatos();
        salida.add(".text");
        salida.add(".globl main");
       // traducir(codigoIntermedio);
        return new ArrayList<>(salida);
    }

    /**
     * <strong>Nombre:</strong> reiniciar
     *
     * <p><strong>Objetivo:</strong> Limpiar la salida, los registros y todas las tablas internas antes de una nueva generación.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void reiniciar() {
        salida.clear();
        registros.reiniciar();
        tipos.clear();
        direcciones.clear();
        columnasArreglo.clear();
        dimensionesDeclaradas.clear();
        cadenas.clear();
        flotantes.clear();
        parametrosFuncion.clear();
        retornosFuncion.clear();
        contadorCadena = 0;
        contadorFlotante = 0;
        contadorEtiquetaInterna = 0;
        funcionActual = null;
        indiceParametroFormal = 0;
    }

     /**
     * <strong>Nombre:</strong> analizar
     *
     * <p><strong>Objetivo:</strong> Primer recorrido del código intermedio para recolectar tipos,
     * dimensiones de arreglos, constantes y conteo de parámetros, y propagar tipos hasta un punto fijo.</p>
     *
     * <p><strong>Entrada:</strong> List&lt;Instruccion&gt; codigo.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor; llena las tablas internas.</p>
     *
     * <p><strong>Restricciones:</strong> Debe ejecutarse antes de traducir.</p>
     */
    private void analizar(List<Instruccion> codigo) {
        String funcion = null;
        for (Instruccion instruccion : codigo) {
            if (instruccion.op == Operacion.INICIO_FUNC) { //Si es inicio de funcion, se guarda el nombre de la funcion y se inicializa el contador de parametros
                funcion = instruccion.resultado;    //Se guarda el nombre de la funcion
                parametrosFuncion.put(funcion, 0);  //Se inicializa el contador de parametros en 0
                continue;   //Se continua con la siguiente instruccion
            }
            if (instruccion.op == Operacion.FIN_FUNC) {// Si es fin de funcion, se reinicia el nombre de la funcion
                funcion = null; //Se reinicia el nombre de la funcion
                continue;   //Se continua con la siguiente instruccion
            }
            if (funcion == null) {
                continue;
            }
            if (instruccion.op == Operacion.DECL || instruccion.op == Operacion.FORMAL_PARAM) { //Si es declaracion o parametro formal, se guarda el tipo de la variable en la tabla de tipos
                tipos.put(clave(funcion, instruccion.resultado), normalizarTipo(instruccion.op1)); // Se guarda el tipo de la variable
                if (instruccion.op == Operacion.FORMAL_PARAM) { //Si es parametro formal, se incrementa el contador de parametros de la funcion
                    parametrosFuncion.put(funcion, parametrosFuncion.get(funcion) + 1);
                }
            } else if (instruccion.op == Operacion.DECL_ARRAY) { //Si es declaracion de arreglo, se guarda el tipo y las dimensiones del arreglo
                tipos.put(clave(funcion, instruccion.resultado), normalizarTipo(instruccion.op1)); // Se guarda el tipo del arreglo
                int[] dimensiones = dimensiones(instruccion.op2); // Se obtienen las dimensiones del arreglo
                columnasArreglo.put(clave(funcion, instruccion.resultado), dimensiones[1]); // Se guarda el numero de columnas del arreglo
                dimensionesDeclaradas.put(clave(funcion, instruccion.resultado), // Se guarda el numero de celdas del arreglo
                        dimensiones[0] * dimensiones[1]);
            }
            registrarConstante(instruccion.op1);    // Se registran las constantes literales de cadena y flotante
            registrarConstante(instruccion.op2);    // Se registran las constantes literales de cadena y flotante
            // PRINT/READ guardan su operando en resultado; sin esto los literales
            // de cadena o flotante impresos directamente nunca se reservan en .data.
            registrarConstante(instruccion.resultado);  // Se registran las constantes literales de cadena y flotante
        }

        // Propaga tipos de temporales y resultados hasta alcanzar un punto fijo.
        for (int vuelta = 0; vuelta < 4; vuelta++) { // Se hacen 4 vueltas para propagar los tipos de los resultados y operandos
            funcion = null;                         // Se reinicia el nombre de la funcion
            for (Instruccion i : codigo) {      // Se recorre el codigo intermedio
                if (i.op == Operacion.INICIO_FUNC) {    // Si es inicio de funcion, se guarda el nombre de la funcion
                    funcion = i.resultado;              // Se guarda el nombre de la funcion
                    continue;
                }
                if (i.op == Operacion.FIN_FUNC) {           // Si es fin de funcion, se reinicia el nombre de la funcion
                    funcion = null;                     // Se reinicia el nombre de la funcion
                    continue;
                }
                if (funcion == null || i.resultado == null) {       // Si no hay funcion o no hay resultado, se continua con la siguiente instruccion
                    if (funcion != null && i.op == Operacion.RETURN && i.op1 != null) { // Si hay funcion y es RETURN con operando, se guarda el tipo de retorno de la funcion
                        retornosFuncion.put(funcion, tipoOperando(i.op1, funcion)); // Se guarda el tipo de retorno de la funcion
                    }
                    continue;
                }
                String tipo = tipoResultado(i, funcion);        // Se obtiene el tipo del resultado de la instruccion
                if (tipo != null) {                             // Si hay tipo, se guarda en la tabla de tipos
                    tipos.put(clave(funcion, i.resultado), tipo);   // Se guarda el tipo del resultado de la instruccion
                }
            }
        }
        construirTablaDirecciones();
    }
  /**
     * <strong>Nombre:</strong> construirTablaDirecciones
     *
     * <p><strong>Objetivo:</strong> Asignar a cada variable/temporal una etiqueta única en {@code .data},
     * desambiguando con un sufijo cuando dos nombres producirían la misma etiqueta.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna (usa la tabla de tipos).</p>
     *
     * <p><strong>Salida:</strong> No retorna valor; llena la tabla de direcciones.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void construirTablaDirecciones() {
        Map<String, Integer> repeticiones = new LinkedHashMap<>();
        for (String clave : tipos.keySet()) {
            String base = etiquetaDato(clave);
            int repeticion = repeticiones.getOrDefault(base, 0);
            repeticiones.put(base, repeticion + 1);
            direcciones.put(clave, repeticion == 0 ? base : base + "_" + repeticion);
        }
    }

      /**
     * <strong>Nombre:</strong> tipoResultado
     *
     * <p><strong>Objetivo:</strong> Deducir el tipo del resultado de una instrucción según su operación y operandos.</p>
     *
     * <p><strong>Entrada:</strong> Instruccion i, String funcion.</p>
     *
     * <p><strong>Salida:</strong> String con el tipo ("int", "float", "bool"), o {@code null} si no aplica.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private String tipoResultado(Instruccion i, String funcion) {
        switch (i.op) {
            case LOAD:
            case NEG:
                return tipoOperando(i.op1, funcion);
            case CALL:
                return retornosFuncion.getOrDefault(i.op1, "int");
            case SUMA:
            case RESTA:
            case MULT:
            case DIV:
            case MOD:
            case POW:
                return esFloat(tipoOperando(i.op1, funcion))
                        || esFloat(tipoOperando(i.op2, funcion)) ? "float" : "int";
            case AND:
            case OR:
            case NOT:
            case IGUAL:
            case DISTINTO:
            case MENOR:
            case MAYOR:
            case MENOR_IGUAL:
            case MAYOR_IGUAL:
                return "bool";
            default:
                return null;
        }
    }


    /**
     * <strong>Nombre:</strong> clave
     *
     * <p><strong>Objetivo:</strong> Construir la clave única de un símbolo combinando su función y su nombre.</p>
     *
     * <p><strong>Entrada:</strong> String funcion, String nombre.</p>
     *
     * <p><strong>Salida:</strong> String con la forma {@code funcion::nombre}.</p>
     *
     * <p><strong>Restricciones:</strong> Si la función es {@code null}, usa "global".</p>
     */
    private static String clave(String funcion, String nombre) {
        return (funcion == null ? "global" : funcion) + "::" + nombre;
    }

     /**
     * <strong>Nombre:</strong> etiquetaDato
     *
     * <p><strong>Objetivo:</strong> Derivar la etiqueta base de {@code .data} a partir de una clave de símbolo.</p>
     *
     * <p><strong>Entrada:</strong> String clave.</p>
     *
     * <p><strong>Salida:</strong> String con la etiqueta {@code _d_...}.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static String etiquetaDato(String clave) {
        return "_d_" + limpiar(clave.replace("::", "__"));
    }

    /**
     * <strong>Nombre:</strong> etiquetaFuncion
     *
     * <p><strong>Objetivo:</strong> Traducir el nombre de una función a su etiqueta MIPS ({@code main} para __main__, {@code _fn_...} para el resto).</p>
     *
     * <p><strong>Entrada:</strong> String nombre.</p>
     *
     * <p><strong>Salida:</strong> String con la etiqueta de la función.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static String etiquetaFuncion(String nombre) {
        return "__main__".equals(nombre) ? "main" : "_fn_" + limpiar(nombre);
    }

    /**
     * <strong>Nombre:</strong> etiquetaEpilogo
     *
     * <p><strong>Objetivo:</strong> Construir la etiqueta del epílogo de una función (su etiqueta más {@code __fin}).</p>
     *
     * <p><strong>Entrada:</strong> String nombre.</p>
     *
     * <p><strong>Salida:</strong> String con la etiqueta del epílogo.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static String etiquetaEpilogo(String nombre) {
        return etiquetaFuncion(nombre) + "__fin";
    }

    /**
     * <strong>Nombre:</strong> etiquetaCodigo
     *
     * <p><strong>Objetivo:</strong> Traducir una etiqueta del código intermedio a su etiqueta MIPS ({@code _ic_...}).</p>
     *
     * <p><strong>Entrada:</strong> String nombre.</p>
     *
     * <p><strong>Salida:</strong> String con la etiqueta de código.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static String etiquetaCodigo(String nombre) {
        return "_ic_" + limpiar(nombre);
    }

    /**
     * <strong>Nombre:</strong> nuevaEtiquetaInterna
     *
     * <p><strong>Objetivo:</strong> Generar una etiqueta auxiliar única para las rutinas internas del generador.</p>
     *
     * <p><strong>Entrada:</strong> String prefijo.</p>
     *
     * <p><strong>Salida:</strong> String con la etiqueta ({@code _mips_<prefijo>_N}).</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private String nuevaEtiquetaInterna(String prefijo) {
        return "_mips_" + prefijo + "_" + contadorEtiquetaInterna++;
    }

    /**
     * <strong>Nombre:</strong> instruccion
     *
     * <p><strong>Objetivo:</strong> Agregar una línea de instrucción a la salida con la tabulación de sangría.</p>
     *
     * <p><strong>Entrada:</strong> String texto.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void instruccion(String texto) {
        salida.add("\t" + texto);
    }

    /**
     * <strong>Nombre:</strong> limpiar
     *
     * <p><strong>Objetivo:</strong> Reemplazar por {@code _} los caracteres no válidos en una etiqueta MIPS.</p>
     *
     * <p><strong>Entrada:</strong> String texto.</p>
     *
     * <p><strong>Salida:</strong> String saneado.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static String limpiar(String texto) {
        return texto.replaceAll("[^A-Za-z0-9_]", "_");
    }

    /**
     * <strong>Nombre:</strong> normalizarTipo
     *
     * <p><strong>Objetivo:</strong> Pasar un tipo a minúsculas, usando "int" si viene {@code null}.</p>
     *
     * <p><strong>Entrada:</strong> String tipo.</p>
     *
     * <p><strong>Salida:</strong> String con el tipo normalizado.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static String normalizarTipo(String tipo) {
        return tipo == null ? "int" : tipo.toLowerCase(Locale.ROOT);
    }

    /**
     * <strong>Nombre:</strong> esFloat
     *
     * <p><strong>Objetivo:</strong> Indicar si un tipo es flotante.</p>
     *
     * <p><strong>Entrada:</strong> String tipo.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si es "float".</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static boolean esFloat(String tipo) {
        return "float".equals(tipo);
    }

    /**
     * <strong>Nombre:</strong> esAritmetica
     *
     * <p><strong>Objetivo:</strong> Indicar si una operación es aritmética (+, -, *, /, %, ^).</p>
     *
     * <p><strong>Entrada:</strong> Operacion op.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si es aritmética.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static boolean esAritmetica(Operacion op) {
        return op == Operacion.SUMA || op == Operacion.RESTA || op == Operacion.MULT
                || op == Operacion.DIV || op == Operacion.MOD || op == Operacion.POW;
    }

    /**
     * <strong>Nombre:</strong> esComparacion
     *
     * <p><strong>Objetivo:</strong> Indicar si una operación es de comparación (==, !=, <, >, <=, >=).</p>
     *
     * <p><strong>Entrada:</strong> Operacion op.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si es comparación.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static boolean esComparacion(Operacion op) {
        return op == Operacion.IGUAL || op == Operacion.DISTINTO || op == Operacion.MENOR
                || op == Operacion.MAYOR || op == Operacion.MENOR_IGUAL
                || op == Operacion.MAYOR_IGUAL;
    }

    /**
     * <strong>Nombre:</strong> esCadena
     *
     * <p><strong>Objetivo:</strong> Indicar si un operando es un literal de cadena (entre comillas dobles).</p>
     *
     * <p><strong>Entrada:</strong> String valor.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si es cadena.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static boolean esCadena(String valor) {
        return valor != null && valor.length() >= 2 && valor.startsWith("\"") && valor.endsWith("\"");
    }

    /**
     * <strong>Nombre:</strong> esChar
     *
     * <p><strong>Objetivo:</strong> Indicar si un operando es un literal de carácter (entre apóstrofos).</p>
     *
     * <p><strong>Entrada:</strong> String valor.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si es carácter.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static boolean esChar(String valor) {
        return valor != null && valor.length() >= 3 && valor.startsWith("'") && valor.endsWith("'");
    }

    /**
     * <strong>Nombre:</strong> valorChar
     *
     * <p><strong>Objetivo:</strong> Extraer el carácter contenido en un literal de carácter.</p>
     *
     * <p><strong>Entrada:</strong> String valor.</p>
     *
     * <p><strong>Salida:</strong> char con el carácter.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static char valorChar(String valor) {
        return valor.charAt(1);
    }

    /**
     * <strong>Nombre:</strong> esFloatLiteral
     *
     * <p><strong>Objetivo:</strong> Indicar si un operando es un literal flotante (decimal o fracción).</p>
     *
     * <p><strong>Entrada:</strong> String valor.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si es flotante literal.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static boolean esFloatLiteral(String valor) {
        return valor != null && (valor.matches("[0-9]+\\.[0-9]+")
                || valor.matches("[0-9]+/[1-9][0-9]*"));
    }