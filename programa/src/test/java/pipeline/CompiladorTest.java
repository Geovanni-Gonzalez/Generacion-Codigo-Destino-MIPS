package pipeline;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

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
}
