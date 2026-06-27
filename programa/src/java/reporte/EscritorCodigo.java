package reporte;

import intermedio.Instruccion;
import intermedio.Operacion;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import pipeline.ResultadoCompilacion;

/**
 * <strong>Nombre:</strong> EscritorCodigo
 *
 * <p><strong>Objetivo:</strong> Escribir en disco el archivo de código intermedio ({@code .ic}) del
 * compilador, con un encabezado de metadatos y una instrucción por línea.</p>
 *
 * <p><strong>Entrada:</strong> El directorio de salida, el resultado de la compilación o una lista de instrucciones.</p>
 *
 * <p><strong>Salida:</strong> Un archivo {@code .ic} escrito en UTF-8.</p>
 *
 * <p><strong>Restricciones:</strong> Solo formatea y persiste información ya calculada; no analiza.</p>
 */
public final class EscritorCodigo {
    private static final String INTEGRANTES = "Geovanni Gonzalez";

    private EscritorCodigo() {
    }

    /**
     * <strong>Nombre:</strong> escribir
     *
     * <p><strong>Objetivo:</strong> Generar el archivo {@code .ic} correspondiente a un resultado de
     * compilación. Si el fuente no fue aceptado, borra cualquier {@code .ic} previo y no genera salida.</p>
     *
     * <p><strong>Entrada:</strong> Path directorioSalida, ResultadoCompilacion resultado.</p>
     *
     * <p><strong>Salida:</strong> Path del archivo destino (exista o no).</p>
     *
     * <p><strong>Restricciones:</strong> Crea el directorio de salida si no existe.</p>
     */
    public static Path escribir(Path directorioSalida, ResultadoCompilacion resultado) throws Exception {
        Files.createDirectories(directorioSalida);
        Path archivoSalida = resolverArchivoSalida(directorioSalida, resultado.getFuente());
        if (!resultado.isAceptado()) {
            Files.deleteIfExists(archivoSalida);
            return archivoSalida;
        }

        escribir(archivoSalida, resultado.getFuente(), resultado.getCodigoIntermedio());
        return archivoSalida;
    }

    /**
     * <strong>Nombre:</strong> escribir
     *
     * <p><strong>Objetivo:</strong> Escribir las instrucciones intermedias en un archivo concreto, con
     * encabezado. Las etiquetas van sin sangría y el resto con tabulación.</p>
     *
     * <p><strong>Entrada:</strong> Path archivoSalida, Path fuente, List&lt;Instruccion&gt; instrucciones.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public static void escribir(Path archivoSalida, Path fuente, List<Instruccion> instrucciones)
            throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(archivoSalida, StandardCharsets.UTF_8)) {
            escribirEncabezado(writer, fuente);
            writer.newLine();
            for (Instruccion instruccion : instrucciones) {
                if (instruccion.getOp() != Operacion.LABEL) {
                    writer.write('\t');
                }
                writer.write(instruccion.toString());
                writer.newLine();
            }
        }
    }

    /**
     * <strong>Nombre:</strong> resolverArchivoSalida
     *
     * <p><strong>Objetivo:</strong> Calcular la ruta destino: el mismo nombre base que el fuente pero
     * con extensión {@code .ic}.</p>
     *
     * <p><strong>Entrada:</strong> Path directorioSalida, Path fuente.</p>
     *
     * <p><strong>Salida:</strong> Path del archivo {@code .ic} destino.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public static Path resolverArchivoSalida(Path directorioSalida, Path fuente) {
        String nombreFuente = fuente.getFileName().toString();
        int punto = nombreFuente.lastIndexOf('.');
        String base = punto > 0 ? nombreFuente.substring(0, punto) : nombreFuente;
        return directorioSalida.resolve(base + ".ic");
    }

    /**
     * <strong>Nombre:</strong> escribirEncabezado
     *
     * <p><strong>Objetivo:</strong> Escribir el encabezado con fecha, archivo fuente e integrantes.</p>
     *
     * <p><strong>Entrada:</strong> BufferedWriter writer, Path fuente.</p>
     *
     * <p><strong>Salida:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    private static void escribirEncabezado(BufferedWriter writer, Path fuente) throws Exception {
        writer.write("// Codigo intermedio");
        writer.newLine();
        writer.write("// Fecha: " + LocalDate.now());
        writer.newLine();
        writer.write("// Archivo fuente: " + fuente.getFileName());
        writer.newLine();
        writer.write("// Integrantes: " + INTEGRANTES);
        writer.newLine();
    }
}
