package intermedio;

import ast.AsignacionNodo;
import ast.AccesoArregloNodo;
import ast.BloqueNodo;
import ast.DeclaracionVariableNodo;
import ast.ExpresionBinariaNodo;
import ast.ExpresionNodo;
import ast.ExpresionSentenciaNodo;
import ast.ExpresionUnariaNodo;
import ast.FuncionNodo;
import ast.IdentificadorNodo;
import ast.IfNodo;
import ast.LiteralNodo;
import ast.LlamadaFuncionNodo;
import ast.Nodo;
import ast.ProgramaNodo;
import ast.ReturnNodo;
import ast.SalidaNodo;
import ast.TipoDato;
import ast.WhileNodo;
import java.util.ArrayList;
import java.util.List;

/**
 * <strong>Objetivo:</strong> Recorre el AST aceptado y emite instrucciones de tres direcciones.
 *
 * <p><strong>Entradas:</strong> AST validado, operaciones u operandos necesarios para representar codigo intermedio.</p>
 *
 * <p><strong>Salidas:</strong> Instrucciones, operaciones o texto de codigo intermedio.</p>
 *
 * <p><strong>Restricciones:</strong> Debe asumir programas ya aceptados y no reemplazar las validaciones semanticas.</p>
 */
public class GeneradorCodigoIntermedio {
    /**
     * <strong>Objetivo:</strong> Acumular las instrucciones emitidas durante una
     * corrida de generacion.
     *
     * <p><strong>Entradas:</strong> Instrucciones agregadas por los metodos de
     * generacion.</p>
     *
     * <p><strong>Salidas:</strong> Lista usada para construir la copia devuelta
     * por {@code generar}.</p>
     *
     * <p><strong>Restricciones:</strong> Debe limpiarse al iniciar cada nueva
     * generacion para no mezclar resultados de programas distintos.</p>
     */
    private final List<Instruccion> instrucciones = new ArrayList<>();
    private int contadorTemporales;
    private int contadorEtiquetas;

    /**
     * <strong>Objetivo:</strong> Genera la representacion correspondiente dentro del compilador.
     *
     * <p><strong>Entradas:</strong> ProgramaNodo programa</p>
     *
     * <p><strong>Salidas:</strong> Retorna List<Instruccion>.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public List<Instruccion> generar(ProgramaNodo programa) {
        instrucciones.clear();
        contadorTemporales = 0;
        contadorEtiquetas = 0;
        for (FuncionNodo funcion : programa.getFunciones()) {
            generarFuncion(funcion);
        }
        return new ArrayList<>(instrucciones);
    }

    /**
     * <strong>Objetivo:</strong> Genera la representacion correspondiente dentro del compilador.
     *
     * <p><strong>Entradas:</strong> FuncionNodo funcion</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void generarFuncion(FuncionNodo funcion) {
        instrucciones.add(new Instruccion(Operacion.INICIO_FUNC, funcion.getNombre()));
        generarBloque(funcion.getCuerpo());
        instrucciones.add(new Instruccion(Operacion.FIN_FUNC, funcion.getNombre()));
    }

    /**
     * <strong>Objetivo:</strong> Genera la representacion correspondiente dentro del compilador.
     *
     * <p><strong>Entradas:</strong> BloqueNodo bloque</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void generarBloque(BloqueNodo bloque) {
        for (Nodo nodo : bloque.getInstrucciones()) {
            generarNodo(nodo);
        }
    }

    /**
     * <strong>Objetivo:</strong> Genera la representacion correspondiente dentro del compilador.
     *
     * <p><strong>Entradas:</strong> Nodo nodo</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void generarNodo(Nodo nodo) {
        if (nodo instanceof AsignacionNodo) {
            generarAsignacion((AsignacionNodo) nodo);
        } else if (nodo instanceof DeclaracionVariableNodo) {
            generarDeclaracionVariable((DeclaracionVariableNodo) nodo);
        } else if (nodo instanceof ReturnNodo) {
            generarReturn((ReturnNodo) nodo);
        } else if (nodo instanceof BloqueNodo) {
            generarBloque((BloqueNodo) nodo);
        } else if (nodo instanceof IfNodo) {
            generarIf((IfNodo) nodo);
        } else if (nodo instanceof WhileNodo) {
            generarWhile((WhileNodo) nodo);
        } else if (nodo instanceof ExpresionSentenciaNodo) {
            generarExpresionSentencia((ExpresionSentenciaNodo) nodo);
        } else if (nodo instanceof SalidaNodo) {
            generarSalida((SalidaNodo) nodo);
        }
    }

    /**
     * <strong>Objetivo:</strong> Genera la representacion correspondiente dentro del compilador.
     *
     * <p><strong>Entradas:</strong> DeclaracionVariableNodo declaracion</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void generarDeclaracionVariable(DeclaracionVariableNodo declaracion) {
        if (declaracion.getInicializador() == null) {
            return;
        }
        String valor = generarExpresion(declaracion.getInicializador());
        instrucciones.add(new Instruccion(Operacion.ASIG, declaracion.getNombre(), valor));
    }

    /**
     * <strong>Objetivo:</strong> Genera la representacion correspondiente dentro del compilador.
     *
     * <p><strong>Entradas:</strong> AsignacionNodo asignacion</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void generarAsignacion(AsignacionNodo asignacion) {
        String valor = generarExpresion(asignacion.getValor());
        String destino = generarExpresion(asignacion.getDestino());
        instrucciones.add(new Instruccion(Operacion.ASIG, destino, valor));
    }

    /**
     * <strong>Objetivo:</strong> Genera la representacion correspondiente dentro del compilador.
     *
     * <p><strong>Entradas:</strong> ReturnNodo retorno</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void generarReturn(ReturnNodo retorno) {
        String valor = retorno.getValor() == null ? null : generarExpresion(retorno.getValor());
        instrucciones.add(new Instruccion(Operacion.RETURN, null, valor));
    }

    /**
     * <strong>Objetivo:</strong> Genera la representacion correspondiente dentro del compilador.
     *
     * <p><strong>Entradas:</strong> ExpresionSentenciaNodo sentencia</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void generarExpresionSentencia(ExpresionSentenciaNodo sentencia) {
        generarExpresion(sentencia.getExpresion());
    }

    /**
     * <strong>Objetivo:</strong> Genera la representacion correspondiente dentro del compilador.
     *
     * <p><strong>Entradas:</strong> SalidaNodo salida</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void generarSalida(SalidaNodo salida) {
        instrucciones.add(new Instruccion(Operacion.PRINT, generarExpresion(salida.getValor())));
    }

    /**
     * <strong>Objetivo:</strong> Genera la representacion correspondiente dentro del compilador.
     *
     * <p><strong>Entradas:</strong> IfNodo sentencia</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void generarIf(IfNodo sentencia) {
        String condicion = generarExpresion(sentencia.getCondicion());
        String etiquetaElseOFin = nuevaEtiqueta();

        instrucciones.add(new Instruccion(Operacion.IF_FALSE, etiquetaElseOFin, condicion));
        generarBloque(sentencia.getBloqueEntonces());

        if (sentencia.getBloqueSino() == null) {
            instrucciones.add(new Instruccion(Operacion.LABEL, etiquetaElseOFin));
            return;
        }

        String etiquetaFin = nuevaEtiqueta();
        instrucciones.add(new Instruccion(Operacion.GOTO, etiquetaFin));
        instrucciones.add(new Instruccion(Operacion.LABEL, etiquetaElseOFin));
        generarBloque(sentencia.getBloqueSino());
        instrucciones.add(new Instruccion(Operacion.LABEL, etiquetaFin));
    }

    /**
     * <strong>Objetivo:</strong> Genera la representacion correspondiente dentro del compilador.
     *
     * <p><strong>Entradas:</strong> WhileNodo sentencia</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void generarWhile(WhileNodo sentencia) {
        String etiquetaInicio = nuevaEtiqueta();
        String etiquetaSalida = nuevaEtiqueta();

        instrucciones.add(new Instruccion(Operacion.LABEL, etiquetaInicio));

        if (sentencia.isDoWhile()) {
            generarBloque(sentencia.getCuerpo());
            String condicion = generarExpresion(sentencia.getCondicion());
            instrucciones.add(new Instruccion(Operacion.IF_FALSE, etiquetaSalida, condicion));
            instrucciones.add(new Instruccion(Operacion.GOTO, etiquetaInicio));
            instrucciones.add(new Instruccion(Operacion.LABEL, etiquetaSalida));
            return;
        }

        String condicion = generarExpresion(sentencia.getCondicion());
        instrucciones.add(new Instruccion(Operacion.IF_FALSE, etiquetaSalida, condicion));
        generarBloque(sentencia.getCuerpo());
        instrucciones.add(new Instruccion(Operacion.GOTO, etiquetaInicio));
        instrucciones.add(new Instruccion(Operacion.LABEL, etiquetaSalida));
    }

    /**
     * <strong>Objetivo:</strong> Genera la representacion correspondiente dentro del compilador.
     *
     * <p><strong>Entradas:</strong> ExpresionNodo expresion</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private String generarExpresion(ExpresionNodo expresion) {
        if (expresion instanceof IdentificadorNodo) {
            return ((IdentificadorNodo) expresion).getNombre();
        }
        if (expresion instanceof AccesoArregloNodo) {
            AccesoArregloNodo acceso = (AccesoArregloNodo) expresion;
            return acceso.getNombre() + "[" + generarExpresion(acceso.getFila()) + "]"
                    + "[" + generarExpresion(acceso.getColumnaIndice()) + "]";
        }
        if (expresion instanceof LiteralNodo) {
            Object valor = ((LiteralNodo) expresion).getValor();
            return valor == null ? "null" : valor.toString();
        }
        if (expresion instanceof ExpresionBinariaNodo) {
            return generarBinaria((ExpresionBinariaNodo) expresion);
        }
        if (expresion instanceof ExpresionUnariaNodo) {
            return generarUnaria((ExpresionUnariaNodo) expresion);
        }
        if (expresion instanceof LlamadaFuncionNodo) {
            return generarLlamada((LlamadaFuncionNodo) expresion);
        }
        /*
         * Valor defensivo para expresiones aun no implementadas en el IR. En un
         * programa aceptado no deberia aparecer, pero facilita detectar huecos
         * durante nuevas extensiones.
         */
        return "<expr>";
    }

    /**
     * <strong>Objetivo:</strong> Genera la representacion correspondiente dentro del compilador.
     *
     * <p><strong>Entradas:</strong> ExpresionBinariaNodo expresion</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private String generarBinaria(ExpresionBinariaNodo expresion) {
        String izquierda = generarExpresion(expresion.getIzquierda());
        String derecha = generarExpresion(expresion.getDerecha());
        String temporal = nuevoTemporal();
        instrucciones.add(new Instruccion(operacionBinaria(expresion.getOperador()), temporal, izquierda, derecha));
        return temporal;
    }

    /**
     * <strong>Objetivo:</strong> Genera la representacion correspondiente dentro del compilador.
     *
     * <p><strong>Entradas:</strong> ExpresionUnariaNodo expresion</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private String generarUnaria(ExpresionUnariaNodo expresion) {
        if ("++".equals(expresion.getOperador()) || "--".equals(expresion.getOperador())) {
            String destino = generarExpresion(expresion.getExpresion());
            String temporal = nuevoTemporal();
            Operacion operacion = "++".equals(expresion.getOperador()) ? Operacion.SUMA : Operacion.RESTA;
            instrucciones.add(new Instruccion(operacion, temporal, destino, "1"));
            instrucciones.add(new Instruccion(Operacion.ASIG, destino, temporal));
            return temporal;
        }
        String valor = generarExpresion(expresion.getExpresion());
        String temporal = nuevoTemporal();
        instrucciones.add(new Instruccion(operacionUnaria(expresion.getOperador()), temporal, valor));
        return temporal;
    }

    /**
     * <strong>Objetivo:</strong> Genera la representacion correspondiente dentro del compilador.
     *
     * <p><strong>Entradas:</strong> LlamadaFuncionNodo llamada</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private String generarLlamada(LlamadaFuncionNodo llamada) {
        for (ExpresionNodo argumento : llamada.getArgumentos()) {
            instrucciones.add(new Instruccion(Operacion.PARAM, generarExpresion(argumento)));
        }
        if (esLlamadaVoid(llamada)) {
            instrucciones.add(new Instruccion(Operacion.CALL, null, llamada.getNombre(),
                    String.valueOf(llamada.getArgumentos().size())));
            return null;
        }
        String temporal = nuevoTemporal();
        instrucciones.add(new Instruccion(Operacion.CALL, temporal, llamada.getNombre(),
                String.valueOf(llamada.getArgumentos().size())));
        return temporal;
    }

    /**
     * <strong>Objetivo:</strong> Consulta una condicion booleana del objeto.
     *
     * <p><strong>Entradas:</strong> LlamadaFuncionNodo llamada</p>
     *
     * <p><strong>Salidas:</strong> Retorna boolean.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private boolean esLlamadaVoid(LlamadaFuncionNodo llamada) {
        return llamada.getTipo() == TipoDato.VOID || llamada.getTipo() == TipoDato.EMPTY;
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private String nuevoTemporal() {
        return "_t" + contadorTemporales++;
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private String nuevaEtiqueta() {
        return "_L" + contadorEtiquetas++;
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String operador</p>
     *
     * <p><strong>Salidas:</strong> Retorna Operacion.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private Operacion operacionBinaria(String operador) {
        switch (operador) {
            case "+":
                return Operacion.SUMA;
            case "-":
                return Operacion.RESTA;
            case "*":
                return Operacion.MULT;
            case "/":
                return Operacion.DIV;
            case "%":
                return Operacion.MOD;
            case "^":
                return Operacion.POW;
            case "#":
                return Operacion.OR;
            case "@":
                return Operacion.AND;
            case "equal":
                return Operacion.IGUAL;
            case "n_equal":
                return Operacion.DISTINTO;
            case "less_t":
                return Operacion.MENOR;
            case "greather_t":
                return Operacion.MAYOR;
            case "less_te":
                return Operacion.MENOR_IGUAL;
            case "greather_te":
                return Operacion.MAYOR_IGUAL;
            default:
                throw new IllegalArgumentException("Operador binario no soportado: " + operador);
        }
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String operador</p>
     *
     * <p><strong>Salidas:</strong> Retorna Operacion.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private Operacion operacionUnaria(String operador) {
        switch (operador) {
            case "-":
                return Operacion.NEG;
            case "$":
                return Operacion.NOT;
            default:
                throw new IllegalArgumentException("Operador unario no soportado: " + operador);
        }
    }
}
