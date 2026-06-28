package mips;

import intermedio.Instruccion;
import intermedio.Operacion;
import java.util.function.Function;

/**
 * Nombre: TraductorOperacionesMIPS
 *
 * Objetivo: Analizar, traducir, emitir u optimizar codigo destino MIPS.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
final class TraductorOperacionesMIPS {
    private final EmisorMIPS salida;
    private final AdministradorRegistros registros;
    private final TraductorMemoriaMIPS memoria;
    private final Function<String, String> nuevaEtiqueta;

    TraductorOperacionesMIPS(EmisorMIPS salida, AdministradorRegistros registros,
                             TraductorMemoriaMIPS memoria, Function<String, String> nuevaEtiqueta) {
        this.salida = salida;
        this.registros = registros;
        this.memoria = memoria;
        this.nuevaEtiqueta = nuevaEtiqueta;
    }

    /**
     * Nombre: traducirBinaria
     *
     * Objetivo: Convertir una instruccion o construccion intermedia a codigo MIPS.
     *
     * Entrada: Instruccion i; String funcion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    void traducirBinaria(Instruccion i, String funcion) {
        boolean flotante = OperandosMIPS.esFloat(memoria.tipoOperando(i.op1, funcion))
                || OperandosMIPS.esFloat(memoria.tipoOperando(i.op2, funcion));
        if (flotante && OperandosMIPS.esComparacion(i.op)) {
            traducirComparacionFloat(i, funcion);
            return;
        }
        if (flotante && OperandosMIPS.esAritmetica(i.op)) {
            traducirBinariaFloat(i, funcion);
            return;
        }

        String izquierdo = memoria.cargarValor(i.op1, funcion);
        String derecho = memoria.cargarValor(i.op2, funcion);
        String resultado = registros.obtenerRegistro();
        switch (i.op) {
            case SUMA: salida.instruccion("add " + resultado + ", " + izquierdo + ", " + derecho); break;
            case RESTA: salida.instruccion("sub " + resultado + ", " + izquierdo + ", " + derecho); break;
            case MULT: salida.instruccion("mul " + resultado + ", " + izquierdo + ", " + derecho); break;
            case DIV:
                salida.instruccion("div " + izquierdo + ", " + derecho);
                salida.instruccion("mflo " + resultado);
                break;
            case MOD:
                salida.instruccion("div " + izquierdo + ", " + derecho);
                salida.instruccion("mfhi " + resultado);
                break;
            case POW:
                traducirPotenciaEntera(izquierdo, derecho, resultado);
                break;
            case AND: salida.instruccion("and " + resultado + ", " + izquierdo + ", " + derecho); break;
            case OR: salida.instruccion("or " + resultado + ", " + izquierdo + ", " + derecho); break;
            case IGUAL: salida.instruccion("seq " + resultado + ", " + izquierdo + ", " + derecho); break;
            case DISTINTO: salida.instruccion("sne " + resultado + ", " + izquierdo + ", " + derecho); break;
            case MENOR: salida.instruccion("slt " + resultado + ", " + izquierdo + ", " + derecho); break;
            case MAYOR: salida.instruccion("sgt " + resultado + ", " + izquierdo + ", " + derecho); break;
            case MENOR_IGUAL: salida.instruccion("sle " + resultado + ", " + izquierdo + ", " + derecho); break;
            case MAYOR_IGUAL: salida.instruccion("sge " + resultado + ", " + izquierdo + ", " + derecho); break;
            default: throw new IllegalStateException("Operacion binaria no soportada: " + i.op);
        }
        salida.instruccion("sw " + resultado + ", " + memoria.etiqueta(i.resultado, funcion));
        registros.liberarRegistro(resultado);
        registros.liberarRegistro(derecho);
        registros.liberarRegistro(izquierdo);
    }

    /**
     * Nombre: traducirUnaria
     *
     * Objetivo: Convertir una instruccion o construccion intermedia a codigo MIPS.
     *
     * Entrada: Instruccion i; String funcion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    void traducirUnaria(Instruccion i, String funcion) {
        if (i.op == Operacion.NEG && OperandosMIPS.esFloat(memoria.tipoOperando(i.op1, funcion))) {
            String a = RegistrosMIPS.SCRATCH_FLOAT_A;
            String b = RegistrosMIPS.SCRATCH_FLOAT_B;
            memoria.cargarFloat(i.op1, a, funcion);
            salida.instruccion("neg.s " + b + ", " + a);
            salida.instruccion("s.s " + b + ", " + memoria.etiqueta(i.resultado, funcion));
            return;
        }
        String operando = memoria.cargarValor(i.op1, funcion);
        String resultado = registros.obtenerRegistro();
        if (i.op == Operacion.NEG) {
            salida.instruccion("sub " + resultado + ", $zero, " + operando);
        } else {
            salida.instruccion("seq " + resultado + ", " + operando + ", $zero");
        }
        salida.instruccion("sw " + resultado + ", " + memoria.etiqueta(i.resultado, funcion));
        registros.liberarRegistro(resultado);
        registros.liberarRegistro(operando);
    }

    /**
     * Nombre: traducirBinariaFloat
     *
     * Objetivo: Convertir una instruccion o construccion intermedia a codigo MIPS.
     *
     * Entrada: Instruccion i; String funcion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void traducirBinariaFloat(Instruccion i, String funcion) {
        String a = RegistrosMIPS.SCRATCH_FLOAT_A;
        String b = RegistrosMIPS.SCRATCH_FLOAT_B;
        String r = RegistrosMIPS.SCRATCH_FLOAT_RES;
        String aux = RegistrosMIPS.SCRATCH_FLOAT_AUX;
        memoria.cargarFloat(i.op1, a, funcion);
        memoria.cargarFloat(i.op2, b, funcion);
        String operacion;
        switch (i.op) {
            case SUMA: operacion = "add.s"; break;
            case RESTA: operacion = "sub.s"; break;
            case MULT: operacion = "mul.s"; break;
            case DIV: operacion = "div.s"; break;
            case MOD:
                salida.instruccion("div.s " + r + ", " + a + ", " + b);
                salida.instruccion("trunc.w.s " + aux + ", " + r);
                salida.instruccion("cvt.s.w " + aux + ", " + aux);
                salida.instruccion("mul.s " + aux + ", " + aux + ", " + b);
                salida.instruccion("sub.s " + r + ", " + a + ", " + aux);
                salida.instruccion("s.s " + r + ", " + memoria.etiqueta(i.resultado, funcion));
                return;
            case POW:
                traducirPotenciaFloat(i.resultado, funcion);
                return;
            default: throw new IllegalStateException("Operacion flotante no soportada: " + i.op);
        }
        salida.instruccion(operacion + " " + r + ", " + a + ", " + b);
        salida.instruccion("s.s " + r + ", " + memoria.etiqueta(i.resultado, funcion));
    }

    /**
     * Nombre: traducirComparacionFloat
     *
     * Objetivo: Convertir una instruccion o construccion intermedia a codigo MIPS.
     *
     * Entrada: Instruccion i; String funcion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void traducirComparacionFloat(Instruccion i, String funcion) {
        String a = RegistrosMIPS.SCRATCH_FLOAT_A;
        String b = RegistrosMIPS.SCRATCH_FLOAT_B;
        memoria.cargarFloat(i.op1, a, funcion);
        memoria.cargarFloat(i.op2, b, funcion);
        String verdadero = nuevaEtiqueta.apply("cmp_true");
        String fin = nuevaEtiqueta.apply("cmp_fin");
        String resultado = registros.obtenerRegistro();
        salida.instruccion("li " + resultado + ", 0");
        switch (i.op) {
            case IGUAL:
                salida.instruccion("c.eq.s " + a + ", " + b);
                salida.instruccion("bc1t " + verdadero);
                break;
            case DISTINTO:
                salida.instruccion("c.eq.s " + a + ", " + b);
                salida.instruccion("bc1f " + verdadero);
                break;
            case MENOR:
                salida.instruccion("c.lt.s " + a + ", " + b);
                salida.instruccion("bc1t " + verdadero);
                break;
            case MENOR_IGUAL:
                salida.instruccion("c.le.s " + a + ", " + b);
                salida.instruccion("bc1t " + verdadero);
                break;
            case MAYOR:
                salida.instruccion("c.lt.s " + b + ", " + a);
                salida.instruccion("bc1t " + verdadero);
                break;
            case MAYOR_IGUAL:
                salida.instruccion("c.le.s " + b + ", " + a);
                salida.instruccion("bc1t " + verdadero);
                break;
            default: throw new IllegalStateException("Comparacion flotante no soportada: " + i.op);
        }
        salida.instruccion("j " + fin);
        salida.add(verdadero + ":");
        salida.instruccion("li " + resultado + ", 1");
        salida.add(fin + ":");
        salida.instruccion("sw " + resultado + ", " + memoria.etiqueta(i.resultado, funcion));
        registros.liberarRegistro(resultado);
    }

    /**
     * Nombre: traducirPotenciaEntera
     *
     * Objetivo: Convertir una instruccion o construccion intermedia a codigo MIPS.
     *
     * Entrada: String base; String exponente; String resultado.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void traducirPotenciaEntera(String base, String exponente, String resultado) {
        String ciclo = nuevaEtiqueta.apply("pow");
        String fin = nuevaEtiqueta.apply("pow_fin");
        salida.instruccion("li " + resultado + ", 1");
        salida.add(ciclo + ":");
        salida.instruccion("blez " + exponente + ", " + fin);
        salida.instruccion("mul " + resultado + ", " + resultado + ", " + base);
        salida.instruccion("addiu " + exponente + ", " + exponente + ", -1");
        salida.instruccion("j " + ciclo);
        salida.add(fin + ":");
    }

    /**
     * Nombre: traducirPotenciaFloat
     *
     * Objetivo: Convertir una instruccion o construccion intermedia a codigo MIPS.
     *
     * Entrada: String resultado; String funcion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void traducirPotenciaFloat(String resultado, String funcion) {
        String base = RegistrosMIPS.SCRATCH_FLOAT_A;
        String exponente = RegistrosMIPS.SCRATCH_FLOAT_B;
        String acumulador = RegistrosMIPS.SCRATCH_FLOAT_RES;
        String aux = RegistrosMIPS.SCRATCH_FLOAT_AUX;
        String ciclo = nuevaEtiqueta.apply("powf");
        String fin = nuevaEtiqueta.apply("powf_fin");
        String contador = registros.obtenerRegistro();
        salida.instruccion("li " + contador + ", 1");
        salida.instruccion("mtc1 " + contador + ", " + acumulador);
        salida.instruccion("cvt.s.w " + acumulador + ", " + acumulador);
        salida.instruccion("trunc.w.s " + aux + ", " + exponente);
        salida.instruccion("mfc1 " + contador + ", " + aux);
        salida.add(ciclo + ":");
        salida.instruccion("blez " + contador + ", " + fin);
        salida.instruccion("mul.s " + acumulador + ", " + acumulador + ", " + base);
        salida.instruccion("addiu " + contador + ", " + contador + ", -1");
        salida.instruccion("j " + ciclo);
        salida.add(fin + ":");
        salida.instruccion("s.s " + acumulador + ", " + memoria.etiqueta(resultado, funcion));
        registros.liberarRegistro(contador);
    }
}
