package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FuncionNodo extends Nodo {
    private final String nombre;
    private final TipoDato tipoRetorno;
    private final List<ParametroNodo> parametros;
    private final BloqueNodo cuerpo;
    private final boolean principal;
    public FuncionNodo(int linea, int columna, String nombre, TipoDato tipoRetorno,
                       List<ParametroNodo> parametros, BloqueNodo cuerpo, boolean principal) {
        super(linea, columna, tipoRetorno);
        this.nombre = nombre;
        this.tipoRetorno = tipoRetorno;
        this.parametros = new ArrayList<>(parametros);
        this.cuerpo = cuerpo;
        this.principal = principal;
    }
    public String getNombre() {
        return nombre;
    }

    public TipoDato getTipoRetorno() {
        return tipoRetorno;
    }

    public List<ParametroNodo> getParametros() {
        return Collections.unmodifiableList(parametros);
    }

    public BloqueNodo getCuerpo() {
        return cuerpo;
    }

    public boolean isPrincipal() {
        return principal;
    }
}
