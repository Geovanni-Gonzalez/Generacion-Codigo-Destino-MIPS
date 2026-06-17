package reporte;

/**
 * <strong>Objetivo:</strong> Fabrica centralizada para mensajes de error del compilador.
 *
 * <p><strong>Entradas:</strong> Resultados del analisis, errores, tokens, rutas de salida y metadatos de reporte.</p>
 *
 * <p><strong>Salidas:</strong> Mensajes formateados o archivos de reporte escritos en UTF-8.</p>
 *
 * <p><strong>Restricciones:</strong> No debe recalcular analisis; solo formatea o persiste informacion recibida.</p>
 */
public final class ReportadorErrores {
    /**
     * <strong>Objetivo:</strong> Fases del compilador que pueden producir diagnosticos.
     *
     * <p><strong>Entradas:</strong> Resultados del analisis, errores, tokens, rutas de salida y metadatos de reporte.</p>
     *
     * <p><strong>Salidas:</strong> Mensajes formateados o archivos de reporte escritos en UTF-8.</p>
     *
     * <p><strong>Restricciones:</strong> No debe recalcular analisis; solo formatea o persiste informacion recibida.</p>
     */
    public enum Tipo {
        /** Error producido durante el analisis lexico. */
        LEXICO("lexico"),
        /** Error producido durante el analisis sintactico. */
        SINTACTICO("sintactico"),
        /** Error producido durante el analisis semantico. */
        SEMANTICO("semantico");

        private final String etiqueta;

        /**
         * <strong>Objetivo:</strong> Inicializar la etiqueta textual asociada a
         * un tipo de error.
         *
         * <p><strong>Entradas:</strong> String etiqueta</p>
         *
         * <p><strong>Salidas:</strong> Instancia inicializada de Tipo.</p>
         *
         * <p><strong>Restricciones:</strong> La etiqueta debe mantenerse estable
         * porque se usa en los reportes generados.</p>
         */
        Tipo(String etiqueta) {
            this.etiqueta = etiqueta;
        }
    }

    /**
     * <strong>Objetivo:</strong> Evita crear instancias de una clase utilitaria.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de ReportadorErrores.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private ReportadorErrores() {
    }

    /**
     * <strong>Objetivo:</strong> Formatea un error lexico sin imprimirlo.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, String descripcion</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public static String lexico(int linea, int columna, String descripcion) {
        return formatear(Tipo.LEXICO, linea, columna, descripcion);
    }

    /**
     * <strong>Objetivo:</strong> Formatea un error sintactico sin imprimirlo.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, String descripcion</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public static String sintactico(int linea, int columna, String descripcion) {
        return formatear(Tipo.SINTACTICO, linea, columna, descripcion);
    }

    /**
     * <strong>Objetivo:</strong> Formatea un error semantico sin imprimirlo.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, String descripcion</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public static String semantico(int linea, int columna, String descripcion) {
        return formatear(Tipo.SEMANTICO, linea, columna, descripcion);
    }

    /**
     * <strong>Objetivo:</strong> Formatea e imprime un error lexico.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, String descripcion</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public static String reportarLexico(int linea, int columna, String descripcion) {
        return reportar(Tipo.LEXICO, linea, columna, descripcion);
    }

    /**
     * <strong>Objetivo:</strong> Formatea e imprime un error sintactico.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, String descripcion</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public static String reportarSintactico(int linea, int columna, String descripcion) {
        return reportar(Tipo.SINTACTICO, linea, columna, descripcion);
    }

    /**
     * <strong>Objetivo:</strong> Formatea e imprime un error semantico.
     *
     * <p><strong>Entradas:</strong> int linea, int columna, String descripcion</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public static String reportarSemantico(int linea, int columna, String descripcion) {
        return reportar(Tipo.SEMANTICO, linea, columna, descripcion);
    }

    /**
     * <strong>Objetivo:</strong> Imprime en stderr un diagnostico de la fase indicada y lo devuelve.
     *
     * <p><strong>Entradas:</strong> Tipo tipo, int linea, int columna, String descripcion</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public static String reportar(Tipo tipo, int linea, int columna, String descripcion) {
        String mensaje = formatear(tipo, linea, columna, descripcion);
        System.err.println(mensaje);
        return mensaje;
    }

    /**
     * <strong>Objetivo:</strong> Construye el mensaje canonico de error con coordenadas normalizadas.
     *
     * <p><strong>Entradas:</strong> Tipo tipo, int linea, int columna, String descripcion</p>
     *
     * <p><strong>Salidas:</strong> Retorna String.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public static String formatear(Tipo tipo, int linea, int columna, String descripcion) {
        int lineaNormalizada = linea > 0 ? linea : 1;
        int columnaNormalizada = columna > 0 ? columna : 1;
        return "Error " + tipo.etiqueta + " [linea " + lineaNormalizada
                + ", col " + columnaNormalizada + "]: " + descripcion;
    }
}
