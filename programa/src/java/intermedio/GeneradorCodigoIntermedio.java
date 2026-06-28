package intermedio;

import ast.AsignacionNodo;
import ast.AccesoArregloNodo;
import ast.BloqueNodo;
import ast.BreakNodo;
import ast.CasoSwitchNodo;
import ast.DeclaracionVariableNodo;
import ast.EntradaNodo;
import ast.ExpresionBinariaNodo;
import ast.ExpresionNodo;
import ast.ExpresionSentenciaNodo;
import ast.ExpresionUnariaNodo;
import ast.FuncionNodo;
import ast.IdentificadorNodo;
import ast.IfNodo;
import ast.InicializacionArregloNodo;
import ast.LiteralNodo;
import ast.LlamadaFuncionNodo;
import ast.Nodo;
import ast.ProgramaNodo;
import ast.ParametroNodo;
import ast.ReturnNodo;
import ast.SalidaNodo;
import ast.SwitchNodo;
import ast.TipoDato;
import ast.WhileNodo;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import pipeline.CompiladorInternoException;

/**
 * Nombre: GeneradorCodigoIntermedio
 *
 * Objetivo: Representar, generar u optimizar instrucciones de codigo intermedio.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class GeneradorCodigoIntermedio {
    /** Instrucciones emitidas durante la generación; se limpia en cada corrida. */
    private final List<Instruccion> instrucciones = new ArrayList<>();
    private int contadorTemporales;
    private int contadorEtiquetas;
    /** Pila con la etiqueta de fin del switch actual, destino de los {@code break}. */
    private final Deque<String> destinosBreak = new ArrayDeque<>();

    /**
     * Nombre: generar
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: ProgramaNodo programa.
     *
     * Salida: Valor de tipo List<Instruccion>.
     *
     * Restricciones: Ninguna.
     */
    public List<Instruccion> generar(ProgramaNodo programa) {
        instrucciones.clear();
        contadorTemporales = 0;
        contadorEtiquetas = 0;
        destinosBreak.clear();
        for (FuncionNodo funcion : programa.getFunciones()) {
            generarFuncion(funcion);
        }
        return new ArrayList<>(instrucciones);
    }

    /**
     * Nombre: generarFuncion
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: FuncionNodo funcion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void generarFuncion(FuncionNodo funcion) {
        instrucciones.add(new Instruccion(Operacion.INICIO_FUNC, funcion.getNombre()));
        for (ParametroNodo parametro : funcion.getParametros()) {
            instrucciones.add(new Instruccion(Operacion.FORMAL_PARAM, parametro.getNombre(),
                    parametro.getTipo().toString()));
        }
        generarBloque(funcion.getCuerpo());
        instrucciones.add(new Instruccion(Operacion.FIN_FUNC, funcion.getNombre()));
    }

    /**
     * Nombre: generarBloque
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: BloqueNodo bloque.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void generarBloque(BloqueNodo bloque) {
        for (Nodo nodo : bloque.getInstrucciones()) {
            generarNodo(nodo);
        }
    }

    /**
     * Nombre: generarNodo
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: Nodo nodo.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
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
        } else if (nodo instanceof EntradaNodo) {
            generarEntrada((EntradaNodo) nodo);
        } else if (nodo instanceof SwitchNodo) {
            generarSwitch((SwitchNodo) nodo);
        } else if (nodo instanceof BreakNodo) {
            generarBreak();
        }
    }

    /**
     * Nombre: generarDeclaracionVariable
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: DeclaracionVariableNodo declaracion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void generarDeclaracionVariable(DeclaracionVariableNodo declaracion) {
        if (declaracion.esArreglo()) {
            generarDeclaracionArreglo(declaracion);
            return;
        }
        instrucciones.add(new Instruccion(Operacion.DECL, declaracion.getNombre(),
                declaracion.getTipo().toString()));
        if (declaracion.getInicializador() != null) {
            String valor = generarExpresion(declaracion.getInicializador());
            instrucciones.add(new Instruccion(Operacion.ASIG, declaracion.getNombre(), valor));
        }
    }

    /**
     * Nombre: generarDeclaracionArreglo
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: DeclaracionVariableNodo declaracion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void generarDeclaracionArreglo(DeclaracionVariableNodo declaracion) {
        String filas = generarExpresionSinCarga(declaracion.getFilas());
        String columnas = generarExpresionSinCarga(declaracion.getColumnas());
        instrucciones.add(new Instruccion(Operacion.DECL_ARRAY, declaracion.getNombre(),
                declaracion.getTipo().toString(), "[" + filas + "][" + columnas + "]"));
        InicializacionArregloNodo inicializacion = declaracion.getInicializacionArreglo();
        if (inicializacion == null) {
            return;
        }
        List<List<ExpresionNodo>> valores = inicializacion.getFilas();
        for (int fila = 0; fila < valores.size(); fila++) {
            for (int columna = 0; columna < valores.get(fila).size(); columna++) {
                String destino = declaracion.getNombre() + "[" + fila + "][" + columna + "]";
                String valor = generarExpresion(valores.get(fila).get(columna));
                instrucciones.add(new Instruccion(Operacion.STORE_ARRAY, destino, valor));
            }
        }
    }

    /**
     * Nombre: generarAsignacion
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: AsignacionNodo asignacion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void generarAsignacion(AsignacionNodo asignacion) {
        String valor = generarExpresion(asignacion.getValor());
        String destino = generarDestino(asignacion.getDestino());
        Operacion operacion = asignacion.getDestino() instanceof AccesoArregloNodo
                ? Operacion.STORE_ARRAY : Operacion.ASIG;
        instrucciones.add(new Instruccion(operacion, destino, valor));
    }

    /**
     * Nombre: generarReturn
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: ReturnNodo retorno.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void generarReturn(ReturnNodo retorno) {
        String valor = retorno.getValor() == null ? null : generarExpresion(retorno.getValor());
        instrucciones.add(new Instruccion(Operacion.RETURN, null, valor));
    }

    /**
     * Nombre: generarExpresionSentencia
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: ExpresionSentenciaNodo sentencia.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void generarExpresionSentencia(ExpresionSentenciaNodo sentencia) {
        generarExpresion(sentencia.getExpresion());
    }

    /**
     * Nombre: generarSalida
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: SalidaNodo salida.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void generarSalida(SalidaNodo salida) {
        instrucciones.add(new Instruccion(Operacion.PRINT, generarExpresion(salida.getValor())));
    }

    /**
     * Nombre: generarEntrada
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: EntradaNodo entrada.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void generarEntrada(EntradaNodo entrada) {
        instrucciones.add(new Instruccion(Operacion.READ, entrada.getDestino()));
    }

    /**
     * Nombre: generarBreak
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: Ninguna.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void generarBreak() {
        if (!destinosBreak.isEmpty()) {
            instrucciones.add(new Instruccion(Operacion.GOTO, destinosBreak.peek()));
        }
    }

    /**
     * Nombre: generarSwitch
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: SwitchNodo sentencia.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void generarSwitch(SwitchNodo sentencia) {
        String selector = generarExpresion(sentencia.getExpresion());
        String etiquetaFin = nuevaEtiqueta();
        List<CasoSwitchNodo> casos = sentencia.getCasos();
        List<String> etiquetasCasos = new ArrayList<>();
        String etiquetaDefault = etiquetaFin;
        for (CasoSwitchNodo caso : casos) {
            String etiqueta = nuevaEtiqueta();
            etiquetasCasos.add(etiqueta);
            if (caso.isDefecto()) {
                etiquetaDefault = etiqueta;
            }
        }

        for (int i = 0; i < casos.size(); i++) {
            CasoSwitchNodo caso = casos.get(i);
            if (caso.isDefecto()) {
                continue;
            }
            String valorCaso = generarExpresion(caso.getValor());
            String comparacion = nuevoTemporal();
            instrucciones.add(new Instruccion(Operacion.IGUAL, comparacion, selector, valorCaso));
            String siguiente = nuevaEtiqueta();
            instrucciones.add(new Instruccion(Operacion.IF_FALSE, siguiente, comparacion));
            instrucciones.add(new Instruccion(Operacion.GOTO, etiquetasCasos.get(i)));
            instrucciones.add(new Instruccion(Operacion.LABEL, siguiente));
        }
        instrucciones.add(new Instruccion(Operacion.GOTO, etiquetaDefault));

        destinosBreak.push(etiquetaFin);
        for (int i = 0; i < casos.size(); i++) {
            instrucciones.add(new Instruccion(Operacion.LABEL, etiquetasCasos.get(i)));
            generarBloque(casos.get(i).getBloque());
        }
        destinosBreak.pop();
        instrucciones.add(new Instruccion(Operacion.LABEL, etiquetaFin));
    }

    /**
     * Nombre: generarIf
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: IfNodo sentencia.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
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
     * Nombre: generarWhile
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: WhileNodo sentencia.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
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
     * Nombre: generarExpresion
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: ExpresionNodo expresion.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private String generarExpresion(ExpresionNodo expresion) {
        if (expresion == null) {
            throw new IllegalArgumentException("No se puede generar codigo para una expresion nula.");
        }
        if (expresion instanceof IdentificadorNodo) {
            String temporal = nuevoTemporal();
            instrucciones.add(new Instruccion(Operacion.LOAD, temporal,
                    ((IdentificadorNodo) expresion).getNombre()));
            return temporal;
        }
        if (expresion instanceof AccesoArregloNodo) {
            String origen = generarDestino(expresion);
            String temporal = nuevoTemporal();
            instrucciones.add(new Instruccion(Operacion.LOAD, temporal, origen));
            return temporal;
        }
        if (expresion instanceof LiteralNodo) {
            Object valor = ((LiteralNodo) expresion).getValor();
            return formatearLiteral((LiteralNodo) expresion);
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
        throw new CompiladorInternoException("Expresion no soportada en codigo intermedio: "
                + expresion.getClass().getSimpleName(),
                expresion.getLinea(), expresion.getColumna());
    }

    /**
     * Nombre: generarDestino
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: ExpresionNodo expresion.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private String generarDestino(ExpresionNodo expresion) {
        if (expresion instanceof IdentificadorNodo) {
            return ((IdentificadorNodo) expresion).getNombre();
        }
        if (expresion instanceof AccesoArregloNodo) {
            AccesoArregloNodo acceso = (AccesoArregloNodo) expresion;
            return acceso.getNombre() + "[" + generarExpresion(acceso.getFila()) + "]"
                    + "[" + generarExpresion(acceso.getColumnaIndice()) + "]";
        }
        throw new IllegalArgumentException("Destino de asignacion no soportado: "
                + (expresion == null ? "<null>" : expresion.getClass().getSimpleName()));
    }

    /**
     * Nombre: generarExpresionSinCarga
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: ExpresionNodo expresion.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private String generarExpresionSinCarga(ExpresionNodo expresion) {
        if (expresion instanceof IdentificadorNodo) {
            return ((IdentificadorNodo) expresion).getNombre();
        }
        return generarExpresion(expresion);
    }

    /**
     * Nombre: formatearLiteral
     *
     * Objetivo: Convertir un valor interno a su representacion textual.
     *
     * Entrada: LiteralNodo literal.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private String formatearLiteral(LiteralNodo literal) {
        Object valor = literal.getValor();
        if (valor == null) {
            return "null";
        }
        if (literal.getTipo() == TipoDato.STRING) {
            String texto = valor.toString();
            return texto.length() >= 2 && texto.startsWith("\"") && texto.endsWith("\"")
                    ? texto : "\"" + texto + "\"";
        }
        if (literal.getTipo() == TipoDato.CHAR) {
            return "'" + valor + "'";
        }
        return valor.toString();
    }

    /**
     * Nombre: generarBinaria
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: ExpresionBinariaNodo expresion.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private String generarBinaria(ExpresionBinariaNodo expresion) {
        String izquierda = generarExpresion(expresion.getIzquierda());
        String derecha = generarExpresion(expresion.getDerecha());
        String temporal = nuevoTemporal();
        instrucciones.add(new Instruccion(operacionBinaria(expresion.getOperador()), temporal, izquierda, derecha));
        return temporal;
    }

    /**
     * Nombre: generarUnaria
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: ExpresionUnariaNodo expresion.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private String generarUnaria(ExpresionUnariaNodo expresion) {
        if ("++".equals(expresion.getOperador()) || "--".equals(expresion.getOperador())) {
            String destino = generarDestino(expresion.getExpresion());
            String valorActual = generarExpresion(expresion.getExpresion());
            String temporal = nuevoTemporal();
            Operacion operacion = "++".equals(expresion.getOperador()) ? Operacion.SUMA : Operacion.RESTA;
            instrucciones.add(new Instruccion(operacion, temporal, valorActual, "1"));
            Operacion escritura = expresion.getExpresion() instanceof AccesoArregloNodo
                    ? Operacion.STORE_ARRAY : Operacion.ASIG;
            instrucciones.add(new Instruccion(escritura, destino, temporal));
            return temporal;
        }
        String valor = generarExpresion(expresion.getExpresion());
        String temporal = nuevoTemporal();
        instrucciones.add(new Instruccion(operacionUnaria(expresion.getOperador()), temporal, valor));
        return temporal;
    }

    /**
     * Nombre: generarLlamada
     *
     * Objetivo: Generar instrucciones o artefactos derivados del arbol sintactico.
     *
     * Entrada: LlamadaFuncionNodo llamada.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
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
     * Nombre: esLlamadaVoid
     *
     * Objetivo: Indicar si se cumple la condicion LlamadaVoid.
     *
     * Entrada: LlamadaFuncionNodo llamada.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Uso interno de la clase.
     */
    private boolean esLlamadaVoid(LlamadaFuncionNodo llamada) {
        return llamada.getTipo() == TipoDato.VOID || llamada.getTipo() == TipoDato.EMPTY;
    }

    /**
     * Nombre: nuevoTemporal
     *
     * Objetivo: Ejecutar la operacion nuevoTemporal definida por GeneradorCodigoIntermedio.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private String nuevoTemporal() {
        return "_t" + contadorTemporales++;
    }

    /**
     * Nombre: nuevaEtiqueta
     *
     * Objetivo: Ejecutar la operacion nuevaEtiqueta definida por GeneradorCodigoIntermedio.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private String nuevaEtiqueta() {
        return "_L" + contadorEtiquetas++;
    }

    /**
     * Nombre: operacionBinaria
     *
     * Objetivo: Ejecutar la operacion operacionBinaria definida por GeneradorCodigoIntermedio.
     *
     * Entrada: String operador.
     *
     * Salida: Valor de tipo Operacion.
     *
     * Restricciones: Uso interno de la clase.
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
     * Nombre: operacionUnaria
     *
     * Objetivo: Ejecutar la operacion operacionUnaria definida por GeneradorCodigoIntermedio.
     *
     * Entrada: String operador.
     *
     * Salida: Valor de tipo Operacion.
     *
     * Restricciones: Uso interno de la clase.
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
