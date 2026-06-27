package reporte;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import pipeline.ResultadoCompilacion;

/**
 * <strong>Nombre:</strong> EscritorMIPS
 *
 * <p><strong>Objetivo:</strong> Guardar en disco el ensamblador MIPS generado para un archivo
 * fuente aceptado, en un archivo con extensión {@code .asm}.</p>
 *
 * <p><strong>Entrada:</strong> El directorio de salida y el resultado de la compilación.</p>
 *
 * <p><strong>Salida:</strong> Un archivo {@code .asm} y la ruta de dicho archivo.</p>
 *
 * <p><strong>Restricciones:</strong> Clase utilitaria; no se instancia.</p>
 */
public final class EscritorMIPS {
    private EscritorMIPS() {
    }

    /**
     * <strong>Nombre:</strong> escribir
     *
     * <p><strong>Objetivo:</strong> Escribir el archivo {@code .asm} con el MIPS del resultado. Si el
     * fuente no fue aceptado, borra cualquier {@code .asm} previo y no genera salida.</p>
     *
     * <p><strong>Entrada:</strong> Path directorioSalida, ResultadoCompilacion resultado.</p>
     *
     * <p><strong>Salida:</strong> Path del archivo destino (exista o no).</p>
     *
     * <p><strong>Restricciones:</strong> Crea el directorio de salida si no existe.</p>
     */
    public static Path escribir(Path directorioSalida, ResultadoCompilacion resultado)
            throws Exception {
        Files.createDirectories(directorioSalida);
        Path salida = resolverArchivoSalida(directorioSalida, resultado.getFuente());
        if (!resultado.isAceptado()) {
            Files.deleteIfExists(salida);
            return salida;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(salida, StandardCharsets.UTF_8)) {
            for (String linea : resultado.getCodigoMIPS()) {
                writer.write(linea);
                writer.newLine();
            }
        }
        return salida;
    }

    /**
     * <strong>Nombre:</strong> resolverArchivoSalida
     *
     * <p><strong>Objetivo:</strong> Calcular la ruta destino: el mismo nombre base que el fuente pero
     * con extensión {@code .asm}.</p>
     *
     * <p><strong>Entrada:</strong> Path directorioSalida, Path fuente.</p>
     *
     * <p><strong>Salida:</strong> Path del archivo {@code .asm} destino.</p>
     *
     * <p><strong>Restricciones:</strong> Ninguna.</p>
     */
    public static Path resolverArchivoSalida(Path directorioSalida, Path fuente) {
        String nombre = fuente.getFileName().toString();
        int punto = nombre.lastIndexOf('.');
        String base = punto > 0 ? nombre.substring(0, punto) : nombre;
        return directorioSalida.resolve(base + ".asm");
    }
}
