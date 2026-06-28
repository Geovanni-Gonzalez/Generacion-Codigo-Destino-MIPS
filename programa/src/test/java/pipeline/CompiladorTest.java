package pipeline;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CompiladorTest {
    @Test
    void compilaProgramaMinimoValido() throws Exception {
        Path fuente = Path.of("test", "01_minimo_valido.chip");

        ResultadoCompilacion resultado = new Compilador().compilar(fuente);

        assertTrue(resultado.isSintaxisCompleta());
        assertTrue(resultado.isAceptado());
        assertFalse(resultado.getCodigoIntermedio().isEmpty());
        assertFalse(resultado.getCodigoMIPS().isEmpty());
        assertTrue(resultado.getCodigoMIPS().contains("main:"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("programasValidos")
    void compilaProgramasValidos(String archivo) throws Exception {
        ResultadoCompilacion resultado = new Compilador().compilar(Path.of("test", archivo));

        assertTrue(resultado.isSintaxisCompleta());
        assertTrue(resultado.isAceptado());
        assertFalse(resultado.getCodigoIntermedio().isEmpty());
        assertFalse(resultado.getCodigoMIPS().isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("programasInvalidos")
    void rechazaProgramasInvalidos(String archivo) throws Exception {
        ResultadoCompilacion resultado = new Compilador().compilar(Path.of("test", archivo));

        assertFalse(resultado.isAceptado());
        assertTrue(resultado.getCodigoIntermedio().isEmpty());
        assertTrue(resultado.getCodigoMIPS().isEmpty());
    }

    private static Stream<Arguments> programasValidos() {
        return Stream.of(
                "01_minimo_valido.chip",
                "02_variables_enunciado.chip",
                "03_asignaciones.chip",
                "04_aritmeticas.chip",
                "05_relacionales.chip",
                "06_logicas.chip",
                "07_if_else.chip",
                "08_do_while.chip",
                "09_switch.chip",
                "10_funciones.chip",
                "12_comentarios.chip",
                "22_condiciones_bool_validas.chip"
        ).map(Arguments::of);
    }

    private static Stream<Arguments> programasInvalidos() {
        return Stream.of(
                "13_error_lexico.chip",
                "14_error_sintactico.chip",
                "11_arreglos_enunciado.chip",
                "15_doble_main.chip",
                "16_main_con_parametros.chip",
                "17_main_tipo_invalido.chip",
                "18_sin_main.chip",
                "19_errores_lexicos_malformados.chip",
                "20_comentario_multilinea_sin_cerrar.chip",
                "21_asignaciones_tipado_fuerte.chip",
                "23_condiciones_no_bool.chip",
                "24_llamadas_funciones.chip",
                "25_returns_tipado.chip",
                "26_error_sin_cascada.chip",
                "27_recuperacion_frase_fin_sentencia.chip"
        ).map(Arguments::of);
    }
}
