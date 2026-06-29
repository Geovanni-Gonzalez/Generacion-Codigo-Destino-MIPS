package mips;

import intermedio.Instruccion;

/**
 * Nombre: TraductorObjetosMIPS
 *
 * Objetivo: Traducir las operaciones de objetos (instanciacion y acceso a campos) a codigo MIPS.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
final class TraductorObjetosMIPS {
    private final EmisorMIPS salida;
    private final AdministradorRegistros registros;
    private final TraductorMemoriaMIPS memoria;

    /**
     * Nombre: TraductorObjetosMIPS
     *
     * Objetivo: Inicializar una instancia de TraductorObjetosMIPS con los datos requeridos.
     *
     * Entrada: EmisorMIPS salida; AdministradorRegistros registros; TraductorMemoriaMIPS memoria.
     *
     * Salida: Nueva instancia de TraductorObjetosMIPS.
     *
     * Restricciones: Ninguna.
     */
    TraductorObjetosMIPS(EmisorMIPS salida, AdministradorRegistros registros,
                         TraductorMemoriaMIPS memoria) {
        this.salida = salida;
        this.registros = registros;
        this.memoria = memoria;
    }

    /**
     * Nombre: traducirNuevo
     *
     * Objetivo: Reservar en heap (syscall 9) el bloque de un objeto y guardar su puntero en el destino.
     *
     * Entrada: Instruccion i; String funcion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: i.op2 es el tamaño en bytes; i.resultado es la variable/temporal puntero.
     */
    void traducirNuevo(Instruccion i, String funcion) {
        int tamano = OperandosMIPS.parseEntero(i.op2, 4);
        salida.instruccion("li $v0, 9");
        salida.instruccion("li $a0, " + tamano);
        salida.instruccion("syscall");
        salida.instruccion("sw $v0, " + memoria.etiqueta(i.resultado, funcion));
    }

    /**
     * Nombre: traducirCargaCampo
     *
     * Objetivo: Leer un campo del objeto (offset desde el puntero) y guardarlo en el destino.
     *
     * Entrada: Instruccion i; String funcion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: i.resultado destino, i.op1 objeto, i.op2 "offset:tipo".
     */
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

    /**
     * Nombre: traducirGuardaCampo
     *
     * Objetivo: Escribir un valor en un campo del objeto (offset desde el puntero).
     *
     * Entrada: Instruccion i; String funcion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: i.resultado objeto, i.op1 valor, i.op2 "offset:tipo".
     */
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

    /**
     * Nombre: offset
     *
     * Objetivo: Extraer el offset numerico de una referencia de campo con formato "offset:tipo".
     *
     * Entrada: String referencia.
     *
     * Salida: Valor de tipo int.
     *
     * Restricciones: Uso interno de la clase.
     */
    private int offset(String referencia) {
        if (referencia == null) {
            return 0;
        }
        int separador = referencia.indexOf(':');
        String numero = separador >= 0 ? referencia.substring(0, separador) : referencia;
        return OperandosMIPS.parseEntero(numero, 0);
    }

    /**
     * Nombre: esFloat
     *
     * Objetivo: Indicar si la referencia de campo corresponde a un campo de tipo float.
     *
     * Entrada: String referencia.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Uso interno de la clase.
     */
    private boolean esFloat(String referencia) {
        return referencia != null && referencia.endsWith(":float");
    }
}
