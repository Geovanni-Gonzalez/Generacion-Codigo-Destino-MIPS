package mips;

import java.util.Map;

final class TraductorMemoriaMIPS {
    private final EmisorMIPS salida;
    private final AdministradorRegistros registros;
    private final Map<String, String> tipos;
    private final Map<String, String> direcciones;
    private final Map<String, Integer> columnasArreglo;
    private final Map<String, String> cadenas;
    private final Map<String, String> flotantes;

    TraductorMemoriaMIPS(EmisorMIPS salida, AdministradorRegistros registros,
                         Map<String, String> tipos, Map<String, String> direcciones,
                         Map<String, Integer> columnasArreglo, Map<String, String> cadenas,
                         Map<String, String> flotantes) {
        this.salida = salida;
        this.registros = registros;
        this.tipos = tipos;
        this.direcciones = direcciones;
        this.columnasArreglo = columnasArreglo;
        this.cadenas = cadenas;
        this.flotantes = flotantes;
    }

    String tipoOperando(String operando, String funcion) {
        if (operando == null) {
            return "int";
        }
        if (OperandosMIPS.esCadena(operando)) {
            return "string";
        }
        if (OperandosMIPS.esChar(operando)) {
            return "char";
        }
        if ("true".equals(operando) || "false".equals(operando)) {
            return "bool";
        }
        if (OperandosMIPS.esFloatLiteral(operando)) {
            return "float";
        }
        if (OperandosMIPS.esEnteroLiteral(operando)) {
            return "int";
        }
        String base = OperandosMIPS.esAccesoArreglo(operando)
                ? operando.substring(0, operando.indexOf('[')) : operando;
        return tipos.getOrDefault(EtiquetasMIPS.clave(funcion, base), "int");
    }

    String cargarValor(String operando, String funcion) {
        String registro = registros.obtenerRegistro();
        cargarEntero(operando, registro, funcion);
        return registro;
    }

    void cargarEntero(String operando, String registro, String funcion) {
        if (operando == null) {
            salida.instruccion("move " + registro + ", $zero");
        } else if (OperandosMIPS.esCadena(operando)) {
            salida.instruccion("la " + registro + ", " + cadenas.get(operando));
        } else if (OperandosMIPS.esChar(operando)) {
            salida.instruccion("li " + registro + ", " + (int) OperandosMIPS.valorChar(operando));
        } else if ("true".equals(operando) || "false".equals(operando)) {
            salida.instruccion("li " + registro + ", " + ("true".equals(operando) ? 1 : 0));
        } else if (OperandosMIPS.esEnteroLiteral(operando)) {
            salida.instruccion("li " + registro + ", " + OperandosMIPS.valorEntero(operando));
        } else if (OperandosMIPS.esAccesoArreglo(operando)) {
            direccionArreglo(operando, RegistrosMIPS.SCRATCH_DIRECCION, funcion);
            salida.instruccion("lw " + registro + ", 0(" + RegistrosMIPS.SCRATCH_DIRECCION + ")");
        } else {
            salida.instruccion("lw " + registro + ", " + etiqueta(operando, funcion));
        }
    }

    void cargarFloat(String operando, String registro, String funcion) {
        if (OperandosMIPS.esFloatLiteral(operando)) {
            salida.instruccion("l.s " + registro + ", " + flotantes.get(operando));
        } else if (OperandosMIPS.esEnteroLiteral(operando)) {
            cargarEntero(operando, RegistrosMIPS.SCRATCH_ENTERO_A, funcion);
            salida.instruccion("mtc1 " + RegistrosMIPS.SCRATCH_ENTERO_A + ", " + registro);
            salida.instruccion("cvt.s.w " + registro + ", " + registro);
        } else if (OperandosMIPS.esAccesoArreglo(operando)) {
            direccionArreglo(operando, RegistrosMIPS.SCRATCH_DIRECCION, funcion);
            salida.instruccion("l.s " + registro + ", 0(" + RegistrosMIPS.SCRATCH_DIRECCION + ")");
        } else {
            salida.instruccion("l.s " + registro + ", " + etiqueta(operando, funcion));
        }
    }

    void guardar(String destino, String registroEntero, String registroFloat, String funcion) {
        if (OperandosMIPS.esAccesoArreglo(destino)) {
            direccionArreglo(destino, RegistrosMIPS.SCRATCH_DIRECCION, funcion);
            salida.instruccion((OperandosMIPS.esFloat(tipoOperando(destino, funcion))
                    ? "s.s " + registroFloat : "sw " + registroEntero)
                    + ", 0(" + RegistrosMIPS.SCRATCH_DIRECCION + ")");
        } else if (OperandosMIPS.esFloat(tipoOperando(destino, funcion))) {
            salida.instruccion("s.s " + registroFloat + ", " + etiqueta(destino, funcion));
        } else {
            salida.instruccion("sw " + registroEntero + ", " + etiqueta(destino, funcion));
        }
    }

    String etiqueta(String operando, String funcion) {
        String base = OperandosMIPS.esAccesoArreglo(operando)
                ? operando.substring(0, operando.indexOf('[')) : operando;
        return direccionDato(EtiquetasMIPS.clave(funcion, base));
    }

    void direccionArreglo(String acceso, String registroDireccion, String funcion) {
        int primero = acceso.indexOf('[');
        String nombre = acceso.substring(0, primero);
        int cierreFila = acceso.indexOf(']', primero);
        int inicioColumna = acceso.indexOf('[', cierreFila);
        int cierreColumna = acceso.indexOf(']', inicioColumna);
        String fila = acceso.substring(primero + 1, cierreFila);
        String columna = acceso.substring(inicioColumna + 1, cierreColumna);

        String fil = RegistrosMIPS.SCRATCH_INDICE_FILA;
        String col = RegistrosMIPS.SCRATCH_INDICE_COL;
        String aux = RegistrosMIPS.SCRATCH_ENTERO_A;
        cargarEntero(fila, fil, funcion);
        cargarEntero(columna, col, funcion);
        int columnas = columnasArreglo.getOrDefault(EtiquetasMIPS.clave(funcion, nombre), 1);
        salida.instruccion("li " + aux + ", " + columnas);
        salida.instruccion("mul " + fil + ", " + fil + ", " + aux);
        salida.instruccion("add " + fil + ", " + fil + ", " + col);
        salida.instruccion("sll " + fil + ", " + fil + ", 2");
        salida.instruccion("la " + registroDireccion + ", " + etiqueta(nombre, funcion));
        salida.instruccion("add " + registroDireccion + ", " + registroDireccion + ", " + fil);
    }

    private String direccionDato(String clave) {
        String direccion = direcciones.get(clave);
        if (direccion == null) {
            throw new IllegalStateException("No se reservo memoria MIPS para " + clave);
        }
        return direccion;
    }
}
