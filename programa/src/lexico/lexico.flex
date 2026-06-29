package lexico;

import java_cup.runtime.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import reporte.ReportadorErrores;
import reporte.TokenInfo;
import sintactico.sym;

%%

%class MiLexer
%public
%unicode
%cup
%line
%column
%state COMMENT

%{
private final List<TokenInfo> tokens = new ArrayList<>();
private final List<String> erroresLexicos = new ArrayList<>();
private boolean imprimirErrores = true;
private boolean comentarioSinCerrarReportado = false;

private void reportarComentarioSinCerrar() {
    if (comentarioSinCerrarReportado) {
        return;
    }
    comentarioSinCerrarReportado = true;
    errorLexico("comentario multilinea sin cerrar");
}

public List<TokenInfo> getTokens() {
    return Collections.unmodifiableList(tokens);
}

public List<String> getErroresLexicos() {
    return Collections.unmodifiableList(erroresLexicos);
}

public void setImprimirErrores(boolean imprimirErrores) {
    this.imprimirErrores = imprimirErrores;
}

private Symbol symbol(int type) {
    return symbol(type, yytext());
}

private Symbol symbol(int type, Object value) {
    int linea = yyline + 1;
    int columna = yycolumn + 1;
    String lexema = yytext();
    tokens.add(new TokenInfo(type, nombreToken(type), lexema, linea, columna,
                             tablaPara(type), informacionPara(type, lexema, value)));
    return new Symbol(type, linea, columna, value);
}

private void errorLexico() {
    errorLexico("simbolo no reconocido");
}

private void errorLexico(String descripcion) {
    String lexema = yytext()
            .replace("\r", "\\r")
            .replace("\n", "\\n")
            .replace("\t", "\\t");
    String error = ReportadorErrores.lexico(yyline + 1, yycolumn + 1,
            descripcion + " '" + lexema + "'");
    erroresLexicos.add(error);
    if (imprimirErrores) {
        System.err.println(error);
    }
}

private String nombreToken(int type) {
    try {
        for (java.lang.reflect.Field field : sym.class.getFields()) {
            if (field.getType() == int.class && field.getInt(null) == type) {
                return field.getName();
            }
        }
    } catch (IllegalAccessException ignored) {
    }
    return "TOKEN_" + type;
}

private String tablaPara(int type) {
    switch (type) {
        case sym.ID:
            return "Tabla de identificadores";
        case sym.LIT_ENTERO:
        case sym.LIT_FLOTANTE:
        case sym.LIT_FRACCION:
        case sym.LIT_EXPONENTE:
        case sym.LIT_CHAR:
        case sym.LIT_STRING:
        case sym.TRUE:
        case sym.FALSE:
            return "Tabla de literales/constantes";
        default:
            return "Tabla de palabras reservadas y simbolos";
    }
}

private String informacionPara(int type, String lexema, Object value) {
    switch (type) {
        case sym.ID:
            return "lexema=" + lexema + ", clase=identificador";
        case sym.LIT_ENTERO:
        case sym.LIT_FLOTANTE:
        case sym.LIT_FRACCION:
        case sym.LIT_EXPONENTE:
        case sym.LIT_CHAR:
        case sym.LIT_STRING:
        case sym.TRUE:
        case sym.FALSE:
            return "lexema=" + lexema + ", valor=" + value;
        default:
            return "lexema=" + lexema;
    }
}
%}

%eofval{
    if (yystate() == COMMENT) {
        reportarComentarioSinCerrar();
    }
    return new Symbol(sym.EOF, yyline + 1, yycolumn + 1, "<EOF>");
%eofval}

/* DEFINICIONES */
DIGITO = [0-9]
DIGITO_POS = [1-9]
LETRA = [a-zA-Z]
ID = ({LETRA} | "_") ({LETRA} | {DIGITO} | "_")*
ENTERO_POS = ({DIGITO_POS}{DIGITO}* | "0")
NATURAL_POS = {DIGITO_POS}{DIGITO}*
ENTERO = {ENTERO_POS}
FLOTANTE = {ENTERO}"."{DIGITO}+
FRACCION = {ENTERO}"/"{NATURAL_POS}
EXPONENTE = {ENTERO}"e"{NATURAL_POS}
FRACCION_DENOMINADOR_CERO = {ENTERO}"/""0"+
EXPONENTE_CERO = {ENTERO}"e""0"+
CHAR = [^\r\n']
STRING = [^\r\n\"]*

%%

/* REGLAS */

"int"           { return symbol(sym.INT); }
"entero"        { return symbol(sym.INT); }
"float"         { return symbol(sym.FLOAT); }
"flotante"      { return symbol(sym.FLOAT); }
"bool"          { return symbol(sym.BOOL); }
"booleano"      { return symbol(sym.BOOL); }
"char"          { return symbol(sym.CHAR_TYPE); }
"string"        { return symbol(sym.STRING_TYPE); }

"void"          { return symbol(sym.VOID); }
"empty"         { return symbol(sym.EMPTY); }
"__main__"      { return symbol(sym.MAIN); }
"class"         { return symbol(sym.CLASS); }
"new"           { return symbol(sym.NEW); }
"this"          { return symbol(sym.THIS); }
"return"        { return symbol(sym.RETURN); }
"break"         { return symbol(sym.BREAK); }
"if"            { return symbol(sym.IF); }
"else"          { return symbol(sym.ELSE); }
"do"            { return symbol(sym.DO); }
"while"         { return symbol(sym.WHILE); }
"switch"        { return symbol(sym.SWITCH); }
"case"          { return symbol(sym.CASE); }
"default"       { return symbol(sym.DEFAULT); }
"cin"           { return symbol(sym.CIN); }
"cout"          { return symbol(sym.COUT); }

"true"          { return symbol(sym.TRUE, true); }
"false"         { return symbol(sym.FALSE, false); }

"equal"         { return symbol(sym.EQUAL); }
"n_equal"       { return symbol(sym.N_EQUAL); }
"less_t"        { return symbol(sym.LESS_T); }
"less_te"       { return symbol(sym.LESS_TE); }
"greather_t"    { return symbol(sym.GREATHER_T); }
"greather_te"   { return symbol(sym.GREATHER_TE); }

"++"            { return symbol(sym.INC); }
"--"            { return symbol(sym.DEC); }
"+"             { return symbol(sym.PLUS); }
"-"             { return symbol(sym.MINUS); }
"*"             { return symbol(sym.TIMES); }
"//"            { return symbol(sym.DIV); }
"/"             { return symbol(sym.DIV); }
"%"             { return symbol(sym.MOD); }
"^"             { return symbol(sym.POW); }

"@"             { return symbol(sym.AND_LOG); }
"#"             { return symbol(sym.OR_LOG); }
"$"             { return symbol(sym.NOT_LOG); }

"<-"            { return symbol(sym.ASSIGN); }

"<|"            { return symbol(sym.L_PAREN); }
"|>"            { return symbol(sym.R_PAREN); }

"|:"            { return symbol(sym.L_BLOCK); }
":|"            { return symbol(sym.R_BLOCK); }

"<<"            { return symbol(sym.L_ARRAY); }
">>"            { return symbol(sym.R_ARRAY); }

"~"             { return symbol(sym.SEPARATOR); }
","             { return symbol(sym.COMMA); }
":"             { return symbol(sym.COLON); }
"."             { return symbol(sym.DOT); }
"!"             { return symbol(sym.END_EXPR); }

{EXPONENTE_CERO} { errorLexico("literal exponencial con exponente no positivo"); }
{FRACCION_DENOMINADOR_CERO} { errorLexico("literal fraccionario con denominador cero"); }
{EXPONENTE}     { return symbol(sym.LIT_EXPONENTE, yytext()); }
{FRACCION}      { return symbol(sym.LIT_FRACCION, yytext()); }
{FLOTANTE}      { return symbol(sym.LIT_FLOTANTE, Double.parseDouble(yytext())); }
{ENTERO}        { return symbol(sym.LIT_ENTERO, Integer.parseInt(yytext())); }
"''"            { errorLexico("literal char vacio"); }
"'"{CHAR}{CHAR}+"'" { errorLexico("literal char mal formado"); }
"'"{CHAR}*      { errorLexico("literal char sin cerrar"); }
"'"{CHAR}"'"     { return symbol(sym.LIT_CHAR, yytext().charAt(1)); }
\"{STRING}\"    { return symbol(sym.LIT_STRING, yytext()); }
\"{STRING}(\r|\n) { errorLexico("cadena sin cerrar"); }

{ID}            { return symbol(sym.ID, yytext()); }
[ \t\r\n]+      { /* ignorar */ }
/* Comentarios de línea */
\u00A1\u00A1.* { /* ignorar */ }
\u00C2\u00A1\u00C2\u00A1.* { /* ignorar */ }
\u0104\u0104.* { /* ignorar */ }

/* Comentarios multilínea */
"{-" {
    yybegin(COMMENT);
}

<COMMENT> "-}" {
    yybegin(YYINITIAL);
}

/* consumir cualquier cosa excepto el cierre */
<COMMENT> [^-]+ { }

<COMMENT> "-" { }

<COMMENT> \r|\n { }

<COMMENT><<EOF>> {
    reportarComentarioSinCerrar();
    return new Symbol(sym.EOF, yyline + 1, yycolumn + 1, "<EOF>");
}


.               { errorLexico(); }
