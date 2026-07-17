package semantico;

import ast.AccesoArregloNodo;
import ast.AccesoMiembroNodo;
import ast.AsignacionNodo;
import ast.BloqueNodo;
import ast.BreakNodo;
import ast.CasoSwitchNodo;
import ast.ClaseNodo;
import ast.DeclaracionVariableNodo;
import ast.EntradaNodo;
import ast.ExpresionBinariaNodo;
import ast.ExpresionNodo;
import ast.ExpresionUnariaNodo;
import ast.FuncionNodo;
import ast.IdentificadorNodo;
import ast.InicializacionArregloNodo;
import ast.LlamadaFuncionNodo;
import ast.LlamadaMetodoNodo;
import ast.LiteralNodo;
import ast.Nodo;
import ast.NuevoObjetoNodo;
import ast.ParametroNodo;
import ast.ProgramaNodo;
import ast.ReturnNodo;
import ast.SalidaNodo;
import ast.SwitchNodo;
import ast.TipoDato;
import ast.WhileNodo;
import ast.IfNodo;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AnalizadorSemantico {
    private final TablaDeSimbolos tablaSimbolos;
    private final Consumer<String> reportadorSintactico;
    /** Registro de clases declaradas: nombre -> informacion estructural. */
    private final Map<String, ClaseInfo> clases = new LinkedHashMap<>();
    /** Clase cuyo cuerpo se esta analizando actualmente (null fuera de una clase). */
    private ClaseInfo claseEnConstruccion;
    /** Indica si la clase en construccion debe registrarse al finalizar (false si es redeclaracion). */
    private boolean registrarClaseActual;
    private int cantidadMain;
    private TipoDato tipoRetornoActual = TipoDato.DESCONOCIDO;
    private String funcionActual;
    private int lineaFuncionActual;

    public AnalizadorSemantico(TablaDeSimbolos tablaSimbolos, Consumer<String> reportadorSintactico) {
        this.tablaSimbolos = tablaSimbolos;
        this.reportadorSintactico = reportadorSintactico;
    }

    public void registrarMain() {
        cantidadMain++;
    }

    public void verificarMain() {
        if (cantidadMain == 0) {
            tablaSimbolos.reportarMainObligatorio();
        }
    }

    public void abrirPrograma() {
        tablaSimbolos.abrirAlcance();
    }

    public void cerrarPrograma() {
        tablaSimbolos.cerrarAlcance();
    }

    public void abrirFuncion(String nombre, TipoDato tipoRetorno, int linea) {
        registrarFuncion(nombre, tipoRetorno, linea);
        tipoRetornoActual = tipoRetorno;
        funcionActual = nombre;
        lineaFuncionActual = linea;
        tablaSimbolos.abrirAlcance();
    }

    public void cerrarFuncion() {
        tablaSimbolos.cerrarAlcance();
        tipoRetornoActual = TipoDato.DESCONOCIDO;
        funcionActual = null;
        lineaFuncionActual = 0;
    }

    public void abrirBloque() {
        tablaSimbolos.abrirAlcance();
    }

    public void cerrarBloque() {
        tablaSimbolos.cerrarAlcance();
    }

    public void actualizarFirmaFuncion(String nombre, TipoDato tipoRetorno,
                                       List<ParametroNodo> parametros, int linea) {
        List<TipoDato> tiposParametros = new ArrayList<>();
        for (ParametroNodo parametro : parametros) {
            tiposParametros.add(parametro.getTipo());
        }
        tablaSimbolos.actualizarFirmaFuncion(nombre, tiposParametros, tipoRetorno, linea);
    }

    public void registrarParametro(String nombre, TipoDato tipo, int linea) {
        insertarSimbolo(new Simbolo(nombre, tipo, CategoriaSimb.PARAMETRO, linea, true));
        if (funcionActual != null) {
            tablaSimbolos.agregarParametroAFuncion(funcionActual, tipo, lineaFuncionActual);
        }
    }

    public void registrarVariable(String nombre, TipoDato tipo, int linea) {
        insertarSimbolo(new Simbolo(nombre, tipo, CategoriaSimb.VAR, linea, false));
    }

    public void registrarDeclaracionVariable(String nombre, TipoDato tipo,
            ExpresionNodo inicializador, int linea) {
        if (!tipo.esDeclarableVariable()) {
            tablaSimbolos.reportarTipoDeclaracionInvalido(tipo, linea);
            return;
        }

        if (inicializador != null) {
            TipoDato tipoInicializador = tipoExpresion(inicializador);
            if (tipoInicializador != TipoDato.ERROR && tipoInicializador != TipoDato.DESCONOCIDO
                    && tipo != tipoInicializador) {
                tablaSimbolos.reportarAsignacionIncompatible(tipoInicializador, tipo, linea);
                return;
            }
        }

        insertarSimbolo(new Simbolo(nombre, tipo, CategoriaSimb.VAR, linea, inicializador != null));
    }

    public void registrarDeclaracionArreglo(String nombre, TipoDato tipo, ExpresionNodo filas,
                                            ExpresionNodo columnas,
                                            InicializacionArregloNodo inicializacion, int linea) {
        if (!tipo.esDeclarableVariable()) {
            tablaSimbolos.reportarTipoDeclaracionInvalido(tipo, linea);
            return;
        }
        validarDimensionArreglo(nombre, "fila", filas);
        validarDimensionArreglo(nombre, "columna", columnas);
        Integer cantidadFilas = valorEnteroLiteral(filas);
        Integer cantidadColumnas = valorEnteroLiteral(columnas);
        if (inicializacion != null) {
            validarInicializacionArreglo(nombre, tipo, inicializacion);
            validarDimensionesInicializacion(nombre, cantidadFilas, cantidadColumnas,
                    inicializacion);
        }
        insertarSimbolo(new Simbolo(nombre, tipo, CategoriaSimb.ARREGLO, linea,
                inicializacion != null, cantidadFilas, cantidadColumnas));
    }

    public void iniciarClase(String nombre, String padre, int linea) {
        claseEnConstruccion = new ClaseInfo(nombre, padre, linea);
        registrarClaseActual = !clases.containsKey(nombre);
        if (!registrarClaseActual) {
            tablaSimbolos.reportarClaseRedeclarada(nombre, linea);
            return;
        }
        // Se registra de inmediato (no al finalizar) para que sus propios metodos puedan
        // referenciarla via 'this' y campos durante el analisis de sus cuerpos.
        clases.put(nombre, claseEnConstruccion);
        insertarSimbolo(new Simbolo(nombre, TipoDato.OBJETO, CategoriaSimb.CLASE, linea, true));
    }

    public void agregarCampoClase(String nombre, TipoDato tipo, int linea) {
        if (claseEnConstruccion == null) {
            return;
        }
        if (!tipo.esDeclarableVariable()) {
            tablaSimbolos.reportarTipoDeclaracionInvalido(tipo, linea);
            return;
        }
        if (!claseEnConstruccion.agregarCampo(nombre, tipo)) {
            tablaSimbolos.reportarCampoNoDeclarado(claseEnConstruccion.getNombre(), nombre, linea);
        }
    }

    public void abrirMetodo(String nombre, TipoDato tipoRetorno, int linea) {
        tablaSimbolos.abrirAlcance();
        if (claseEnConstruccion != null) {
            insertarSimbolo(Simbolo.objeto("this", claseEnConstruccion.getNombre(), linea, true));
        }
        tipoRetornoActual = tipoRetorno;
        funcionActual = nombre;
        lineaFuncionActual = linea;
    }

    public void cerrarMetodo(FuncionNodo metodo) {
        if (claseEnConstruccion != null) {
            List<TipoDato> tiposParametros = new ArrayList<>();
            for (ParametroNodo parametro : metodo.getParametros()) {
                tiposParametros.add(parametro.getTipo());
            }
            claseEnConstruccion.agregarMetodo(new ClaseInfo.MetodoInfo(metodo.getNombre(),
                    metodo.getTipoRetorno(), tiposParametros));
        }
        tablaSimbolos.cerrarAlcance();
        tipoRetornoActual = TipoDato.DESCONOCIDO;
        funcionActual = null;
        lineaFuncionActual = 0;
    }

    public void finalizarClase() {
        // La clase ya se registro en iniciarClase; aqui solo se cierra el contexto de construccion.
        claseEnConstruccion = null;
        registrarClaseActual = false;
    }

    public void registrarDeclaracionObjeto(String nombre, String nombreClase,
                                           ExpresionNodo inicializador, int linea) {
        if (!clases.containsKey(nombreClase)) {
            tablaSimbolos.reportarClaseNoDeclarada(nombreClase, linea);
            return;
        }
        if (inicializador instanceof NuevoObjetoNodo) {
            NuevoObjetoNodo nuevo = (NuevoObjetoNodo) inicializador;
            evaluarTipo(nuevo);
            if (clases.containsKey(nuevo.getNombreClase())
                    && !esCompatibleObjeto(nombreClase, nuevo.getNombreClase())) {
                tablaSimbolos.reportarTipoObjetoIncompatible(nombreClase, nuevo.getNombreClase(), linea);
            }
        }
        insertarSimbolo(Simbolo.objeto(nombre, nombreClase, linea, inicializador != null));
    }

    public void usarIdentificador(String nombre, int linea) {
        Simbolo simbolo = tablaSimbolos.buscar(nombre, linea);
        if (simbolo.getTipo() == TipoDato.ERROR) {
            return;
        }
        if (simbolo.getCategoria() == CategoriaSimb.ARREGLO) {
            tablaSimbolos.reportarUsoArregloComoEscalar(nombre, linea);
            return;
        }
        if (!simbolo.isInicializado()) {
            tablaSimbolos.reportarVariableNoInicializada(nombre, linea);
        }
    }

    public void verificarAsignacion(AsignacionNodo asignacion) {
        TipoDato tipoDestino = evaluarTipoDestino(asignacion.getDestino());
        TipoDato tipoValor = evaluarTipo(asignacion.getValor());

        if (tipoDestino == TipoDato.ERROR || tipoValor == TipoDato.ERROR
                || tipoDestino == TipoDato.DESCONOCIDO || tipoValor == TipoDato.DESCONOCIDO) {
            return;
        }

        if (tipoDestino != tipoValor) {
            tablaSimbolos.reportarAsignacionIncompatible(nombreDestino(asignacion.getDestino()),
                    tipoDestino, tipoValor, asignacion.getLinea());
            return;
        }

        marcarDestinoInicializado(asignacion.getDestino());
    }

    public void verificarSwitch(SwitchNodo switchNodo) {
        TipoDato tipoSwitch = evaluarTipo(switchNodo.getExpresion());
        if (tipoSwitch == TipoDato.ERROR || tipoSwitch == TipoDato.DESCONOCIDO) {
            return;
        }
        if (tipoSwitch == TipoDato.BOOL || tipoSwitch == TipoDato.FLOAT
                || tipoSwitch == TipoDato.VOID || tipoSwitch == TipoDato.EMPTY) {
            tablaSimbolos.reportarSwitchTipoInvalido(tipoSwitch, switchNodo.getLinea());
        }
        for (CasoSwitchNodo caso : switchNodo.getCasos()) {
            if (caso.isDefecto() || caso.getValor() == null) {
                continue;
            }
            TipoDato tipoCase = evaluarTipo(caso.getValor());
            if (tipoCase == TipoDato.ERROR || tipoCase == TipoDato.DESCONOCIDO) {
                continue;
            }
            if (tipoCase != tipoSwitch) {
                tablaSimbolos.reportarCaseTipoIncompatible(tipoSwitch, tipoCase, caso.getLinea());
            }
        }
    }

    public void verificarEntrada(EntradaNodo entrada) {
        Simbolo simbolo = tablaSimbolos.buscar(entrada.getDestino(), entrada.getLinea());
        if (simbolo.getTipo() == TipoDato.ERROR) {
            return;
        }
        if (simbolo.getCategoria() == CategoriaSimb.ARREGLO
                || (simbolo.getTipo() != TipoDato.INT && simbolo.getTipo() != TipoDato.FLOAT)) {
            tablaSimbolos.reportarEntradaTipoInvalido(entrada.getDestino(), simbolo.getTipo(),
                    entrada.getLinea());
            return;
        }
        tablaSimbolos.marcarInicializado(entrada.getDestino());
    }

    public void verificarSalida(SalidaNodo salida) {
        TipoDato tipo = evaluarTipo(salida.getValor());
        if (tipo == TipoDato.ERROR || tipo == TipoDato.DESCONOCIDO) {
            return;
        }
        if (tipo != TipoDato.STRING && tipo != TipoDato.CHAR && tipo != TipoDato.INT
                && tipo != TipoDato.BOOL && tipo != TipoDato.FLOAT) {
            tablaSimbolos.reportarSalidaTipoInvalido(tipo, salida.getLinea());
        }
    }

    public TipoDato evaluarTipo(ExpresionNodo expresion) {
        if (expresion == null) {
            return TipoDato.ERROR;
        }

        TipoDato tipoActual = expresion.getTipo();
        if (tipoActual != TipoDato.DESCONOCIDO) {
            return tipoActual;
        }

        TipoDato tipo = TipoDato.ERROR;
        if (expresion instanceof IdentificadorNodo) {
            IdentificadorNodo id = (IdentificadorNodo) expresion;
            Simbolo simbolo = tablaSimbolos.buscar(id.getNombre(), id.getLinea());
            if (simbolo.getTipo() == TipoDato.ERROR) {
                return TipoDato.ERROR;
            }
            if (simbolo.getCategoria() == CategoriaSimb.ARREGLO) {
                tablaSimbolos.reportarUsoArregloComoEscalar(id.getNombre(), id.getLinea());
                return TipoDato.ERROR;
            }
            if (!simbolo.isInicializado()) {
                tablaSimbolos.reportarVariableNoInicializada(id.getNombre(), id.getLinea());
            }
            tipo = simbolo.getTipo();
        } else if (expresion instanceof AccesoArregloNodo) {
            tipo = evaluarTipoAccesoArreglo((AccesoArregloNodo) expresion, true);
        } else if (expresion instanceof LlamadaFuncionNodo) {
            tipo = evaluarTipoLlamada((LlamadaFuncionNodo) expresion);
        } else if (expresion instanceof ExpresionBinariaNodo) {
            tipo = evaluarTipoBinaria((ExpresionBinariaNodo) expresion);
        } else if (expresion instanceof ExpresionUnariaNodo) {
            tipo = evaluarTipoUnaria((ExpresionUnariaNodo) expresion);
        } else if (expresion instanceof NuevoObjetoNodo) {
            tipo = evaluarTipoNuevo((NuevoObjetoNodo) expresion);
        } else if (expresion instanceof AccesoMiembroNodo) {
            tipo = evaluarTipoAccesoMiembro((AccesoMiembroNodo) expresion);
        } else if (expresion instanceof LlamadaMetodoNodo) {
            tipo = evaluarTipoLlamadaMetodo((LlamadaMetodoNodo) expresion);
        }

        expresion.setTipo(tipo);
        return tipo;
    }

    public TipoDato tipoExpresion(ExpresionNodo expresion) {
        return evaluarTipo(expresion);
    }

    public void verificarCondicionBooleana(ExpresionNodo condicion) {
        TipoDato tipo = evaluarTipo(condicion);
        if (tipo == TipoDato.ERROR || tipo == TipoDato.DESCONOCIDO) {
            return;
        }
        if (tipo != TipoDato.BOOL) {
            tablaSimbolos.reportarCondicionNoBooleana(tipo, condicion.getLinea());
        }
    }

    public void verificarReturn(ReturnNodo retorno) {
        TipoDato tipoEsperado = tipoRetornoActual;
        ExpresionNodo valor = retorno.getValor();
        boolean funcionVoid = tipoEsperado == TipoDato.EMPTY || tipoEsperado == TipoDato.VOID;

        if (valor == null) {
            if (!funcionVoid && tipoEsperado != TipoDato.ERROR && tipoEsperado != TipoDato.DESCONOCIDO) {
                tablaSimbolos.reportarReturnSinValor(tipoEsperado, retorno.getLinea());
            }
            return;
        }

        if (funcionVoid) {
            tablaSimbolos.reportarReturnConValorEnVoid(retorno.getLinea());
            evaluarTipo(valor);
            return;
        }

        TipoDato tipoRecibido = evaluarTipo(valor);
        if (tipoEsperado == TipoDato.ERROR || tipoEsperado == TipoDato.DESCONOCIDO
                || tipoRecibido == TipoDato.ERROR || tipoRecibido == TipoDato.DESCONOCIDO) {
            return;
        }
        if (tipoEsperado != tipoRecibido) {
            tablaSimbolos.reportarReturnTipoIncompatible(tipoEsperado, tipoRecibido, retorno.getLinea());
        }
    }

    public void verificarContextoBreak(ProgramaNodo programa) {
        for (FuncionNodo funcion : programa.getFunciones()) {
            verificarBreakEnBloque(funcion.getCuerpo(), false);
        }
        for (ClaseNodo clase : programa.getClases()) {
            for (FuncionNodo metodo : clase.getMetodos()) {
                verificarBreakEnBloque(metodo.getCuerpo(), false);
            }
        }
    }

    public void verificarRutasRetorno(ProgramaNodo programa) {
        for (FuncionNodo funcion : programa.getFunciones()) {
            verificarRetornoDeFuncion(funcion);
        }
        for (ClaseNodo clase : programa.getClases()) {
            for (FuncionNodo metodo : clase.getMetodos()) {
                verificarRetornoDeFuncion(metodo);
            }
        }
    }

    private void verificarRetornoDeFuncion(FuncionNodo funcion) {
        TipoDato tipo = funcion.getTipoRetorno();
        boolean requiereRetorno = tipo != TipoDato.VOID && tipo != TipoDato.EMPTY
                && tipo != TipoDato.ERROR && tipo != TipoDato.DESCONOCIDO;
        if (requiereRetorno && !bloqueGarantizaRetorno(funcion.getCuerpo())) {
            tablaSimbolos.reportarReturnFaltante(tipo, funcion.getLinea());
        }
    }

    private boolean bloqueGarantizaRetorno(BloqueNodo bloque) {
        if (bloque == null) {
            return false;
        }
        for (Nodo nodo : bloque.getInstrucciones()) {
            if (nodo instanceof ReturnNodo) {
                return ((ReturnNodo) nodo).getValor() != null;
            }
            if (nodo instanceof BreakNodo) {
                return false;
            }
            if (nodo instanceof IfNodo) {
                IfNodo condicional = (IfNodo) nodo;
                if (condicional.getBloqueSino() != null
                        && bloqueGarantizaRetorno(condicional.getBloqueEntonces())
                        && bloqueGarantizaRetorno(condicional.getBloqueSino())) {
                    return true;
                }
            } else if (nodo instanceof WhileNodo) {
                WhileNodo ciclo = (WhileNodo) nodo;
                if (ciclo.isDoWhile() && bloqueGarantizaRetorno(ciclo.getCuerpo())) {
                    return true;
                }
            } else if (nodo instanceof SwitchNodo
                    && switchGarantizaRetorno((SwitchNodo) nodo)) {
                return true;
            } else if (nodo instanceof BloqueNodo
                    && bloqueGarantizaRetorno((BloqueNodo) nodo)) {
                return true;
            }
        }
        return false;
    }

    private boolean switchGarantizaRetorno(SwitchNodo seleccion) {
        boolean tieneDefault = false;
        for (CasoSwitchNodo caso : seleccion.getCasos()) {
            tieneDefault |= caso.isDefecto();
            if (!bloqueGarantizaRetorno(caso.getBloque())) {
                return false;
            }
        }
        return tieneDefault;
    }

    private void verificarBreakEnBloque(BloqueNodo bloque, boolean permitido) {
        if (bloque == null) {
            return;
        }
        for (Nodo nodo : bloque.getInstrucciones()) {
            if (nodo instanceof BreakNodo) {
                if (!permitido) {
                    tablaSimbolos.reportarBreakFueraDeCicloOSwitch(nodo.getLinea());
                }
            } else if (nodo instanceof WhileNodo) {
                verificarBreakEnBloque(((WhileNodo) nodo).getCuerpo(), true);
            } else if (nodo instanceof SwitchNodo) {
                for (CasoSwitchNodo caso : ((SwitchNodo) nodo).getCasos()) {
                    verificarBreakEnBloque(caso.getBloque(), true);
                }
            } else if (nodo instanceof IfNodo) {
                IfNodo condicional = (IfNodo) nodo;
                verificarBreakEnBloque(condicional.getBloqueEntonces(), permitido);
                verificarBreakEnBloque(condicional.getBloqueSino(), permitido);
            } else if (nodo instanceof BloqueNodo) {
                verificarBreakEnBloque((BloqueNodo) nodo, permitido);
            }
        }
    }

    private void registrarFuncion(String nombre, TipoDato tipoRetorno, int linea) {
        insertarSimbolo(new Simbolo(nombre, new ArrayList<TipoDato>(), tipoRetorno, linea));
    }

    private void insertarSimbolo(Simbolo simbolo) {
        try {
            tablaSimbolos.insertar(simbolo);
        } catch (IllegalArgumentException ex) {
            reportadorSintactico.accept(ex.getMessage());
        }
    }

    private TipoDato evaluarTipoLlamada(LlamadaFuncionNodo llamada) {
        Simbolo funcion = tablaSimbolos.buscarFuncion(llamada.getNombre(), llamada.getLinea());
        if (funcion.getTipo() == TipoDato.ERROR) {
            return TipoDato.ERROR;
        }

        List<TipoDato> parametros = funcion.getTiposParametros();
        List<ExpresionNodo> argumentos = llamada.getArgumentos();
        if (parametros.size() != argumentos.size()) {
            tablaSimbolos.reportarCantidadArgumentosIncorrecta(parametros.size(), argumentos.size(),
                    llamada.getLinea());
            return TipoDato.ERROR;
        }

        for (int i = 0; i < argumentos.size(); i++) {
            TipoDato tipoArgumento = evaluarTipo(argumentos.get(i));
            TipoDato tipoParametro = parametros.get(i);
            if (tipoArgumento == TipoDato.ERROR || tipoArgumento == TipoDato.DESCONOCIDO) {
                return TipoDato.ERROR;
            }
            if (tipoArgumento != tipoParametro) {
                tablaSimbolos.reportarTipoArgumentoIncorrecto(i + 1, tipoParametro, tipoArgumento,
                        argumentos.get(i).getLinea());
                return TipoDato.ERROR;
            }
        }

        return funcion.getTipoRetorno() != null ? funcion.getTipoRetorno() : funcion.getTipo();
    }

    private TipoDato evaluarTipoBinaria(ExpresionBinariaNodo expresion) {
        TipoDato izquierda = evaluarTipo(expresion.getIzquierda());
        TipoDato derecha = evaluarTipo(expresion.getDerecha());
        if (izquierda == TipoDato.ERROR || derecha == TipoDato.ERROR
                || izquierda == TipoDato.DESCONOCIDO || derecha == TipoDato.DESCONOCIDO) {
            return TipoDato.ERROR;
        }

        String operador = expresion.getOperador();
        if ("#".equals(operador) || "@".equals(operador)) {
            if (izquierda == TipoDato.BOOL && derecha == TipoDato.BOOL) {
                return TipoDato.BOOL;
            }
            tablaSimbolos.reportarOperacionIncompatible(operador, izquierda, derecha, expresion.getLinea());
            return TipoDato.ERROR;
        }
        if ("equal".equals(operador) || "n_equal".equals(operador)) {
            if (izquierda == derecha && (izquierda == TipoDato.INT
                    || izquierda == TipoDato.FLOAT || izquierda == TipoDato.BOOL)) {
                return TipoDato.BOOL;
            }
            tablaSimbolos.reportarOperacionIncompatible(operador, izquierda, derecha, expresion.getLinea());
            return TipoDato.ERROR;
        }
        if ("less_t".equals(operador) || "less_te".equals(operador)
                || "greather_t".equals(operador) || "greather_te".equals(operador)) {
            if (izquierda.esNumerico() && derecha.esNumerico() && izquierda == derecha) {
                return TipoDato.BOOL;
            }
            tablaSimbolos.reportarOperacionIncompatible(operador, izquierda, derecha, expresion.getLinea());
            return TipoDato.ERROR;
        }
        if ("+".equals(operador) || "-".equals(operador) || "*".equals(operador)
                || "/".equals(operador)) {
            if (izquierda.esNumerico() && derecha.esNumerico()) {
                return izquierda == TipoDato.FLOAT || derecha == TipoDato.FLOAT
                        ? TipoDato.FLOAT
                        : TipoDato.INT;
            }
            tablaSimbolos.reportarOperacionIncompatible(operador, izquierda, derecha, expresion.getLinea());
            return TipoDato.ERROR;
        }
        if ("%".equals(operador)) {
            if (izquierda.esNumerico() && derecha.esNumerico() && izquierda == derecha) {
                return izquierda;
            }
            tablaSimbolos.reportarOperacionIncompatible(operador, izquierda, derecha, expresion.getLinea());
            return TipoDato.ERROR;
        }
        if ("^".equals(operador)) {
            if (izquierda.esNumerico() && izquierda == derecha) {
                return izquierda;
            }
            tablaSimbolos.reportarOperacionIncompatible(operador, izquierda, derecha, expresion.getLinea());
            return TipoDato.ERROR;
        }

        return TipoDato.ERROR;
    }

    private TipoDato evaluarTipoUnaria(ExpresionUnariaNodo expresion) {
        TipoDato tipo = evaluarTipo(expresion.getExpresion());
        if (tipo == TipoDato.ERROR || tipo == TipoDato.DESCONOCIDO) {
            return TipoDato.ERROR;
        }

        String operador = expresion.getOperador();
        if ("$".equals(operador)) {
            if (tipo == TipoDato.BOOL) {
                return TipoDato.BOOL;
            }
            tablaSimbolos.reportarOperacionIncompatible(operador, tipo, expresion.getLinea());
            return TipoDato.ERROR;
        }
        if ("-".equals(operador) || "++".equals(operador) || "--".equals(operador)) {
            if ("-".equals(operador) && !(expresion.getExpresion() instanceof LiteralNodo)) {
                tablaSimbolos.reportarNegativoSobreExpresionNoLiteral(tipo, expresion.getLinea());
                return TipoDato.ERROR;
            }
            if (("++".equals(operador) || "--".equals(operador)) && !esModificable(expresion.getExpresion())) {
                tablaSimbolos.reportarOperandoNoModificable(operador, tipo, expresion.getLinea());
                return TipoDato.ERROR;
            }
            if (tipo.esNumerico()) {
                return tipo;
            }
            tablaSimbolos.reportarOperacionIncompatible(operador, tipo, expresion.getLinea());
            return TipoDato.ERROR;
        }

        return TipoDato.ERROR;
    }

    private TipoDato evaluarTipoNuevo(NuevoObjetoNodo nuevo) {
        if (!clases.containsKey(nuevo.getNombreClase())) {
            tablaSimbolos.reportarClaseNoDeclarada(nuevo.getNombreClase(), nuevo.getLinea());
            return TipoDato.ERROR;
        }
        ClaseInfo.MetodoInfo constructor = clases.get(nuevo.getNombreClase())
                .metodo(nuevo.getNombreClase());
        if (constructor != null) {
            validarArgumentos(constructor.getTiposParametros(), nuevo.getArgumentos(),
                    nuevo.getLinea());
        } else if (!nuevo.getArgumentos().isEmpty()) {
            tablaSimbolos.reportarCantidadArgumentosIncorrecta(0, nuevo.getArgumentos().size(),
                    nuevo.getLinea());
        }
        return TipoDato.OBJETO;
    }

    private TipoDato evaluarTipoLlamadaMetodo(LlamadaMetodoNodo llamada) {
        TipoDato tipoObjeto = evaluarTipo(llamada.getObjeto());
        String clase = claseDe(llamada.getObjeto());
        if (clase == null || !clases.containsKey(clase)) {
            if (tipoObjeto != TipoDato.ERROR && tipoObjeto != TipoDato.DESCONOCIDO) {
                tablaSimbolos.reportarAccesoCampoSobreNoObjeto(tipoObjeto, llamada.getLinea());
            }
            return TipoDato.ERROR;
        }
        ClaseInfo.MetodoInfo metodo = clases.get(clase).metodo(llamada.getNombreMetodo());
        if (metodo == null) {
            tablaSimbolos.reportarMetodoNoDeclarado(clase, llamada.getNombreMetodo(),
                    llamada.getLinea());
            return TipoDato.ERROR;
        }
        validarArgumentos(metodo.getTiposParametros(), llamada.getArgumentos(), llamada.getLinea());
        return metodo.getTipoRetorno();
    }

    private void validarArgumentos(List<TipoDato> esperados, List<ExpresionNodo> argumentos,
                                   int linea) {
        if (esperados.size() != argumentos.size()) {
            tablaSimbolos.reportarCantidadArgumentosIncorrecta(esperados.size(), argumentos.size(),
                    linea);
            return;
        }
        for (int i = 0; i < argumentos.size(); i++) {
            TipoDato tipoArgumento = evaluarTipo(argumentos.get(i));
            if (tipoArgumento == TipoDato.ERROR || tipoArgumento == TipoDato.DESCONOCIDO) {
                continue;
            }
            if (tipoArgumento != esperados.get(i)) {
                tablaSimbolos.reportarTipoArgumentoIncorrecto(i + 1, esperados.get(i), tipoArgumento,
                        argumentos.get(i).getLinea());
            }
        }
    }

    private TipoDato evaluarTipoAccesoMiembro(AccesoMiembroNodo acceso) {
        TipoDato tipoObjeto = evaluarTipo(acceso.getObjeto());
        String clase = claseDe(acceso.getObjeto());
        if (clase == null || !clases.containsKey(clase)) {
            if (tipoObjeto != TipoDato.ERROR && tipoObjeto != TipoDato.DESCONOCIDO) {
                tablaSimbolos.reportarAccesoCampoSobreNoObjeto(tipoObjeto, acceso.getLinea());
            }
            return TipoDato.ERROR;
        }
        TipoDato tipoCampo = tipoCampoEnJerarquia(clase, acceso.getNombreCampo());
        if (tipoCampo == null) {
            tablaSimbolos.reportarCampoNoDeclarado(clase, acceso.getNombreCampo(), acceso.getLinea());
            return TipoDato.ERROR;
        }
        return tipoCampo;
    }

    private String claseDe(ExpresionNodo expresion) {
        if (expresion instanceof IdentificadorNodo) {
            Simbolo simbolo = tablaSimbolos.buscar(((IdentificadorNodo) expresion).getNombre(),
                    expresion.getLinea());
            return simbolo.getNombreClase();
        }
        if (expresion instanceof NuevoObjetoNodo) {
            return ((NuevoObjetoNodo) expresion).getNombreClase();
        }
        return null;
    }

    private TipoDato tipoCampoEnJerarquia(String nombreClase, String campo) {
        ClaseInfo info = clases.get(nombreClase);
        return info == null ? null : info.tipoCampo(campo);
    }

    private boolean esCompatibleObjeto(String destino, String origen) {
        return destino != null && destino.equals(origen);
    }

    private String nombreDestino(ExpresionNodo destino) {
        if (destino instanceof IdentificadorNodo) {
            return ((IdentificadorNodo) destino).getNombre();
        }
        if (destino instanceof AccesoArregloNodo) {
            return ((AccesoArregloNodo) destino).getNombre();
        }
        if (destino instanceof AccesoMiembroNodo) {
            return ((AccesoMiembroNodo) destino).getNombreCampo();
        }
        return "<desconocido>";
    }

    private TipoDato evaluarTipoDestino(ExpresionNodo destino) {
        if (destino instanceof IdentificadorNodo) {
            IdentificadorNodo id = (IdentificadorNodo) destino;
            Simbolo simbolo = tablaSimbolos.buscar(id.getNombre(), id.getLinea());
            if (simbolo.getTipo() == TipoDato.ERROR) {
                return TipoDato.ERROR;
            }
            if (simbolo.getCategoria() == CategoriaSimb.ARREGLO) {
                tablaSimbolos.reportarAsignacionArregloCompleto(id.getNombre(), id.getLinea());
                return TipoDato.ERROR;
            }
            return simbolo.getTipo();
        }
        if (destino instanceof AccesoArregloNodo) {
            return evaluarTipoAccesoArreglo((AccesoArregloNodo) destino, false);
        }
        if (destino instanceof AccesoMiembroNodo) {
            return evaluarTipoAccesoMiembro((AccesoMiembroNodo) destino);
        }
        return TipoDato.ERROR;
    }

    private TipoDato evaluarTipoAccesoArreglo(AccesoArregloNodo acceso, boolean requiereInicializado) {
        Simbolo simbolo = tablaSimbolos.buscar(acceso.getNombre(), acceso.getLinea());
        if (simbolo.getTipo() == TipoDato.ERROR) {
            return TipoDato.ERROR;
        }
        if (simbolo.getCategoria() != CategoriaSimb.ARREGLO) {
            tablaSimbolos.reportarUsoEscalarComoArreglo(acceso.getNombre(), acceso.getLinea());
            return TipoDato.ERROR;
        }
        validarIndiceArreglo(acceso.getFila());
        validarIndiceArreglo(acceso.getColumnaIndice());
        validarRangoIndice(acceso.getNombre(), "de fila", acceso.getFila(),
                simbolo.getFilasArreglo());
        validarRangoIndice(acceso.getNombre(), "de columna", acceso.getColumnaIndice(),
                simbolo.getColumnasArreglo());
        if (requiereInicializado && !simbolo.isInicializado()) {
            tablaSimbolos.reportarVariableNoInicializada(acceso.getNombre(), acceso.getLinea());
        }
        return simbolo.getTipo();
    }

    private void validarIndiceArreglo(ExpresionNodo indice) {
        TipoDato tipo = evaluarTipo(indice);
        if (tipo != TipoDato.ERROR && tipo != TipoDato.DESCONOCIDO && tipo != TipoDato.INT) {
            tablaSimbolos.reportarIndiceNoEntero(tipo, indice.getLinea());
        }
    }

    private void validarDimensionArreglo(String nombre, String dimension, ExpresionNodo expresion) {
        TipoDato tipo = evaluarTipo(expresion);
        if (tipo != TipoDato.ERROR && tipo != TipoDato.DESCONOCIDO && tipo != TipoDato.INT) {
            tablaSimbolos.reportarDimensionArregloInvalida(nombre, dimension, expresion.getLinea());
            return;
        }
        Integer valor = valorEnteroLiteral(expresion);
        if (valor != null && valor <= 0) {
            tablaSimbolos.reportarDimensionArregloNoPositiva(nombre, dimension, valor,
                    expresion.getLinea());
        }
    }

    private void validarRangoIndice(String nombre, String dimension, ExpresionNodo indice,
                                    Integer limite) {
        Integer valor = valorEnteroLiteral(indice);
        if (valor != null && limite != null && limite > 0 && (valor < 0 || valor >= limite)) {
            tablaSimbolos.reportarIndiceFueraDeRango(nombre, dimension, valor, limite,
                    indice.getLinea());
        }
    }

    private Integer valorEnteroLiteral(ExpresionNodo expresion) {
        if (expresion == null || expresion.getTipo() != TipoDato.INT) {
            return null;
        }
        if (expresion instanceof LiteralNodo) {
            Object valor = ((LiteralNodo) expresion).getValor();
            return valor instanceof Number ? ((Number) valor).intValue() : null;
        }
        if (expresion instanceof ExpresionUnariaNodo) {
            ExpresionUnariaNodo unaria = (ExpresionUnariaNodo) expresion;
            Integer valor = valorEnteroLiteral(unaria.getExpresion());
            if (valor != null && "-".equals(unaria.getOperador())) {
                return -valor;
            }
            if (valor != null && "+".equals(unaria.getOperador())) {
                return valor;
            }
        }
        return null;
    }

    private void validarDimensionesInicializacion(String nombre, Integer filasEsperadas,
                                                  Integer columnasEsperadas,
                                                  InicializacionArregloNodo inicializacion) {
        List<List<ExpresionNodo>> filas = inicializacion.getFilas();
        int cantidadFilas = filas.size();
        int cantidadColumnas = filas.isEmpty() ? 0 : filas.get(0).size();
        boolean irregular = false;
        for (List<ExpresionNodo> fila : filas) {
            if (fila.size() != cantidadColumnas) {
                irregular = true;
                break;
            }
        }
        if (irregular) {
            tablaSimbolos.reportarInicializacionArregloIrregular(nombre,
                    inicializacion.getLinea());
            return;
        }
        if (filasEsperadas != null && columnasEsperadas != null
                && (cantidadFilas != filasEsperadas || cantidadColumnas != columnasEsperadas)) {
            tablaSimbolos.reportarInicializacionDimensionIncompatible(nombre, filasEsperadas,
                    columnasEsperadas, cantidadFilas, cantidadColumnas,
                    inicializacion.getLinea());
        }
    }

    private void validarInicializacionArreglo(String nombre, TipoDato tipoEsperado,
                                              InicializacionArregloNodo inicializacion) {
        for (List<ExpresionNodo> fila : inicializacion.getFilas()) {
            for (ExpresionNodo valor : fila) {
                TipoDato tipoValor = evaluarTipo(valor);
                if (tipoValor != TipoDato.ERROR && tipoValor != TipoDato.DESCONOCIDO
                        && tipoValor != tipoEsperado) {
                    tablaSimbolos.reportarInicializacionArregloIncompatible(nombre, tipoEsperado,
                            tipoValor, valor.getLinea());
                }
            }
        }
    }

    private void marcarDestinoInicializado(ExpresionNodo destino) {
        if (destino instanceof IdentificadorNodo) {
            tablaSimbolos.marcarInicializado(((IdentificadorNodo) destino).getNombre());
        } else if (destino instanceof AccesoArregloNodo) {
            tablaSimbolos.marcarInicializado(((AccesoArregloNodo) destino).getNombre());
        }
    }

    private boolean esModificable(ExpresionNodo expresion) {
        return expresion instanceof IdentificadorNodo || expresion instanceof AccesoArregloNodo;
    }
}
