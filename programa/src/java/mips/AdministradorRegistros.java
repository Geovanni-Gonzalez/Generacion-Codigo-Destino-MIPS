package mips;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <strong>Nombre:</strong> AdministradorRegistros
 *
 * <p><strong>Objetivo:</strong> Administrar el banco de registros temporales ({@code $t0}–{@code $t5})
 * que usa el generador MIPS: entregar registros libres, marcarlos como ocupados y liberarlos.</p>
 *
 * <p><strong>Entrada:</strong> Peticiones de obtener y liberar registros durante la generación.</p>
 *
 * <p><strong>Salida:</strong> Nombres de registros temporales disponibles.</p>
 *
 * <p><strong>Restricciones:</strong> Si se piden más registros de los disponibles, lanza un error.</p>
 */
public final class AdministradorRegistros {
    private static final String[] REGISTROS = {"$t0", "$t1", "$t2", "$t3", "$t4", "$t5"};
    private final Deque<String> disponibles = new ArrayDeque<>();
    private final Set<String> ocupados = new LinkedHashSet<>();

    /**
     * <strong>Nombre:</strong> AdministradorRegistros
     *
     * <p><strong>Objetivo:</strong> Crear el administrador con todos los registros libres.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> Nueva instancia de AdministradorRegistros.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public AdministradorRegistros() {
        reiniciar();
    }

    /**
     * <strong>Nombre:</strong> obtenerRegistro
     *
     * <p><strong>Objetivo:</strong> Obtener un registro temporal libre y marcarlo como ocupado.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> String con el nombre del registro.</p>
     *
     * <p><strong>Restricciones:</strong> Lanza excepción si no hay registros disponibles.</p>
     */
    public String obtenerRegistro() {
        String registro = disponibles.pollFirst();
        if (registro == null) {
            throw new IllegalStateException("No hay registros temporales MIPS disponibles");
        }
        ocupados.add(registro);
        return registro;
    }

    /**
     * <strong>Nombre:</strong> liberarRegistro
     *
     * <p><strong>Objetivo:</strong> Liberar un registro para que pueda reutilizarse en la siguiente operación.</p>
     *
     * <p><strong>Entrada:</strong> String registro.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ignora valores {@code null} o registros que no estaban ocupados.</p>
     */
    public void liberarRegistro(String registro) {
        if (registro != null && ocupados.remove(registro)) {
            disponibles.addFirst(registro);
        }
    }

    /**
     * <strong>Nombre:</strong> reiniciar
     *
     * <p><strong>Objetivo:</strong> Restablecer el banco al iniciar una nueva generación.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Deja todos los registros como disponibles.</p>
     */
    public void reiniciar() {
        disponibles.clear();
        ocupados.clear();
        disponibles.addAll(Arrays.asList(REGISTROS));
    }

    /**
     * <strong>Nombre:</strong> cantidadDisponibles
     *
     * <p><strong>Objetivo:</strong> Indicar cuántos registros temporales están libres.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna.</p>
     *
     * <p><strong>Salida:</strong> int con la cantidad de registros disponibles.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public int cantidadDisponibles() {
        return disponibles.size();
    }
}
