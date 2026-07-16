package mips;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Set;
import pipeline.CompiladorInternoException;

public final class AdministradorRegistros {
    private static final String[] REGISTROS = RegistrosMIPS.POOL_TEMPORALES;
    private final Deque<String> disponibles = new ArrayDeque<>();
    private final Set<String> ocupados = new LinkedHashSet<>();

    public AdministradorRegistros() {
        reiniciar();
    }

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

    public void liberarRegistro(String registro) {
        if (registro != null && ocupados.remove(registro)) {
            disponibles.addFirst(registro);
        }
    }

    public void reiniciar() {
        disponibles.clear();
        ocupados.clear();
        disponibles.addAll(Arrays.asList(REGISTROS));
    }

    public int cantidadDisponibles() {
        return disponibles.size();
    }
}
