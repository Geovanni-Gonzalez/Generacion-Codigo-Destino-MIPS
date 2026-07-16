package mips;

final class TraductorIOMIPS {
    private final EmisorMIPS salida;
    private final TraductorMemoriaMIPS memoria;

    TraductorIOMIPS(EmisorMIPS salida, TraductorMemoriaMIPS memoria) {
        this.salida = salida;
        this.memoria = memoria;
    }

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
