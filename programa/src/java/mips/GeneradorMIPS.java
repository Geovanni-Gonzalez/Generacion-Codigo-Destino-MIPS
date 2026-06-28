package mips;

import intermedio.Instruccion;
import intermedio.Operacion;
import java.util.LinkedHashMap;
import java.util.List;
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
    private final EmisorMIPS salida = new EmisorMIPS();
    private final AdministradorRegistros registros = new AdministradorRegistros();
    private final Map<String, String> tipos = new LinkedHashMap<>();
    /** Relacion estable entre cada variable/temporal 3D y su etiqueta en .data. */
    private final Map<String, String> direcciones = new LinkedHashMap<>();
    private final Map<String, Integer> columnasArreglo = new LinkedHashMap<>();
    private final Map<String, String> cadenas = new LinkedHashMap<>();
    private final Map<String, String> flotantes = new LinkedHashMap<>();
    private final Map<String, Integer> parametrosFuncion = new LinkedHashMap<>();
    private final Map<String, String> retornosFuncion = new LinkedHashMap<>();
    private final Map<String, Integer> dimensionesDeclaradas = new LinkedHashMap<>();
    private int contadorEtiquetaInterna;
    private String funcionActual;
    private int indiceParametroFormal;



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
        aplicarAnalisis(new AnalizadorIRMIPS().analizar(codigoIntermedio));
        emitirDatos();
        salida.add(".text");
        salida.add(".globl main");
       traducir(codigoIntermedio);
        return salida.lineas();
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
        salida.limpiar();
        registros.reiniciar();
        tipos.clear();
        direcciones.clear();
        columnasArreglo.clear();
        dimensionesDeclaradas.clear();
        cadenas.clear();
        flotantes.clear();
        parametrosFuncion.clear();
        retornosFuncion.clear();
        contadorEtiquetaInterna = 0;
        funcionActual = null;
        indiceParametroFormal = 0;
    }

    private void aplicarAnalisis(ResultadoAnalisisMIPS analisis) {
        tipos.putAll(analisis.tipos);
        direcciones.putAll(analisis.direcciones);
        columnasArreglo.putAll(analisis.columnasArreglo);
        dimensionesDeclaradas.putAll(analisis.dimensionesDeclaradas);
        cadenas.putAll(analisis.cadenas);
        flotantes.putAll(analisis.flotantes);
        parametrosFuncion.putAll(analisis.parametrosFuncion);
        retornosFuncion.putAll(analisis.retornosFuncion);
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
    
    /**
     * <strong>Nombre:</strong> espacioArreglo
     *
     * <p><strong>Objetivo:</strong> Calcular los bytes a reservar para un arreglo (número de celdas por 4).</p>
     *
     * <p><strong>Entrada:</strong> String clave.</p>
     *
     * <p><strong>Salida:</strong> int con el tamaño en bytes.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
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
        return EtiquetasMIPS.clave(funcion, nombre);
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
        return EtiquetasMIPS.etiquetaDato(clave);
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
        return EtiquetasMIPS.etiquetaFuncion(nombre);
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
        return EtiquetasMIPS.etiquetaEpilogo(nombre);
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
        return EtiquetasMIPS.etiquetaCodigo(nombre);
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
        return EtiquetasMIPS.etiquetaInterna(prefijo, contadorEtiquetaInterna++);
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
        salida.instruccion(texto);
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
        return OperandosMIPS.normalizarTipo(tipo);
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
        return OperandosMIPS.esFloat(tipo);
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
        return OperandosMIPS.esAritmetica(op);
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
        return OperandosMIPS.esComparacion(op);
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
        return OperandosMIPS.esCadena(valor);
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
        return OperandosMIPS.esChar(valor);
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
        return OperandosMIPS.valorChar(valor);
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
        return OperandosMIPS.esFloatLiteral(valor);
    }
        /**
     * <strong>Nombre:</strong> esEnteroLiteral
     *
     * <p><strong>Objetivo:</strong> Indicar si un operando es un literal entero (con o sin notación exponencial).</p>
     *
     * <p><strong>Entrada:</strong> String valor.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si es entero literal.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static boolean esEnteroLiteral(String valor) {
        return OperandosMIPS.esEnteroLiteral(valor);
    }

    /**
     * <strong>Nombre:</strong> esAccesoArreglo
     *
     * <p><strong>Objetivo:</strong> Indicar si un operando tiene la forma de acceso a arreglo {@code nombre[..][..]}.</p>
     *
     * <p><strong>Entrada:</strong> String valor.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si es acceso a arreglo.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static boolean esAccesoArreglo(String valor) {
        return OperandosMIPS.esAccesoArreglo(valor);
    }


    /**
     * <strong>Nombre:</strong> tipoOperando
     *
     * <p><strong>Objetivo:</strong> Determinar el tipo de un operando: por su forma si es literal, o consultando la tabla de tipos.</p>
     *
     * <p><strong>Entrada:</strong> String operando, String funcion.</p>
     *
     * <p><strong>Salida:</strong> String con el tipo ("int", "float", "bool", "char", "string").</p>
     *
     * <p><strong>Restricciones:</strong> Por defecto asume "int".</p>
     */
    private String tipoOperando(String operando, String funcion) {
        if (operando == null) return "int";
        if (esCadena(operando)) return "string";
        if (esChar(operando)) return "char";
        if ("true".equals(operando) || "false".equals(operando)) return "bool";
        if (esFloatLiteral(operando)) return "float";
        if (esEnteroLiteral(operando)) return "int";
        String base = esAccesoArreglo(operando) ? operando.substring(0, operando.indexOf('[')) : operando;
        return tipos.getOrDefault(clave(funcion, base), "int");
    }

        /**
     * <strong>Nombre:</strong> registrarConstante
     *
     * <p><strong>Objetivo:</strong> Reservar una etiqueta en {@code .data} para una constante de cadena
     * ({@code _str_N}) o flotante ({@code _flt_N}) la primera vez que aparece.</p>
     *
     * <p><strong>Entrada:</strong> String valor.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ignora valores que no son cadena ni flotante literal.</p>
     */
        /**
     * <strong>Nombre:</strong> dimensiones
     *
     * <p><strong>Objetivo:</strong> Extraer las dimensiones {@code [filas][columnas]} de un arreglo desde su texto.</p>
     *
     * <p><strong>Entrada:</strong> String texto.</p>
     *
     * <p><strong>Salida:</strong> int[] con {filas, columnas}; {1,1} si el texto no tiene el formato esperado.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void emitirDatos() {
        new EmisorDatosMIPS().emitir(salida, tipos, direcciones, columnasArreglo,
                dimensionesDeclaradas, cadenas, flotantes);
    }


    /**
     * <strong>Nombre:</strong> direccionDato
     *
     * <p><strong>Objetivo:</strong> Devolver la etiqueta de memoria reservada para una clave variable/función.</p>
     *
     * <p><strong>Entrada:</strong> String clave.</p>
     *
     * <p><strong>Salida:</strong> String con la etiqueta.</p>
     *
     * <p><strong>Restricciones:</strong> Lanza excepción si no se reservó memoria para la clave.</p>
     */
    private String direccionDato(String clave) {
        String direccion = direcciones.get(clave);
        if (direccion == null) {
            throw new IllegalStateException("No se reservo memoria MIPS para " + clave);
        }
        return direccion;
    }
    /**
     * <strong>Nombre:</strong> valorEntero
     *
     * <p><strong>Objetivo:</strong> Convertir un literal entero (incluida la notación {@code NeM}) a su valor int.</p>
     *
     * <p><strong>Entrada:</strong> String valor.</p>
     *
     * <p><strong>Salida:</strong> int con el valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static int valorEntero(String valor) {
        return OperandosMIPS.valorEntero(valor);
    }

    /**
     * <strong>Nombre:</strong> valorFloat
     *
     * <p><strong>Objetivo:</strong> Convertir un literal flotante (decimal o fracción {@code a/b}) a su texto numérico.</p>
     *
     * <p><strong>Entrada:</strong> String valor.</p>
     *
     * <p><strong>Salida:</strong> String con el valor flotante.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static String valorFloat(String valor) {
        return OperandosMIPS.valorFloat(valor);
    }

        /**
     * <strong>Nombre:</strong> traducir
     *
     * <p><strong>Objetivo:</strong> Segundo recorrido: traducir cada instrucción intermedia a sus
     * líneas MIPS, fusionando comparación + salto cuando es posible.</p>
     *
     * <p><strong>Entrada:</strong> List&lt;Instruccion&gt; codigo.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor; agrega líneas a la salida.</p>
     *
     * <p><strong>Restricciones:</strong> Requiere que {@link #analizar} ya se haya ejecutado.</p>
     */
    private void traducir(List<Instruccion> codigo) {
        funcionActual = null;
        for (int indice = 0; indice < codigo.size(); indice++) {
            Instruccion i = codigo.get(indice);
            if (indice + 1 < codigo.size()
                    && puedeFusionarSalto(i, codigo.get(indice + 1))) {
                traducirSaltoComparacion(i, codigo.get(indice + 1).resultado);
                indice++;
                continue;
            }
            switch (i.op) {
                case INICIO_FUNC:
                    iniciarFuncion(i.resultado);
                    break;
                case FIN_FUNC:
                    finalizarFuncion(i.resultado);
                    break;
                case DECL:
                case DECL_ARRAY:
                    break;
                case FORMAL_PARAM:
                    traducirParametroFormal(i);
                    break;
                case LOAD:
                case ASIG:
                case STORE_ARRAY:
                    traducirTransferencia(i);
                    break;
                case SUMA:
                case RESTA:
                case MULT:
                case DIV:
                case MOD:
                case POW:
                case AND:
                case OR:
                case IGUAL:
                case DISTINTO:
                case MENOR:
                case MAYOR:
                case MENOR_IGUAL:
                case MAYOR_IGUAL:
                    traducirBinaria(i);
                    break;
                case NEG:
                case NOT:
                    traducirUnaria(i);
                    break;
                case LABEL:
                    salida.add(etiquetaCodigo(i.resultado) + ":");
                    break;
                case GOTO:
                    instruccion("j " + etiquetaCodigo(i.resultado));
                    break;
                case IF_FALSE:
                    String condicion = cargarValor(i.op1);
                    instruccion("beq " + condicion + ", $zero, " + etiquetaCodigo(i.resultado));
                    registros.liberarRegistro(condicion);
                    break;
                case PARAM:
                    String argumento = i.op1 != null ? i.op1 : i.resultado;
                    String registroParametro = registros.obtenerRegistro();
                    if (esFloat(tipoOperando(argumento, funcionActual))) {
                        cargarFloat(argumento, "$f0");
                        instruccion("mfc1 " + registroParametro + ", $f0");
                    } else {
                        cargarEntero(argumento, registroParametro);
                    }
                    instruccion("addiu $sp, $sp, -4");
                    instruccion("sw " + registroParametro + ", 0($sp)");
                    registros.liberarRegistro(registroParametro);
                    break;
                case CALL:
                    traducirLlamada(i);
                    break;
                case RETURN:
                    traducirRetorno(i);
                    break;
                case PRINT:
                    traducirPrint(i.op1 != null ? i.op1 : i.resultado);
                    break;
                case READ:
                    traducirRead(i.op1 != null ? i.op1 : i.resultado);
                    break;
                default:
                    instruccion("# Operacion no implementada: " + i.op);
            }
        }
    }

    /**
     * <strong>Nombre:</strong> puedeFusionarSalto
     *
     * <p><strong>Objetivo:</strong> Decidir si una comparación entera seguida de un IF_FALSE sobre su
     * resultado puede traducirse como un único branch condicional.</p>
     *
     * <p><strong>Entrada:</strong> Instruccion comparacion, Instruccion salto.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si pueden fusionarse.</p>
     *
     * <p><strong>Restricciones:</strong> No aplica a comparaciones de flotantes.</p>
     */
    private boolean puedeFusionarSalto(Instruccion comparacion, Instruccion salto) {
        return esComparacion(comparacion.op)
                && salto.op == Operacion.IF_FALSE
                && comparacion.resultado != null
                && comparacion.resultado.equals(salto.op1)
                && !esFloat(tipoOperando(comparacion.op1, funcionActual))
                && !esFloat(tipoOperando(comparacion.op2, funcionActual));
    }

    /**
     * <strong>Nombre:</strong> traducirSaltoComparacion
     *
     * <p><strong>Objetivo:</strong> Traducir directamente el caso falso de una comparación hacia su
     * etiqueta destino, usando el branch MIPS inverso ({@code bne}, {@code bge}, {@code ble}, ...).</p>
     *
     * <p><strong>Entrada:</strong> Instruccion comparacion, String destino.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Solo para comparaciones enteras.</p>
     */
    private void traducirSaltoComparacion(Instruccion comparacion, String destino) {
        String izquierdo = cargarValor(comparacion.op1);
        String derecho = cargarValor(comparacion.op2);
        String operacion;
        switch (comparacion.op) {
            case IGUAL: operacion = "bne"; break;
            case DISTINTO: operacion = "beq"; break;
            case MENOR: operacion = "bge"; break;
            case MENOR_IGUAL: operacion = "bgt"; break;
            case MAYOR: operacion = "ble"; break;
            case MAYOR_IGUAL: operacion = "blt"; break;
            default: throw new IllegalStateException("Comparacion no soportada en salto: " + comparacion.op);
        }
        instruccion(operacion + " " + izquierdo + ", " + derecho + ", " + etiquetaCodigo(destino));
        registros.liberarRegistro(derecho);
        registros.liberarRegistro(izquierdo);
    }

        /**
     * <strong>Nombre:</strong> cargarValor
     *
     * <p><strong>Objetivo:</strong> Obtener un registro temporal libre y cargar en él el valor entero solicitado.</p>
     *
     * <p><strong>Entrada:</strong> String operando.</p>
     *
     * <p><strong>Salida:</strong> String con el nombre del registro usado.</p>
     *
     * <p><strong>Restricciones:</strong> Quien lo llama debe liberar el registro.</p>
     */
    private String cargarValor(String operando) {
        String registro = registros.obtenerRegistro();
        cargarEntero(operando, registro);
        return registro;
    }

    /**
     * <strong>Nombre:</strong> cargarEntero
     *
     * <p><strong>Objetivo:</strong> Cargar en un registro un valor entero, según sea constante, carácter,
     * booleano, cadena (dirección), celda de arreglo o variable en memoria.</p>
     *
     * <p><strong>Entrada:</strong> String operando, String registro.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Un operando {@code null} carga $zero.</p>
     */
    private void cargarEntero(String operando, String registro) {
        if (operando == null) {
            instruccion("move " + registro + ", $zero");
        } else if (esCadena(operando)) {
            instruccion("la " + registro + ", " + cadenas.get(operando));
        } else if (esChar(operando)) {
            instruccion("li " + registro + ", " + (int) valorChar(operando));
        } else if ("true".equals(operando) || "false".equals(operando)) {
            instruccion("li " + registro + ", " + ("true".equals(operando) ? 1 : 0));
        } else if (esEnteroLiteral(operando)) {
            instruccion("li " + registro + ", " + valorEntero(operando));
        } else if (esAccesoArreglo(operando)) {
            direccionArreglo(operando, "$t7");
            instruccion("lw " + registro + ", 0($t7)");
        } else {
            instruccion("lw " + registro + ", " + etiqueta(operando));
        }
    }
        /**
     * <strong>Nombre:</strong> etiqueta
     *
     * <p><strong>Objetivo:</strong> Obtener la etiqueta {@code .data} de una variable o de la base de un acceso a arreglo.</p>
     *
     * <p><strong>Entrada:</strong> String operando.</p>
     *
     * <p><strong>Salida:</strong> String con la etiqueta en memoria.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private String etiqueta(String operando) {
        String base = esAccesoArreglo(operando) ? operando.substring(0, operando.indexOf('[')) : operando;
        return direccionDato(clave(funcionActual, base));
    }

        /**
     * <strong>Nombre:</strong> direccionArreglo
     *
     * <p><strong>Objetivo:</strong> Calcular en un registro la dirección de una celda {@code nombre[fila][col]}
     * con la fórmula {@code base + (fila*columnas + col)*4}.</p>
     *
     * <p><strong>Entrada:</strong> String acceso, String registroDireccion.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor; deja la dirección en el registro indicado.</p>
     *
     * <p><strong>Restricciones:</strong> Usa {@code $t6}, {@code $t8} y {@code $t9} como auxiliares.</p>
     */
    private void direccionArreglo(String acceso, String registroDireccion) {
        int primero = acceso.indexOf('[');
        String nombre = acceso.substring(0, primero);
        int cierreFila = acceso.indexOf(']', primero);
        int inicioColumna = acceso.indexOf('[', cierreFila);
        int cierreColumna = acceso.indexOf(']', inicioColumna);
        String fila = acceso.substring(primero + 1, cierreFila);
        String columna = acceso.substring(inicioColumna + 1, cierreColumna);
        cargarEntero(fila, "$t8");
        cargarEntero(columna, "$t9");
        int columnas = columnasArreglo.getOrDefault(clave(funcionActual, nombre), 1);
        instruccion("li $t6, " + columnas);
        instruccion("mul $t8, $t8, $t6");
        instruccion("add $t8, $t8, $t9");
        instruccion("sll $t8, $t8, 2");
        instruccion("la " + registroDireccion + ", " + etiqueta(nombre));
        instruccion("add " + registroDireccion + ", " + registroDireccion + ", $t8");
    }
        /**
     * <strong>Nombre:</strong> iniciarFuncion
     *
     * <p><strong>Objetivo:</strong> Emitir la etiqueta de la función y su prólogo (guardar {@code $ra} en la pila, salvo en main).</p>
     *
     * <p><strong>Entrada:</strong> String nombre.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void iniciarFuncion(String nombre) {
        funcionActual = nombre;
        indiceParametroFormal = 0;
        salida.add("");
        salida.add(etiquetaFuncion(nombre) + ":");
        if (!"__main__".equals(nombre)) {
            instruccion("addiu $sp, $sp, -4");
            instruccion("sw $ra, 0($sp)");
        }
    }

    /**
     * <strong>Nombre:</strong> cargarFloat
     *
     * <p><strong>Objetivo:</strong> Cargar en un registro del coprocesador un valor flotante, convirtiendo
     * desde entero o leyendo desde una celda de arreglo o variable según el caso.</p>
     *
     * <p><strong>Entrada:</strong> String operando, String registro.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void cargarFloat(String operando, String registro) {
        if (esFloatLiteral(operando)) {
            instruccion("l.s " + registro + ", " + flotantes.get(operando));
        } else if (esEnteroLiteral(operando)) {
            cargarEntero(operando, "$t6");
            instruccion("mtc1 $t6, " + registro);
            instruccion("cvt.s.w " + registro + ", " + registro);
        } else if (esAccesoArreglo(operando)) {
            direccionArreglo(operando, "$t7");
            instruccion("l.s " + registro + ", 0($t7)");
        } else {
            instruccion("l.s " + registro + ", " + etiqueta(operando));
        }
    }

    
    /**
     * <strong>Nombre:</strong> finalizarFuncion
     *
     * <p><strong>Objetivo:</strong> Emitir el epílogo: en main termina con {@code li $v0, 10} + {@code syscall};
     * en las demás restaura {@code $ra} y retorna con {@code jr $ra}.</p>
     *
     * <p><strong>Entrada:</strong> String nombre.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void finalizarFuncion(String nombre) {
        salida.add(etiquetaEpilogo(nombre) + ":");
        if ("__main__".equals(nombre)) {
            instruccion("li $v0, 10");
            instruccion("syscall");
        } else {
            instruccion("lw $ra, 0($sp)");
            instruccion("addiu $sp, $sp, 4");
            instruccion("jr $ra");
        }
        funcionActual = null;
    }

    /**
     * <strong>Nombre:</strong> traducirParametroFormal
     *
     * <p><strong>Objetivo:</strong> Copiar un parámetro recibido por la pila a la variable local que lo representa.</p>
     *
     * <p><strong>Entrada:</strong> Instruccion i.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Usa el orden de los parámetros para calcular el desplazamiento en la pila.</p>
     */
    private void traducirParametroFormal(Instruccion i) {
        int total = parametrosFuncion.getOrDefault(funcionActual, 0);
        int desplazamiento = 4 * (total - indiceParametroFormal);
        String registro = registros.obtenerRegistro();
        instruccion("lw " + registro + ", " + desplazamiento + "($sp)");
        instruccion("sw " + registro + ", " + etiqueta(i.resultado));
        registros.liberarRegistro(registro);
        indiceParametroFormal++;
    }
        /**
     * <strong>Nombre:</strong> traducirTransferencia
     *
     * <p><strong>Objetivo:</strong> Traducir una transferencia (LOAD/ASIG/STORE_ARRAY) cargando el origen y guardándolo en el destino.</p>
     *
     * <p><strong>Entrada:</strong> Instruccion i.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Distingue entre enteros y flotantes.</p>
     */
    private void traducirTransferencia(Instruccion i) {
        if (esFloat(tipoOperando(i.op1, funcionActual))) {
            cargarFloat(i.op1, "$f0");
            guardar(i.resultado, null, "$f0");
            return;
        }
        String registro = cargarValor(i.op1);
        guardar(i.resultado, registro, null);
        registros.liberarRegistro(registro);
    }

    /**
     * <strong>Nombre:</strong> guardar
     *
     * <p><strong>Objetivo:</strong> Guardar un valor (entero o flotante) en el destino, sea una celda de arreglo o una variable.</p>
     *
     * <p><strong>Entrada:</strong> String destino, String registroEntero, String registroFloat.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Usa el registro entero o el flotante según el tipo del destino.</p>
     */
    private void guardar(String destino, String registroEntero, String registroFloat) {
        if (esAccesoArreglo(destino)) {
            direccionArreglo(destino, "$t7");
            instruccion((esFloat(tipoOperando(destino, funcionActual)) ? "s.s " + registroFloat
                    : "sw " + registroEntero) + ", 0($t7)");
        } else if (esFloat(tipoOperando(destino, funcionActual))) {
            instruccion("s.s " + registroFloat + ", " + etiqueta(destino));
        } else {
            instruccion("sw " + registroEntero + ", " + etiqueta(destino));
        }
    }

   /**
     * <strong>Nombre:</strong> traducirBinaria
     *
     * <p><strong>Objetivo:</strong> Traducir una operación binaria (aritmética, lógica o de comparación),
     * eligiendo la variante entera o flotante según los operandos.</p>
     *
     * <p><strong>Entrada:</strong> Instruccion i.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Para flotantes delega en rutinas específicas.</p>
     */
    private void traducirBinaria(Instruccion i) {
        boolean flotante = esFloat(tipoOperando(i.op1, funcionActual))
                || esFloat(tipoOperando(i.op2, funcionActual));
        if (flotante && esComparacion(i.op)) {
            traducirComparacionFloat(i);
            return;
        }
        if (flotante && esAritmetica(i.op)) {
            cargarFloat(i.op1, "$f0");
            cargarFloat(i.op2, "$f2");
            String operacion;
            switch (i.op) {
                case SUMA: operacion = "add.s"; break;
                case RESTA: operacion = "sub.s"; break;
                case MULT: operacion = "mul.s"; break;
                case DIV: operacion = "div.s"; break;
                case MOD:
                    instruccion("div.s $f4, $f0, $f2");
                    instruccion("trunc.w.s $f6, $f4");
                    instruccion("cvt.s.w $f6, $f6");
                    instruccion("mul.s $f6, $f6, $f2");
                    instruccion("sub.s $f4, $f0, $f6");
                    instruccion("s.s $f4, " + etiqueta(i.resultado));
                    return;
                case POW:
                    traducirPotenciaFloat(i.resultado);
                    return;
                default: throw new IllegalStateException("Operacion flotante no soportada: " + i.op);
            }
            instruccion(operacion + " $f4, $f0, $f2");
            instruccion("s.s $f4, " + etiqueta(i.resultado));
            return;
        }

        String izquierdo = cargarValor(i.op1);
        String derecho = cargarValor(i.op2);
        String resultado = registros.obtenerRegistro();
        switch (i.op) {
            case SUMA: instruccion("add " + resultado + ", " + izquierdo + ", " + derecho); break;
            case RESTA: instruccion("sub " + resultado + ", " + izquierdo + ", " + derecho); break;
            case MULT: instruccion("mul " + resultado + ", " + izquierdo + ", " + derecho); break;
            case DIV:
                instruccion("div " + izquierdo + ", " + derecho);
                instruccion("mflo " + resultado);
                break;
            case MOD:
                instruccion("div " + izquierdo + ", " + derecho);
                instruccion("mfhi " + resultado);
                break;
            case POW:
                traducirPotenciaEntera(izquierdo, derecho, resultado);
                break;
            case AND: instruccion("and " + resultado + ", " + izquierdo + ", " + derecho); break;
            case OR: instruccion("or " + resultado + ", " + izquierdo + ", " + derecho); break;
            case IGUAL: instruccion("seq " + resultado + ", " + izquierdo + ", " + derecho); break;
            case DISTINTO: instruccion("sne " + resultado + ", " + izquierdo + ", " + derecho); break;
            case MENOR: instruccion("slt " + resultado + ", " + izquierdo + ", " + derecho); break;
            case MAYOR: instruccion("sgt " + resultado + ", " + izquierdo + ", " + derecho); break;
            case MENOR_IGUAL: instruccion("sle " + resultado + ", " + izquierdo + ", " + derecho); break;
            case MAYOR_IGUAL: instruccion("sge " + resultado + ", " + izquierdo + ", " + derecho); break;
            default: throw new IllegalStateException("Operacion binaria no soportada: " + i.op);
        }
        instruccion("sw " + resultado + ", " + etiqueta(i.resultado));
        registros.liberarRegistro(resultado);
        registros.liberarRegistro(derecho);
        registros.liberarRegistro(izquierdo);
    }


       /**
     * <strong>Nombre:</strong> traducirComparacionFloat
     *
     * <p><strong>Objetivo:</strong> Traducir una comparación entre flotantes usando {@code c.*.s} y los
     * branches del coprocesador, dejando 0 o 1 en el resultado.</p>
     *
     * <p><strong>Entrada:</strong> Instruccion i.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void traducirComparacionFloat(Instruccion i) {
        cargarFloat(i.op1, "$f0");
        cargarFloat(i.op2, "$f2");
        String verdadero = nuevaEtiquetaInterna("cmp_true");
        String fin = nuevaEtiquetaInterna("cmp_fin");
        String resultado = registros.obtenerRegistro();
        instruccion("li " + resultado + ", 0");
        switch (i.op) {
            case IGUAL:
                instruccion("c.eq.s $f0, $f2");
                instruccion("bc1t " + verdadero);
                break;
            case DISTINTO:
                instruccion("c.eq.s $f0, $f2");
                instruccion("bc1f " + verdadero);
                break;
            case MENOR:
                instruccion("c.lt.s $f0, $f2");
                instruccion("bc1t " + verdadero);
                break;
            case MENOR_IGUAL:
                instruccion("c.le.s $f0, $f2");
                instruccion("bc1t " + verdadero);
                break;
            case MAYOR:
                instruccion("c.lt.s $f2, $f0");
                instruccion("bc1t " + verdadero);
                break;
            case MAYOR_IGUAL:
                instruccion("c.le.s $f2, $f0");
                instruccion("bc1t " + verdadero);
                break;
            default: throw new IllegalStateException("Comparacion flotante no soportada: " + i.op);
        }
        instruccion("j " + fin);
        salida.add(verdadero + ":");
        instruccion("li " + resultado + ", 1");
        salida.add(fin + ":");
        instruccion("sw " + resultado + ", " + etiqueta(i.resultado));
        registros.liberarRegistro(resultado);
    }

        /**
     * <strong>Nombre:</strong> traducirPotenciaEntera
     *
     * <p><strong>Objetivo:</strong> Calcular una potencia entera mediante un ciclo de multiplicaciones.</p>
     *
     * <p><strong>Entrada:</strong> String base, String exponente, String resultado (registros).</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Consume el registro del exponente como contador.</p>
     */
    private void traducirPotenciaEntera(String base, String exponente, String resultado) {
        String ciclo = nuevaEtiquetaInterna("pow");
        String fin = nuevaEtiquetaInterna("pow_fin");
        instruccion("li " + resultado + ", 1");
        salida.add(ciclo + ":");
        instruccion("blez " + exponente + ", " + fin);
        instruccion("mul " + resultado + ", " + resultado + ", " + base);
        instruccion("addiu " + exponente + ", " + exponente + ", -1");
        instruccion("j " + ciclo);
        salida.add(fin + ":");
    }

   /**
     * <strong>Nombre:</strong> traducirPotenciaFloat
     *
     * <p><strong>Objetivo:</strong> Calcular una potencia con base flotante mediante un ciclo de multiplicaciones.</p>
     *
     * <p><strong>Entrada:</strong> String resultado (etiqueta destino).</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> El exponente se toma truncado de {@code $f2}.</p>
     */
    private void traducirPotenciaFloat(String resultado) {
        String ciclo = nuevaEtiquetaInterna("powf");
        String fin = nuevaEtiquetaInterna("powf_fin");
        String contador = registros.obtenerRegistro();
        instruccion("li " + contador + ", 1");
        instruccion("mtc1 " + contador + ", $f4");
        instruccion("cvt.s.w $f4, $f4");
        instruccion("trunc.w.s $f6, $f2");
        instruccion("mfc1 " + contador + ", $f6");
        salida.add(ciclo + ":");
        instruccion("blez " + contador + ", " + fin);
        instruccion("mul.s $f4, $f4, $f0");
        instruccion("addiu " + contador + ", " + contador + ", -1");
        instruccion("j " + ciclo);
        salida.add(fin + ":");
        instruccion("s.s $f4, " + etiqueta(resultado));
        registros.liberarRegistro(contador);
    }


    /**
     * <strong>Nombre:</strong> traducirLlamada
     *
     * <p><strong>Objetivo:</strong> Emitir la llamada ({@code jal}), recuperar el espacio de los parámetros y
     * guardar el valor retornado ({@code $v0}) si la llamada produce resultado.</p>
     *
     * <p><strong>Entrada:</strong> Instruccion i.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Maneja por separado los retornos flotantes.</p>
     */
    private void traducirLlamada(Instruccion i) {
        instruccion("jal " + etiquetaFuncion(i.op1));
        int cantidad = parseEntero(i.op2, 0);
        if (cantidad > 0) {
            instruccion("addiu $sp, $sp, " + (cantidad * 4));
        }
        if (i.resultado != null) {
            if (esFloat(tipoOperando(i.resultado, funcionActual))) {
                instruccion("mtc1 $v0, $f0");
                instruccion("s.s $f0, " + etiqueta(i.resultado));
            } else {
                instruccion("sw $v0, " + etiqueta(i.resultado));
            }
        }
    }

        /**
     * <strong>Nombre:</strong> parseEntero
     *
     * <p><strong>Objetivo:</strong> Convertir un texto a int, devolviendo un valor por defecto si no es válido.</p>
     *
     * <p><strong>Entrada:</strong> String valor, int defecto.</p>
     *
     * <p><strong>Salida:</strong> int con el valor convertido o el defecto.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static int parseEntero(String valor, int defecto) {
        return OperandosMIPS.parseEntero(valor, defecto);
    }



    /**
     * <strong>Nombre:</strong> traducirRetorno
     *
     * <p><strong>Objetivo:</strong> Colocar el valor de retorno en {@code $v0} (o desde {@code $f0} si es flotante)
     * y saltar al epílogo de la función.</p>
     *
     * <p><strong>Entrada:</strong> Instruccion i.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void traducirRetorno(Instruccion i) {
        if (i.op1 != null) {
            if (esFloat(tipoOperando(i.op1, funcionActual))) {
                cargarFloat(i.op1, "$f0");
                instruccion("mfc1 $v0, $f0");
            } else {
                cargarEntero(i.op1, "$v0");
            }
        }
        instruccion("j " + etiquetaEpilogo(funcionActual));
    }

    /**
     * <strong>Nombre:</strong> traducirPrint
     *
     * <p><strong>Objetivo:</strong> Emitir la impresión del operando con el syscall adecuado según su tipo
     * (entero, cadena, carácter o flotante).</p>
     *
     * <p><strong>Entrada:</strong> String operando.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void traducirPrint(String operando) {
        String tipo = tipoOperando(operando, funcionActual);
        if ("string".equals(tipo)) {
            cargarEntero(operando, "$a0");
            instruccion("li $v0, 4");
        } else if ("char".equals(tipo)) {
            cargarEntero(operando, "$a0");
            instruccion("li $v0, 11");
        } else if (esFloat(tipo)) {
            cargarFloat(operando, "$f12");
            instruccion("li $v0, 2");
        } else {
            cargarEntero(operando, "$a0");
            instruccion("li $v0, 1");
        }
        instruccion("syscall");
    }
    /**
     * <strong>Nombre:</strong> traducirRead
     *
     * <p><strong>Objetivo:</strong> Emitir la lectura de un valor desde el usuario (syscall 5 o 6) y guardarlo en el destino.</p>
     *
     * <p><strong>Entrada:</strong> String destino.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Distingue entre lectura entera y flotante.</p>
     */
    private void traducirRead(String destino) {
        if (esFloat(tipoOperando(destino, funcionActual))) {
            instruccion("li $v0, 6");
            instruccion("syscall");
            guardar(destino, "$t0", "$f0");
        } else {
            instruccion("li $v0, 5");
            instruccion("syscall");
            guardar(destino, "$v0", "$f0");
        }
    }

    /**
     * <strong>Nombre:</strong> traducirUnaria
     *
     * <p><strong>Objetivo:</strong> Traducir una operación unaria: negación aritmética (entera o flotante) o negación lógica.</p>
     *
     * <p><strong>Entrada:</strong> Instruccion i.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void traducirUnaria(Instruccion i) {
        if (i.op == Operacion.NEG && esFloat(tipoOperando(i.op1, funcionActual))) {
            cargarFloat(i.op1, "$f0");
            instruccion("neg.s $f2, $f0");
            instruccion("s.s $f2, " + etiqueta(i.resultado));
            return;
        }
        String operando = cargarValor(i.op1);
        String resultado = registros.obtenerRegistro();
        if (i.op == Operacion.NEG) {
            instruccion("sub " + resultado + ", $zero, " + operando);
        } else {
            instruccion("seq " + resultado + ", " + operando + ", $zero");
        }
        instruccion("sw " + resultado + ", " + etiqueta(i.resultado));
        registros.liberarRegistro(resultado);
        registros.liberarRegistro(operando);
    }
}
