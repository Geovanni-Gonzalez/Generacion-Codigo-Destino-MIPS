package semantico;

import ast.AccesoArregloNodo;
import ast.AsignacionNodo;
import ast.CasoSwitchNodo;
import ast.EntradaNodo;
import ast.ExpresionBinariaNodo;
import ast.ExpresionNodo;
import ast.ExpresionUnariaNodo;
import ast.IdentificadorNodo;
import ast.InicializacionArregloNodo;
import ast.LlamadaFuncionNodo;
import ast.LiteralNodo;
import ast.ParametroNodo;
import ast.ReturnNodo;
import ast.SalidaNodo;
import ast.SwitchNodo;
import ast.TipoDato;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * <strong>Objetivo:</strong> Ejecuta las validaciones semanticas mientras el parser construye el AST.
 *
 * <p><strong>Entradas:</strong> Simbolos, tipos, nodos y ubicaciones producidos por las fases previas.</p>
 *
 * <p><strong>Salidas:</strong> Estado semantico actualizado, simbolos resueltos o diagnosticos acumulados.</p>
 *
 * <p><strong>Restricciones:</strong> No debe generar codigo intermedio ni escribir reportes directamente.</p>
 */
public class AnalizadorSemantico {
    private final TablaDeSimbolos tablaSimbolos;
    private final Consumer<String> reportadorSintactico;
    private int cantidadMain;
    private TipoDato tipoRetornoActual = TipoDato.DESCONOCIDO;
    private String funcionActual;
    private int lineaFuncionActual;
    private boolean retornoEncontradoActual;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> TablaDeSimbolos tablaSimbolos, Consumer<String> reportadorSintactico</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de AnalizadorSemantico.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public AnalizadorSemantico(TablaDeSimbolos tablaSimbolos, Consumer<String> reportadorSintactico) {
        this.tablaSimbolos = tablaSimbolos;
        this.reportadorSintactico = reportadorSintactico;
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void registrarMain() {
        cantidadMain++;
    }

    /**
     * <strong>Objetivo:</strong> Valida una regla o escenario especifico del compilador.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void verificarMain() {
        if (cantidadMain == 0) {
            tablaSimbolos.reportarMainObligatorio();
        }
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void abrirPrograma() {
        tablaSimbolos.abrirAlcance();
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void cerrarPrograma() {
        tablaSimbolos.cerrarAlcance();
    }

    /**
     * <strong>Objetivo:</strong> Registra una funcion antes de analizar su cuerpo. Registrar la funcion temprano permite llamadas recursivas. Tambien guarda el tipo de retorno esperado para validar sentencias return.
     *
     * <p><strong>Entradas:</strong> String nombre, TipoDato tipoRetorno, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void abrirFuncion(String nombre, TipoDato tipoRetorno, int linea) {
        registrarFuncion(nombre, tipoRetorno, linea);
        tipoRetornoActual = tipoRetorno;
        funcionActual = nombre;
        lineaFuncionActual = linea;
        retornoEncontradoActual = false;
        tablaSimbolos.abrirAlcance();
    }

    /**
     * <strong>Objetivo:</strong> Cierra la funcion actual y valida que las funciones con retorno tengan al menos una sentencia return.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void cerrarFuncion() {
        boolean requiereReturn = tipoRetornoActual != TipoDato.EMPTY
                && tipoRetornoActual != TipoDato.VOID
                && tipoRetornoActual != TipoDato.ERROR
                && tipoRetornoActual != TipoDato.DESCONOCIDO;
        if (requiereReturn && !retornoEncontradoActual) {
            tablaSimbolos.reportarReturnFaltante(tipoRetornoActual, lineaFuncionActual);
        }
        tablaSimbolos.cerrarAlcance();
        tipoRetornoActual = TipoDato.DESCONOCIDO;
        funcionActual = null;
        lineaFuncionActual = 0;
        retornoEncontradoActual = false;
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void abrirBloque() {
        tablaSimbolos.abrirAlcance();
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void cerrarBloque() {
        tablaSimbolos.cerrarAlcance();
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, TipoDato tipoRetorno, List parametros, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void actualizarFirmaFuncion(String nombre, TipoDato tipoRetorno, List parametros, int linea) {
        List<TipoDato> tiposParametros = new ArrayList<>();
        for (Object parametro : parametros) {
            tiposParametros.add(((ParametroNodo) parametro).getTipo());
        }
        tablaSimbolos.actualizarFirmaFuncion(nombre, tiposParametros, tipoRetorno, linea);
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, TipoDato tipo, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void registrarParametro(String nombre, TipoDato tipo, int linea) {
        insertarSimbolo(new Simbolo(nombre, tipo, CategoriaSimb.PARAMETRO, linea, true));
        if (funcionActual != null) {
            tablaSimbolos.agregarParametroAFuncion(funcionActual, tipo, lineaFuncionActual);
        }
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, TipoDato tipo, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void registrarVariable(String nombre, TipoDato tipo, int linea) {
        insertarSimbolo(new Simbolo(nombre, tipo, CategoriaSimb.VAR, linea, false));
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, TipoDato tipo, ExpresionNodo inicializador, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, TipoDato tipo, ExpresionNodo filas, ExpresionNodo columnas, InicializacionArregloNodo inicializacion, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Valida una regla o escenario especifico del compilador.
     *
     * <p><strong>Entradas:</strong> AsignacionNodo asignacion</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Valida una regla o escenario especifico del compilador.
     *
     * <p><strong>Entradas:</strong> SwitchNodo switchNodo</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Valida una regla o escenario especifico del compilador.
     *
     * <p><strong>Entradas:</strong> EntradaNodo entrada</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Valida una regla o escenario especifico del compilador.
     *
     * <p><strong>Entradas:</strong> SalidaNodo salida</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Calcula el tipo semantico de cualquier expresion. El resultado se guarda en el nodo para reutilizarlo. Si se detecta un error, se retorna {@link TipoDato#ERROR} para evitar cascadas innecesarias.
     *
     * <p><strong>Entradas:</strong> ExpresionNodo expresion</p>
     *
     * <p><strong>Salidas:</strong> Retorna TipoDato.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> ExpresionNodo expresion</p>
     *
     * <p><strong>Salidas:</strong> Retorna TipoDato.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public TipoDato tipoExpresion(ExpresionNodo expresion) {
        return evaluarTipo(expresion);
    }

    /**
     * <strong>Objetivo:</strong> Valida una regla o escenario especifico del compilador.
     *
     * <p><strong>Entradas:</strong> ExpresionNodo condicion</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Valida una regla o escenario especifico del compilador.
     *
     * <p><strong>Entradas:</strong> ReturnNodo retorno</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void verificarReturn(ReturnNodo retorno) {
        retornoEncontradoActual = true;
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
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, TipoDato tipoRetorno, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void registrarFuncion(String nombre, TipoDato tipoRetorno, int linea) {
        insertarSimbolo(new Simbolo(nombre, new ArrayList<TipoDato>(), tipoRetorno, linea));
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Simbolo simbolo</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void insertarSimbolo(Simbolo simbolo) {
        try {
            tablaSimbolos.insertar(simbolo);
        } catch (IllegalArgumentException ex) {
            reportadorSintactico.accept(ex.getMessage());
        }
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> LlamadaFuncionNodo llamada</p>
     *
     * <p><strong>Salidas:</strong> Retorna TipoDato.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> ExpresionBinariaNodo expresion</p>
     *
     * <p><strong>Salidas:</strong> Retorna TipoDato.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
            if (izquierda == derecha) {
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
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> ExpresionUnariaNodo expresion</p>
     *
     * <p><strong>Salidas:</strong> Retorna TipoDato.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> ExpresionNodo destino</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> ExpresionNodo destino</p>
     *
     * <p><strong>Salidas:</strong> Retorna TipoDato.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> AccesoArregloNodo acceso, boolean requiereInicializado</p>
     *
     * <p><strong>Salidas:</strong> Retorna TipoDato.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> ExpresionNodo indice</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void validarIndiceArreglo(ExpresionNodo indice) {
        TipoDato tipo = evaluarTipo(indice);
        if (tipo != TipoDato.ERROR && tipo != TipoDato.DESCONOCIDO && tipo != TipoDato.INT) {
            tablaSimbolos.reportarIndiceNoEntero(tipo, indice.getLinea());
        }
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, String dimension, ExpresionNodo expresion</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, TipoDato tipoEsperado, InicializacionArregloNodo inicializacion</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> ExpresionNodo destino</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void marcarDestinoInicializado(ExpresionNodo destino) {
        if (destino instanceof IdentificadorNodo) {
            tablaSimbolos.marcarInicializado(((IdentificadorNodo) destino).getNombre());
        } else if (destino instanceof AccesoArregloNodo) {
            tablaSimbolos.marcarInicializado(((AccesoArregloNodo) destino).getNombre());
        }
    }

    /**
     * <strong>Objetivo:</strong> Consulta una condicion booleana del objeto.
     *
     * <p><strong>Entradas:</strong> ExpresionNodo expresion</p>
     *
     * <p><strong>Salidas:</strong> Retorna boolean.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private boolean esModificable(ExpresionNodo expresion) {
        return expresion instanceof IdentificadorNodo || expresion instanceof AccesoArregloNodo;
    }
}
