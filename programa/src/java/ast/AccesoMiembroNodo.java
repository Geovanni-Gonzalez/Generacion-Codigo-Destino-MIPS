package ast;

public class AccesoMiembroNodo extends ExpresionNodo {
    private final ExpresionNodo objeto;
    private final String nombreCampo;

    public AccesoMiembroNodo(int linea, int columna, ExpresionNodo objeto, String nombreCampo) {
        super(linea, columna);
        this.objeto = objeto;
        this.nombreCampo = nombreCampo;
    }

    public ExpresionNodo getObjeto() {
        return objeto;
    }

    public String getNombreCampo() {
        return nombreCampo;
    }
}
