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
