package reporte;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import pipeline.ResultadoCompilacion;

/**
 * Nombre: EscritorMIPS
 *
 * Objetivo: Formatear o escribir reportes y artefactos generados por el compilador.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
public final class EscritorMIPS {
    /**
     * Nombre: EscritorMIPS
     *
     * Objetivo: Inicializar una instancia de EscritorMIPS con los datos requeridos.
     *
     * Entrada: Ninguna.
     *
     * Salida: Nueva instancia de EscritorMIPS.
     *
     * Restricciones: Uso interno de la clase.
     */
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
        String nombre = fuente.getFileName().toString();
        int punto = nombre.lastIndexOf('.');
        String base = punto > 0 ? nombre.substring(0, punto) : nombre;
        return directorioSalida.resolve(base + ".asm");
    }
}
