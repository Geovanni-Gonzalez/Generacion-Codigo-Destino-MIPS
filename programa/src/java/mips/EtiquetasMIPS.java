package mips;

final class EtiquetasMIPS {
    private EtiquetasMIPS() {
    }

    static String clave(String funcion, String nombre) {
        return (funcion == null ? "global" : funcion) + "::" + nombre;
    }

    static String etiquetaDato(String clave) {
        return colapsar("d_" + limpiar(clave.replace("::", "_")));
    }

    static String etiquetaFuncion(String nombre) {
        return "__main__".equals(nombre) ? "main" : colapsar("_fn_" + limpiar(nombre));
    }

    static String etiquetaEpilogo(String nombre) {
        return colapsar(etiquetaFuncion(nombre) + "_fin");
    }

    static String etiquetaCodigo(String nombre) {
        return colapsar("_ic_" + limpiar(nombre));
    }

    static String etiquetaInterna(String prefijo, int indice) {
        return colapsar("_mips_" + prefijo + "_" + indice);
    }

    private static String limpiar(String texto) {
        return texto.replaceAll("[^A-Za-z0-9_]", "_");
    }

    private static String colapsar(String texto) {
        return texto.replaceAll("_+", "_");
    }
}
