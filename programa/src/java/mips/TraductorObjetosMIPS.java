package mips;

import intermedio.Instruccion;

final class TraductorObjetosMIPS {
    private final EmisorMIPS salida;
    private final AdministradorRegistros registros;
    private final TraductorMemoriaMIPS memoria;

    TraductorObjetosMIPS(EmisorMIPS salida, AdministradorRegistros registros,
                         TraductorMemoriaMIPS memoria) {
        this.salida = salida;
        this.registros = registros;
        this.memoria = memoria;
    }

    void traducirNuevo(Instruccion i, String funcion) {
        int tamano = OperandosMIPS.parseEntero(i.op2, 4);
        salida.instruccion("li $v0, 9");
        salida.instruccion("li $a0, " + tamano);
        salida.instruccion("syscall");
        salida.instruccion("sw $v0, " + memoria.etiqueta(i.resultado, funcion));
    }

    void traducirCargaCampo(Instruccion i, String funcion) {
        int offset = offset(i.op2);
        String puntero = memoria.cargarValor(i.op1, funcion);
        if (esFloat(i.op2)) {
            salida.instruccion("l.s " + RegistrosMIPS.SCRATCH_FLOAT_A + ", " + offset + "(" + puntero + ")");
            memoria.guardar(i.resultado, null, RegistrosMIPS.SCRATCH_FLOAT_A, funcion);
        } else {
            String valor = registros.obtenerRegistro();
            salida.instruccion("lw " + valor + ", " + offset + "(" + puntero + ")");
            memoria.guardar(i.resultado, valor, null, funcion);
            registros.liberarRegistro(valor);
        }
        registros.liberarRegistro(puntero);
    }

    void traducirGuardaCampo(Instruccion i, String funcion) {
        int offset = offset(i.op2);
        String puntero = memoria.cargarValor(i.resultado, funcion);
        if (esFloat(i.op2)) {
            memoria.cargarFloat(i.op1, RegistrosMIPS.SCRATCH_FLOAT_A, funcion);
            salida.instruccion("s.s " + RegistrosMIPS.SCRATCH_FLOAT_A + ", " + offset + "(" + puntero + ")");
        } else {
            String valor = memoria.cargarValor(i.op1, funcion);
            salida.instruccion("sw " + valor + ", " + offset + "(" + puntero + ")");
            registros.liberarRegistro(valor);
        }
        registros.liberarRegistro(puntero);
    }

    private int offset(String referencia) {
        if (referencia == null) {
            return 0;
        }
        int separador = referencia.indexOf(':');
        String numero = separador >= 0 ? referencia.substring(0, separador) : referencia;
        return OperandosMIPS.parseEntero(numero, 0);
    }

    private boolean esFloat(String referencia) {
        return referencia != null && referencia.endsWith(":float");
    }
}
