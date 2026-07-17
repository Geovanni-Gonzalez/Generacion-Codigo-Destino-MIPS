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

public class TablaDeSimbolos {
    private final Stack<HashMap<String, Simbolo>> alcances;
    private final List<String> erroresSemanticos;
    private final Set<String> variablesNoDeclaradasReportadas;
    private final Set<String> funcionesNoDeclaradasReportadas;

    public TablaDeSimbolos() {
        this.alcances = new Stack<>();
        this.erroresSemanticos = new ArrayList<>();
        this.variablesNoDeclaradasReportadas = new HashSet<>();
        this.funcionesNoDeclaradasReportadas = new HashSet<>();
    }

    public void abrirAlcance() {
        alcances.push(new HashMap<>());
    }

    public void cerrarAlcance() {
        if (alcances.isEmpty()) {
            throw new IllegalStateException("No hay alcances abiertos para cerrar.");
        }
        alcances.pop();
    }

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

    public void reportarTipoDeclaracionInvalido(TipoDato tipo, int linea) {
        reportar("tipo declarado invalido '" + tipo + "'", linea);
    }

    public void reportarAsignacionIncompatible(TipoDato tipoOrigen, TipoDato tipoDestino, int linea) {
        reportar("no se puede asignar tipo " + tipoOrigen
                + " a variable de tipo " + tipoDestino, linea);
    }

    public Simbolo buscar(String nombre) {
        return buscar(nombre, -1);
    }

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

    public boolean existeEnAlcanceActual(String nombre) {
        if (alcances.isEmpty()) {
            return false;
        }
        return alcances.peek().containsKey(nombre);
    }

    public void agregarParametroAFuncion(String nombreFuncion, TipoDato tipoParametro, int lineaFuncion) {
        Simbolo funcion = buscarFuncionPorFirma(nombreFuncion, lineaFuncion);
        if (funcion != null) {
            funcion.agregarTipoParametro(tipoParametro);
        }
    }

    public void marcarInicializado(String nombre) {
        Simbolo simbolo = buscarSinReportar(nombre);
        if (simbolo != null) {
            simbolo.setInicializado(true);
        }
    }

    public List<String> getErroresSemanticos() {
        return Collections.unmodifiableList(erroresSemanticos);
    }

    public void reportarMainObligatorio() {
        reportar("el programa debe contener exactamente un metodo main", 0);
    }

    public void reportarClaseRedeclarada(String nombre, int linea) {
        reportar("la clase '" + nombre + "' ya esta declarada", linea);
    }

    public void reportarClaseNoDeclarada(String nombre, int linea) {
        reportar("la clase '" + nombre + "' no esta declarada. Declare la clase antes de usarla "
                + "y verifique la escritura de su nombre", linea);
    }

    public void reportarCampoNoDeclarado(String clase, String campo, int linea) {
        reportar("la clase '" + clase + "' no tiene un campo llamado '" + campo + "'", linea);
    }

    public void reportarMetodoNoDeclarado(String clase, String metodo, int linea) {
        reportar("la clase '" + clase + "' no tiene un metodo llamado '" + metodo + "'", linea);
    }

    public void reportarAccesoCampoSobreNoObjeto(TipoDato tipo, int linea) {
        reportar("el acceso '.campo' requiere un objeto; se encontro tipo " + tipo, linea);
    }

    public void reportarTipoObjetoIncompatible(String esperado, String recibido, int linea) {
        reportar("asignacion de objeto incompatible: se esperaba una instancia de '" + esperado
                + "' y se obtuvo '" + recibido + "'", linea);
    }

    public void reportarAsignacionIncompatible(String nombre, TipoDato esperado, TipoDato recibido, int linea) {
        reportar("asignacion incompatible para '" + nombre + "': se esperaba tipo "
                + esperado + " y se obtuvo tipo " + recibido, linea);
    }

    public void reportarOperacionIncompatible(String operador, TipoDato izquierda, TipoDato derecha, int linea) {
        reportar("tipos incompatibles para operador '" + operador
                + "': operando izquierdo de tipo " + izquierda
                + " y operando derecho de tipo " + derecha
                + ". " + requisitoOperador(operador), linea);
    }

    public void reportarOperacionIncompatible(String operador, TipoDato operando, int linea) {
        reportar("tipo incompatible para operador '" + operador
                + "': se obtuvo tipo " + operando + ". " + requisitoOperador(operador), linea);
    }

    public void reportarOperandoNoModificable(String operador, TipoDato tipo, int linea) {
        reportar("el operador '" + operador + "' no puede aplicarse a un literal o expresion "
                + "no modificable de tipo " + tipo
                + ". Use una variable o una posicion de arreglo como operando", linea);
    }

    public void reportarNegativoSobreExpresionNoLiteral(TipoDato tipo, int linea) {
        reportar("el operador '-' solo puede aplicarse a literales numericos; se intento aplicar "
                + "a una expresion de tipo " + tipo, linea);
    }

    public void reportarVariableNoInicializada(String nombre, int linea) {
        reportar("variable '" + nombre + "' usada antes de inicializarse. Asigne un valor antes "
                + "de leerla", linea);
    }

    public void reportarUsoArregloComoEscalar(String nombre, int linea) {
        reportar("'" + nombre + "' es un arreglo y debe accederse con indices. Use '" + nombre
                + "<<fila>><<columna>>'", linea);
    }

    public void reportarUsoEscalarComoArreglo(String nombre, int linea) {
        reportar("'" + nombre + "' no es un arreglo; fue declarado como variable escalar y no "
                + "admite indices", linea);
    }

    public void reportarAsignacionArregloCompleto(String nombre, int linea) {
        reportar("no se puede asignar directamente al arreglo completo '" + nombre
                + "'. Asigne una posicion con '" + nombre + "<<fila>><<columna>> <- valor'", linea);
    }

    public void reportarInicializacionArregloIncompatible(String nombre, TipoDato esperado,
                                                          TipoDato encontrado, int linea) {
        reportar("inicializacion incompatible en arreglo '" + nombre + "': se esperaba tipo "
                + esperado + " y se obtuvo tipo " + encontrado, linea);
    }

    public void reportarDimensionArregloInvalida(String nombre, String dimension, int linea) {
        reportar("la dimension " + dimension + " del arreglo '" + nombre
                + "' debe ser de tipo int", linea);
    }

    public void reportarDimensionArregloNoPositiva(String nombre, String dimension,
                                                    int valor, int linea) {
        reportar("la dimension " + dimension + " del arreglo '" + nombre
                + "' debe ser mayor que cero, se encontro " + valor, linea);
    }

    public void reportarInicializacionDimensionIncompatible(String nombre, int filasEsperadas,
                                                            int columnasEsperadas, int filas,
                                                            int columnas, int linea) {
        reportar("la inicializacion del arreglo '" + nombre + "' tiene dimensiones "
                + filas + "x" + columnas + ", pero se declararon "
                + filasEsperadas + "x" + columnasEsperadas, linea);
    }

    public void reportarInicializacionArregloIrregular(String nombre, int linea) {
        reportar("la inicializacion del arreglo '" + nombre
                + "' debe tener la misma cantidad de columnas en todas sus filas", linea);
    }

    public void reportarSwitchTipoInvalido(TipoDato tipo, int linea) {
        reportar("la expresion de switch no puede ser de tipo " + tipo, linea);
    }

    public void reportarCaseTipoIncompatible(TipoDato esperado, TipoDato encontrado, int linea) {
        reportar("tipo incompatible en case: se esperaba tipo " + esperado
                + " y se obtuvo tipo " + encontrado, linea);
    }

    public void reportarEntradaTipoInvalido(String nombre, TipoDato tipo, int linea) {
        reportar("cin solo puede leer variables escalares de tipo int o float; '" + nombre
                + "' tiene tipo " + tipo, linea);
    }

    public void reportarSalidaTipoInvalido(TipoDato tipo, int linea) {
        reportar("cout no puede imprimir una expresion de tipo " + tipo, linea);
    }

    public void reportarReturnFaltante(TipoDato esperado, int linea) {
        reportar("la funcion de tipo " + esperado
                + " debe retornar un valor en todas las rutas de ejecucion", linea);
    }

    public void reportarIndiceNoEntero(TipoDato tipoIndice, int linea) {
        reportar("el indice del arreglo debe ser de tipo int, se encontro " + tipoIndice, linea);
    }

    public void reportarIndiceFueraDeRango(String nombre, String dimension, int indice,
                                           int limite, int linea) {
        reportar("el indice " + dimension + " " + indice + " esta fuera de rango para el arreglo '"
                + nombre + "' (rango valido: 0 a " + (limite - 1) + ")", linea);
    }

    public void reportarCondicionNoBooleana(TipoDato recibido, int linea) {
        reportar("la condicion debe ser de tipo bool, pero se encontro tipo " + recibido, linea);
    }

    public void reportarReturnSinValor(TipoDato esperado, int linea) {
        reportar("la funcion de tipo " + esperado + " debe retornar un valor", linea);
    }

    public void reportarReturnConValorEnVoid(int linea) {
        reportar("la funcion void no puede retornar un valor. Use 'return!' o elimine la "
                + "sentencia return", linea);
    }

    public void reportarReturnTipoIncompatible(TipoDato esperado, TipoDato encontrado, int linea) {
        reportar("tipo incompatible en return: se esperaba tipo " + esperado
                + " y se obtuvo tipo " + encontrado, linea);
    }

    public void reportarBreakFueraDeCicloOSwitch(int linea) {
        reportar("la sentencia break solo puede utilizarse dentro de un ciclo o switch", linea);
    }

    public void reportarCantidadArgumentosIncorrecta(int esperados, int encontrados, int linea) {
        reportar("cantidad de argumentos incorrecta: se esperaban " + esperados
                + " argumentos y se encontraron " + encontrados, linea);
    }

    public void reportarTipoArgumentoIncorrecto(int argumento, TipoDato esperado, TipoDato encontrado, int linea) {
        reportar("argumento " + argumento + " incompatible: se esperaba tipo "
                + esperado + " y se encontro tipo " + encontrado, linea);
    }

    private void reportarVariableNoDeclarada(String nombre, int linea) {
        reportar("variable '" + nombre + "' no declarada. Declare la variable antes de usarla y "
                + "verifique la escritura de su nombre", linea);
    }

    private void reportarFuncionNoDeclarada(String nombre, int linea) {
        reportar("funcion '" + nombre + "' no declarada. Declare la funcion antes de invocarla y "
                + "verifique la escritura de su nombre", linea);
    }

    private void reportarRedeclaracion(String nombre, int linea) {
        reportar("'" + nombre + "' ya esta declarado en este alcance", linea);
    }

    private boolean existeParametroVisible(String nombre) {
        for (int i = alcances.size() - 1; i >= 0; i--) {
            Simbolo simbolo = alcances.get(i).get(nombre);
            if (simbolo != null) {
                return simbolo.getCategoria() == CategoriaSimb.PARAMETRO;
            }
        }
        return false;
    }

    private Simbolo buscarSinReportar(String nombre) {
        for (int i = alcances.size() - 1; i >= 0; i--) {
            Simbolo simbolo = alcances.get(i).get(nombre);
            if (simbolo != null) {
                return simbolo;
            }
        }
        return null;
    }

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

    private void reportar(String descripcion, int linea) {
        erroresSemanticos.add(ReportadorErrores.reportarSemantico(linea, 0, descripcion));
    }

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

    private void insertarSimboloError(Simbolo simbolo) {
        if (alcances.isEmpty()) {
            abrirAlcance();
        }
        alcances.peek().putIfAbsent(simbolo.getNombre(), simbolo);
    }
}
