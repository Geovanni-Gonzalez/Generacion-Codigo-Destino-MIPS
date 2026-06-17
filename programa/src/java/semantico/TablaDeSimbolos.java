package semantico;

import ast.TipoDato;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import reporte.ReportadorErrores;

/**
 * <strong>Objetivo:</strong> Administra simbolos, alcances y errores semanticos.
 *
 * <p><strong>Entradas:</strong> Simbolos, tipos, nodos y ubicaciones producidos por las fases previas.</p>
 *
 * <p><strong>Salidas:</strong> Estado semantico actualizado, simbolos resueltos o diagnosticos acumulados.</p>
 *
 * <p><strong>Restricciones:</strong> No debe generar codigo intermedio ni escribir reportes directamente.</p>
 */
public class TablaDeSimbolos {
    private final Stack<HashMap<String, Simbolo>> alcances;
    private final List<String> erroresSemanticos;
    private final Set<String> variablesNoDeclaradasReportadas;
    private final Set<String> funcionesNoDeclaradasReportadas;
    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de TablaDeSimbolos.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public TablaDeSimbolos() {
        this.alcances = new Stack<>();
        this.erroresSemanticos = new ArrayList<>();
        this.variablesNoDeclaradasReportadas = new HashSet<>();
        this.funcionesNoDeclaradasReportadas = new HashSet<>();
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
    public void abrirAlcance() {
        alcances.push(new HashMap<>());
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
    public void cerrarAlcance() {
        if (alcances.isEmpty()) {
            throw new IllegalStateException("No hay alcances abiertos para cerrar.");
        }
        alcances.pop();
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
    public void insertar(Simbolo simbolo) {
        if (alcances.isEmpty()) {
            throw new IllegalStateException("No hay un alcance abierto para insertar simbolos.");
        }

        String nombre = simbolo.getNombre();
        if (existeEnAlcanceActual(nombre)) {
            reportarRedeclaracion(nombre, simbolo.getLinea());
            return;
        }
        if ((simbolo.getCategoria() == CategoriaSimb.VAR
                || simbolo.getCategoria() == CategoriaSimb.ARREGLO)
                && existeParametroVisible(nombre)) {
            reportarRedeclaracion(nombre, simbolo.getLinea());
            return;
        }

        alcances.peek().put(nombre, simbolo);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> TipoDato tipo, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarTipoDeclaracionInvalido(TipoDato tipo, int linea) {
        reportar("tipo declarado invalido '" + tipo + "'", linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> TipoDato tipoOrigen, TipoDato tipoDestino, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarAsignacionIncompatible(TipoDato tipoOrigen, TipoDato tipoDestino, int linea) {
        reportar("no se puede asignar tipo " + tipoOrigen
                + " a variable de tipo " + tipoDestino, linea);
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre</p>
     *
     * <p><strong>Salidas:</strong> Retorna Simbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public Simbolo buscar(String nombre) {
        return buscar(nombre, -1);
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, int linea</p>
     *
     * <p><strong>Salidas:</strong> Retorna Simbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public Simbolo buscar(String nombre, int linea) {
        for (int i = alcances.size() - 1; i >= 0; i--) {
            Simbolo simbolo = alcances.get(i).get(nombre);
            if (simbolo != null) {
                return simbolo;
            }
        }

        if (variablesNoDeclaradasReportadas.add(nombre)) {
            reportarVariableNoDeclarada(nombre, linea);
        }
        return new Simbolo(nombre, TipoDato.ERROR, CategoriaSimb.VAR, linea);
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, int linea</p>
     *
     * <p><strong>Salidas:</strong> Retorna Simbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public Simbolo buscarFuncion(String nombre, int linea) {
        for (int i = alcances.size() - 1; i >= 0; i--) {
            Simbolo simbolo = alcances.get(i).get(nombre);
            if (simbolo != null && simbolo.getCategoria() == CategoriaSimb.FUNCION) {
                return simbolo;
            }
        }

        if (funcionesNoDeclaradasReportadas.add(nombre)) {
            reportarFuncionNoDeclarada(nombre, linea);
        }
        return new Simbolo(nombre, TipoDato.ERROR, CategoriaSimb.FUNCION, linea);
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, List<TipoDato> tiposParametros, TipoDato tipoRetorno, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void actualizarFirmaFuncion(String nombre, List<TipoDato> tiposParametros,
                                       TipoDato tipoRetorno, int linea) {
        for (int i = alcances.size() - 1; i >= 0; i--) {
            HashMap<String, Simbolo> alcance = alcances.get(i);
            Simbolo simbolo = alcance.get(nombre);
            if (simbolo != null && simbolo.getCategoria() == CategoriaSimb.FUNCION
                    && simbolo.getLinea() == linea) {
                alcance.put(nombre, new Simbolo(nombre, tiposParametros, tipoRetorno, simbolo.getLinea()));
                return;
            }
        }
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre</p>
     *
     * <p><strong>Salidas:</strong> Retorna boolean.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public boolean existeEnAlcanceActual(String nombre) {
        if (alcances.isEmpty()) {
            return false;
        }
        return alcances.peek().containsKey(nombre);
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombreFuncion, TipoDato tipoParametro, int lineaFuncion</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void agregarParametroAFuncion(String nombreFuncion, TipoDato tipoParametro, int lineaFuncion) {
        Simbolo funcion = buscarFuncionPorFirma(nombreFuncion, lineaFuncion);
        if (funcion != null) {
            funcion.agregarTipoParametro(tipoParametro);
        }
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void marcarInicializado(String nombre) {
        Simbolo simbolo = buscarSinReportar(nombre);
        if (simbolo != null) {
            simbolo.setInicializado(true);
        }
    }

    /**
     * <strong>Objetivo:</strong> Consulta el valor asociado a esta propiedad.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Retorna List<String>.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public List<String> getErroresSemanticos() {
        return Collections.unmodifiableList(erroresSemanticos);
    }
    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarMainObligatorio() {
        reportar("el programa debe contener exactamente un metodo main", 0);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> String nombre, TipoDato esperado, TipoDato recibido, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarAsignacionIncompatible(String nombre, TipoDato esperado, TipoDato recibido, int linea) {
        reportar("asignacion incompatible para '" + nombre + "': se esperaba tipo "
                + esperado + " y se obtuvo tipo " + recibido, linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> String operador, TipoDato izquierda, TipoDato derecha, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarOperacionIncompatible(String operador, TipoDato izquierda, TipoDato derecha, int linea) {
        reportar("tipos incompatibles para operador '" + operador
                + "': operando izquierdo de tipo " + izquierda
                + " y operando derecho de tipo " + derecha, linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> String operador, TipoDato operando, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarOperacionIncompatible(String operador, TipoDato operando, int linea) {
        reportar("tipo incompatible para operador '" + operador
                + "': se obtuvo tipo " + operando, linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> String nombre, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarVariableNoInicializada(String nombre, int linea) {
        reportar("variable '" + nombre + "' usada antes de inicializarse", linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> String nombre, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarUsoArregloComoEscalar(String nombre, int linea) {
        reportar("'" + nombre + "' es un arreglo y debe accederse con indices", linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> String nombre, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarUsoEscalarComoArreglo(String nombre, int linea) {
        reportar("'" + nombre + "' no es un arreglo", linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> String nombre, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarAsignacionArregloCompleto(String nombre, int linea) {
        reportar("no se puede asignar directamente al arreglo completo '" + nombre + "'", linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> String nombre, TipoDato esperado, TipoDato encontrado, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarInicializacionArregloIncompatible(String nombre, TipoDato esperado,
                                                          TipoDato encontrado, int linea) {
        reportar("inicializacion incompatible en arreglo '" + nombre + "': se esperaba tipo "
                + esperado + " y se obtuvo tipo " + encontrado, linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> String nombre, String dimension, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarDimensionArregloInvalida(String nombre, String dimension, int linea) {
        reportar("la dimension " + dimension + " del arreglo '" + nombre
                + "' debe ser de tipo int", linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> TipoDato tipo, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarSwitchTipoInvalido(TipoDato tipo, int linea) {
        reportar("la expresion de switch no puede ser de tipo " + tipo, linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> TipoDato esperado, TipoDato encontrado, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarCaseTipoIncompatible(TipoDato esperado, TipoDato encontrado, int linea) {
        reportar("tipo incompatible en case: se esperaba tipo " + esperado
                + " y se obtuvo tipo " + encontrado, linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> String nombre, TipoDato tipo, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarEntradaTipoInvalido(String nombre, TipoDato tipo, int linea) {
        reportar("cin solo puede leer variables escalares declarables; '" + nombre
                + "' tiene tipo " + tipo, linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> TipoDato tipo, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarSalidaTipoInvalido(TipoDato tipo, int linea) {
        reportar("cout no puede imprimir una expresion de tipo " + tipo, linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> TipoDato esperado, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarReturnFaltante(TipoDato esperado, int linea) {
        reportar("la funcion de tipo " + esperado + " debe contener al menos un return", linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> TipoDato tipoIndice, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarIndiceNoEntero(TipoDato tipoIndice, int linea) {
        reportar("el indice del arreglo debe ser de tipo int, se encontro " + tipoIndice, linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> TipoDato recibido, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarCondicionNoBooleana(TipoDato recibido, int linea) {
        reportar("la condicion debe ser de tipo bool, pero se encontro tipo " + recibido, linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> TipoDato esperado, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarReturnSinValor(TipoDato esperado, int linea) {
        reportar("la funcion de tipo " + esperado + " debe retornar un valor", linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarReturnConValorEnVoid(int linea) {
        reportar("la funcion void no puede retornar un valor", linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> TipoDato esperado, TipoDato encontrado, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarReturnTipoIncompatible(TipoDato esperado, TipoDato encontrado, int linea) {
        reportar("tipo incompatible en return: se esperaba tipo " + esperado
                + " y se obtuvo tipo " + encontrado, linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> int esperados, int encontrados, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarCantidadArgumentosIncorrecta(int esperados, int encontrados, int linea) {
        reportar("cantidad de argumentos incorrecta: se esperaban " + esperados
                + " argumentos y se encontraron " + encontrados, linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> int argumento, TipoDato esperado, TipoDato encontrado, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public void reportarTipoArgumentoIncorrecto(int argumento, TipoDato esperado, TipoDato encontrado, int linea) {
        reportar("argumento " + argumento + " incompatible: se esperaba tipo "
                + esperado + " y se encontro tipo " + encontrado, linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> String nombre, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void reportarVariableNoDeclarada(String nombre, int linea) {
        reportar("variable '" + nombre + "' no declarada", linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> String nombre, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void reportarFuncionNoDeclarada(String nombre, int linea) {
        reportar("funcion '" + nombre + "' no declarada", linea);
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> String nombre, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void reportarRedeclaracion(String nombre, int linea) {
        reportar("'" + nombre + "' ya esta declarado en este alcance", linea);
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre</p>
     *
     * <p><strong>Salidas:</strong> Retorna boolean.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private boolean existeParametroVisible(String nombre) {
        for (int i = alcances.size() - 1; i >= 0; i--) {
            Simbolo simbolo = alcances.get(i).get(nombre);
            if (simbolo != null) {
                return simbolo.getCategoria() == CategoriaSimb.PARAMETRO;
            }
        }
        return false;
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre</p>
     *
     * <p><strong>Salidas:</strong> Retorna Simbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private Simbolo buscarSinReportar(String nombre) {
        for (int i = alcances.size() - 1; i >= 0; i--) {
            Simbolo simbolo = alcances.get(i).get(nombre);
            if (simbolo != null) {
                return simbolo;
            }
        }
        return null;
    }

    /**
     * <strong>Objetivo:</strong> Ejecuta la responsabilidad principal indicada por el nombre de la funcion.
     *
     * <p><strong>Entradas:</strong> String nombre, int linea</p>
     *
     * <p><strong>Salidas:</strong> Retorna Simbolo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private Simbolo buscarFuncionPorFirma(String nombre, int linea) {
        for (int i = alcances.size() - 1; i >= 0; i--) {
            Simbolo simbolo = alcances.get(i).get(nombre);
            if (simbolo != null && simbolo.getCategoria() == CategoriaSimb.FUNCION
                    && simbolo.getLinea() == linea) {
                return simbolo;
            }
        }
        return null;
    }

    /**
     * <strong>Objetivo:</strong> Registra un diagnostico de error para el reporte semantico.
     *
     * <p><strong>Entradas:</strong> String descripcion, int linea</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private void reportar(String descripcion, int linea) {
        erroresSemanticos.add(ReportadorErrores.reportarSemantico(linea, 0, descripcion));
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
    private void insertarSimboloError(Simbolo simbolo) {
        if (alcances.isEmpty()) {
            abrirAlcance();
        }
        alcances.peek().putIfAbsent(simbolo.getNombre(), simbolo);
    }
}
