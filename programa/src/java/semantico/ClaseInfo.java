package semantico;

import ast.TipoDato;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClaseInfo {
    private final String nombre;
    private final String padre;
    private final int linea;
    /** Campos propios de la clase, en orden de declaracion: nombre -> tipo. */
    private final Map<String, TipoDato> campos = new LinkedHashMap<>();
    /** Metodos propios de la clase, en orden de declaracion: nombre -> firma. */
    private final Map<String, MetodoInfo> metodos = new LinkedHashMap<>();

    public static final class MetodoInfo {
        private final String nombre;
        private final TipoDato tipoRetorno;
        private final List<TipoDato> tiposParametros;

        public MetodoInfo(String nombre, TipoDato tipoRetorno, List<TipoDato> tiposParametros) {
            this.nombre = nombre;
            this.tipoRetorno = tipoRetorno;
            this.tiposParametros = new ArrayList<>(tiposParametros);
        }

        public String getNombre() {
            return nombre;
        }

        public TipoDato getTipoRetorno() {
            return tipoRetorno;
        }

        public List<TipoDato> getTiposParametros() {
            return Collections.unmodifiableList(tiposParametros);
        }
    }

    public ClaseInfo(String nombre, String padre, int linea) {
        this.nombre = nombre;
        this.padre = padre;
        this.linea = linea;
    }

    public boolean agregarCampo(String nombre, TipoDato tipo) {
        if (campos.containsKey(nombre)) {
            return false;
        }
        campos.put(nombre, tipo);
        return true;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPadre() {
        return padre;
    }

    public int getLinea() {
        return linea;
    }

    public boolean tieneCampo(String nombre) {
        return campos.containsKey(nombre);
    }

    public TipoDato tipoCampo(String nombre) {
        return campos.get(nombre);
    }

    public boolean agregarMetodo(MetodoInfo metodo) {
        if (metodos.containsKey(metodo.getNombre())) {
            return false;
        }
        metodos.put(metodo.getNombre(), metodo);
        return true;
    }

    public MetodoInfo metodo(String nombre) {
        return metodos.get(nombre);
    }

    public boolean tieneConstructor() {
        return metodos.containsKey(nombre);
    }
}
