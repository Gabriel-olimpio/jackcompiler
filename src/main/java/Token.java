public class Token {
    final TokenType type;
    final String lexeme;

    public  Token (TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public boolean isSymbol (TokenType type) {
        return switch (type) {
            case RPAREN, LPAREN, LBRACE, RBRACE, LBRACKET, RBRACKET,  COMMA,
                 SEMICOLON, DOT, PLUS, MINUS, ASTERISK,
                 SLASH, AND, OR, NOT, LT, GT, EQ -> true;
            default -> false;
        };
    }


    public String toString() {
        String categoria = switch (type) {
            case NUMBER -> "integerConstant";
            case IDENT -> "identifier";
            case STRING -> "stringConstant";
            default -> isSymbol(type) ? "symbol" : "keyword";
        };
        String valor = lexeme;
        if (categoria.equals("symbol")) {
            valor = switch (valor) {
                case ">" -> "&gt;";
                case "<" -> "&lt;";
                case "\"" -> "&quot;";
                case "&" -> "&amp;";
                default -> valor;
            };
        }
        return "<" + categoria + "> " + valor + " </" + categoria + ">";
    }
}



