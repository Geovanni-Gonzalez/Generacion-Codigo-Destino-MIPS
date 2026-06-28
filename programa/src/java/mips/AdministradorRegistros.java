package mips;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Set;
import pipeline.CompiladorInternoException;

/**
 * Nombre: AdministradorRegistros
 *
 * Objetivo: Analizar, traducir, emitir u optimizar codigo destino MIPS.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public final class AdministradorRegistros {
    private static final String[] REGISTROS = RegistrosMIPS.POOL_TEMPORALES;
    private final Deque<String> disponibles = new ArrayDeque<>();
    private final Set<String> ocupados = new LinkedHashSet<>();

    /**
     * Nombre: AdministradorRegistros
     *
     * Objetivo: Inicializar una instancia de AdministradorRegistros con los datos requeridos.
     *
     * Entrada: Ninguna.
     *
     * Salida: Nueva instancia de AdministradorRegistros.
     *
     * Restricciones: Ninguna.
     */
    public AdministradorRegistros() {
        reiniciar();
    }

    /**
     * Nombre: obtenerRegistro
     *
     * Objetivo: Ejecutar la operacion obtenerRegistro definida por AdministradorRegistros.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String obtenerRegistro() {
        String registro = disponibles.pollFirst();
        if (registro == null) {
            throw new CompiladorInternoException("No hay registros temporales MIPS disponibles; "
                    + "la expresion es demasiado compleja para el banco de "
                    + REGISTROS.length + " registros ($t0-$t5)");
        }
        ocupados.add(registro);
        return registro;
    }

    /**
     * Nombre: liberarRegistro
     *
     * Objetivo: Ejecutar la operacion liberarRegistro definida por AdministradorRegistros.
     *
     * Entrada: String registro.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void liberarRegistro(String registro) {
        if (registro != null && ocupados.remove(registro)) {
            disponibles.addFirst(registro);
        }
    }

    /**
     * Nombre: reiniciar
     *
     * Objetivo: Restablecer el estado interno a sus valores iniciales.
     *
     * Entrada: Ninguna.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void reiniciar() {
        disponibles.clear();
        ocupados.clear();
        disponibles.addAll(Arrays.asList(REGISTROS));
    }

    /**
     * Nombre: cantidadDisponibles
     *
     * Objetivo: Ejecutar la operacion cantidadDisponibles definida por AdministradorRegistros.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo int.
     *
     * Restricciones: Ninguna.
     */
    public int cantidadDisponibles() {
        return disponibles.size();
    }
}
