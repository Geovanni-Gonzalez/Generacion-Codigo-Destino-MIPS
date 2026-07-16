package reporte;

public final class ReportadorErrores {
    public enum Tipo {
        /** Error producido durante el análisis léxico. */
        LEXICO("lexico"),
        /** Error producido durante el análisis sintáctico. */
        SINTACTICO("sintactico"),
        /** Error producido durante el análisis semántico. */
        SEMANTICO("semantico");

        private final String etiqueta;

        Tipo(String etiqueta) {
            this.etiqueta = etiqueta;
        }
    }

    private ReportadorErrores() {
    }

    public static String lexico(int linea, int columna, String descripcion) {
        return formatear(Tipo.LEXICO, linea, columna, descripcion);
    }

    public static String sintactico(int linea, int columna, String descripcion) {
        return formatear(Tipo.SINTACTICO, linea, columna, descripcion);
    }

    public static String semantico(int linea, int columna, String descripcion) {
        return formatear(Tipo.SEMANTICO, linea, columna, descripcion);
    }

    public static String reportarLexico(int linea, int columna, String descripcion) {
        return reportar(Tipo.LEXICO, linea, columna, descripcion);
    }

    public static String reportarSintactico(int linea, int columna, String descripcion) {
        return reportar(Tipo.SINTACTICO, linea, columna, descripcion);
    }

    public static String reportarSemantico(int linea, int columna, String descripcion) {
        return reportar(Tipo.SEMANTICO, linea, columna, descripcion);
    }

    public static String reportar(Tipo tipo, int linea, int columna, String descripcion) {
        String mensaje = formatear(tipo, linea, columna, descripcion);
        System.err.println(mensaje);
        return mensaje;
    }

    public static String formatear(Tipo tipo, int linea, int columna, String descripcion) {
        int lineaNormalizada = linea > 0 ? linea : 1;
        int columnaNormalizada = columna > 0 ? columna : 1;
        return "Error " + tipo.etiqueta + " [linea " + lineaNormalizada
                + ", col " + columnaNormalizada + "]: " + descripcion;
    }
}
