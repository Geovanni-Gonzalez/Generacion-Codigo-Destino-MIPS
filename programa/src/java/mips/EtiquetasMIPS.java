package mips;

/**
 * Utilidades para construir nombres de simbolos y etiquetas MIPS de forma uniforme.
 */
final class EtiquetasMIPS {
    private EtiquetasMIPS() {
    }

    static String clave(String funcion, String nombre) {
        return (funcion == null ? "global" : funcion) + "::" + nombre;
    }

    static String etiquetaDato(String clave) {
        return "d_" + limpiar(clave.replace("::", "_"));
    }

    static String etiquetaFuncion(String nombre) {
        return "__main__".equals(nombre) ? "main" : "_fn_" + limpiar(nombre);
    }

    static String etiquetaEpilogo(String nombre) {
        return etiquetaFuncion(nombre) + "_fin";
    }

    static String etiquetaCodigo(String nombre) {
        return "_ic_" + limpiar(nombre);
    }

    static String etiquetaInterna(String prefijo, int indice) {
        return "_mips_" + prefijo + "_" + indice;
    }

    private static String limpiar(String texto) {
        return texto.replaceAll("[^A-Za-z0-9_]", "_");
    }
}
