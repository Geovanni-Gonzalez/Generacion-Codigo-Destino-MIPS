package mips;

final class RegistrosMIPS {

    /** Banco de temporales administrado por {@link AdministradorRegistros}. */
    static final String[] POOL_TEMPORALES = {"$t0", "$t1", "$t2", "$t3", "$t4", "$t5"};

    // Scratch entero de corta vida (no administrado, no debe solaparse con POOL_TEMPORALES).
    static final String SCRATCH_ENTERO_A = "$t6";
    static final String SCRATCH_DIRECCION = "$t7";
    static final String SCRATCH_INDICE_FILA = "$t8";
    static final String SCRATCH_INDICE_COL = "$t9";

    // Scratch de punto flotante de corta vida.
    static final String SCRATCH_FLOAT_A = "$f0";
    static final String SCRATCH_FLOAT_B = "$f2";
    static final String SCRATCH_FLOAT_RES = "$f4";
    static final String SCRATCH_FLOAT_AUX = "$f6";

    /** Registro de argumento flotante para syscalls de impresión (convenio SPIM/MARS). */
    static final String ARG_FLOAT = "$f12";

    private RegistrosMIPS() {
    }
}
