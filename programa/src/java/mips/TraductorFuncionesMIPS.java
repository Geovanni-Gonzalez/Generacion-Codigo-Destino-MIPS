package mips;

import intermedio.Instruccion;
import java.util.Map;

/**
 * Nombre: TraductorFuncionesMIPS
 *
 * Objetivo: Analizar, traducir, emitir u optimizar codigo destino MIPS.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
final class TraductorFuncionesMIPS {
    private final EmisorMIPS salida;
    private final AdministradorRegistros registros;
    private final TraductorMemoriaMIPS memoria;
    private final Map<String, Integer> parametrosFuncion;

    TraductorFuncionesMIPS(EmisorMIPS salida, AdministradorRegistros registros,
            TraductorMemoriaMIPS memoria, Map<String, Integer> parametrosFuncion) {
        this.salida = salida;
        this.registros = registros;
        this.memoria = memoria;
        this.parametrosFuncion = parametrosFuncion;
    }

    /**
     * Nombre: iniciarFuncion
     *
     * Objetivo: Ejecutar la operacion iniciarFuncion definida por TraductorFuncionesMIPS.
     *
     * Entrada: String nombre.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    void iniciarFuncion(String nombre) {
        salida.add("");
        salida.add(EtiquetasMIPS.etiquetaFuncion(nombre) + ":");
        if (!"__main__".equals(nombre)) {
            salida.instruccion("addiu $sp, $sp, -4");
            salida.instruccion("sw $ra, 0($sp)");
        }
    }

    /**
     * Nombre: finalizarFuncion
     *
     * Objetivo: Ejecutar la operacion finalizarFuncion definida por TraductorFuncionesMIPS.
     *
     * Entrada: String nombre.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    void finalizarFuncion(String nombre) {
        salida.add(EtiquetasMIPS.etiquetaEpilogo(nombre) + ":");
        if ("__main__".equals(nombre)) {
            salida.instruccion("li $v0, 10");
            salida.instruccion("syscall");
            return;
        }

        salida.instruccion("lw $ra, 0($sp)");
        salida.instruccion("addiu $sp, $sp, 4");
        salida.instruccion("jr $ra");
    }

    /**
     * Nombre: traducirParametroFormal
     *
     * Objetivo: Convertir una instruccion o construccion intermedia a codigo MIPS.
     *
     * Entrada: Instruccion i; String funcion; int indiceParametroFormal.
     *
     * Salida: Valor de tipo int.
     *
     * Restricciones: Ninguna.
     */
    int traducirParametroFormal(Instruccion i, String funcion, int indiceParametroFormal) {
        int total = parametrosFuncion.getOrDefault(funcion, 0);
        int desplazamiento = 4 * (total - indiceParametroFormal);
        String registro = registros.obtenerRegistro();
        salida.instruccion("lw " + registro + ", " + desplazamiento + "($sp)");
        salida.instruccion("sw " + registro + ", " + memoria.etiqueta(i.resultado, funcion));
        registros.liberarRegistro(registro);
        return indiceParametroFormal + 1;
    }

    /**
     * Nombre: traducirParametro
     *
     * Objetivo: Convertir una instruccion o construccion intermedia a codigo MIPS.
     *
     * Entrada: Instruccion i; String funcion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    void traducirParametro(Instruccion i, String funcion) {
        String argumento = i.op1 != null ? i.op1 : i.resultado;
        String registroParametro = registros.obtenerRegistro();
        if (OperandosMIPS.esFloat(memoria.tipoOperando(argumento, funcion))) {
            memoria.cargarFloat(argumento, RegistrosMIPS.SCRATCH_FLOAT_A, funcion);
            salida.instruccion("mfc1 " + registroParametro + ", " + RegistrosMIPS.SCRATCH_FLOAT_A);
        } else {
            memoria.cargarEntero(argumento, registroParametro, funcion);
        }
        salida.instruccion("addiu $sp, $sp, -4");
        salida.instruccion("sw " + registroParametro + ", 0($sp)");
        registros.liberarRegistro(registroParametro);
    }

    /**
     * Nombre: traducirLlamada
     *
     * Objetivo: Convertir una instruccion o construccion intermedia a codigo MIPS.
     *
     * Entrada: Instruccion i; String funcion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    void traducirLlamada(Instruccion i, String funcion) {
        salida.instruccion("jal " + EtiquetasMIPS.etiquetaFuncion(i.op1));
        int cantidad = OperandosMIPS.parseEntero(i.op2, 0);
        if (cantidad > 0) {
            salida.instruccion("addiu $sp, $sp, " + (cantidad * 4));
        }
        if (i.resultado == null) {
            return;
        }

        if (OperandosMIPS.esFloat(memoria.tipoOperando(i.resultado, funcion))) {
            salida.instruccion("mtc1 $v0, " + RegistrosMIPS.SCRATCH_FLOAT_A);
            salida.instruccion("s.s " + RegistrosMIPS.SCRATCH_FLOAT_A + ", "
                    + memoria.etiqueta(i.resultado, funcion));
        } else {
            salida.instruccion("sw $v0, " + memoria.etiqueta(i.resultado, funcion));
        }
    }

    /**
     * Nombre: traducirRetorno
     *
     * Objetivo: Convertir una instruccion o construccion intermedia a codigo MIPS.
     *
     * Entrada: Instruccion i; String funcion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    void traducirRetorno(Instruccion i, String funcion) {
        if (i.op1 != null) {
            if (OperandosMIPS.esFloat(memoria.tipoOperando(i.op1, funcion))) {
                memoria.cargarFloat(i.op1, RegistrosMIPS.SCRATCH_FLOAT_A, funcion);
                salida.instruccion("mfc1 $v0, " + RegistrosMIPS.SCRATCH_FLOAT_A);
            } else {
                memoria.cargarEntero(i.op1, "$v0", funcion);
            }
        }
        salida.instruccion("j " + EtiquetasMIPS.etiquetaEpilogo(funcion));
    }
}
