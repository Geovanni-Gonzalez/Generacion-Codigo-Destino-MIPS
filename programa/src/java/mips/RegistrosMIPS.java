package mips;

/**
 * <strong>Nombre:</strong> RegistrosMIPS
 *
 * <p><strong>Objetivo:</strong> Ser la única fuente de verdad del convenio de uso de registros del
 * generador MIPS. Documenta y nombra los registros que hasta ahora estaban dispersos como literales
 * (por ejemplo {@code "$t7"}) en los traductores, para evitar colisiones difíciles de rastrear.</p>
 *
 * <p><strong>Convenio:</strong></p>
 * <ul>
 *   <li><b>Banco temporal administrado</b> ({@link #POOL_TEMPORALES}, {@code $t0}–{@code $t5}):
 *       lo reparte y libera {@link AdministradorRegistros}. Mantienen valores vivos entre operaciones.</li>
 *   <li><b>Scratch entero</b> ({@code $t6}–{@code $t9}): registros de apoyo de muy corta vida usados
 *       dentro de una sola instrucción traducida (cálculo de direcciones de arreglos, conversiones).
 *       Nunca deben solaparse con el banco administrado.</li>
 *   <li><b>Scratch de punto flotante</b> ({@code $f0}–{@code $f6}): registros de apoyo para aritmética
 *       y comparación flotante dentro de una sola instrucción.</li>
 *   <li><b>Argumento flotante</b> ({@code $f12}): convenio de SPIM/MARS para imprimir flotantes.</li>
 * </ul>
 *
 * <p><strong>Restricciones:</strong> Clase de constantes; no se instancia.</p>
 */
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
