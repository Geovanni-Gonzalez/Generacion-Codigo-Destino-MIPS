package mips;

import intermedio.Instruccion;
import java.util.Map;

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

    void iniciarFuncion(String nombre) {
        salida.add("");
        salida.add(EtiquetasMIPS.etiquetaFuncion(nombre) + ":");
        if (!"__main__".equals(nombre)) {
            salida.instruccion("addiu $sp, $sp, -4");
            salida.instruccion("sw $ra, 0($sp)");
        }
    }

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

    int traducirParametroFormal(Instruccion i, String funcion, int indiceParametroFormal) {
        int total = parametrosFuncion.getOrDefault(funcion, 0);
        int desplazamiento = 4 * (total - indiceParametroFormal);
        String registro = registros.obtenerRegistro();
        salida.instruccion("lw " + registro + ", " + desplazamiento + "($sp)");
        salida.instruccion("sw " + registro + ", " + memoria.etiqueta(i.resultado, funcion));
        registros.liberarRegistro(registro);
        return indiceParametroFormal + 1;
    }

    void traducirParametro(Instruccion i, String funcion) {
        String argumento = i.op1 != null ? i.op1 : i.resultado;
        String registroParametro = registros.obtenerRegistro();
        if (OperandosMIPS.esFloat(memoria.tipoOperando(argumento, funcion))) {
            memoria.cargarFloat(argumento, "$f0", funcion);
            salida.instruccion("mfc1 " + registroParametro + ", $f0");
        } else {
            memoria.cargarEntero(argumento, registroParametro, funcion);
        }
        salida.instruccion("addiu $sp, $sp, -4");
        salida.instruccion("sw " + registroParametro + ", 0($sp)");
        registros.liberarRegistro(registroParametro);
    }

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
            salida.instruccion("mtc1 $v0, $f0");
            salida.instruccion("s.s $f0, " + memoria.etiqueta(i.resultado, funcion));
        } else {
            salida.instruccion("sw $v0, " + memoria.etiqueta(i.resultado, funcion));
        }
    }

    void traducirRetorno(Instruccion i, String funcion) {
        if (i.op1 != null) {
            if (OperandosMIPS.esFloat(memoria.tipoOperando(i.op1, funcion))) {
                memoria.cargarFloat(i.op1, "$f0", funcion);
                salida.instruccion("mfc1 $v0, $f0");
            } else {
                memoria.cargarEntero(i.op1, "$v0", funcion);
            }
        }
        salida.instruccion("j " + EtiquetasMIPS.etiquetaEpilogo(funcion));
    }
}
