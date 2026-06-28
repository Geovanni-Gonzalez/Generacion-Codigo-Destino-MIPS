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

/**
 * <strong>Nombre:</strong> GeneradorCodigoIntermedio
 *
 * <p><strong>Objetivo:</strong> Recorrer el AST ya aceptado y emitir el código intermedio de tres
 * direcciones, generando temporales y etiquetas a medida que descompone expresiones y estructuras
 * de control.</p>
 *
 * <p><strong>Entrada:</strong> El nodo raíz del programa ({@link ProgramaNodo}).</p>
 *
 * <p><strong>Salida:</strong> Una lista de instrucciones intermedias.</p>
 *
 * <p><strong>Restricciones:</strong> Asume programas ya validados; no vuelve a comprobar tipos.</p>
 */
public class GeneradorCodigoIntermedio {
    /** Instrucciones emitidas durante la generación; se limpia en cada corrida. */
    private final List<Instruccion> instrucciones = new ArrayList<>();
    private int contadorTemporales;
    private int contadorEtiquetas;
    /** Pila con la etiqueta de fin del switch actual, destino de los {@code break}. */
    private final Deque<String> destinosBreak = new ArrayDeque<>();

    /**
     * <strong>Nombre:</strong> generar
     *
     * <p><strong>Objetivo:</strong> Generar y devolver el código intermedio de todo el programa.</p>
     *
     * <p><strong>Entrada:</strong> ProgramaNodo programa.</p>
     *
     * <p><strong>Salida:</strong> List&lt;Instruccion&gt; con el código intermedio.</p>
     *
     * <p><strong>Restricciones:</strong> Reinicia los acumuladores en cada llamada.</p>
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
     * <strong>Nombre:</strong> generarFuncion
     *
     * <p><strong>Objetivo:</strong> Emitir el inicio de función, sus parámetros formales, su cuerpo y el fin.</p>
     *
     * <p><strong>Entrada:</strong> FuncionNodo funcion.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> generarBloque
     *
     * <p><strong>Objetivo:</strong> Generar, en orden, el código de cada instrucción de un bloque.</p>
     *
     * <p><strong>Entrada:</strong> BloqueNodo bloque.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void generarBloque(BloqueNodo bloque) {
        for (Nodo nodo : bloque.getInstrucciones()) {
            generarNodo(nodo);
        }
    }

    /**
     * <strong>Nombre:</strong> generarNodo
     *
     * <p><strong>Objetivo:</strong> Despachar un nodo de sentencia al método de generación que le corresponde.</p>
     *
     * <p><strong>Entrada:</strong> Nodo nodo.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ignora tipos de nodo no manejados.</p>
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
     * <strong>Nombre:</strong> generarDeclaracionVariable
     *
     * <p><strong>Objetivo:</strong> Declarar una variable escalar y, si tiene inicializador, asignarle su valor.</p>
     *
     * <p><strong>Entrada:</strong> DeclaracionVariableNodo declaracion.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Si la declaración es de arreglo, delega en {@link #generarDeclaracionArreglo}.</p>
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
     * <strong>Nombre:</strong> generarDeclaracionArreglo
     *
     * <p><strong>Objetivo:</strong> Declarar un arreglo con sus dimensiones y, si los hay, almacenar sus valores iniciales.</p>
     *
     * <p><strong>Entrada:</strong> DeclaracionVariableNodo declaracion.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> generarAsignacion
     *
     * <p><strong>Objetivo:</strong> Evaluar el valor, resolver el destino y emitir la asignación (a variable o a celda de arreglo).</p>
     *
     * <p><strong>Entrada:</strong> AsignacionNodo asignacion.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void generarAsignacion(AsignacionNodo asignacion) {
        String valor = generarExpresion(asignacion.getValor());
        String destino = generarDestino(asignacion.getDestino());
        Operacion operacion = asignacion.getDestino() instanceof AccesoArregloNodo
                ? Operacion.STORE_ARRAY : Operacion.ASIG;
        instrucciones.add(new Instruccion(operacion, destino, valor));
    }

    /**
     * <strong>Nombre:</strong> generarReturn
     *
     * <p><strong>Objetivo:</strong> Emitir el retorno de la función, con o sin valor.</p>
     *
     * <p><strong>Entrada:</strong> ReturnNodo retorno.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void generarReturn(ReturnNodo retorno) {
        String valor = retorno.getValor() == null ? null : generarExpresion(retorno.getValor());
        instrucciones.add(new Instruccion(Operacion.RETURN, null, valor));
    }

    /**
     * <strong>Nombre:</strong> generarExpresionSentencia
     *
     * <p><strong>Objetivo:</strong> Generar una expresión usada como sentencia (por su efecto, descartando el resultado).</p>
     *
     * <p><strong>Entrada:</strong> ExpresionSentenciaNodo sentencia.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void generarExpresionSentencia(ExpresionSentenciaNodo sentencia) {
        generarExpresion(sentencia.getExpresion());
    }

    /**
     * <strong>Nombre:</strong> generarSalida
     *
     * <p><strong>Objetivo:</strong> Emitir la impresión del valor de la expresión de salida ({@code cout}).</p>
     *
     * <p><strong>Entrada:</strong> SalidaNodo salida.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void generarSalida(SalidaNodo salida) {
        instrucciones.add(new Instruccion(Operacion.PRINT, generarExpresion(salida.getValor())));
    }

    /**
     * <strong>Nombre:</strong> generarEntrada
     *
     * <p><strong>Objetivo:</strong> Emitir la lectura de un valor hacia la variable destino ({@code cin}).</p>
     *
     * <p><strong>Entrada:</strong> EntradaNodo entrada.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void generarEntrada(EntradaNodo entrada) {
        instrucciones.add(new Instruccion(Operacion.READ, entrada.getDestino()));
    }

    /**
     * <strong>Nombre:</strong> generarBreak
     *
     * <p><strong>Objetivo:</strong> Emitir un salto a la etiqueta de fin del switch actual.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> No hace nada si no hay un switch activo.</p>
     */
    private void generarBreak() {
        if (!destinosBreak.isEmpty()) {
            instrucciones.add(new Instruccion(Operacion.GOTO, destinosBreak.peek()));
        }
    }

    /**
     * <strong>Nombre:</strong> generarSwitch
     *
     * <p><strong>Objetivo:</strong> Generar un switch: comparar el selector contra cada caso saltando
     * al bloque que coincida (o al default), y luego emitir los bloques de los casos.</p>
     *
     * <p><strong>Entrada:</strong> SwitchNodo sentencia.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Apila la etiqueta de fin para que los {@code break} salgan del switch.</p>
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
     * <strong>Nombre:</strong> generarIf
     *
     * <p><strong>Objetivo:</strong> Generar un if/else: saltar sobre el bloque verdadero cuando la
     * condición es falsa y, si hay rama else, colocarla tras un salto que omite el bloque verdadero.</p>
     *
     * <p><strong>Entrada:</strong> IfNodo sentencia.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> generarWhile
     *
     * <p><strong>Objetivo:</strong> Generar un ciclo. En {@code do-while} ejecuta el cuerpo antes de
     * evaluar la condición; en {@code while} la evalúa antes. En ambos vuelve al inicio mientras sea verdadera.</p>
     *
     * <p><strong>Entrada:</strong> WhileNodo sentencia.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> generarExpresion
     *
     * <p><strong>Objetivo:</strong> Generar el código de una expresión y devolver el operando (temporal
     * o literal) que contiene su resultado.</p>
     *
     * <p><strong>Entrada:</strong> ExpresionNodo expresion.</p>
     *
     * <p><strong>Salida:</strong> String con el operando del resultado.</p>
     *
     * <p><strong>Restricciones:</strong> Devuelve {@code "<expr>"} para tipos de expresión no implementados.</p>
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
        throw new UnsupportedOperationException("Expresion no soportada en codigo intermedio: "
                + expresion.getClass().getSimpleName() + " (linea " + expresion.getLinea() + ")");
    }

    /**
     * <strong>Nombre:</strong> generarDestino
     *
     * <p><strong>Objetivo:</strong> Resolver el destino de una asignación: el nombre de la variable o
     * la referencia indexada {@code nombre[fila][col]} de un arreglo.</p>
     *
     * <p><strong>Entrada:</strong> ExpresionNodo expresion.</p>
     *
     * <p><strong>Salida:</strong> String con la referencia del destino.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> generarExpresionSinCarga
     *
     * <p><strong>Objetivo:</strong> Como {@link #generarExpresion}, pero usa el nombre directo de un
     * identificador (sin emitir un LOAD); útil para las dimensiones de un arreglo.</p>
     *
     * <p><strong>Entrada:</strong> ExpresionNodo expresion.</p>
     *
     * <p><strong>Salida:</strong> String con el operando.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private String generarExpresionSinCarga(ExpresionNodo expresion) {
        if (expresion instanceof IdentificadorNodo) {
            return ((IdentificadorNodo) expresion).getNombre();
        }
        return generarExpresion(expresion);
    }

    /**
     * <strong>Nombre:</strong> formatearLiteral
     *
     * <p><strong>Objetivo:</strong> Dar formato a un literal como operando: entre comillas las cadenas,
     * entre apóstrofos los caracteres.</p>
     *
     * <p><strong>Entrada:</strong> LiteralNodo literal.</p>
     *
     * <p><strong>Salida:</strong> String con el literal formateado.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> generarBinaria
     *
     * <p><strong>Objetivo:</strong> Generar una operación binaria: evaluar ambos operandos y guardar el resultado en un temporal.</p>
     *
     * <p><strong>Entrada:</strong> ExpresionBinariaNodo expresion.</p>
     *
     * <p><strong>Salida:</strong> String con el temporal del resultado.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private String generarBinaria(ExpresionBinariaNodo expresion) {
        String izquierda = generarExpresion(expresion.getIzquierda());
        String derecha = generarExpresion(expresion.getDerecha());
        String temporal = nuevoTemporal();
        instrucciones.add(new Instruccion(operacionBinaria(expresion.getOperador()), temporal, izquierda, derecha));
        return temporal;
    }

    /**
     * <strong>Nombre:</strong> generarUnaria
     *
     * <p><strong>Objetivo:</strong> Generar una operación unaria. Para {@code ++}/{@code --} calcula el
     * nuevo valor y lo vuelve a guardar; para las demás, emite la operación sobre un temporal.</p>
     *
     * <p><strong>Entrada:</strong> ExpresionUnariaNodo expresion.</p>
     *
     * <p><strong>Salida:</strong> String con el temporal del resultado.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> generarLlamada
     *
     * <p><strong>Objetivo:</strong> Generar una llamada a función: emitir un PARAM por cada argumento y
     * luego el CALL. Si la función es void no usa temporal; si retorna valor, lo deja en uno.</p>
     *
     * <p><strong>Entrada:</strong> LlamadaFuncionNodo llamada.</p>
     *
     * <p><strong>Salida:</strong> String con el temporal del resultado, o {@code null} si es void.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> esLlamadaVoid
     *
     * <p><strong>Objetivo:</strong> Indicar si la llamada no produce valor (función void/empty).</p>
     *
     * <p><strong>Entrada:</strong> LlamadaFuncionNodo llamada.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si la llamada es void.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private boolean esLlamadaVoid(LlamadaFuncionNodo llamada) {
        return llamada.getTipo() == TipoDato.VOID || llamada.getTipo() == TipoDato.EMPTY;
    }

    /**
     * <strong>Nombre:</strong> nuevoTemporal
     *
     * <p><strong>Objetivo:</strong> Generar un nombre de temporal nuevo y único ({@code _t0}, {@code _t1}, ...).</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> String con el temporal.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private String nuevoTemporal() {
        return "_t" + contadorTemporales++;
    }

    /**
     * <strong>Nombre:</strong> nuevaEtiqueta
     *
     * <p><strong>Objetivo:</strong> Generar un nombre de etiqueta nuevo y único ({@code _L0}, {@code _L1}, ...).</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> String con la etiqueta.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private String nuevaEtiqueta() {
        return "_L" + contadorEtiquetas++;
    }

    /**
     * <strong>Nombre:</strong> operacionBinaria
     *
     * <p><strong>Objetivo:</strong> Traducir el símbolo de un operador binario (incluidos los relacionales) a su {@link Operacion}.</p>
     *
     * <p><strong>Entrada:</strong> String operador.</p>
     *
     * <p><strong>Salida:</strong> Operacion correspondiente.</p>
     *
     * <p><strong>Restricciones:</strong> Lanza excepción si el operador no está soportado.</p>
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
     * <strong>Nombre:</strong> operacionUnaria
     *
     * <p><strong>Objetivo:</strong> Traducir el símbolo de un operador unario a su {@link Operacion} ({@code -} → NEG, {@code $} → NOT).</p>
     *
     * <p><strong>Entrada:</strong> String operador.</p>
     *
     * <p><strong>Salida:</strong> Operacion correspondiente.</p>
     *
     * <p><strong>Restricciones:</strong> Lanza excepción si el operador no está soportado.</p>
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
