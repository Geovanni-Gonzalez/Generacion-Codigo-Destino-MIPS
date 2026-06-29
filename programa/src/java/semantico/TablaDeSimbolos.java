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
 * Nombre: TablaDeSimbolos
 *
 * Objetivo: Validar reglas semanticas y administrar informacion de simbolos.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public class TablaDeSimbolos {
    private final Stack<HashMap<String, Simbolo>> alcances;
    private final List<String> erroresSemanticos;
    private final Set<String> variablesNoDeclaradasReportadas;
    private final Set<String> funcionesNoDeclaradasReportadas;

    /**
     * Nombre: TablaDeSimbolos
     *
     * Objetivo: Inicializar una instancia de TablaDeSimbolos con los datos requeridos.
     *
     * Entrada: Ninguna.
     *
     * Salida: Nueva instancia de TablaDeSimbolos.
     *
     * Restricciones: Ninguna.
     */
    public TablaDeSimbolos() {
        this.alcances = new Stack<>();
        this.erroresSemanticos = new ArrayList<>();
        this.variablesNoDeclaradasReportadas = new HashSet<>();
        this.funcionesNoDeclaradasReportadas = new HashSet<>();
    }

    /**
     * Nombre: abrirAlcance
     *
     * Objetivo: Abrir un contexto, alcance o fase de procesamiento.
     *
     * Entrada: Ninguna.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void abrirAlcance() {
        alcances.push(new HashMap<>());
    }

    /**
     * Nombre: cerrarAlcance
     *
     * Objetivo: Cerrar el contexto, alcance o fase de procesamiento actual.
     *
     * Entrada: Ninguna.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void cerrarAlcance() {
        if (alcances.isEmpty()) {
            throw new IllegalStateException("No hay alcances abiertos para cerrar.");
        }
        alcances.pop();
    }

    /**
     * Nombre: insertar
     *
     * Objetivo: Insertar informacion en la estructura interna correspondiente.
     *
     * Entrada: Simbolo simbolo.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
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
     * Nombre: reportarTipoDeclaracionInvalido
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: TipoDato tipo; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarTipoDeclaracionInvalido(TipoDato tipo, int linea) {
        reportar("tipo declarado invalido '" + tipo + "'", linea);
    }

    /**
     * Nombre: reportarAsignacionIncompatible
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: TipoDato tipoOrigen; TipoDato tipoDestino; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarAsignacionIncompatible(TipoDato tipoOrigen, TipoDato tipoDestino, int linea) {
        reportar("no se puede asignar tipo " + tipoOrigen
                + " a variable de tipo " + tipoDestino, linea);
    }

    /**
     * Nombre: buscar
     *
     * Objetivo: Localizar un simbolo o dato en las estructuras internas.
     *
     * Entrada: String nombre.
     *
     * Salida: Valor de tipo Simbolo.
     *
     * Restricciones: Ninguna.
     */
    public Simbolo buscar(String nombre) {
        return buscar(nombre, -1);
    }

    /**
     * Nombre: buscar
     *
     * Objetivo: Localizar un simbolo o dato en las estructuras internas.
     *
     * Entrada: String nombre; int linea.
     *
     * Salida: Valor de tipo Simbolo.
     *
     * Restricciones: Ninguna.
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
     * Nombre: buscarFuncion
     *
     * Objetivo: Localizar un simbolo o dato en las estructuras internas.
     *
     * Entrada: String nombre; int linea.
     *
     * Salida: Valor de tipo Simbolo.
     *
     * Restricciones: Ninguna.
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
     * <strong>Nombre:</strong> actualizarFirmaFuncion
     *
     * <p><strong>Objetivo:</strong> Reemplazar el símbolo de una función por uno con su firma completa (parámetros y retorno).</p>
     *
     * <p><strong>Entrada:</strong> String nombre, List&lt;TipoDato&gt; tiposParametros, TipoDato tipoRetorno, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Solo actualiza la función declarada en la misma línea.</p>
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
     * Nombre: existeEnAlcanceActual
     *
     * Objetivo: Ejecutar la operacion existeEnAlcanceActual definida por TablaDeSimbolos.
     *
     * Entrada: String nombre.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Ninguna.
     */
    public boolean existeEnAlcanceActual(String nombre) {
        if (alcances.isEmpty()) {
            return false;
        }
        return alcances.peek().containsKey(nombre);
    }

    /**
     * Nombre: agregarParametroAFuncion
     *
     * Objetivo: Ejecutar la operacion agregarParametroAFuncion definida por TablaDeSimbolos.
     *
     * Entrada: String nombreFuncion; TipoDato tipoParametro; int lineaFuncion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void agregarParametroAFuncion(String nombreFuncion, TipoDato tipoParametro, int lineaFuncion) {
        Simbolo funcion = buscarFuncionPorFirma(nombreFuncion, lineaFuncion);
        if (funcion != null) {
            funcion.agregarTipoParametro(tipoParametro);
        }
    }

    /**
     * Nombre: marcarInicializado
     *
     * Objetivo: Actualizar marcas de estado asociadas a simbolos o destinos.
     *
     * Entrada: String nombre.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void marcarInicializado(String nombre) {
        Simbolo simbolo = buscarSinReportar(nombre);
        if (simbolo != null) {
            simbolo.setInicializado(true);
        }
    }

    /**
     * Nombre: getErroresSemanticos
     *
     * Objetivo: Obtener el valor de ErroresSemanticos almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo List<String>.
     *
     * Restricciones: Ninguna.
     */
    public List<String> getErroresSemanticos() {
        return Collections.unmodifiableList(erroresSemanticos);
    }

    /**
     * Nombre: reportarMainObligatorio
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: Ninguna.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarMainObligatorio() {
        reportar("el programa debe contener exactamente un metodo main", 0);
    }

    /**
     * Nombre: reportarClaseRedeclarada
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String nombre; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarClaseRedeclarada(String nombre, int linea) {
        reportar("la clase '" + nombre + "' ya esta declarada", linea);
    }

    /**
     * Nombre: reportarClaseNoDeclarada
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String nombre; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarClaseNoDeclarada(String nombre, int linea) {
        reportar("la clase '" + nombre + "' no esta declarada. Declare la clase antes de usarla "
                + "y verifique la escritura de su nombre", linea);
    }

    /**
     * Nombre: reportarCampoNoDeclarado
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String clase; String campo; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarCampoNoDeclarado(String clase, String campo, int linea) {
        reportar("la clase '" + clase + "' no tiene un campo llamado '" + campo + "'", linea);
    }

    /**
     * Nombre: reportarAccesoCampoSobreNoObjeto
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: TipoDato tipo; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarAccesoCampoSobreNoObjeto(TipoDato tipo, int linea) {
        reportar("el acceso '.campo' requiere un objeto; se encontro tipo " + tipo, linea);
    }

    /**
     * Nombre: reportarTipoObjetoIncompatible
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String esperado; String recibido; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarTipoObjetoIncompatible(String esperado, String recibido, int linea) {
        reportar("asignacion de objeto incompatible: se esperaba una instancia de '" + esperado
                + "' y se obtuvo '" + recibido + "'", linea);
    }

    /**
     * Nombre: reportarAsignacionIncompatible
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String nombre; TipoDato esperado; TipoDato recibido; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarAsignacionIncompatible(String nombre, TipoDato esperado, TipoDato recibido, int linea) {
        reportar("asignacion incompatible para '" + nombre + "': se esperaba tipo "
                + esperado + " y se obtuvo tipo " + recibido, linea);
    }

    /**
     * Nombre: reportarOperacionIncompatible
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String operador; TipoDato izquierda; TipoDato derecha; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarOperacionIncompatible(String operador, TipoDato izquierda, TipoDato derecha, int linea) {
        reportar("tipos incompatibles para operador '" + operador
                + "': operando izquierdo de tipo " + izquierda
                + " y operando derecho de tipo " + derecha
                + ". " + requisitoOperador(operador), linea);
    }

    /**
     * Nombre: reportarOperacionIncompatible
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String operador; TipoDato operando; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarOperacionIncompatible(String operador, TipoDato operando, int linea) {
        reportar("tipo incompatible para operador '" + operador
                + "': se obtuvo tipo " + operando + ". " + requisitoOperador(operador), linea);
    }

    /**
     * Nombre: reportarOperandoNoModificable
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String operador; TipoDato tipo; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarOperandoNoModificable(String operador, TipoDato tipo, int linea) {
        reportar("el operador '" + operador + "' no puede aplicarse a un literal o expresion "
                + "no modificable de tipo " + tipo
                + ". Use una variable o una posicion de arreglo como operando", linea);
    }

    /**
     * Nombre: reportarNegativoSobreExpresionNoLiteral
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: TipoDato tipo; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarNegativoSobreExpresionNoLiteral(TipoDato tipo, int linea) {
        reportar("el operador '-' solo puede aplicarse a literales numericos; se intento aplicar "
                + "a una expresion de tipo " + tipo, linea);
    }

    /**
     * Nombre: reportarVariableNoInicializada
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String nombre; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarVariableNoInicializada(String nombre, int linea) {
        reportar("variable '" + nombre + "' usada antes de inicializarse. Asigne un valor antes "
                + "de leerla", linea);
    }

    /**
     * Nombre: reportarUsoArregloComoEscalar
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String nombre; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarUsoArregloComoEscalar(String nombre, int linea) {
        reportar("'" + nombre + "' es un arreglo y debe accederse con indices. Use '" + nombre
                + "<<fila>><<columna>>'", linea);
    }

    /**
     * Nombre: reportarUsoEscalarComoArreglo
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String nombre; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarUsoEscalarComoArreglo(String nombre, int linea) {
        reportar("'" + nombre + "' no es un arreglo; fue declarado como variable escalar y no "
                + "admite indices", linea);
    }

    /**
     * Nombre: reportarAsignacionArregloCompleto
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String nombre; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarAsignacionArregloCompleto(String nombre, int linea) {
        reportar("no se puede asignar directamente al arreglo completo '" + nombre
                + "'. Asigne una posicion con '" + nombre + "<<fila>><<columna>> <- valor'", linea);
    }

    /**
     * <strong>Nombre:</strong> reportarInicializacionArregloIncompatible
     *
     * <p><strong>Objetivo:</strong> Reportar un valor de inicialización de arreglo con tipo incompatible.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, TipoDato esperado, TipoDato encontrado, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarInicializacionArregloIncompatible(String nombre, TipoDato esperado,
                                                          TipoDato encontrado, int linea) {
        reportar("inicializacion incompatible en arreglo '" + nombre + "': se esperaba tipo "
                + esperado + " y se obtuvo tipo " + encontrado, linea);
    }

    /**
     * Nombre: reportarDimensionArregloInvalida
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String nombre; String dimension; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarDimensionArregloInvalida(String nombre, String dimension, int linea) {
        reportar("la dimension " + dimension + " del arreglo '" + nombre
                + "' debe ser de tipo int", linea);
    }

    /**
     * <strong>Nombre:</strong> reportarDimensionArregloNoPositiva
     *
     * <p><strong>Objetivo:</strong> Reportar que una dimensión de un arreglo no es mayor que cero.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, String dimension, int valor, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarDimensionArregloNoPositiva(String nombre, String dimension,
                                                    int valor, int linea) {
        reportar("la dimension " + dimension + " del arreglo '" + nombre
                + "' debe ser mayor que cero, se encontro " + valor, linea);
    }

    /**
     * <strong>Nombre:</strong> reportarInicializacionDimensionIncompatible
     *
     * <p><strong>Objetivo:</strong> Reportar que la inicialización de un arreglo no coincide con las dimensiones declaradas.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, int filasEsperadas, int columnasEsperadas, int filas, int columnas, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarInicializacionDimensionIncompatible(String nombre, int filasEsperadas,
                                                            int columnasEsperadas, int filas,
                                                            int columnas, int linea) {
        reportar("la inicializacion del arreglo '" + nombre + "' tiene dimensiones "
                + filas + "x" + columnas + ", pero se declararon "
                + filasEsperadas + "x" + columnasEsperadas, linea);
    }

    /**
     * Nombre: reportarInicializacionArregloIrregular
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String nombre; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarInicializacionArregloIrregular(String nombre, int linea) {
        reportar("la inicializacion del arreglo '" + nombre
                + "' debe tener la misma cantidad de columnas en todas sus filas", linea);
    }

    /**
     * Nombre: reportarSwitchTipoInvalido
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: TipoDato tipo; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarSwitchTipoInvalido(TipoDato tipo, int linea) {
        reportar("la expresion de switch no puede ser de tipo " + tipo, linea);
    }

    /**
     * Nombre: reportarCaseTipoIncompatible
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: TipoDato esperado; TipoDato encontrado; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarCaseTipoIncompatible(TipoDato esperado, TipoDato encontrado, int linea) {
        reportar("tipo incompatible en case: se esperaba tipo " + esperado
                + " y se obtuvo tipo " + encontrado, linea);
    }

    /**
     * Nombre: reportarEntradaTipoInvalido
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String nombre; TipoDato tipo; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarEntradaTipoInvalido(String nombre, TipoDato tipo, int linea) {
        reportar("cin solo puede leer variables escalares de tipo int o float; '" + nombre
                + "' tiene tipo " + tipo, linea);
    }

    /**
     * Nombre: reportarSalidaTipoInvalido
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: TipoDato tipo; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarSalidaTipoInvalido(TipoDato tipo, int linea) {
        reportar("cout no puede imprimir una expresion de tipo " + tipo, linea);
    }

    /**
     * Nombre: reportarReturnFaltante
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: TipoDato esperado; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarReturnFaltante(TipoDato esperado, int linea) {
        reportar("la funcion de tipo " + esperado
                + " debe retornar un valor en todas las rutas de ejecucion", linea);
    }

    /**
     * Nombre: reportarIndiceNoEntero
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: TipoDato tipoIndice; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarIndiceNoEntero(TipoDato tipoIndice, int linea) {
        reportar("el indice del arreglo debe ser de tipo int, se encontro " + tipoIndice, linea);
    }

    /**
     * <strong>Nombre:</strong> reportarIndiceFueraDeRango
     *
     * <p><strong>Objetivo:</strong> Reportar que un índice constante está fuera del rango del arreglo.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, String dimension, int indice, int limite, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarIndiceFueraDeRango(String nombre, String dimension, int indice,
                                           int limite, int linea) {
        reportar("el indice " + dimension + " " + indice + " esta fuera de rango para el arreglo '"
                + nombre + "' (rango valido: 0 a " + (limite - 1) + ")", linea);
    }

    /**
     * Nombre: reportarCondicionNoBooleana
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: TipoDato recibido; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarCondicionNoBooleana(TipoDato recibido, int linea) {
        reportar("la condicion debe ser de tipo bool, pero se encontro tipo " + recibido, linea);
    }

    /**
     * Nombre: reportarReturnSinValor
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: TipoDato esperado; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarReturnSinValor(TipoDato esperado, int linea) {
        reportar("la funcion de tipo " + esperado + " debe retornar un valor", linea);
    }

    /**
     * Nombre: reportarReturnConValorEnVoid
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarReturnConValorEnVoid(int linea) {
        reportar("la funcion void no puede retornar un valor. Use 'return!' o elimine la "
                + "sentencia return", linea);
    }

    /**
     * Nombre: reportarReturnTipoIncompatible
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: TipoDato esperado; TipoDato encontrado; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarReturnTipoIncompatible(TipoDato esperado, TipoDato encontrado, int linea) {
        reportar("tipo incompatible en return: se esperaba tipo " + esperado
                + " y se obtuvo tipo " + encontrado, linea);
    }

    /**
     * Nombre: reportarBreakFueraDeCicloOSwitch
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarBreakFueraDeCicloOSwitch(int linea) {
        reportar("la sentencia break solo puede utilizarse dentro de un ciclo o switch", linea);
    }

    /**
     * Nombre: reportarCantidadArgumentosIncorrecta
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: int esperados; int encontrados; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarCantidadArgumentosIncorrecta(int esperados, int encontrados, int linea) {
        reportar("cantidad de argumentos incorrecta: se esperaban " + esperados
                + " argumentos y se encontraron " + encontrados, linea);
    }

    /**
     * Nombre: reportarTipoArgumentoIncorrecto
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: int argumento; TipoDato esperado; TipoDato encontrado; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reportarTipoArgumentoIncorrecto(int argumento, TipoDato esperado, TipoDato encontrado, int linea) {
        reportar("argumento " + argumento + " incompatible: se esperaba tipo "
                + esperado + " y se encontro tipo " + encontrado, linea);
    }

    /**
     * Nombre: reportarVariableNoDeclarada
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String nombre; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void reportarVariableNoDeclarada(String nombre, int linea) {
        reportar("variable '" + nombre + "' no declarada. Declare la variable antes de usarla y "
                + "verifique la escritura de su nombre", linea);
    }

    /**
     * Nombre: reportarFuncionNoDeclarada
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String nombre; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void reportarFuncionNoDeclarada(String nombre, int linea) {
        reportar("funcion '" + nombre + "' no declarada. Declare la funcion antes de invocarla y "
                + "verifique la escritura de su nombre", linea);
    }

    /**
     * Nombre: reportarRedeclaracion
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String nombre; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void reportarRedeclaracion(String nombre, int linea) {
        reportar("'" + nombre + "' ya esta declarado en este alcance", linea);
    }

    /**
     * Nombre: existeParametroVisible
     *
     * Objetivo: Ejecutar la operacion existeParametroVisible definida por TablaDeSimbolos.
     *
     * Entrada: String nombre.
     *
     * Salida: Valor de tipo boolean.
     *
     * Restricciones: Uso interno de la clase.
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
     * Nombre: buscarSinReportar
     *
     * Objetivo: Localizar un simbolo o dato en las estructuras internas.
     *
     * Entrada: String nombre.
     *
     * Salida: Valor de tipo Simbolo.
     *
     * Restricciones: Uso interno de la clase.
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
     * Nombre: buscarFuncionPorFirma
     *
     * Objetivo: Localizar un simbolo o dato en las estructuras internas.
     *
     * Entrada: String nombre; int linea.
     *
     * Salida: Valor de tipo Simbolo.
     *
     * Restricciones: Uso interno de la clase.
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
     * Nombre: reportar
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: String descripcion; int linea.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void reportar(String descripcion, int linea) {
        erroresSemanticos.add(ReportadorErrores.reportarSemantico(linea, 0, descripcion));
    }

    /**
     * Nombre: requisitoOperador
     *
     * Objetivo: Ejecutar la operacion requisitoOperador definida por TablaDeSimbolos.
     *
     * Entrada: String operador.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private String requisitoOperador(String operador) {
        if ("%".equals(operador)) {
            return "Se requieren dos operandos numericos del mismo tipo (int-int o float-float)";
        }
        if ("^".equals(operador)) {
            return "Se requieren operandos numericos del mismo tipo (int ^ int o float ^ float)";
        }
        if ("less_t".equals(operador) || "less_te".equals(operador)
                || "greather_t".equals(operador) || "greather_te".equals(operador)) {
            return "Se requieren dos operandos numericos del mismo tipo";
        }
        if ("equal".equals(operador) || "n_equal".equals(operador)) {
            return "Se requieren dos operandos del mismo tipo";
        }
        if ("#".equals(operador) || "@".equals(operador) || "$".equals(operador)) {
            return "Se requieren operandos de tipo bool";
        }
        if ("++".equals(operador) || "--".equals(operador)) {
            return "Se requiere una variable o posicion de arreglo de tipo int o float";
        }
        return "Se requieren operandos numericos de tipo int o float";
    }

    /**
     * Nombre: insertarSimboloError
     *
     * Objetivo: Insertar informacion en la estructura interna correspondiente.
     *
     * Entrada: Simbolo simbolo.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Uso interno de la clase.
     */
    private void insertarSimboloError(Simbolo simbolo) {
        if (alcances.isEmpty()) {
            abrirAlcance();
        }
        alcances.peek().putIfAbsent(simbolo.getNombre(), simbolo);
    }
}
