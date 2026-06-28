package mips;

/**
 * Nombre: EtiquetasMIPS
 *
 * Objetivo: Analizar, traducir, emitir u optimizar codigo destino MIPS.
 *
 * Entrada: Dependencias, datos o estructuras recibidas por sus constructores y metodos.
 *
 * Salida: Estado, datos o artefactos producidos por la clase.
 *
 * Restricciones: Debe respetar el contrato del paquete y las validaciones de sus metodos.
 */
final class EtiquetasMIPS {
    /**
     * Nombre: EtiquetasMIPS
     *
     * Objetivo: Inicializar una instancia de EtiquetasMIPS con los datos requeridos.
     *
     * Entrada: Ninguna.
     *
     * Salida: Nueva instancia de EtiquetasMIPS.
     *
     * Restricciones: Uso interno de la clase.
     */
    private EtiquetasMIPS() {
    }

    /**
     * Nombre: clave
     *
     * Objetivo: Ejecutar la operacion clave definida por EtiquetasMIPS.
     *
     * Entrada: String funcion; String nombre.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    static String clave(String funcion, String nombre) {
        return (funcion == null ? "global" : funcion) + "::" + nombre;
    }

    /**
     * Nombre: etiquetaDato
     *
     * Objetivo: Construir o devolver una etiqueta valida para codigo MIPS.
     *
     * Entrada: String clave.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    static String etiquetaDato(String clave) {
        return colapsar("d_" + limpiar(clave.replace("::", "_")));
    }

    /**
     * Nombre: etiquetaFuncion
     *
     * Objetivo: Construir o devolver una etiqueta valida para codigo MIPS.
     *
     * Entrada: String nombre.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    static String etiquetaFuncion(String nombre) {
        return "__main__".equals(nombre) ? "main" : colapsar("_fn_" + limpiar(nombre));
    }

    /**
     * Nombre: etiquetaEpilogo
     *
     * Objetivo: Construir o devolver una etiqueta valida para codigo MIPS.
     *
     * Entrada: String nombre.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    static String etiquetaEpilogo(String nombre) {
        return colapsar(etiquetaFuncion(nombre) + "_fin");
    }

    /**
     * Nombre: etiquetaCodigo
     *
     * Objetivo: Construir o devolver una etiqueta valida para codigo MIPS.
     *
     * Entrada: String nombre.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    static String etiquetaCodigo(String nombre) {
        return colapsar("_ic_" + limpiar(nombre));
    }

    /**
     * Nombre: etiquetaInterna
     *
     * Objetivo: Construir o devolver una etiqueta valida para codigo MIPS.
     *
     * Entrada: String prefijo; int indice.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Ninguna.
     */
    static String etiquetaInterna(String prefijo, int indice) {
        return colapsar("_mips_" + prefijo + "_" + indice);
    }

    /**
     * Nombre: limpiar
     *
     * Objetivo: Normalizar texto o limpiar estado interno.
     *
     * Entrada: String texto.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private static String limpiar(String texto) {
        return texto.replaceAll("[^A-Za-z0-9_]", "_");
    }

    /**
     * Nombre: colapsar
     *
     * Objetivo: Ejecutar la operacion colapsar definida por EtiquetasMIPS.
     *
     * Entrada: String texto.
     *
     * Salida: Valor de tipo String.
     *
     * Restricciones: Uso interno de la clase.
     */
    private static String colapsar(String texto) {
        return texto.replaceAll("_+", "_");
    }
}
