package semantico;

import ast.AccesoArregloNodo;
import ast.AsignacionNodo;
import ast.BloqueNodo;
import ast.BreakNodo;
import ast.CasoSwitchNodo;
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
import ast.ParametroNodo;
import ast.ProgramaNodo;
import ast.ReturnNodo;
import ast.SalidaNodo;
import ast.SwitchNodo;
import ast.TipoDato;
import ast.WhileNodo;
import ast.IfNodo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * <strong>Nombre:</strong> AnalizadorSemantico
 *
 * <p><strong>Objetivo:</strong> Ejecutar las validaciones semánticas mientras el parser construye el
 * AST: manejo de alcances, declaración y uso de símbolos, verificación de tipos en expresiones,
 * asignaciones, condiciones, return, switch, arreglos y llamadas a función.</p>
 *
 * <p><strong>Entrada:</strong> Nodos del AST, nombres, tipos y posiciones que el parser le entrega.</p>
 *
 * <p><strong>Salida:</strong> El tipo de las expresiones y errores semánticos acumulados en la tabla de símbolos.</p>
 *
 * <p><strong>Restricciones:</strong> No genera código intermedio ni escribe archivos de reporte.</p>
 */
public class AnalizadorSemantico {
    private final TablaDeSimbolos tablaSimbolos;
    private final Consumer<String> reportadorSintactico;
    private int cantidadMain;
    private TipoDato tipoRetornoActual = TipoDato.DESCONOCIDO;
    private String funcionActual;
    private int lineaFuncionActual;

    /**
     * <strong>Nombre:</strong> AnalizadorSemantico
     *
     * <p><strong>Objetivo:</strong> Crear el analizador enlazándolo a la tabla de símbolos y al reportador de errores sintácticos.</p>
     *
     * <p><strong>Entrada:</strong> TablaDeSimbolos tablaSimbolos, Consumer&lt;String&gt; reportadorSintactico.</p>
     *
     * <p><strong>Salida:</strong> Nueva instancia de AnalizadorSemantico.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public AnalizadorSemantico(TablaDeSimbolos tablaSimbolos, Consumer<String> reportadorSintactico) {
        this.tablaSimbolos = tablaSimbolos;
        this.reportadorSintactico = reportadorSintactico;
    }

    /**
     * <strong>Nombre:</strong> registrarMain
     *
     * <p><strong>Objetivo:</strong> Contar una declaración del procedimiento principal main.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void registrarMain() {
        cantidadMain++;
    }

    /**
     * <strong>Nombre:</strong> verificarMain
     *
     * <p><strong>Objetivo:</strong> Verificar que el programa contenga el procedimiento main.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Reporta error si no hay ningún main.</p>
     */
    public void verificarMain() {
        if (cantidadMain == 0) {
            tablaSimbolos.reportarMainObligatorio();
        }
    }

    /**
     * <strong>Nombre:</strong> abrirPrograma
     *
     * <p><strong>Objetivo:</strong> Abrir el alcance global del programa.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void abrirPrograma() {
        tablaSimbolos.abrirAlcance();
    }

    /**
     * <strong>Nombre:</strong> cerrarPrograma
     *
     * <p><strong>Objetivo:</strong> Cerrar el alcance global del programa.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void cerrarPrograma() {
        tablaSimbolos.cerrarAlcance();
    }

    /**
     * <strong>Nombre:</strong> abrirFuncion
     *
     * <p><strong>Objetivo:</strong> Registrar una función antes de analizar su cuerpo (para permitir
     * recursión), guardar su tipo de retorno esperado y abrir su alcance.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, TipoDato tipoRetorno, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void abrirFuncion(String nombre, TipoDato tipoRetorno, int linea) {
        registrarFuncion(nombre, tipoRetorno, linea);
        tipoRetornoActual = tipoRetorno;
        funcionActual = nombre;
        lineaFuncionActual = linea;
        tablaSimbolos.abrirAlcance();
    }

    /**
     * <strong>Nombre:</strong> cerrarFuncion
     *
     * <p><strong>Objetivo:</strong> Cerrar el alcance de la función actual y limpiar su estado de contexto.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void cerrarFuncion() {
        tablaSimbolos.cerrarAlcance();
        tipoRetornoActual = TipoDato.DESCONOCIDO;
        funcionActual = null;
        lineaFuncionActual = 0;
    }

    /**
     * <strong>Nombre:</strong> abrirBloque
     *
     * <p><strong>Objetivo:</strong> Abrir un alcance para un bloque interno (if, ciclo, etc.).</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void abrirBloque() {
        tablaSimbolos.abrirAlcance();
    }

    /**
     * <strong>Nombre:</strong> cerrarBloque
     *
     * <p><strong>Objetivo:</strong> Cerrar el alcance de un bloque interno.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> registrarParametro
     *
     * <p><strong>Objetivo:</strong> Registrar un parámetro como símbolo inicializado en el alcance de la función y sumarlo a su firma.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, TipoDato tipo, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void registrarParametro(String nombre, TipoDato tipo, int linea) {
        insertarSimbolo(new Simbolo(nombre, tipo, CategoriaSimb.PARAMETRO, linea, true));
        if (funcionActual != null) {
            tablaSimbolos.agregarParametroAFuncion(funcionActual, tipo, lineaFuncionActual);
        }
    }

    /**
     * <strong>Nombre:</strong> registrarVariable
     *
     * <p><strong>Objetivo:</strong> Registrar una variable escalar como símbolo sin inicializar.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, TipoDato tipo, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> usarIdentificador
     *
     * <p><strong>Objetivo:</strong> Validar el uso de un identificador escalar: que exista, que no sea un
     * arreglo y que esté inicializado.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> verificarAsignacion
     *
     * <p><strong>Objetivo:</strong> Validar que el tipo del valor sea compatible con el del destino y marcar el destino como inicializado.</p>
     *
     * <p><strong>Entrada:</strong> AsignacionNodo asignacion.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> verificarSwitch
     *
     * <p><strong>Objetivo:</strong> Validar que la expresión del switch tenga un tipo permitido y que cada case coincida con ese tipo.</p>
     *
     * <p><strong>Entrada:</strong> SwitchNodo switchNodo.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> verificarEntrada
     *
     * <p><strong>Objetivo:</strong> Validar que {@code cin} se aplique a una variable escalar int o float, y marcarla como inicializada.</p>
     *
     * <p><strong>Entrada:</strong> EntradaNodo entrada.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> verificarSalida
     *
     * <p><strong>Objetivo:</strong> Validar que {@code cout} imprima una expresión de un tipo imprimible.</p>
     *
     * <p><strong>Entrada:</strong> SalidaNodo salida.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> evaluarTipo
     *
     * <p><strong>Objetivo:</strong> Calcular el tipo semántico de cualquier expresión, guardándolo en el nodo
     * para reutilizarlo. Ante un error devuelve {@link TipoDato#ERROR} para evitar cascadas.</p>
     *
     * <p><strong>Entrada:</strong> ExpresionNodo expresion.</p>
     *
     * <p><strong>Salida:</strong> TipoDato de la expresión.</p>
     *
     * <p><strong>Restricciones:</strong> Una expresión {@code null} se considera ERROR.</p>
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
        }

        expresion.setTipo(tipo);
        return tipo;
    }

    /**
     * <strong>Nombre:</strong> tipoExpresion
     *
     * <p><strong>Objetivo:</strong> Obtener el tipo de una expresión (alias de {@link #evaluarTipo}).</p>
     *
     * <p><strong>Entrada:</strong> ExpresionNodo expresion.</p>
     *
     * <p><strong>Salida:</strong> TipoDato de la expresión.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public TipoDato tipoExpresion(ExpresionNodo expresion) {
        return evaluarTipo(expresion);
    }

    /**
     * <strong>Nombre:</strong> verificarCondicionBooleana
     *
     * <p><strong>Objetivo:</strong> Validar que una condición (de if, while, etc.) sea de tipo bool.</p>
     *
     * <p><strong>Entrada:</strong> ExpresionNodo condicion.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> verificarReturn
     *
     * <p><strong>Objetivo:</strong> Validar que un return sea coherente con el tipo de la función: sin valor en
     * void, con valor del tipo correcto en las demás.</p>
     *
     * <p><strong>Entrada:</strong> ReturnNodo retorno.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> verificarContextoBreak
     *
     * <p><strong>Objetivo:</strong> Validar que cada break esté dentro de un ciclo o switch, recorriendo todas las funciones.</p>
     *
     * <p><strong>Entrada:</strong> ProgramaNodo programa.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void verificarContextoBreak(ProgramaNodo programa) {
        for (FuncionNodo funcion : programa.getFunciones()) {
            verificarBreakEnBloque(funcion.getCuerpo(), false);
        }
    }

    /**
     * <strong>Nombre:</strong> verificarRutasRetorno
     *
     * <p><strong>Objetivo:</strong> Exigir que cada función no void/empty retorne un valor en todas sus rutas de ejecución.</p>
     *
     * <p><strong>Entrada:</strong> ProgramaNodo programa.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> bloqueGarantizaRetorno
     *
     * <p><strong>Objetivo:</strong> Determinar si un bloque retorna un valor con certeza por todas sus rutas
     * (return con valor, if/else completos, do-while o switch con default que retornan).</p>
     *
     * <p><strong>Entrada:</strong> BloqueNodo bloque.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si el bloque garantiza retorno.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> switchGarantizaRetorno
     *
     * <p><strong>Objetivo:</strong> Determinar si un switch retorna en todos sus casos y además tiene un caso default.</p>
     *
     * <p><strong>Entrada:</strong> SwitchNodo seleccion.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si el switch garantiza retorno.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> verificarBreakEnBloque
     *
     * <p><strong>Objetivo:</strong> Recorrer un bloque comprobando que los break aparezcan solo donde están permitidos
     * (dentro de ciclos o switch).</p>
     *
     * <p><strong>Entrada:</strong> BloqueNodo bloque, boolean permitido.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> registrarFuncion
     *
     * <p><strong>Objetivo:</strong> Insertar el símbolo de una función con su tipo de retorno y aún sin parámetros.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, TipoDato tipoRetorno, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void registrarFuncion(String nombre, TipoDato tipoRetorno, int linea) {
        insertarSimbolo(new Simbolo(nombre, new ArrayList<TipoDato>(), tipoRetorno, linea));
    }

    /**
     * <strong>Nombre:</strong> insertarSimbolo
     *
     * <p><strong>Objetivo:</strong> Insertar un símbolo en la tabla, derivando al reportador sintáctico si la inserción falla.</p>
     *
     * <p><strong>Entrada:</strong> Simbolo simbolo.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void insertarSimbolo(Simbolo simbolo) {
        try {
            tablaSimbolos.insertar(simbolo);
        } catch (IllegalArgumentException ex) {
            reportadorSintactico.accept(ex.getMessage());
        }
    }

    /**
     * <strong>Nombre:</strong> evaluarTipoLlamada
     *
     * <p><strong>Objetivo:</strong> Validar una llamada a función (existencia, cantidad y tipos de argumentos) y devolver su tipo de retorno.</p>
     *
     * <p><strong>Entrada:</strong> LlamadaFuncionNodo llamada.</p>
     *
     * <p><strong>Salida:</strong> TipoDato de retorno de la función, o ERROR si hay incompatibilidades.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> evaluarTipoBinaria
     *
     * <p><strong>Objetivo:</strong> Calcular y validar el tipo resultante de una operación binaria según el operador y los tipos de sus operandos.</p>
     *
     * <p><strong>Entrada:</strong> ExpresionBinariaNodo expresion.</p>
     *
     * <p><strong>Salida:</strong> TipoDato del resultado, o ERROR si los operandos no son compatibles.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> evaluarTipoUnaria
     *
     * <p><strong>Objetivo:</strong> Calcular y validar el tipo resultante de una operación unaria (negación lógica, signo, ++/--).</p>
     *
     * <p><strong>Entrada:</strong> ExpresionUnariaNodo expresion.</p>
     *
     * <p><strong>Salida:</strong> TipoDato del resultado, o ERROR si el operando no es válido.</p>
     *
     * <p><strong>Restricciones:</strong> {@code -} solo aplica a literales; {@code ++}/{@code --} solo a operandos modificables.</p>
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
     * <strong>Nombre:</strong> nombreDestino
     *
     * <p><strong>Objetivo:</strong> Obtener el nombre del destino de una asignación (variable o arreglo) para los mensajes de error.</p>
     *
     * <p><strong>Entrada:</strong> ExpresionNodo destino.</p>
     *
     * <p><strong>Salida:</strong> String con el nombre, o {@code "<desconocido>"}.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private String nombreDestino(ExpresionNodo destino) {
        if (destino instanceof IdentificadorNodo) {
            return ((IdentificadorNodo) destino).getNombre();
        }
        if (destino instanceof AccesoArregloNodo) {
            return ((AccesoArregloNodo) destino).getNombre();
        }
        return "<desconocido>";
    }

    /**
     * <strong>Nombre:</strong> evaluarTipoDestino
     *
     * <p><strong>Objetivo:</strong> Determinar el tipo del destino de una asignación, reportando si se asigna a un arreglo completo.</p>
     *
     * <p><strong>Entrada:</strong> ExpresionNodo destino.</p>
     *
     * <p><strong>Salida:</strong> TipoDato del destino, o ERROR.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
        return TipoDato.ERROR;
    }

    /**
     * <strong>Nombre:</strong> evaluarTipoAccesoArreglo
     *
     * <p><strong>Objetivo:</strong> Validar un acceso {@code arreglo[fila][col]} (que sea arreglo, índices enteros y en rango) y devolver el tipo de sus celdas.</p>
     *
     * <p><strong>Entrada:</strong> AccesoArregloNodo acceso, boolean requiereInicializado.</p>
     *
     * <p><strong>Salida:</strong> TipoDato de las celdas, o ERROR.</p>
     *
     * <p><strong>Restricciones:</strong> Solo comprueba inicialización cuando {@code requiereInicializado} es true.</p>
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
     * <strong>Nombre:</strong> validarIndiceArreglo
     *
     * <p><strong>Objetivo:</strong> Verificar que un índice de arreglo sea de tipo int.</p>
     *
     * <p><strong>Entrada:</strong> ExpresionNodo indice.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void validarIndiceArreglo(ExpresionNodo indice) {
        TipoDato tipo = evaluarTipo(indice);
        if (tipo != TipoDato.ERROR && tipo != TipoDato.DESCONOCIDO && tipo != TipoDato.INT) {
            tablaSimbolos.reportarIndiceNoEntero(tipo, indice.getLinea());
        }
    }

    /**
     * <strong>Nombre:</strong> validarDimensionArreglo
     *
     * <p><strong>Objetivo:</strong> Verificar que una dimensión declarada de un arreglo sea int y, si es literal, mayor que cero.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, String dimension, ExpresionNodo expresion.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> valorEnteroLiteral
     *
     * <p><strong>Objetivo:</strong> Obtener el valor entero de una expresión cuando es un literal int (con signo +/-), o {@code null} en otro caso.</p>
     *
     * <p><strong>Entrada:</strong> ExpresionNodo expresion.</p>
     *
     * <p><strong>Salida:</strong> Integer con el valor, o {@code null}.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> marcarDestinoInicializado
     *
     * <p><strong>Objetivo:</strong> Marcar como inicializado el símbolo destino de una asignación (variable o arreglo).</p>
     *
     * <p><strong>Entrada:</strong> ExpresionNodo destino.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private void marcarDestinoInicializado(ExpresionNodo destino) {
        if (destino instanceof IdentificadorNodo) {
            tablaSimbolos.marcarInicializado(((IdentificadorNodo) destino).getNombre());
        } else if (destino instanceof AccesoArregloNodo) {
            tablaSimbolos.marcarInicializado(((AccesoArregloNodo) destino).getNombre());
        }
    }

    /**
     * <strong>Nombre:</strong> esModificable
     *
     * <p><strong>Objetivo:</strong> Indicar si una expresión puede ser destino de una modificación (variable o celda de arreglo).</p>
     *
     * <p><strong>Entrada:</strong> ExpresionNodo expresion.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si es modificable.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private boolean esModificable(ExpresionNodo expresion) {
        return expresion instanceof IdentificadorNodo || expresion instanceof AccesoArregloNodo;
    }
}
