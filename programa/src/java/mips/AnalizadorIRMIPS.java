package mips;

import intermedio.Instruccion;
import intermedio.Operacion;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recolecta tipos, constantes, arreglos y metadatos de funciones necesarios para emitir MIPS.
 */
final class AnalizadorIRMIPS {
    private final ResultadoAnalisisMIPS resultado = new ResultadoAnalisisMIPS();
    private int contadorCadena;
    private int contadorFlotante;

    ResultadoAnalisisMIPS analizar(List<Instruccion> codigo) {
        recolectarDeclaracionesYConstantes(codigo);
        propagarTipos(codigo);
        construirTablaDirecciones();
        return resultado;
    }

    private void recolectarDeclaracionesYConstantes(List<Instruccion> codigo) {
        String funcion = null;
        for (Instruccion instruccion : codigo) {
            if (instruccion.op == Operacion.INICIO_FUNC) {
                funcion = instruccion.resultado;
                resultado.parametrosFuncion.put(funcion, 0);
                continue;
            }
            if (instruccion.op == Operacion.FIN_FUNC) {
                funcion = null;
                continue;
            }
            if (funcion == null) {
                continue;
            }

            if (instruccion.op == Operacion.DECL || instruccion.op == Operacion.FORMAL_PARAM) {
                resultado.tipos.put(EtiquetasMIPS.clave(funcion, instruccion.resultado),
                        OperandosMIPS.normalizarTipo(instruccion.op1));
                if (instruccion.op == Operacion.FORMAL_PARAM) {
                    resultado.parametrosFuncion.put(funcion,
                            resultado.parametrosFuncion.get(funcion) + 1);
                }
            } else if (instruccion.op == Operacion.DECL_ARRAY) {
                String clave = EtiquetasMIPS.clave(funcion, instruccion.resultado);
                resultado.tipos.put(clave, OperandosMIPS.normalizarTipo(instruccion.op1));
                int[] dimensiones = dimensiones(instruccion.op2);
                resultado.columnasArreglo.put(clave, dimensiones[1]);
                resultado.dimensionesDeclaradas.put(clave, dimensiones[0] * dimensiones[1]);
            }

            registrarConstante(instruccion.op1);
            registrarConstante(instruccion.op2);
            registrarConstante(instruccion.resultado);
        }
    }

    private void propagarTipos(List<Instruccion> codigo) {
        for (int vuelta = 0; vuelta < 4; vuelta++) {
            String funcion = null;
            for (Instruccion i : codigo) {
                if (i.op == Operacion.INICIO_FUNC) {
                    funcion = i.resultado;
                    continue;
                }
                if (i.op == Operacion.FIN_FUNC) {
                    funcion = null;
                    continue;
                }
                if (funcion == null || i.resultado == null) {
                    if (funcion != null && i.op == Operacion.RETURN && i.op1 != null) {
                        resultado.retornosFuncion.put(funcion, tipoOperando(i.op1, funcion));
                    }
                    continue;
                }

                String tipo = tipoResultado(i, funcion);
                if (tipo != null) {
                    resultado.tipos.put(EtiquetasMIPS.clave(funcion, i.resultado), tipo);
                }
            }
        }
    }

    private void construirTablaDirecciones() {
        Map<String, Integer> repeticiones = new LinkedHashMap<>();
        for (String clave : resultado.tipos.keySet()) {
            String base = EtiquetasMIPS.etiquetaDato(clave);
            int repeticion = repeticiones.getOrDefault(base, 0);
            repeticiones.put(base, repeticion + 1);
            resultado.direcciones.put(clave, repeticion == 0 ? base : base + "_" + repeticion);
        }
    }

    private String tipoResultado(Instruccion i, String funcion) {
        switch (i.op) {
            case LOAD:
            case NEG:
                return tipoOperando(i.op1, funcion);
            case CALL:
                return resultado.retornosFuncion.getOrDefault(i.op1, "int");
            case SUMA:
            case RESTA:
            case MULT:
            case DIV:
            case MOD:
            case POW:
                return OperandosMIPS.esFloat(tipoOperando(i.op1, funcion))
                        || OperandosMIPS.esFloat(tipoOperando(i.op2, funcion)) ? "float" : "int";
            case AND:
            case OR:
            case NOT:
            case IGUAL:
            case DISTINTO:
            case MENOR:
            case MAYOR:
            case MENOR_IGUAL:
            case MAYOR_IGUAL:
                return "bool";
            default:
                return null;
        }
    }

    private String tipoOperando(String operando, String funcion) {
        if (operando == null) {
            return "int";
        }
        if (OperandosMIPS.esCadena(operando)) {
            return "string";
        }
        if (OperandosMIPS.esChar(operando)) {
            return "char";
        }
        if ("true".equals(operando) || "false".equals(operando)) {
            return "bool";
        }
        if (OperandosMIPS.esFloatLiteral(operando)) {
            return "float";
        }
        if (OperandosMIPS.esEnteroLiteral(operando)) {
            return "int";
        }
        String base = OperandosMIPS.esAccesoArreglo(operando)
                ? operando.substring(0, operando.indexOf('[')) : operando;
        return resultado.tipos.getOrDefault(EtiquetasMIPS.clave(funcion, base), "int");
    }

    private void registrarConstante(String valor) {
        if (OperandosMIPS.esCadena(valor)) {
            resultado.cadenas.computeIfAbsent(valor, k -> "_str_" + contadorCadena++);
        } else if (OperandosMIPS.esFloatLiteral(valor)) {
            resultado.flotantes.computeIfAbsent(valor, k -> "_flt_" + contadorFlotante++);
        }
    }

    private static int[] dimensiones(String texto) {
        if (texto != null && texto.matches("\\[[0-9]+\\]\\[[0-9]+\\]")) {
            int medio = texto.indexOf("][");
            int filas = Integer.parseInt(texto.substring(1, medio));
            int columnas = Integer.parseInt(texto.substring(medio + 2, texto.length() - 1));
            return new int[] { filas, columnas };
        }
        return new int[] { 1, 1 };
    }
}
