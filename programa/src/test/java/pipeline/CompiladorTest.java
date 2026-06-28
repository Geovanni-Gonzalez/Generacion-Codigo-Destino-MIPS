package pipeline;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import intermedio.Instruccion;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
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

    @Test
    void generaCodigoIntermedioEsperado() throws Exception {
        ResultadoCompilacion resultado =
                new Compilador().compilar(Path.of("test", "04_aritmeticas.chip"));

        String ir = irComoTexto(resultado.getCodigoIntermedio());

        assertTrue(ir.contains("begin_function __main__"), ir);
        assertTrue(ir.contains("declare int x"), ir);
        assertTrue(ir.contains("declare float f"), ir);
        assertTrue(ir.contains("end_function __main__"), ir);

        // El optimizador debe plegar las constantes enteras y eliminar los temporales muertos.
        assertTrue(ir.contains("x = 20"), ir);   // 10 + 5 * 2
        assertTrue(ir.contains("y = 30"), ir);   // <|10 + 5|> * 2
        assertTrue(ir.contains("z = 2"), ir);    // 2 ^ 3 % 3
        assertFalse(ir.contains("_t0 ="), ir);   // temporal plegado y eliminado
    }

    @Test
    void generaMipsConEstructuraEsperada() throws Exception {
        ResultadoCompilacion resultado =
                new Compilador().compilar(Path.of("test", "04_aritmeticas.chip"));

        String mips = String.join("\n", resultado.getCodigoMIPS());

        assertTrue(mips.contains(".data"), mips);
        assertTrue(mips.contains(".text"), mips);
        assertTrue(mips.contains(".globl main"), mips);
        assertTrue(mips.contains("main:"), mips);
        assertTrue(mips.contains("d_main_x: .word 0"), mips);
        // Epilogo de salida del programa (syscall 10).
        assertTrue(mips.contains("li $v0, 10"), mips);
        assertTrue(mips.contains("syscall"), mips);

        // El peephole convierte los store/load redundantes en move (entero y flotante).
        assertTrue(mips.contains("move $t0, $t2"), mips);
        assertTrue(mips.contains("mov.s $f0, $f4"), mips);
        // El store/load redundante al mismo registro debe haberse eliminado.
        assertFalse(mips.contains("sw $t0, d_main_t9\n\tlw $t0, d_main_t9"), mips);
    }

    private static String irComoTexto(List<Instruccion> codigo) {
        return codigo.stream().map(Instruccion::toString).collect(Collectors.joining("\n"));
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
