package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Nombre: GeneradorCodigo
 *
 * Objetivo: Representar GeneradorCodigo dentro del arbol sintactico abstracto del lenguaje.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public final class GeneradorCodigo {
    /**
     * <strong>Objetivo:</strong> Mantener la unica instancia compartida del
     * generador historico de codigo.
     *
     * <p><strong>Entradas:</strong> Inicializacion estatica de la clase.</p>
     *
     * <p><strong>Salidas:</strong> Instancia reutilizable devuelta por
     * {@code getInstancia}.</p>
     *
     * <p><strong>Restricciones:</strong> Debe usarse como singleton y reiniciarse
     * explicitamente antes de reutilizar sus acumuladores.</p>
     */
    private static final GeneradorCodigo INSTANCIA = new GeneradorCodigo();

    private int contadorTemporales;
    private int contadorEtiquetas;
    private final List<Instruccion> instrucciones;
    /**
     * Nombre: GeneradorCodigo
     *
     * Objetivo: Inicializar una instancia de GeneradorCodigo con los datos requeridos.
     *
     * Entrada: Ninguna.
     *
     * Salida: Nueva instancia de GeneradorCodigo.
     *
     * Restricciones: Uso interno de la clase.
     */
    private GeneradorCodigo() {
        instrucciones = new ArrayList<>();
    }
    /**
     * Nombre: getInstancia
     *
     * Objetivo: Obtener el valor de Instancia almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo GeneradorCodigo.
     *
     * Restricciones: Ninguna.
     */
    public static GeneradorCodigo getInstancia() {
        return INSTANCIA;
    }

    /**
     * Nombre: nuevoTemp
     *
     * Objetivo: Ejecutar la operacion nuevoTemp definida por GeneradorCodigo.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String nuevoTemp() {
        return "_t" + contadorTemporales++;
    }

    /**
     * Nombre: nuevaEtiqueta
     *
     * Objetivo: Ejecutar la operacion nuevaEtiqueta definida por GeneradorCodigo.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public String nuevaEtiqueta() {
        return "_L" + contadorEtiquetas++;
    }

    /**
     * Nombre: emitir
     *
     * Objetivo: Agregar lineas o instrucciones al artefacto de salida correspondiente.
     *
     * Entrada: Instruccion instruccion.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Ninguna.
     */
    public void emitir(Instruccion instruccion) {
        instrucciones.add(Objects.requireNonNull(instruccion, "instruccion"));
    }

    /**
     * Nombre: getInstrucciones
     *
     * Objetivo: Obtener el valor de Instrucciones almacenado en la instancia.
     *
     * Entrada: Ninguna.
     *
     * Salida: Valor de tipo List<Instruccion>.
     *
     * Restricciones: Ninguna.
     */
    public List<Instruccion> getInstrucciones() {
        return Collections.unmodifiableList(instrucciones);
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
        contadorTemporales = 0;
        contadorEtiquetas = 0;
        instrucciones.clear();
    }
}
