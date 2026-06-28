package reporte;

/**
 * Nombre: ReportadorErrores
 *
 * Objetivo: Formatear o escribir reportes y artefactos generados por el compilador.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public final class ReportadorErrores {
    /**
     * Nombre: Tipo
     *
     * Objetivo: Formatear o escribir reportes y artefactos generados por el compilador.
     *
     * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
     *
     * Salida: Estado, datos o artefactos producidos por la clase.
     *
     * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
     */
    public enum Tipo {
        /** Error producido durante el análisis léxico. */
        LEXICO("lexico"),
        /** Error producido durante el análisis sintáctico. */
        SINTACTICO("sintactico"),
        /** Error producido durante el análisis semántico. */
        SEMANTICO("semantico");

        private final String etiqueta;

        /**
         * <strong>Nombre:</strong> Tipo
         *
         * <p><strong>Objetivo:</strong> Asociar a cada tipo su etiqueta textual, usada en los reportes.</p>
         *
         * <p><strong>Entrada:</strong> String etiqueta.</p>
         *
         * <p><strong>Salida:</strong> Constante del enum inicializada.</p>
         *
         * <p><strong>Restricciones:</strong> La etiqueta debe mantenerse estable porque aparece en los reportes.</p>
         */
        Tipo(String etiqueta) {
            this.etiqueta = etiqueta;
        }
    }

    /**
     * Nombre: ReportadorErrores
     *
     * Objetivo: Inicializar una instancia de ReportadorErrores con los datos requeridos.
     *
     * Entrada: Ninguna.
     *
     * Salida: Nueva instancia de ReportadorErrores.
     *
     * Restricciones: Uso interno de la clase.
     */
    private ReportadorErrores() {
    }

    /**
     * Nombre: lexico
     *
     * Objetivo: Ejecutar la operacion lexico definida por ReportadorErrores.
     *
     * Entrada: int linea; int columna; String descripcion.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public static String lexico(int linea, int columna, String descripcion) {
        return formatear(Tipo.LEXICO, linea, columna, descripcion);
    }

    /**
     * Nombre: sintactico
     *
     * Objetivo: Ejecutar la operacion sintactico definida por ReportadorErrores.
     *
     * Entrada: int linea; int columna; String descripcion.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public static String sintactico(int linea, int columna, String descripcion) {
        return formatear(Tipo.SINTACTICO, linea, columna, descripcion);
    }

    /**
     * Nombre: semantico
     *
     * Objetivo: Ejecutar la operacion semantico definida por ReportadorErrores.
     *
     * Entrada: int linea; int columna; String descripcion.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public static String semantico(int linea, int columna, String descripcion) {
        return formatear(Tipo.SEMANTICO, linea, columna, descripcion);
    }

    /**
     * Nombre: reportarLexico
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: int linea; int columna; String descripcion.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public static String reportarLexico(int linea, int columna, String descripcion) {
        return reportar(Tipo.LEXICO, linea, columna, descripcion);
    }

    /**
     * Nombre: reportarSintactico
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: int linea; int columna; String descripcion.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public static String reportarSintactico(int linea, int columna, String descripcion) {
        return reportar(Tipo.SINTACTICO, linea, columna, descripcion);
    }

    /**
     * Nombre: reportarSemantico
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: int linea; int columna; String descripcion.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public static String reportarSemantico(int linea, int columna, String descripcion) {
        return reportar(Tipo.SEMANTICO, linea, columna, descripcion);
    }

    /**
     * Nombre: reportar
     *
     * Objetivo: Registrar un diagnostico de error con el formato del compilador.
     *
     * Entrada: Tipo tipo; int linea; int columna; String descripcion.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public static String reportar(Tipo tipo, int linea, int columna, String descripcion) {
        String mensaje = formatear(tipo, linea, columna, descripcion);
        System.err.println(mensaje);
        return mensaje;
    }

    /**
     * Nombre: formatear
     *
     * Objetivo: Convertir un valor interno a su representacion textual.
     *
     * Entrada: Tipo tipo; int linea; int columna; String descripcion.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    public static String formatear(Tipo tipo, int linea, int columna, String descripcion) {
        int lineaNormalizada = linea > 0 ? linea : 1;
        int columnaNormalizada = columna > 0 ? columna : 1;
        return "Error " + tipo.etiqueta + " [linea " + lineaNormalizada
                + ", col " + columnaNormalizada + "]: " + descripcion;
    }
}
