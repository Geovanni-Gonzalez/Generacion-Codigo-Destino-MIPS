package intermedio;

import ast.AsignacionNodo;
import ast.AccesoArregloNodo;
import ast.AccesoMiembroNodo;
import ast.BloqueNodo;
import ast.BreakNodo;
import ast.CasoSwitchNodo;
import ast.ClaseNodo;
import ast.DeclaracionObjetoNodo;
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
import ast.LlamadaMetodoNodo;
import ast.Nodo;
import ast.NuevoObjetoNodo;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pipeline.CompiladorInternoException;

public class GeneradorCodigoIntermedio {
    /** Instrucciones emitidas durante la generación; se limpia en cada corrida. */
    private final List<Instruccion> instrucciones = new ArrayList<>();
    private int contadorTemporales;
    private int contadorEtiquetas;
    /** Pila con la etiqueta de fin del switch actual, destino de los {@code break}. */
    private final Deque<String> destinosBreak = new ArrayDeque<>();
    /** Layout (offset y tipo de cada campo, tamaño total) por clase. */
    private final Map<String, ClaseLayout> clasesLayout = new LinkedHashMap<>();
    /** Variable de objeto -> nombre de su clase, dentro de la función actual. */
    private final Map<String, String> objetoClase = new LinkedHashMap<>();
    /** Clases que declaran un constructor (método con el nombre de la clase). */
    private final Set<String> clasesConConstructor = new HashSet<>();

    /** Disposición en memoria de una clase: offset y tipo por campo, y tamaño total en bytes. */
    private static final class ClaseLayout {
        private final Map<String, Integer> offsets;
        private final Map<String, String> tipos;
        private final int tamanoBytes;

        ClaseLayout(Map<String, Integer> offsets, Map<String, String> tipos, int tamanoBytes) {
            this.offsets = offsets;
            this.tipos = tipos;
            this.tamanoBytes = tamanoBytes;
        }
    }

    public List<Instruccion> generar(ProgramaNodo programa) {
        instrucciones.clear();
        contadorTemporales = 0;
        contadorEtiquetas = 0;
        destinosBreak.clear();
        construirLayoutClases(programa);
        for (ClaseNodo clase : programa.getClases()) {
            for (FuncionNodo metodo : clase.getMetodos()) {
                generarMetodo(clase, metodo);
            }
        }
        for (FuncionNodo funcion : programa.getFunciones()) {
            generarFuncion(funcion);
        }
        return new ArrayList<>(instrucciones);
    }

    private void generarMetodo(ClaseNodo clase, FuncionNodo metodo) {
        objetoClase.clear();
        String nombreFuncion = mangle(clase.getNombre(), metodo.getNombre());
        instrucciones.add(new Instruccion(Operacion.INICIO_FUNC, nombreFuncion));
        instrucciones.add(new Instruccion(Operacion.FORMAL_PARAM, "this", "objeto"));
        objetoClase.put("this", clase.getNombre());
        for (ParametroNodo parametro : metodo.getParametros()) {
            instrucciones.add(new Instruccion(Operacion.FORMAL_PARAM, parametro.getNombre(),
                    parametro.getTipo().toString()));
        }
        generarBloque(metodo.getCuerpo());
        instrucciones.add(new Instruccion(Operacion.FIN_FUNC, nombreFuncion));
    }

    private String mangle(String clase, String metodo) {
        return clase + "_" + metodo;
    }

    private void construirLayoutClases(ProgramaNodo programa) {
        clasesLayout.clear();
        clasesConConstructor.clear();
        for (ClaseNodo clase : programa.getClases()) {
            Map<String, Integer> offsets = new LinkedHashMap<>();
            Map<String, String> tipos = new LinkedHashMap<>();
            int offset = 4;
            for (DeclaracionVariableNodo campo : clase.getCampos()) {
                offsets.put(campo.getNombre(), offset);
                tipos.put(campo.getNombre(), campo.getTipo().toString());
                offset += 4;
            }
            clasesLayout.put(clase.getNombre(), new ClaseLayout(offsets, tipos, offset));
            for (FuncionNodo metodo : clase.getMetodos()) {
                if (metodo.getNombre().equals(clase.getNombre())) {
                    clasesConConstructor.add(clase.getNombre());
                }
            }
        }
    }

    private void generarFuncion(FuncionNodo funcion) {
        objetoClase.clear();
        instrucciones.add(new Instruccion(Operacion.INICIO_FUNC, funcion.getNombre()));
        for (ParametroNodo parametro : funcion.getParametros()) {
            instrucciones.add(new Instruccion(Operacion.FORMAL_PARAM, parametro.getNombre(),
                    parametro.getTipo().toString()));
        }
        generarBloque(funcion.getCuerpo());
        instrucciones.add(new Instruccion(Operacion.FIN_FUNC, funcion.getNombre()));
    }

    private void generarBloque(BloqueNodo bloque) {
        for (Nodo nodo : bloque.getInstrucciones()) {
            generarNodo(nodo);
        }
    }

    private void generarNodo(Nodo nodo) {
        if (nodo instanceof AsignacionNodo) {
            generarAsignacion((AsignacionNodo) nodo);
        } else if (nodo instanceof DeclaracionObjetoNodo) {
            generarDeclaracionObjeto((DeclaracionObjetoNodo) nodo);
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

    private void generarAsignacion(AsignacionNodo asignacion) {
        if (asignacion.getDestino() instanceof AccesoMiembroNodo) {
            AccesoMiembroNodo acceso = (AccesoMiembroNodo) asignacion.getDestino();
            String valor = generarExpresion(asignacion.getValor());
            String objeto = nombreObjeto(acceso.getObjeto());
            String campoRef = offsetTipo(objeto, acceso.getNombreCampo());
            instrucciones.add(new Instruccion(Operacion.STORE_FIELD, objeto, valor, campoRef));
            return;
        }
        String valor = generarExpresion(asignacion.getValor());
        String destino = generarDestino(asignacion.getDestino());
        Operacion operacion = asignacion.getDestino() instanceof AccesoArregloNodo
                ? Operacion.STORE_ARRAY : Operacion.ASIG;
        instrucciones.add(new Instruccion(operacion, destino, valor));
    }

    private void generarDeclaracionObjeto(DeclaracionObjetoNodo declaracion) {
        objetoClase.put(declaracion.getNombre(), declaracion.getNombreClase());
        instrucciones.add(new Instruccion(Operacion.DECL, declaracion.getNombre(), "objeto"));
        if (declaracion.getInicializador() != null) {
            String valor = generarExpresion(declaracion.getInicializador());
            instrucciones.add(new Instruccion(Operacion.ASIG, declaracion.getNombre(), valor));
        }
    }

    private void generarReturn(ReturnNodo retorno) {
        String valor = retorno.getValor() == null ? null : generarExpresion(retorno.getValor());
        instrucciones.add(new Instruccion(Operacion.RETURN, null, valor));
    }

    private void generarExpresionSentencia(ExpresionSentenciaNodo sentencia) {
        generarExpresion(sentencia.getExpresion());
    }

    private void generarSalida(SalidaNodo salida) {
        instrucciones.add(new Instruccion(Operacion.PRINT, generarExpresion(salida.getValor())));
    }

    private void generarEntrada(EntradaNodo entrada) {
        instrucciones.add(new Instruccion(Operacion.READ, entrada.getDestino()));
    }

    private void generarBreak() {
        if (!destinosBreak.isEmpty()) {
            instrucciones.add(new Instruccion(Operacion.GOTO, destinosBreak.peek()));
        }
    }

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
        if (expresion instanceof NuevoObjetoNodo) {
            return generarNuevoObjeto((NuevoObjetoNodo) expresion);
        }
        if (expresion instanceof LlamadaMetodoNodo) {
            return generarLlamadaMetodo((LlamadaMetodoNodo) expresion);
        }
        if (expresion instanceof AccesoMiembroNodo) {
            AccesoMiembroNodo acceso = (AccesoMiembroNodo) expresion;
            String objeto = nombreObjeto(acceso.getObjeto());
            String campoRef = offsetTipo(objeto, acceso.getNombreCampo());
            String temporal = nuevoTemporal();
            instrucciones.add(new Instruccion(Operacion.LOAD_FIELD, temporal, objeto, campoRef));
            return temporal;
        }
        throw new CompiladorInternoException("Expresion no soportada en codigo intermedio: "
                + expresion.getClass().getSimpleName(),
                expresion.getLinea(), expresion.getColumna());
    }

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

    private String generarExpresionSinCarga(ExpresionNodo expresion) {
        if (expresion instanceof IdentificadorNodo) {
            return ((IdentificadorNodo) expresion).getNombre();
        }
        return generarExpresion(expresion);
    }

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

    private String generarBinaria(ExpresionBinariaNodo expresion) {
        String izquierda = generarExpresion(expresion.getIzquierda());
        String derecha = generarExpresion(expresion.getDerecha());
        String temporal = nuevoTemporal();
        instrucciones.add(new Instruccion(operacionBinaria(expresion.getOperador()), temporal, izquierda, derecha));
        return temporal;
    }

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

    private boolean esLlamadaVoid(LlamadaFuncionNodo llamada) {
        return llamada.getTipo() == TipoDato.VOID || llamada.getTipo() == TipoDato.EMPTY;
    }

    private String nuevoTemporal() {
        return "_t" + contadorTemporales++;
    }

    private String generarNuevoObjeto(NuevoObjetoNodo nuevo) {
        ClaseLayout layout = clasesLayout.get(nuevo.getNombreClase());
        int tamano = layout != null ? layout.tamanoBytes : 4;
        String temporal = nuevoTemporal();
        instrucciones.add(new Instruccion(Operacion.NEW, temporal, nuevo.getNombreClase(),
                String.valueOf(tamano)));
        if (clasesConConstructor.contains(nuevo.getNombreClase())) {
            instrucciones.add(new Instruccion(Operacion.PARAM, temporal));
            for (ExpresionNodo argumento : nuevo.getArgumentos()) {
                instrucciones.add(new Instruccion(Operacion.PARAM, generarExpresion(argumento)));
            }
            instrucciones.add(new Instruccion(Operacion.CALL, null,
                    mangle(nuevo.getNombreClase(), nuevo.getNombreClase()),
                    String.valueOf(1 + nuevo.getArgumentos().size())));
        }
        return temporal;
    }

    private String generarLlamadaMetodo(LlamadaMetodoNodo llamada) {
        String receptor = nombreObjeto(llamada.getObjeto());
        String clase = objetoClase.get(receptor);
        if (clase == null) {
            throw new CompiladorInternoException("No se pudo determinar la clase del objeto receptor '"
                    + receptor + "'", llamada.getLinea(), llamada.getColumna());
        }
        String nombreFuncion = mangle(clase, llamada.getNombreMetodo());
        instrucciones.add(new Instruccion(Operacion.PARAM, receptor));
        for (ExpresionNodo argumento : llamada.getArgumentos()) {
            instrucciones.add(new Instruccion(Operacion.PARAM, generarExpresion(argumento)));
        }
        String cantidad = String.valueOf(1 + llamada.getArgumentos().size());
        if (esTipoVacio(llamada.getTipo())) {
            instrucciones.add(new Instruccion(Operacion.CALL, null, nombreFuncion, cantidad));
            return null;
        }
        String temporal = nuevoTemporal();
        instrucciones.add(new Instruccion(Operacion.CALL, temporal, nombreFuncion, cantidad));
        return temporal;
    }

    private boolean esTipoVacio(TipoDato tipo) {
        return tipo == TipoDato.VOID || tipo == TipoDato.EMPTY;
    }

    private String nombreObjeto(ExpresionNodo objeto) {
        if (objeto instanceof IdentificadorNodo) {
            return ((IdentificadorNodo) objeto).getNombre();
        }
        return generarExpresion(objeto);
    }

    private String offsetTipo(String objeto, String campo) {
        ClaseLayout layout = clasesLayout.get(objetoClase.get(objeto));
        if (layout == null || !layout.offsets.containsKey(campo)) {
            return "4:int";
        }
        return layout.offsets.get(campo) + ":" + layout.tipos.get(campo);
    }

    private String nuevaEtiqueta() {
        return "_L" + contadorEtiquetas++;
    }

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
