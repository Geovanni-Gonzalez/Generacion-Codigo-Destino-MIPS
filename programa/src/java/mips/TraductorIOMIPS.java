package mips;

/**
 * Nombre: TraductorIOMIPS
 *
 * Objetivo: Analizar, traducir, emitir u optimizar codigo destino MIPS.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
final class TraductorIOMIPS {
    private final EmisorMIPS salida;
    private final TraductorMemoriaMIPS memoria;

    /**
     * Nombre: TraductorIOMIPS
     *
     * Objetivo: Inicializar una instancia de TraductorIOMIPS con los datos requeridos.
     *
     * Entrada: EmisorMIPS salida; TraductorMemoriaMIPS memoria.
     *
     * Salida: Nueva instancia de TraductorIOMIPS.
     *
     * Restricciones: Ninguna.
     */
    TraductorIOMIPS(EmisorMIPS salida, TraductorMemoriaMIPS memoria) {
        this.salida = salida;
        this.memoria = memoria;
    }

    /**
     * Nombre: traducirPrint
     *
     * Objetivo: Convertir una instruccion o construccion intermedia a codigo MIPS.
     *
     * Entrada: String operando; String funcion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    void traducirPrint(String operando, String funcion) {
        String tipo = memoria.tipoOperando(operando, funcion);
        if ("string".equals(tipo)) {
            memoria.cargarEntero(operando, "$a0", funcion);
            salida.instruccion("li $v0, 4");
        } else if ("char".equals(tipo)) {
            memoria.cargarEntero(operando, "$a0", funcion);
            salida.instruccion("li $v0, 11");
        } else if (OperandosMIPS.esFloat(tipo)) {
            memoria.cargarFloat(operando, RegistrosMIPS.ARG_FLOAT, funcion);
            salida.instruccion("li $v0, 2");
        } else {
            memoria.cargarEntero(operando, "$a0", funcion);
            salida.instruccion("li $v0, 1");
        }
        salida.instruccion("syscall");
    }

    /**
     * Nombre: traducirRead
     *
     * Objetivo: Convertir una instruccion o construccion intermedia a codigo MIPS.
     *
     * Entrada: String destino; String funcion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    void traducirRead(String destino, String funcion) {
        if (OperandosMIPS.esFloat(memoria.tipoOperando(destino, funcion))) {
            salida.instruccion("li $v0, 6");
            salida.instruccion("syscall");
            memoria.guardar(destino, "$t0", RegistrosMIPS.SCRATCH_FLOAT_A, funcion);
        } else {
            salida.instruccion("li $v0, 5");
            salida.instruccion("syscall");
            memoria.guardar(destino, "$v0", RegistrosMIPS.SCRATCH_FLOAT_A, funcion);
        }
    }
}
