package mips;

import intermedio.Instruccion;

final class TraductorTransferenciaMIPS {
    private final AdministradorRegistros registros;
    private final TraductorMemoriaMIPS memoria;

    TraductorTransferenciaMIPS(AdministradorRegistros registros, TraductorMemoriaMIPS memoria) {
        this.registros = registros;
        this.memoria = memoria;
    }

    void traducir(Instruccion i, String funcion) {
        if (OperandosMIPS.esFloat(memoria.tipoOperando(i.op1, funcion))) {
            memoria.cargarFloat(i.op1, "$f0", funcion);
            memoria.guardar(i.resultado, null, "$f0", funcion);
            return;
        }

        String registro = memoria.cargarValor(i.op1, funcion);
        memoria.guardar(i.resultado, registro, null, funcion);
        registros.liberarRegistro(registro);
    }
}
