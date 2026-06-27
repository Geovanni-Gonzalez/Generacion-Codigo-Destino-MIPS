package reporte;

/**
 * <strong>Nombre:</strong> ReportadorErrores
 *
 * <p><strong>Objetivo:</strong> Dar un formato uniforme a los mensajes de error de las tres fases
 * (léxica, sintáctica y semántica), con su tipo, posición y descripción.</p>
 *
 * <p><strong>Entrada:</strong> Tipo de error, línea, columna y descripción.</p>
 *
 * <p><strong>Salida:</strong> Texto del error; los métodos {@code reportar*} además lo imprimen en la salida de error.</p>
 *
 * <p><strong>Restricciones:</strong> Clase utilitaria; no se instancia.</p>
 */
public final class ReportadorErrores {
    /**
     * <strong>Nombre:</strong> Tipo
     *
     * <p><strong>Objetivo:</strong> Indicar la fase del compilador que produjo el diagnóstico.</p>
     *
     * <p><strong>Entrada:</strong> Ninguna; son constantes fijas del enum.</p>
     *
     * <p><strong>Salida:</strong> Valor de tipo de error con su etiqueta textual.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
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

    private ReportadorErrores() {
    }

    /**
     * <strong>Nombre:</strong> lexico
     *
     * <p><strong>Objetivo:</strong> Dar formato a un error léxico sin imprimirlo.</p>
     *
     * <p><strong>Entrada:</strong> int linea, int columna, String descripcion.</p>
     *
     * <p><strong>Salida:</strong> String con el mensaje formateado.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public static String lexico(int linea, int columna, String descripcion) {
        return formatear(Tipo.LEXICO, linea, columna, descripcion);
    }

    /**
     * <strong>Nombre:</strong> sintactico
     *
     * <p><strong>Objetivo:</strong> Dar formato a un error sintáctico sin imprimirlo.</p>
     *
     * <p><strong>Entrada:</strong> int linea, int columna, String descripcion.</p>
     *
     * <p><strong>Salida:</strong> String con el mensaje formateado.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public static String sintactico(int linea, int columna, String descripcion) {
        return formatear(Tipo.SINTACTICO, linea, columna, descripcion);
    }

    /**
     * <strong>Nombre:</strong> semantico
     *
     * <p><strong>Objetivo:</strong> Dar formato a un error semántico sin imprimirlo.</p>
     *
     * <p><strong>Entrada:</strong> int linea, int columna, String descripcion.</p>
     *
     * <p><strong>Salida:</strong> String con el mensaje formateado.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public static String semantico(int linea, int columna, String descripcion) {
        return formatear(Tipo.SEMANTICO, linea, columna, descripcion);
    }

    /**
     * <strong>Nombre:</strong> reportarLexico
     *
     * <p><strong>Objetivo:</strong> Dar formato a un error léxico y además imprimirlo en la salida de error.</p>
     *
     * <p><strong>Entrada:</strong> int linea, int columna, String descripcion.</p>
     *
     * <p><strong>Salida:</strong> String con el mensaje formateado.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public static String reportarLexico(int linea, int columna, String descripcion) {
        return reportar(Tipo.LEXICO, linea, columna, descripcion);
    }

    /**
     * <strong>Nombre:</strong> reportarSintactico
     *
     * <p><strong>Objetivo:</strong> Dar formato a un error sintáctico y además imprimirlo en la salida de error.</p>
     *
     * <p><strong>Entrada:</strong> int linea, int columna, String descripcion.</p>
     *
     * <p><strong>Salida:</strong> String con el mensaje formateado.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public static String reportarSintactico(int linea, int columna, String descripcion) {
        return reportar(Tipo.SINTACTICO, linea, columna, descripcion);
    }

    /**
     * <strong>Nombre:</strong> reportarSemantico
     *
     * <p><strong>Objetivo:</strong> Dar formato a un error semántico y además imprimirlo en la salida de error.</p>
     *
     * <p><strong>Entrada:</strong> int linea, int columna, String descripcion.</p>
     *
     * <p><strong>Salida:</strong> String con el mensaje formateado.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public static String reportarSemantico(int linea, int columna, String descripcion) {
        return reportar(Tipo.SEMANTICO, linea, columna, descripcion);
    }

    /**
     * <strong>Nombre:</strong> reportar
     *
     * <p><strong>Objetivo:</strong> Dar formato al diagnóstico de la fase indicada, imprimirlo en stderr y devolverlo.</p>
     *
     * <p><strong>Entrada:</strong> Tipo tipo, int linea, int columna, String descripcion.</p>
     *
     * <p><strong>Salida:</strong> String con el mensaje formateado.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public static String reportar(Tipo tipo, int linea, int columna, String descripcion) {
        String mensaje = formatear(tipo, linea, columna, descripcion);
        System.err.println(mensaje);
        return mensaje;
    }

    /**
     * <strong>Nombre:</strong> formatear
     *
     * <p><strong>Objetivo:</strong> Construir el mensaje canónico de error con la forma
     * {@code "Error <tipo> [linea L, col C]: descripcion"}.</p>
     *
     * <p><strong>Entrada:</strong> Tipo tipo, int linea, int columna, String descripcion.</p>
     *
     * <p><strong>Salida:</strong> String con el mensaje formateado.</p>
     *
     * <p><strong>Restricciones:</strong> Normaliza línea y columna a un mínimo de 1.</p>
     */
    public static String formatear(Tipo tipo, int linea, int columna, String descripcion) {
        int lineaNormalizada = linea > 0 ? linea : 1;
        int columnaNormalizada = columna > 0 ? columna : 1;
        return "Error " + tipo.etiqueta + " [linea " + lineaNormalizada
                + ", col " + columnaNormalizada + "]: " + descripcion;
    }
}
