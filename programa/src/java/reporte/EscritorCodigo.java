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

public final class EscritorCodigo {
    private static final String INTEGRANTES = "Geovanni Gonzalez";

    private EscritorCodigo() {
    }

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

    public static Path resolverArchivoSalida(Path directorioSalida, Path fuente) {
        String nombreFuente = fuente.getFileName().toString();
        int punto = nombreFuente.lastIndexOf('.');
        String base = punto > 0 ? nombreFuente.substring(0, punto) : nombreFuente;
        return directorioSalida.resolve(base + ".ic");
    }

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
