package semantico;

import ast.AccesoArregloNodo;
import ast.AccesoMiembroNodo;
import ast.AsignacionNodo;
import ast.BloqueNodo;
import ast.BreakNodo;
import ast.CasoSwitchNodo;
import ast.DeclaracionVariableNodo;
import ast.EntradaNodo;
import ast.ExpresionBinariaNodo;
import ast.ExpresionNodo;
import ast.ExpresionUnariaNodo;
import ast.FuncionNodo;
import ast.IdentificadorNodo;
import ast.InicializacionArregloNodo;
import ast.LlamadaFuncionNodo;
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

/**
 * Nombre: AnalizadorSemantico
 *
 * Objetivo: Validar reglas semanticas y administrar informacion de simbolos.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class AnalizadorSemantico {
    private final TablaDeSimbolos tablaSimbolos;
    private final Consumer<String> reportadorSintactico;
    /** Registro de clases declaradas: nombre -> informacion estructural. */
    private final Map<String, ClaseInfo> clases = new LinkedHashMap<>();
    private int cantidadMain;
    private TipoDato tipoRetornoActual = TipoDato.DESCONOCIDO;
    private String funcionActual;
    private int lineaFuncionActual;

    /**
     * Nombre: AnalizadorSemantico
     *
     * Objetivo: Inicializar una instancia de AnalizadorSemantico con los datos requeridos.
     *
     * Entrada: TablaDeSimbolos tablaSimbolos; Consumer<String> reportadorSintactico.
     *
     * Salida: Nueva instancia de AnalizadorSemantico.
     *
     * Restricciones: Ninguna.
     */
    public AnalizadorSemantico(TablaDeSimbolos tablaSimbolos, Consumer<String> reportadorSintactico) {
        this.tablaSimbolos = tablaSimbolos;
        this.reportadorSintactico = reportadorSintactico;
    }

    /**
     * Nombre: registrarMain
     *
     * Objetivo: Registrar informacion en las estructuras internas de la fase actual.
     *
     * Entrada: Ninguna.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void registrarMain() {
        cantidadMain++;
    }

    /**
     * Nombre: verificarMain
     *
     * Objetivo: Comprobar una regla del lenguaje o una condicion de consistencia interna.
     *
     * Entrada: Ninguna.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void verificarMain() {
        if (cantidadMain == 0) {
            tablaSimbolos.reportarMainObligatorio();
        }
    }

    /**
     * Nombre: abrirPrograma
     *
     * Objetivo: Abrir un contexto, alcance o fase de procesamiento.
     *
     * Entrada: Ninguna.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void abrirPrograma() {
        tablaSimbolos.abrirAlcance();
    }

    /**
     * Nombre: cerrarPrograma
     *
     * Objetivo: Cerrar el contexto, alcance o fase de procesamiento actual.
     *
     * Entrada: Ninguna.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void cerrarPrograma() {
        tablaSimbolos.cerrarAlcance();
    }

    /**
     * Nombre: abrirFuncion
     *
     * Objetivo: Abrir un contexto, alcance o fase de procesamiento.
     *
     * Entrada: String nombre; TipoDato tipoRetorno; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void abrirFuncion(String nombre, TipoDato tipoRetorno, int linea) {
        registrarFuncion(nombre, tipoRetorno, linea);
        tipoRetornoActual = tipoRetorno;
        funcionActual = nombre;
        lineaFuncionActual = linea;
        tablaSimbolos.abrirAlcance();
    }

    /**
     * Nombre: cerrarFuncion
     *
     * Objetivo: Cerrar el contexto, alcance o fase de procesamiento actual.
     *
     * Entrada: Ninguna.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void cerrarFuncion() {
        tablaSimbolos.cerrarAlcance();
        tipoRetornoActual = TipoDato.DESCONOCIDO;
        funcionActual = null;
        lineaFuncionActual = 0;
    }

    /**
     * Nombre: abrirBloque
     *
     * Objetivo: Abrir un contexto, alcance o fase de procesamiento.
     *
     * Entrada: Ninguna.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void abrirBloque() {
        tablaSimbolos.abrirAlcance();
    }

    /**
     * Nombre: cerrarBloque
     *
     * Objetivo: Cerrar el contexto, alcance o fase de procesamiento actual.
     *
     * Entrada: Ninguna.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void cerrarBloque() {
        tablaSimbolos.cerrarAlcance();
    }

    /**
     * <strong>Nombre:</strong> actualizarFirmaFuncion
     *
     * <p><strong>Objetivo:</strong> Completar la firma de una función con los tipos de sus parámetros y su retorno.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, TipoDato tipoRetorno, List&lt;ParametroNodo&gt; parametros, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void actualizarFirmaFuncion(String nombre, TipoDato tipoRetorno,
                                       List<ParametroNodo> parametros, int linea) {
        List<TipoDato> tiposParametros = new ArrayList<>();
        for (ParametroNodo parametro : parametros) {
            tiposParametros.add(parametro.getTipo());
        }
        tablaSimbolos.actualizarFirmaFuncion(nombre, tiposParametros, tipoRetorno, linea);
    }

    /**
     * Nombre: registrarParametro
     *
     * Objetivo: Registrar informacion en las estructuras internas de la fase actual.
     *
     * Entrada: String nombre; TipoDato tipo; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void registrarParametro(String nombre, TipoDato tipo, int linea) {
        insertarSimbolo(new Simbolo(nombre, tipo, CategoriaSimb.PARAMETRO, linea, true));
        if (funcionActual != null) {
            tablaSimbolos.agregarParametroAFuncion(funcionActual, tipo, lineaFuncionActual);
        }
    }

    /**
     * Nombre: registrarVariable
     *
     * Objetivo: Registrar informacion en las estructuras internas de la fase actual.
     *
     * Entrada: String nombre; TipoDato tipo; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void registrarVariable(String nombre, TipoDato tipo, int linea) {
        insertarSimbolo(new Simbolo(nombre, tipo, CategoriaSimb.VAR, linea, false));
    }

    /**
     * <strong>Nombre:</strong> registrarDeclaracionVariable
     *
     * <p><strong>Objetivo:</strong> Declarar una variable validando que el tipo sea declarable y que el
     * inicializador (si lo hay) sea compatible.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, TipoDato tipo, ExpresionNodo inicializador, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> No inserta el símbolo si detecta un error.</p>
     */
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

    /**
     * <strong>Nombre:</strong> registrarDeclaracionArreglo
     *
     * <p><strong>Objetivo:</strong> Declarar un arreglo validando su tipo, sus dimensiones y, si la hay, su inicialización.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, TipoDato tipo, ExpresionNodo filas, ExpresionNodo columnas, InicializacionArregloNodo inicializacion, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> No inserta el símbolo si el tipo no es declarable.</p>
     */
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

    /**
     * <strong>Nombre:</strong> registrarClase
     *
     * <p><strong>Objetivo:</strong> Registrar la estructura de una clase (campos) validando tipos y duplicados.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, String padre, List&lt;DeclaracionVariableNodo&gt; campos, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> 'padre' puede ser null. La herencia se valida en una fase posterior.</p>
     */
    public void registrarClase(String nombre, String padre, List<DeclaracionVariableNodo> campos,
                               int linea) {
        if (clases.containsKey(nombre)) {
            tablaSimbolos.reportarClaseRedeclarada(nombre, linea);
            return;
        }
        ClaseInfo info = new ClaseInfo(nombre, padre, linea);
        for (DeclaracionVariableNodo campo : campos) {
            TipoDato tipo = campo.getTipo();
            if (!tipo.esDeclarableVariable()) {
                tablaSimbolos.reportarTipoDeclaracionInvalido(tipo, campo.getLinea());
                continue;
            }
            if (!info.agregarCampo(campo.getNombre(), tipo)) {
                tablaSimbolos.reportarCampoNoDeclarado(nombre, campo.getNombre(), campo.getLinea());
            }
        }
        clases.put(nombre, info);
        insertarSimbolo(new Simbolo(nombre, TipoDato.OBJETO, CategoriaSimb.CLASE, linea, true));
    }

    /**
     * <strong>Nombre:</strong> registrarDeclaracionObjeto
     *
     * <p><strong>Objetivo:</strong> Declarar una variable de tipo objeto validando la clase y la instanciacion.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, String nombreClase, ExpresionNodo inicializador, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> No inserta el simbolo si la clase no existe.</p>
     */
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

    /**
     * Nombre: usarIdentificador
     *
     * Objetivo: Ejecutar la operacion usarIdentificador definida por AnalizadorSemantico.
     *
     * Entrada: String nombre; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
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

    /**
     * Nombre: verificarAsignacion
     *
     * Objetivo: Comprobar una regla del lenguaje o una condicion de consistencia interna.
     *
     * Entrada: AsignacionNodo asignacion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
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

    /**
     * Nombre: verificarSwitch
     *
     * Objetivo: Comprobar una regla del lenguaje o una condicion de consistencia interna.
     *
     * Entrada: SwitchNodo switchNodo.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
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

    /**
     * Nombre: verificarEntrada
     *
     * Objetivo: Comprobar una regla del lenguaje o una condicion de consistencia interna.
     *
     * Entrada: EntradaNodo entrada.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
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

    /**
     * Nombre: verificarSalida
     *
     * Objetivo: Comprobar una regla del lenguaje o una condicion de consistencia interna.
     *
     * Entrada: SalidaNodo salida.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
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

    /**
     * Nombre: evaluarTipo
     *
     * Objetivo: Calcular el tipo, valor o resultado auxiliar solicitado.
     *
     * Entrada: ExpresionNodo expresion.
     *
     * Salida: Valor de tipo TipoDato.
     *
     * Restricciones: Ninguna.
     */
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
        }

        expresion.setTipo(tipo);
        return tipo;
    }

    /**
     * Nombre: tipoExpresion
     *
     * Objetivo: Ejecutar la operacion tipoExpresion definida por AnalizadorSemantico.
     *
     * Entrada: ExpresionNodo expresion.
     *
     * Salida: Valor de tipo TipoDato.
     *
     * Restricciones: Ninguna.
     */
    public TipoDato tipoExpresion(ExpresionNodo expresion) {
        return evaluarTipo(expresion);
    }

    /**
     * Nombre: verificarCondicionBooleana
     *
     * Objetivo: Comprobar una regla del lenguaje o una condicion de consistencia interna.
     *
     * Entrada: ExpresionNodo condicion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void verificarCondicionBooleana(ExpresionNodo condicion) {
        TipoDato tipo = evaluarTipo(condicion);
        if (tipo == TipoDato.ERROR || tipo == TipoDato.DESCONOCIDO) {
            return;
        }
        if (tipo != TipoDato.BOOL) {
            tablaSimbolos.reportarCondicionNoBooleana(tipo, condicion.getLinea());
        }
    }

    /**
     * Nombre: verificarReturn
     *
     * Objetivo: Comprobar una regla del lenguaje o una condicion de consistencia interna.
     *
     * Entrada: ReturnNodo retorno.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
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

    /**
     * Nombre: verificarContextoBreak
     *
     * Objetivo: Comprobar una regla del lenguaje o una condicion de consistencia interna.
     *
     * Entrada: ProgramaNodo programa.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void verificarContextoBreak(ProgramaNodo programa) {
        for (FuncionNodo funcion : programa.getFunciones()) {
            verificarBreakEnBloque(funcion.getCuerpo(), false);
        }
    }

    /**
     * Nombre: verificarRutasRetorno
     *
     * Objetivo: Comprobar una regla del lenguaje o una condicion de consistencia interna.
     *
     * Entrada: ProgramaNodo programa.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void verificarRutasRetorno(ProgramaNodo programa) {
        for (FuncionNodo funcion : programa.getFunciones()) {
            TipoDato tipo = funcion.getTipoRetorno();
            boolean requiereRetorno = tipo != TipoDato.VOID && tipo != TipoDato.EMPTY
                    && tipo != TipoDato.ERROR && tipo != TipoDato.DESCONOCIDO;
            if (requiereRetorno && !bloqueGarantizaRetorno(funcion.getCuerpo())) {
                tablaSimbolos.reportarReturnFaltante(tipo, funcion.getLinea());
            }
        }
    }

    /**
     * Nombre: bloqueGarantizaRetorno
     *
     * Objetivo: Ejecutar la operacion bloqueGarantizaRetorno definida por AnalizadorSemantico.
     *
     * Entrada: BloqueNodo bloque.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Uso interno de la clase.
     */
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

    /**
     * Nombre: switchGarantizaRetorno
     *
     * Objetivo: Ejecutar la operacion switchGarantizaRetorno definida por AnalizadorSemantico.
     *
     * Entrada: SwitchNodo seleccion.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Uso interno de la clase.
     */
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

    /**
     * Nombre: verificarBreakEnBloque
     *
     * Objetivo: Comprobar una regla del lenguaje o una condicion de consistencia interna.
     *
     * Entrada: BloqueNodo bloque; boolean permitido.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
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

    /**
     * Nombre: registrarFuncion
     *
     * Objetivo: Registrar informacion en las estructuras internas de la fase actual.
     *
     * Entrada: String nombre; TipoDato tipoRetorno; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void registrarFuncion(String nombre, TipoDato tipoRetorno, int linea) {
        insertarSimbolo(new Simbolo(nombre, new ArrayList<TipoDato>(), tipoRetorno, linea));
    }

    /**
     * Nombre: insertarSimbolo
     *
     * Objetivo: Insertar informacion en la estructura interna correspondiente.
     *
     * Entrada: Simbolo simbolo.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void insertarSimbolo(Simbolo simbolo) {
        try {
            tablaSimbolos.insertar(simbolo);
        } catch (IllegalArgumentException ex) {
            reportadorSintactico.accept(ex.getMessage());
        }
    }

    /**
     * Nombre: evaluarTipoLlamada
     *
     * Objetivo: Calcular el tipo, valor o resultado auxiliar solicitado.
     *
     * Entrada: LlamadaFuncionNodo llamada.
     *
     * Salida: Valor de tipo TipoDato.
     *
     * Restricciones: Uso interno de la clase.
     */
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

    /**
     * Nombre: evaluarTipoBinaria
     *
     * Objetivo: Calcular el tipo, valor o resultado auxiliar solicitado.
     *
     * Entrada: ExpresionBinariaNodo expresion.
     *
     * Salida: Valor de tipo TipoDato.
     *
     * Restricciones: Uso interno de la clase.
     */
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

    /**
     * Nombre: evaluarTipoUnaria
     *
     * Objetivo: Calcular el tipo, valor o resultado auxiliar solicitado.
     *
     * Entrada: ExpresionUnariaNodo expresion.
     *
     * Salida: Valor de tipo TipoDato.
     *
     * Restricciones: Uso interno de la clase.
     */
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

    /**
     * Nombre: evaluarTipoNuevo
     *
     * Objetivo: Calcular el tipo de una instanciacion 'new Clase<|...|>' y validar la clase.
     *
     * Entrada: NuevoObjetoNodo nuevo.
     *
     * Salida: Valor de tipo TipoDato.
     *
     * Restricciones: Uso interno de la clase.
     */
    private TipoDato evaluarTipoNuevo(NuevoObjetoNodo nuevo) {
        if (!clases.containsKey(nuevo.getNombreClase())) {
            tablaSimbolos.reportarClaseNoDeclarada(nuevo.getNombreClase(), nuevo.getLinea());
            return TipoDato.ERROR;
        }
        return TipoDato.OBJETO;
    }

    /**
     * Nombre: evaluarTipoAccesoMiembro
     *
     * Objetivo: Calcular el tipo de un acceso a campo 'objeto.campo' validando objeto y campo.
     *
     * Entrada: AccesoMiembroNodo acceso.
     *
     * Salida: Valor de tipo TipoDato.
     *
     * Restricciones: Uso interno de la clase.
     */
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

    /**
     * Nombre: claseDe
     *
     * Objetivo: Determinar el nombre de la clase a la que pertenece la expresion objeto.
     *
     * Entrada: ExpresionNodo expresion.
     *
     * Salida: Valor de tipo String (null si la expresion no es un objeto conocido).
     *
     * Restricciones: Uso interno de la clase.
     */
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

    /**
     * Nombre: tipoCampoEnJerarquia
     *
     * Objetivo: Obtener el tipo de un campo buscando en la clase (la herencia se incorpora en otra fase).
     *
     * Entrada: String nombreClase; String campo.
     *
     * Salida: Valor de tipo TipoDato (null si no existe el campo).
     *
     * Restricciones: Uso interno de la clase.
     */
    private TipoDato tipoCampoEnJerarquia(String nombreClase, String campo) {
        ClaseInfo info = clases.get(nombreClase);
        return info == null ? null : info.tipoCampo(campo);
    }

    /**
     * Nombre: esCompatibleObjeto
     *
     * Objetivo: Indicar si un objeto de la clase 'origen' es asignable a una variable de la clase 'destino'.
     *
     * Entrada: String destino; String origen.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Uso interno de la clase. La compatibilidad por herencia se amplia en otra fase.
     */
    private boolean esCompatibleObjeto(String destino, String origen) {
        return destino != null && destino.equals(origen);
    }

    /**
     * Nombre: nombreDestino
     *
     * Objetivo: Ejecutar la operacion nombreDestino definida por AnalizadorSemantico.
     *
     * Entrada: ExpresionNodo destino.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
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

    /**
     * Nombre: evaluarTipoDestino
     *
     * Objetivo: Calcular el tipo, valor o resultado auxiliar solicitado.
     *
     * Entrada: ExpresionNodo destino.
     *
     * Salida: Valor de tipo TipoDato.
     *
     * Restricciones: Uso interno de la clase.
     */
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

    /**
     * Nombre: evaluarTipoAccesoArreglo
     *
     * Objetivo: Calcular el tipo, valor o resultado auxiliar solicitado.
     *
     * Entrada: AccesoArregloNodo acceso; boolean requiereInicializado.
     *
     * Salida: Valor de tipo TipoDato.
     *
     * Restricciones: Uso interno de la clase.
     */
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

    /**
     * Nombre: validarIndiceArreglo
     *
     * Objetivo: Ejecutar la operacion validarIndiceArreglo definida por AnalizadorSemantico.
     *
     * Entrada: ExpresionNodo indice.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void validarIndiceArreglo(ExpresionNodo indice) {
        TipoDato tipo = evaluarTipo(indice);
        if (tipo != TipoDato.ERROR && tipo != TipoDato.DESCONOCIDO && tipo != TipoDato.INT) {
            tablaSimbolos.reportarIndiceNoEntero(tipo, indice.getLinea());
        }
    }

    /**
     * Nombre: validarDimensionArreglo
     *
     * Objetivo: Ejecutar la operacion validarDimensionArreglo definida por AnalizadorSemantico.
     *
     * Entrada: String nombre; String dimension; ExpresionNodo expresion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
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

    /**
     * <strong>Nombre:</strong> validarRangoIndice
     *
     * <p><strong>Objetivo:</strong> Si el índice es un literal y se conoce el límite, verificar que esté dentro del rango del arreglo.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, String dimension, ExpresionNodo indice, Integer limite.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Solo aplica a índices literales con límite conocido.</p>
     */
    private void validarRangoIndice(String nombre, String dimension, ExpresionNodo indice,
                                    Integer limite) {
        Integer valor = valorEnteroLiteral(indice);
        if (valor != null && limite != null && limite > 0 && (valor < 0 || valor >= limite)) {
            tablaSimbolos.reportarIndiceFueraDeRango(nombre, dimension, valor, limite,
                    indice.getLinea());
        }
    }

    /**
     * Nombre: valorEnteroLiteral
     *
     * Objetivo: Extraer o convertir el valor representado por la entrada.
     *
     * Entrada: ExpresionNodo expresion.
     *
     * Salida: Valor de tipo Integer.
     *
     * Restricciones: Uso interno de la clase.
     */
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

    /**
     * <strong>Nombre:</strong> validarDimensionesInicializacion
     *
     * <p><strong>Objetivo:</strong> Verificar que la inicialización de un arreglo sea regular (mismas columnas por fila) y coincida con las dimensiones declaradas.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, Integer filasEsperadas, Integer columnasEsperadas, InicializacionArregloNodo inicializacion.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
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

    /**
     * <strong>Nombre:</strong> validarInicializacionArreglo
     *
     * <p><strong>Objetivo:</strong> Verificar que cada valor de inicialización de un arreglo sea del tipo esperado.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, TipoDato tipoEsperado, InicializacionArregloNodo inicializacion.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
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

    /**
     * Nombre: marcarDestinoInicializado
     *
     * Objetivo: Actualizar marcas de estado asociadas a simbolos o destinos.
     *
     * Entrada: ExpresionNodo destino.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void marcarDestinoInicializado(ExpresionNodo destino) {
        if (destino instanceof IdentificadorNodo) {
            tablaSimbolos.marcarInicializado(((IdentificadorNodo) destino).getNombre());
        } else if (destino instanceof AccesoArregloNodo) {
            tablaSimbolos.marcarInicializado(((AccesoArregloNodo) destino).getNombre());
        }
    }

    /**
     * Nombre: esModificable
     *
     * Objetivo: Indicar si se cumple la condicion Modificable.
     *
     * Entrada: ExpresionNodo expresion.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Uso interno de la clase.
     */
    private boolean esModificable(ExpresionNodo expresion) {
        return expresion instanceof IdentificadorNodo || expresion instanceof AccesoArregloNodo;
    }
}
