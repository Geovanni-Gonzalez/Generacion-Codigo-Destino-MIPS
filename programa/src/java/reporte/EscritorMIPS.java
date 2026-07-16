package reporte;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import pipeline.ResultadoCompilacion;

public final class EscritorMIPS {
    private EscritorMIPS() {
    }

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

    public static Path resolverArchivoSalida(Path directorioSalida, Path fuente) {
        String nombre = fuente.getFileName().toString();
        int punto = nombre.lastIndexOf('.');
        String base = punto > 0 ? nombre.substring(0, punto) : nombre;
        return directorioSalida.resolve(base + ".asm");
    }
}
