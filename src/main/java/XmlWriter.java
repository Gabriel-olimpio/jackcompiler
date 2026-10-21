public class XmlWriter {
    private StringBuilder output = new StringBuilder();

    public XmlWriter(){}

    public void openTag(String nome){
        output.append("<"+ nome +">" +'\n');
    }

    public void closeTag(String nome){
        output.append("</"+ nome + ">" + '\n');
    }

    public void writeToken(Token token){
        String categoria = switch (token.type) {
            case NUMBER -> "integerConstant";
            case IDENT -> "identifier";
            case STRING -> "stringConstant";
            default -> Token.isSymbol(token.type) ? "symbol" : "keyword";
        };
        String  valor = token.lexeme;
        String[] alvos = {"&","<",">", "\""};
        String[] subst = {"&amp;","&lt;","&gt;", "&quot;"};
        for(int i = 0; i < alvos.length; i++){
            valor = valor.replace(alvos[i], subst[i]);
        }
        output.append("<" + categoria + "> " + valor + " </" + categoria + ">" + '\n');
    }

    public String getOutput(){
        return output.toString();
    }
}
