package mips;

import intermedio.Instruccion;
import intermedio.Operacion;

/**
 * Traduce etiquetas y saltos de control de flujo a MIPS.
 */
final class TraductorControlMIPS {
    private final EmisorMIPS salida;
    private final AdministradorRegistros registros;
    private final TraductorMemoriaMIPS memoria;

    TraductorControlMIPS(EmisorMIPS salida, AdministradorRegistros registros,
                         TraductorMemoriaMIPS memoria) {
        this.salida = salida;
        this.registros = registros;
        this.memoria = memoria;
    }

    boolean puedeFusionarSalto(Instruccion comparacion, Instruccion salto, String funcion) {
        return OperandosMIPS.esComparacion(comparacion.op)
                && salto.op == Operacion.IF_FALSE
                && comparacion.resultado != null
                && comparacion.resultado.equals(salto.op1)
                && !OperandosMIPS.esFloat(memoria.tipoOperando(comparacion.op1, funcion))
                && !OperandosMIPS.esFloat(memoria.tipoOperando(comparacion.op2, funcion));
    }

    void traducirSaltoComparacion(Instruccion comparacion, String destino, String funcion) {
        String izquierdo = memoria.cargarValor(comparacion.op1, funcion);
        String derecho = memoria.cargarValor(comparacion.op2, funcion);
        String operacion;
        switch (comparacion.op) {
            case IGUAL: operacion = "bne"; break;
            case DISTINTO: operacion = "beq"; break;
            case MENOR: operacion = "bge"; break;
            case MENOR_IGUAL: operacion = "bgt"; break;
            case MAYOR: operacion = "ble"; break;
            case MAYOR_IGUAL: operacion = "blt"; break;
            default: throw new IllegalStateException("Comparacion no soportada en salto: " + comparacion.op);
        }
        salida.instruccion(operacion + " " + izquierdo + ", " + derecho + ", "
                + EtiquetasMIPS.etiquetaCodigo(destino));
        registros.liberarRegistro(derecho);
        registros.liberarRegistro(izquierdo);
    }

    void traducirLabel(String nombre) {
        salida.add(EtiquetasMIPS.etiquetaCodigo(nombre) + ":");
    }

    void traducirGoto(String destino) {
        salida.instruccion("j " + EtiquetasMIPS.etiquetaCodigo(destino));
    }

    void traducirIfFalse(String condicionOperando, String destino, String funcion) {
        String condicion = memoria.cargarValor(condicionOperando, funcion);
        salida.instruccion("beq " + condicion + ", $zero, " + EtiquetasMIPS.etiquetaCodigo(destino));
        registros.liberarRegistro(condicion);
    }
}
