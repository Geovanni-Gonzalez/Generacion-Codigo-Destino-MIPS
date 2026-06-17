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
 * <strong>Objetivo:</strong> Escribe el archivo final de codigo intermedio del compilador.
 *
 * <p><strong>Entradas:</strong> Resultados del analisis, errores, tokens, rutas de salida y metadatos de reporte.</p>
 *
 * <p><strong>Salidas:</strong> Mensajes formateados o archivos de reporte escritos en UTF-8.</p>
 *
 * <p><strong>Restricciones:</strong> No debe recalcular analisis; solo formatea o persiste informacion recibida.</p>
 */
public final class EscritorCodigo {
    private static final String INTEGRANTES = "Geovanni Gonzalez";

    /**
     * <strong>Objetivo:</strong> Evita crear instancias de una clase utilitaria.
     *
     * <p><strong>Entradas:</strong> Sin parametros.</p>
     *
     * <p><strong>Salidas:</strong> Instancia inicializada de EscritorCodigo.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    private EscritorCodigo() {
    }

    /**
     * <strong>Objetivo:</strong> Genera el archivo .ic correspondiente a un resultado de compilacion.
     *
     * <p><strong>Entradas:</strong> Path directorioSalida, ResultadoCompilacion resultado</p>
     *
     * <p><strong>Salidas:</strong> Retorna Path.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Escribe instrucciones ya generadas en un archivo concreto.
     *
     * <p><strong>Entradas:</strong> Path archivoSalida, Path fuente, List<Instruccion> instrucciones</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
     * <strong>Objetivo:</strong> Calcula el nombre del archivo .ic a partir del nombre del fuente.
     *
     * <p><strong>Entradas:</strong> Path directorioSalida, Path fuente</p>
     *
     * <p><strong>Salidas:</strong> Retorna Path.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
     */
    public static Path resolverArchivoSalida(Path directorioSalida, Path fuente) {
        String nombreFuente = fuente.getFileName().toString();
        int punto = nombreFuente.lastIndexOf('.');
        String base = punto > 0 ? nombreFuente.substring(0, punto) : nombreFuente;
        return directorioSalida.resolve(base + ".ic");
    }

    /**
     * <strong>Objetivo:</strong> Agrega metadatos humanos al inicio del codigo intermedio generado.
     *
     * <p><strong>Entradas:</strong> BufferedWriter writer, Path fuente</p>
     *
     * <p><strong>Salidas:</strong> No retorna valor.</p>
     *
     * <p><strong>Restricciones:</strong> Debe construir una instancia consistente sin ejecutar fases externas del compilador.</p>
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
