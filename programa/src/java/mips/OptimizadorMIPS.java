package mips;

import java.util.ArrayList;
import java.util.List;

/**
 * Nombre: OptimizadorMIPS
 *
 * Objetivo: Analizar, traducir, emitir u optimizar codigo destino MIPS.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
final class OptimizadorMIPS {

    /**
     * Nombre: optimizar
     *
     * Objetivo: Aplicar optimizaciones sobre el codigo recibido.
     *
     * Entrada: List<String> lineas.
     *
     * Salida: Valor de tipo List<String>.
     *
     * Restricciones: Ninguna.
     */
    List<String> optimizar(List<String> lineas) {
        List<String> resultado = new ArrayList<>(lineas.size());
        int i = 0;
        while (i < lineas.size()) {
            if (i + 1 < lineas.size()) {
                String fusion = fusionarStoreLoad(lineas.get(i), lineas.get(i + 1));
                if (fusion != null) {
                    resultado.add(lineas.get(i));        // se conserva el store
                    if (!fusion.isEmpty()) {
                        resultado.add(fusion);           // load reemplazada por move
                    }
                    i += 2;
                    continue;
                }
            }
            resultado.add(lineas.get(i));
            i++;
        }
        return resultado;
    }

    /**
     * Nombre: fusionarStoreLoad
     *
     * Objetivo: Ejecutar la operacion fusionarStoreLoad definida por OptimizadorMIPS.
     *
     * Entrada: String store; String load.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private String fusionarStoreLoad(String store, String load) {
        String[] guardar = instruccion(store);
        String[] cargar = instruccion(load);
        if (guardar == null || cargar == null) {
            return null;
        }
        String mnemStore = guardar[0];
        String mnemLoad = cargar[0];
        String movimiento;
        if (mnemStore.equals("sw") && mnemLoad.equals("lw")) {
            movimiento = "move";
        } else if (mnemStore.equals("s.s") && mnemLoad.equals("l.s")) {
            movimiento = "mov.s";
        } else {
            return null;
        }

        String[] opStore = operandos(guardar[1]);
        String[] opLoad = operandos(cargar[1]);
        if (opStore == null || opLoad == null) {
            return null;
        }
        String regStore = opStore[0];
        String dirStore = opStore[1];
        String regLoad = opLoad[0];
        String dirLoad = opLoad[1];

        // Solo etiquetas de datos directas y la misma dirección en ambas instrucciones.
        if (dirStore.contains("(") || !dirStore.equals(dirLoad)) {
            return null;
        }
        if (regStore.equals(regLoad)) {
            return "";  // el valor ya está en el registro: la carga sobra
        }
        return "\t" + movimiento + " " + regLoad + ", " + regStore;
    }

    /** Separa una línea de instrucción (con tabulador) en [mnemónico, operandos]; {@code null} si no es instrucción. */
    /**
     * Nombre: instruccion
     *
     * Objetivo: Ejecutar la operacion instruccion definida por OptimizadorMIPS.
     *
     * Entrada: String linea.
     *
     * Salida: Valor de tipo String[].
     *
     * Restricciones: Uso interno de la clase.
     */
    private String[] instruccion(String linea) {
        if (linea == null || !linea.startsWith("\t")) {
            return null;
        }
        String texto = linea.substring(1).trim();
        int espacio = texto.indexOf(' ');
        if (espacio < 0) {
            return null;
        }
        return new String[] {texto.substring(0, espacio), texto.substring(espacio + 1).trim()};
    }

    /** Separa "reg, direccion" en sus dos partes; {@code null} si no tiene exactamente dos operandos. */
    /**
     * Nombre: operandos
     *
     * Objetivo: Ejecutar la operacion operandos definida por OptimizadorMIPS.
     *
     * Entrada: String texto.
     *
     * Salida: Valor de tipo String[].
     *
     * Restricciones: Uso interno de la clase.
     */
    private String[] operandos(String texto) {
        int coma = texto.indexOf(',');
        if (coma < 0) {
            return null;
        }
        String primero = texto.substring(0, coma).trim();
        String segundo = texto.substring(coma + 1).trim();
        if (segundo.contains(",")) {
            return null;
        }
        return new String[] {primero, segundo};
    }
}
