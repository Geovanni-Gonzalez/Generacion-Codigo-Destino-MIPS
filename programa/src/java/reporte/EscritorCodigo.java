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
 * Nombre: EscritorCodigo
 *
 * Objetivo: Formatear o escribir reportes y artefactos generados por el compilador.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public final class EscritorCodigo {
    private static final String INTEGRANTES = "Geovanni Gonzalez";

    /**
     * Nombre: EscritorCodigo
     *
     * Objetivo: Inicializar una instancia de EscritorCodigo con los datos requeridos.
     *
     * Entrada: Ninguna.
     *
     * Salida: Nueva instancia de EscritorCodigo.
     *
     * Restricciones: Uso interno de la clase.
     */
    private EscritorCodigo() {
    }

    /**
     * Nombre: escribir
     *
     * Objetivo: Indicar si se cumple la condicion cribir.
     *
     * Entrada: Path directorioSalida; ResultadoCompilacion resultado.
     *
     * Salida: Valor de tipo Path.
     *
     * Restricciones: Puede propagar Exception.
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
     * Nombre: resolverArchivoSalida
     *
     * Objetivo: Resolver una ruta, etiqueta o referencia a partir de la entrada.
     *
     * Entrada: Path directorioSalida; Path fuente.
     *
     * Salida: Valor de tipo Path.
     *
     * Restricciones: Ninguna.
     */
    public static Path resolverArchivoSalida(Path directorioSalida, Path fuente) {
        String nombreFuente = fuente.getFileName().toString();
        int punto = nombreFuente.lastIndexOf('.');
        String base = punto > 0 ? nombreFuente.substring(0, punto) : nombreFuente;
        return directorioSalida.resolve(base + ".ic");
    }

    /**
     * Nombre: escribirEncabezado
     *
     * Objetivo: Indicar si se cumple la condicion cribirEncabezado.
     *
     * Entrada: BufferedWriter writer; Path fuente.
     *
     * Salida: No retorna valor.
     *
     * Restricciones: Puede propagar Exception; Uso interno de la clase.
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
