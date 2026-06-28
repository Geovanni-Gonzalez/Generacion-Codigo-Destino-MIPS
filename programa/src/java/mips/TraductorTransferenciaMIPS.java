package mips;

import intermedio.Instruccion;

/**
 * Nombre: TraductorTransferenciaMIPS
 *
 * Objetivo: Analizar, traducir, emitir u optimizar codigo destino MIPS.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
final class TraductorTransferenciaMIPS {
    private final AdministradorRegistros registros;
    private final TraductorMemoriaMIPS memoria;

    /**
     * Nombre: TraductorTransferenciaMIPS
     *
     * Objetivo: Inicializar una instancia de TraductorTransferenciaMIPS con los datos requeridos.
     *
     * Entrada: AdministradorRegistros registros; TraductorMemoriaMIPS memoria.
     *
     * Salida: Nueva instancia de TraductorTransferenciaMIPS.
     *
     * Restricciones: Ninguna.
     */
    TraductorTransferenciaMIPS(AdministradorRegistros registros, TraductorMemoriaMIPS memoria) {
        this.registros = registros;
        this.memoria = memoria;
    }

    /**
     * Nombre: traducir
     *
     * Objetivo: Convertir una instruccion o construccion intermedia a codigo MIPS.
     *
     * Entrada: Instruccion i; String funcion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    void traducir(Instruccion i, String funcion) {
        if (OperandosMIPS.esFloat(memoria.tipoOperando(i.op1, funcion))) {
            memoria.cargarFloat(i.op1, RegistrosMIPS.SCRATCH_FLOAT_A, funcion);
            memoria.guardar(i.resultado, null, RegistrosMIPS.SCRATCH_FLOAT_A, funcion);
            return;
        }

        String registro = memoria.cargarValor(i.op1, funcion);
        memoria.guardar(i.resultado, registro, null, funcion);
        registros.liberarRegistro(registro);
    }
}
