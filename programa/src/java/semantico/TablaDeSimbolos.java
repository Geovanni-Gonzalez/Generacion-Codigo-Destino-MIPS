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
 * <strong>Nombre:</strong> TablaDeSimbolos
 *
 * <p><strong>Objetivo:</strong> Administrar los símbolos del programa por alcances (una pila de
 * ámbitos), permitir su inserción y búsqueda, y acumular los errores semánticos detectados.</p>
 *
 * <p><strong>Entrada:</strong> Símbolos a insertar y nombres a buscar durante el análisis semántico.</p>
 *
 * <p><strong>Salida:</strong> Símbolos resueltos y la lista de errores semánticos.</p>
 *
 * <p><strong>Restricciones:</strong> No genera código intermedio ni escribe archivos de reporte.</p>
 */
public class TablaDeSimbolos {
    private final Stack<HashMap<String, Simbolo>> alcances;
    private final List<String> erroresSemanticos;
    private final Set<String> variablesNoDeclaradasReportadas;
    private final Set<String> funcionesNoDeclaradasReportadas;

    /**
     * <strong>Nombre:</strong> TablaDeSimbolos
     *
     * <p><strong>Objetivo:</strong> Inicializar la pila de alcances y las estructuras de errores.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> Nueva instancia de TablaDeSimbolos.</p>
     *
     * <p><strong>Restricciones:</strong> Comienza sin alcances abiertos.</p>
     */
    public TablaDeSimbolos() {
        this.alcances = new Stack<>();
        this.erroresSemanticos = new ArrayList<>();
        this.variablesNoDeclaradasReportadas = new HashSet<>();
        this.funcionesNoDeclaradasReportadas = new HashSet<>();
    }

    /**
     * <strong>Nombre:</strong> abrirAlcance
     *
     * <p><strong>Objetivo:</strong> Abrir un nuevo alcance (ámbito) apilándolo sobre los actuales.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void abrirAlcance() {
        alcances.push(new HashMap<>());
    }

    /**
     * <strong>Nombre:</strong> cerrarAlcance
     *
     * <p><strong>Objetivo:</strong> Cerrar el alcance actual, descartando sus símbolos.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Lanza excepción si no hay alcances abiertos.</p>
     */
    public void cerrarAlcance() {
        if (alcances.isEmpty()) {
            throw new IllegalStateException("No hay alcances abiertos para cerrar.");
        }
        alcances.pop();
    }

    /**
     * <strong>Nombre:</strong> insertar
     *
     * <p><strong>Objetivo:</strong> Insertar un símbolo en el alcance actual, reportando redeclaración si
     * el nombre ya existe en ese alcance o choca con un parámetro visible.</p>
     *
     * <p><strong>Entrada:</strong> Simbolo simbolo.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Lanza excepción si no hay un alcance abierto.</p>
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
     * <strong>Nombre:</strong> reportarTipoDeclaracionInvalido
     *
     * <p><strong>Objetivo:</strong> Reportar que se declaró una variable con un tipo no permitido.</p>
     *
     * <p><strong>Entrada:</strong> TipoDato tipo, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarTipoDeclaracionInvalido(TipoDato tipo, int linea) {
        reportar("tipo declarado invalido '" + tipo + "'", linea);
    }

    /**
     * <strong>Nombre:</strong> reportarAsignacionIncompatible
     *
     * <p><strong>Objetivo:</strong> Reportar una asignación entre tipos incompatibles (variante por tipos).</p>
     *
     * <p><strong>Entrada:</strong> TipoDato tipoOrigen, TipoDato tipoDestino, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarAsignacionIncompatible(TipoDato tipoOrigen, TipoDato tipoDestino, int linea) {
        reportar("no se puede asignar tipo " + tipoOrigen
                + " a variable de tipo " + tipoDestino, linea);
    }

    /**
     * <strong>Nombre:</strong> buscar
     *
     * <p><strong>Objetivo:</strong> Buscar un símbolo por nombre sin información de línea.</p>
     *
     * <p><strong>Entrada:</strong> String nombre.</p>
     *
     * <p><strong>Salida:</strong> El Simbolo encontrado, o uno de tipo ERROR si no existe.</p>
     *
     * <p><strong>Restricciones:</strong> Si no existe, reporta variable no declarada.</p>
     */
    public Simbolo buscar(String nombre) {
        return buscar(nombre, -1);
    }

    /**
     * <strong>Nombre:</strong> buscar
     *
     * <p><strong>Objetivo:</strong> Buscar un símbolo recorriendo los alcances del más interno al más externo.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, int linea.</p>
     *
     * <p><strong>Salida:</strong> El Simbolo encontrado, o uno de tipo ERROR si no existe.</p>
     *
     * <p><strong>Restricciones:</strong> Reporta "no declarada" una sola vez por nombre.</p>
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
     * <strong>Nombre:</strong> buscarFuncion
     *
     * <p><strong>Objetivo:</strong> Buscar una función por nombre entre todos los alcances.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, int linea.</p>
     *
     * <p><strong>Salida:</strong> El Simbolo de la función, o uno de tipo ERROR si no existe.</p>
     *
     * <p><strong>Restricciones:</strong> Reporta "función no declarada" una sola vez por nombre.</p>
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
     * <strong>Nombre:</strong> existeEnAlcanceActual
     *
     * <p><strong>Objetivo:</strong> Indicar si un nombre ya está declarado en el alcance más interno.</p>
     *
     * <p><strong>Entrada:</strong> String nombre.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si existe en el alcance actual.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public boolean existeEnAlcanceActual(String nombre) {
        if (alcances.isEmpty()) {
            return false;
        }
        return alcances.peek().containsKey(nombre);
    }

    /**
     * <strong>Nombre:</strong> agregarParametroAFuncion
     *
     * <p><strong>Objetivo:</strong> Agregar el tipo de un parámetro a la firma de una función ya declarada.</p>
     *
     * <p><strong>Entrada:</strong> String nombreFuncion, TipoDato tipoParametro, int lineaFuncion.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> No hace nada si no encuentra la función en esa línea.</p>
     */
    public void agregarParametroAFuncion(String nombreFuncion, TipoDato tipoParametro, int lineaFuncion) {
        Simbolo funcion = buscarFuncionPorFirma(nombreFuncion, lineaFuncion);
        if (funcion != null) {
            funcion.agregarTipoParametro(tipoParametro);
        }
    }

    /**
     * <strong>Nombre:</strong> marcarInicializado
     *
     * <p><strong>Objetivo:</strong> Marcar un símbolo como inicializado cuando se le asigna un valor.</p>
     *
     * <p><strong>Entrada:</strong> String nombre.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> No hace nada si el símbolo no existe.</p>
     */
    public void marcarInicializado(String nombre) {
        Simbolo simbolo = buscarSinReportar(nombre);
        if (simbolo != null) {
            simbolo.setInicializado(true);
        }
    }

    /**
     * <strong>Nombre:</strong> getErroresSemanticos
     *
     * <p><strong>Objetivo:</strong> Devolver la lista de errores semánticos acumulados.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> List&lt;String&gt; no modificable con los errores.</p>
     *
     * <p><strong>Restricciones:</strong> La lista no se puede modificar.</p>
     */
    public List<String> getErroresSemanticos() {
        return Collections.unmodifiableList(erroresSemanticos);
    }

    /**
     * <strong>Nombre:</strong> reportarMainObligatorio
     *
     * <p><strong>Objetivo:</strong> Reportar que el programa debe contener exactamente un método main.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarMainObligatorio() {
        reportar("el programa debe contener exactamente un metodo main", 0);
    }

    /**
     * <strong>Nombre:</strong> reportarAsignacionIncompatible
     *
     * <p><strong>Objetivo:</strong> Reportar una asignación incompatible indicando la variable, el tipo esperado y el recibido.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, TipoDato esperado, TipoDato recibido, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarAsignacionIncompatible(String nombre, TipoDato esperado, TipoDato recibido, int linea) {
        reportar("asignacion incompatible para '" + nombre + "': se esperaba tipo "
                + esperado + " y se obtuvo tipo " + recibido, linea);
    }

    /**
     * <strong>Nombre:</strong> reportarOperacionIncompatible
     *
     * <p><strong>Objetivo:</strong> Reportar tipos incompatibles en una operación binaria, con su requisito.</p>
     *
     * <p><strong>Entrada:</strong> String operador, TipoDato izquierda, TipoDato derecha, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarOperacionIncompatible(String operador, TipoDato izquierda, TipoDato derecha, int linea) {
        reportar("tipos incompatibles para operador '" + operador
                + "': operando izquierdo de tipo " + izquierda
                + " y operando derecho de tipo " + derecha
                + ". " + requisitoOperador(operador), linea);
    }

    /**
     * <strong>Nombre:</strong> reportarOperacionIncompatible
     *
     * <p><strong>Objetivo:</strong> Reportar un tipo incompatible en una operación unaria, con su requisito.</p>
     *
     * <p><strong>Entrada:</strong> String operador, TipoDato operando, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarOperacionIncompatible(String operador, TipoDato operando, int linea) {
        reportar("tipo incompatible para operador '" + operador
                + "': se obtuvo tipo " + operando + ". " + requisitoOperador(operador), linea);
    }

    /**
     * <strong>Nombre:</strong> reportarOperandoNoModificable
     *
     * <p><strong>Objetivo:</strong> Reportar que un operador requiere un operando modificable (variable o celda), no un literal.</p>
     *
     * <p><strong>Entrada:</strong> String operador, TipoDato tipo, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarOperandoNoModificable(String operador, TipoDato tipo, int linea) {
        reportar("el operador '" + operador + "' no puede aplicarse a un literal o expresion "
                + "no modificable de tipo " + tipo
                + ". Use una variable o una posicion de arreglo como operando", linea);
    }

    /**
     * <strong>Nombre:</strong> reportarNegativoSobreExpresionNoLiteral
     *
     * <p><strong>Objetivo:</strong> Reportar que el operador {@code -} unario solo aplica a literales numéricos.</p>
     *
     * <p><strong>Entrada:</strong> TipoDato tipo, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarNegativoSobreExpresionNoLiteral(TipoDato tipo, int linea) {
        reportar("el operador '-' solo puede aplicarse a literales numericos; se intento aplicar "
                + "a una expresion de tipo " + tipo, linea);
    }

    /**
     * <strong>Nombre:</strong> reportarVariableNoInicializada
     *
     * <p><strong>Objetivo:</strong> Reportar el uso de una variable antes de asignarle un valor.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarVariableNoInicializada(String nombre, int linea) {
        reportar("variable '" + nombre + "' usada antes de inicializarse. Asigne un valor antes "
                + "de leerla", linea);
    }

    /**
     * <strong>Nombre:</strong> reportarUsoArregloComoEscalar
     *
     * <p><strong>Objetivo:</strong> Reportar el uso de un arreglo sin índices, como si fuera escalar.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarUsoArregloComoEscalar(String nombre, int linea) {
        reportar("'" + nombre + "' es un arreglo y debe accederse con indices. Use '" + nombre
                + "<<fila>><<columna>>'", linea);
    }

    /**
     * <strong>Nombre:</strong> reportarUsoEscalarComoArreglo
     *
     * <p><strong>Objetivo:</strong> Reportar el uso de una variable escalar con índices, como si fuera arreglo.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarUsoEscalarComoArreglo(String nombre, int linea) {
        reportar("'" + nombre + "' no es un arreglo; fue declarado como variable escalar y no "
                + "admite indices", linea);
    }

    /**
     * <strong>Nombre:</strong> reportarAsignacionArregloCompleto
     *
     * <p><strong>Objetivo:</strong> Reportar el intento de asignar al arreglo completo en vez de a una celda.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> reportarDimensionArregloInvalida
     *
     * <p><strong>Objetivo:</strong> Reportar que una dimensión de un arreglo no es de tipo int.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, String dimension, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> reportarInicializacionArregloIrregular
     *
     * <p><strong>Objetivo:</strong> Reportar una inicialización de arreglo con filas de distinta cantidad de columnas.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarInicializacionArregloIrregular(String nombre, int linea) {
        reportar("la inicializacion del arreglo '" + nombre
                + "' debe tener la misma cantidad de columnas en todas sus filas", linea);
    }

    /**
     * <strong>Nombre:</strong> reportarSwitchTipoInvalido
     *
     * <p><strong>Objetivo:</strong> Reportar que la expresión de un switch tiene un tipo no permitido.</p>
     *
     * <p><strong>Entrada:</strong> TipoDato tipo, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarSwitchTipoInvalido(TipoDato tipo, int linea) {
        reportar("la expresion de switch no puede ser de tipo " + tipo, linea);
    }

    /**
     * <strong>Nombre:</strong> reportarCaseTipoIncompatible
     *
     * <p><strong>Objetivo:</strong> Reportar que el valor de un case no coincide con el tipo del switch.</p>
     *
     * <p><strong>Entrada:</strong> TipoDato esperado, TipoDato encontrado, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarCaseTipoIncompatible(TipoDato esperado, TipoDato encontrado, int linea) {
        reportar("tipo incompatible en case: se esperaba tipo " + esperado
                + " y se obtuvo tipo " + encontrado, linea);
    }

    /**
     * <strong>Nombre:</strong> reportarEntradaTipoInvalido
     *
     * <p><strong>Objetivo:</strong> Reportar que {@code cin} se usa sobre una variable de tipo no admitido.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, TipoDato tipo, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarEntradaTipoInvalido(String nombre, TipoDato tipo, int linea) {
        reportar("cin solo puede leer variables escalares de tipo int o float; '" + nombre
                + "' tiene tipo " + tipo, linea);
    }

    /**
     * <strong>Nombre:</strong> reportarSalidaTipoInvalido
     *
     * <p><strong>Objetivo:</strong> Reportar que {@code cout} intenta imprimir una expresión de tipo no admitido.</p>
     *
     * <p><strong>Entrada:</strong> TipoDato tipo, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarSalidaTipoInvalido(TipoDato tipo, int linea) {
        reportar("cout no puede imprimir una expresion de tipo " + tipo, linea);
    }

    /**
     * <strong>Nombre:</strong> reportarReturnFaltante
     *
     * <p><strong>Objetivo:</strong> Reportar que una función con retorno no devuelve valor en todas las rutas.</p>
     *
     * <p><strong>Entrada:</strong> TipoDato esperado, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarReturnFaltante(TipoDato esperado, int linea) {
        reportar("la funcion de tipo " + esperado
                + " debe retornar un valor en todas las rutas de ejecucion", linea);
    }

    /**
     * <strong>Nombre:</strong> reportarIndiceNoEntero
     *
     * <p><strong>Objetivo:</strong> Reportar que el índice de un arreglo no es de tipo int.</p>
     *
     * <p><strong>Entrada:</strong> TipoDato tipoIndice, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> reportarCondicionNoBooleana
     *
     * <p><strong>Objetivo:</strong> Reportar que una condición no es de tipo bool.</p>
     *
     * <p><strong>Entrada:</strong> TipoDato recibido, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarCondicionNoBooleana(TipoDato recibido, int linea) {
        reportar("la condicion debe ser de tipo bool, pero se encontro tipo " + recibido, linea);
    }

    /**
     * <strong>Nombre:</strong> reportarReturnSinValor
     *
     * <p><strong>Objetivo:</strong> Reportar que una función con retorno usó un return sin valor.</p>
     *
     * <p><strong>Entrada:</strong> TipoDato esperado, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarReturnSinValor(TipoDato esperado, int linea) {
        reportar("la funcion de tipo " + esperado + " debe retornar un valor", linea);
    }

    /**
     * <strong>Nombre:</strong> reportarReturnConValorEnVoid
     *
     * <p><strong>Objetivo:</strong> Reportar que una función void intenta retornar un valor.</p>
     *
     * <p><strong>Entrada:</strong> int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarReturnConValorEnVoid(int linea) {
        reportar("la funcion void no puede retornar un valor. Use 'return!' o elimine la "
                + "sentencia return", linea);
    }

    /**
     * <strong>Nombre:</strong> reportarReturnTipoIncompatible
     *
     * <p><strong>Objetivo:</strong> Reportar que el valor retornado no coincide con el tipo de la función.</p>
     *
     * <p><strong>Entrada:</strong> TipoDato esperado, TipoDato encontrado, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarReturnTipoIncompatible(TipoDato esperado, TipoDato encontrado, int linea) {
        reportar("tipo incompatible en return: se esperaba tipo " + esperado
                + " y se obtuvo tipo " + encontrado, linea);
    }

    /**
     * <strong>Nombre:</strong> reportarBreakFueraDeCicloOSwitch
     *
     * <p><strong>Objetivo:</strong> Reportar un {@code break} fuera de un ciclo o switch.</p>
     *
     * <p><strong>Entrada:</strong> int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarBreakFueraDeCicloOSwitch(int linea) {
        reportar("la sentencia break solo puede utilizarse dentro de un ciclo o switch", linea);
    }

    /**
     * <strong>Nombre:</strong> reportarCantidadArgumentosIncorrecta
     *
     * <p><strong>Objetivo:</strong> Reportar una llamada con un número de argumentos distinto al esperado.</p>
     *
     * <p><strong>Entrada:</strong> int esperados, int encontrados, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarCantidadArgumentosIncorrecta(int esperados, int encontrados, int linea) {
        reportar("cantidad de argumentos incorrecta: se esperaban " + esperados
                + " argumentos y se encontraron " + encontrados, linea);
    }

    /**
     * <strong>Nombre:</strong> reportarTipoArgumentoIncorrecto
     *
     * <p><strong>Objetivo:</strong> Reportar que un argumento de una llamada tiene un tipo incompatible.</p>
     *
     * <p><strong>Entrada:</strong> int argumento, TipoDato esperado, TipoDato encontrado, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public void reportarTipoArgumentoIncorrecto(int argumento, TipoDato esperado, TipoDato encontrado, int linea) {
        reportar("argumento " + argumento + " incompatible: se esperaba tipo "
                + esperado + " y se encontro tipo " + encontrado, linea);
    }

    /**
     * <strong>Nombre:</strong> reportarVariableNoDeclarada
     *
     * <p><strong>Objetivo:</strong> Reportar el uso de una variable que no fue declarada.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Método interno usado por {@link #buscar}.</p>
     */
    private void reportarVariableNoDeclarada(String nombre, int linea) {
        reportar("variable '" + nombre + "' no declarada. Declare la variable antes de usarla y "
                + "verifique la escritura de su nombre", linea);
    }

    /**
     * <strong>Nombre:</strong> reportarFuncionNoDeclarada
     *
     * <p><strong>Objetivo:</strong> Reportar la invocación de una función que no fue declarada.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Método interno usado por {@link #buscarFuncion}.</p>
     */
    private void reportarFuncionNoDeclarada(String nombre, int linea) {
        reportar("funcion '" + nombre + "' no declarada. Declare la funcion antes de invocarla y "
                + "verifique la escritura de su nombre", linea);
    }

    /**
     * <strong>Nombre:</strong> reportarRedeclaracion
     *
     * <p><strong>Objetivo:</strong> Reportar que un nombre ya está declarado en el alcance actual.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Método interno usado por {@link #insertar}.</p>
     */
    private void reportarRedeclaracion(String nombre, int linea) {
        reportar("'" + nombre + "' ya esta declarado en este alcance", linea);
    }

    /**
     * <strong>Nombre:</strong> existeParametroVisible
     *
     * <p><strong>Objetivo:</strong> Indicar si el primer símbolo visible con ese nombre es un parámetro.</p>
     *
     * <p><strong>Entrada:</strong> String nombre.</p>
     *
     * <p><strong>Salida:</strong> boolean; true si el símbolo visible es un parámetro.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> buscarSinReportar
     *
     * <p><strong>Objetivo:</strong> Buscar un símbolo sin generar ningún error si no existe.</p>
     *
     * <p><strong>Entrada:</strong> String nombre.</p>
     *
     * <p><strong>Salida:</strong> El Simbolo encontrado, o {@code null} si no existe.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> buscarFuncionPorFirma
     *
     * <p><strong>Objetivo:</strong> Buscar una función por nombre y línea de declaración exactos.</p>
     *
     * <p><strong>Entrada:</strong> String nombre, int linea.</p>
     *
     * <p><strong>Salida:</strong> El Simbolo de la función, o {@code null} si no existe.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> reportar
     *
     * <p><strong>Objetivo:</strong> Formatear un error semántico con su línea y agregarlo a la lista.</p>
     *
     * <p><strong>Entrada:</strong> String descripcion, int linea.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Método interno usado por todos los {@code reportar*}.</p>
     */
    private void reportar(String descripcion, int linea) {
        erroresSemanticos.add(ReportadorErrores.reportarSemantico(linea, 0, descripcion));
    }

    /**
     * <strong>Nombre:</strong> requisitoOperador
     *
     * <p><strong>Objetivo:</strong> Devolver el texto que describe los operandos que requiere un operador, para enriquecer el error.</p>
     *
     * <p><strong>Entrada:</strong> String operador.</p>
     *
     * <p><strong>Salida:</strong> String con el requisito del operador.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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
     * <strong>Nombre:</strong> insertarSimboloError
     *
     * <p><strong>Objetivo:</strong> Insertar un símbolo de recuperación de error, abriendo un alcance si hace falta.</p>
     *
     * <p><strong>Entrada:</strong> Simbolo simbolo.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> No sobreescribe un símbolo ya existente.</p>
     */
    private void insertarSimboloError(Simbolo simbolo) {
        if (alcances.isEmpty()) {
            abrirAlcance();
        }
        alcances.peek().putIfAbsent(simbolo.getNombre(), simbolo);
    }
}
