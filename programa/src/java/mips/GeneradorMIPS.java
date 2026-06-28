package mips;

import intermedio.Instruccion;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pipeline.CompiladorInternoException;

/**
 * Nombre: GeneradorMIPS
 *
 * Objetivo: Analizar, traducir, emitir u optimizar codigo destino MIPS.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public final class GeneradorMIPS {
    private final EmisorMIPS salida = new EmisorMIPS();
    private final AdministradorRegistros registros = new AdministradorRegistros();
    private final Map<String, String> tipos = new LinkedHashMap<>();
    private final Map<String, String> direcciones = new LinkedHashMap<>();
    private final Map<String, Integer> columnasArreglo = new LinkedHashMap<>();
    private final Map<String, String> cadenas = new LinkedHashMap<>();
    private final Map<String, String> flotantes = new LinkedHashMap<>();
    private final Map<String, Integer> parametrosFuncion = new LinkedHashMap<>();
    private final Map<String, String> retornosFuncion = new LinkedHashMap<>();
    private final Map<String, Integer> dimensionesDeclaradas = new LinkedHashMap<>();
    private final TraductorMemoriaMIPS memoria = new TraductorMemoriaMIPS(salida, registros, tipos,
            direcciones, columnasArreglo, cadenas, flotantes);
    private final TraductorTransferenciaMIPS transferencias =
            new TraductorTransferenciaMIPS(registros, memoria);
    private final TraductorOperacionesMIPS operaciones =
            new TraductorOperacionesMIPS(salida, registros, memoria, this::nuevaEtiquetaInterna);
    private final TraductorControlMIPS control = new TraductorControlMIPS(salida, registros, memoria);
    private final TraductorIOMIPS io = new TraductorIOMIPS(salida, memoria);
    private final TraductorFuncionesMIPS funciones =
            new TraductorFuncionesMIPS(salida, registros, memoria, parametrosFuncion);

    private int contadorEtiquetaInterna;
    private String funcionActual;
    private int indiceParametroFormal;

    /**
     * Nombre: generarCodigo
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: List<Instruccion> codigoIntermedio.
     *
     * Salida: Valor de tipo List<String>.
     *
     * Restricciones: Ninguna.
     */
    public List<String> generarCodigo(List<Instruccion> codigoIntermedio) {
        reiniciar();
        aplicarAnalisis(new AnalizadorIRMIPS().analizar(codigoIntermedio));
        emitirDatos();
        salida.add(".text");
        salida.add(".globl main");
        traducir(codigoIntermedio);
        return new OptimizadorMIPS().optimizar(salida.lineas());
    }

    /**
     * Nombre: reiniciar
     *
     * Objetivo: Restablecer el estado interno a sus valores iniciales.
     *
     * Entrada: Ninguna.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
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

    /**
     * Nombre: aplicarAnalisis
     *
     * Objetivo: Ejecutar la operacion aplicarAnalisis definida por GeneradorMIPS.
     *
     * Entrada: ResultadoAnalisisMIPS analisis.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
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
     * Nombre: emitirDatos
     *
     * Objetivo: Agregar lineas o instrucciones al artefacto de salida correspondiente.
     *
     * Entrada: Ninguna.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void emitirDatos() {
        new EmisorDatosMIPS().emitir(salida, tipos, direcciones, columnasArreglo,
                dimensionesDeclaradas, cadenas, flotantes);
    }

    /**
     * Nombre: traducir
     *
     * Objetivo: Convertir una instruccion o construccion intermedia a codigo MIPS.
     *
     * Entrada: List<Instruccion> codigo.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void traducir(List<Instruccion> codigo) {
        funcionActual = null;
        for (int indice = 0; indice < codigo.size(); indice++) {
            Instruccion i = codigo.get(indice);
            if (indice + 1 < codigo.size() && puedeFusionarSalto(i, codigo.get(indice + 1))) {
                control.traducirSaltoComparacion(i, codigo.get(indice + 1).resultado, funcionActual);
                indice++;
                continue;
            }
            traducirInstruccion(i);
        }
    }

    /**
     * Nombre: puedeFusionarSalto
     *
     * Objetivo: Ejecutar la operacion puedeFusionarSalto definida por GeneradorMIPS.
     *
     * Entrada: Instruccion comparacion; Instruccion salto.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Uso interno de la clase.
     */
    private boolean puedeFusionarSalto(Instruccion comparacion, Instruccion salto) {
        return control.puedeFusionarSalto(comparacion, salto, funcionActual);
    }

    /**
     * Nombre: traducirInstruccion
     *
     * Objetivo: Convertir una instruccion o construccion intermedia a codigo MIPS.
     *
     * Entrada: Instruccion i.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void traducirInstruccion(Instruccion i) {
        switch (i.op) {
            case INICIO_FUNC:
                funcionActual = i.resultado;
                indiceParametroFormal = 0;
                funciones.iniciarFuncion(i.resultado);
                break;
            case FIN_FUNC:
                funciones.finalizarFuncion(i.resultado);
                funcionActual = null;
                break;
            case DECL:
            case DECL_ARRAY:
                break;
            case FORMAL_PARAM:
                indiceParametroFormal = funciones.traducirParametroFormal(i, funcionActual, indiceParametroFormal);
                break;
            case LOAD:
            case ASIG:
            case STORE_ARRAY:
                transferencias.traducir(i, funcionActual);
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
                operaciones.traducirBinaria(i, funcionActual);
                break;
            case NEG:
            case NOT:
                operaciones.traducirUnaria(i, funcionActual);
                break;
            case LABEL:
                control.traducirLabel(i.resultado);
                break;
            case GOTO:
                control.traducirGoto(i.resultado);
                break;
            case IF_FALSE:
                control.traducirIfFalse(i.op1, i.resultado, funcionActual);
                break;
            case PARAM:
                funciones.traducirParametro(i, funcionActual);
                break;
            case CALL:
                funciones.traducirLlamada(i, funcionActual);
                break;
            case RETURN:
                funciones.traducirRetorno(i, funcionActual);
                break;
            case PRINT:
                io.traducirPrint(i.op1 != null ? i.op1 : i.resultado, funcionActual);
                break;
            case READ:
                io.traducirRead(i.op1 != null ? i.op1 : i.resultado, funcionActual);
                break;
            default:
                throw new CompiladorInternoException("Operacion MIPS no implementada: " + i.op);
        }
    }

    /**
     * Nombre: nuevaEtiquetaInterna
     *
     * Objetivo: Ejecutar la operacion nuevaEtiquetaInterna definida por GeneradorMIPS.
     *
     * Entrada: String prefijo.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private String nuevaEtiquetaInterna(String prefijo) {
        return EtiquetasMIPS.etiquetaInterna(prefijo, contadorEtiquetaInterna++);
    }
}
