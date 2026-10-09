import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Scanner {
    private byte[] input;
    private int  current;

    private static final Map<String, TokenType> keywords;
        static {
            keywords = new HashMap<>();
            keywords.put("constructor", TokenType.CONSTRUCTOR);
            keywords.put("let", TokenType.LET);
            keywords.put("class", TokenType.CLASS);
            keywords.put("function", TokenType.FUNCTION);
            keywords.put("method", TokenType.METHOD);
            keywords.put("static", TokenType.STATIC);
            keywords.put("field", TokenType.FIELD);
            keywords.put("int", TokenType.INT);
            keywords.put("char", TokenType.CHAR);
            keywords.put("boolean", TokenType.BOOLEAN);
            keywords.put("void", TokenType.VOID);
            keywords.put("true", TokenType.TRUE);
            keywords.put("false", TokenType.FALSE);
            keywords.put("null", TokenType.NULL);
            keywords.put("this", TokenType.THIS);
            keywords.put("if", TokenType.IF);
            keywords.put("do", TokenType.DO);
            keywords.put("else", TokenType.ELSE);
            keywords.put("while", TokenType.WHILE);
            keywords.put("return", TokenType.RETURN);
            keywords.put("var", TokenType.VAR);
        }

    public Scanner(byte[] input) {
        this.input = input;
    }

    private void advance(){
            char ch = peek();
            if(ch != '\0') {
                current++;
            }
    }

    private Token number() {
            int start = current;
            while(Character.isDigit(peek())) {
                advance();
            }
            String n = new String(input, start, current-start);
            return new Token (TokenType.NUMBER, n);
    }

    private boolean isAlpha(char ch) {
            return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch == '_');
    }

    private boolean isAlphaNumeric(char ch) {
            return isAlpha(ch) || Character.isDigit(ch);
    }

    private Token identifier(){
        int start = current;
        while(isAlphaNumeric(peek())) {
            advance();
        }
        String id = new String(input, start, current-start);
        TokenType type = keywords.get(id);
        if(type == null){
            type = TokenType.IDENT;
        }
        return new Token(type, id);
    }

    private void skipWhiteSpace(){
            char ch = peek();
            while(ch == ' ' || ch ==  '\r' || ch == '\t' || ch == '\n'){
                advance();
                ch = peek();
            }
    }

    private void skipLineComments(){
            while (peek() != '\n' && peekNext() != '\0') {
                advance();
            }
    }

    private void skipBlockComments(){
        advance();
        advance();
        while (true) {
            if(peek() == '\0') {
                throw  new Error("bloco de cometário aberto");
            } else if (peek() == '*' && peekNext() == '/') {
                advance();
                advance(); // advances servem para consurmirem os tokens * e /
                return;
            }
            advance();
        }
    }

    private char peek(){
        if(current < input.length) {
            return (char)input[current];
        }
        return '\0';
    }

    private char peekNext(){
            int next = current + 1;
            return next < input.length ? (char) input[next] : '\0';
    }

    private Token string() {
            advance();
            int start = current;
            while (peek() != '"' && peek() != '\0') {
                advance();
            }
            String s = new String(input, start, current-start, StandardCharsets.UTF_8);
            advance();
            return new Token (TokenType.STRING, s);
    }

    private Token slashToken() {
        if(peekNext() == '/') {
            skipLineComments();
            return nextToken();
        } else if (peekNext() == '*') {
            skipBlockComments();
            return nextToken();
        } else {
            advance();
            return new Token (TokenType.SLASH, "/");
        }
    }

    public Token nextToken() {
            skipWhiteSpace();
            char ch = peek();

            if(isAlpha(ch)) {
                return identifier();
            }

            if(ch == '0') {
                advance();
                return new Token (TokenType.NUMBER, Character.toString(ch));
            } else if (Character.isDigit(ch)) {
                return number();
            }
            switch (ch) {
                case '+': advance(); return new Token (TokenType.PLUS, "+");
                case '-': advance(); return new Token (TokenType.MINUS, "-");
                case '=': advance(); return new Token (TokenType.EQ, "=");
                case '*': advance(); return new Token (TokenType.ASTERISK, "*");
                case '|': advance(); return new Token (TokenType.OR, "|");
                case '~': advance(); return new Token (TokenType.NOT, "~");
                case '.': advance(); return new Token (TokenType.DOT, ".");
                case '&': advance(); return new Token (TokenType.AND, "&");
                case '>': advance(); return new Token (TokenType.GT, ">");
                case '<': advance(); return new Token (TokenType.LT, "<");
                case '(': advance(); return new Token (TokenType.LPAREN, "(");
                case ')': advance(); return new Token (TokenType.RPAREN, ")");
                case '[': advance(); return new Token (TokenType.LBRACKET, "[");
                case ']': advance(); return new Token (TokenType.RBRACKET, "]");
                case ';': advance(); return new Token (TokenType.SEMICOLON, ";");
                case ',': advance(); return new Token (TokenType.COMMA, ",");
                case '{': advance(); return new Token (TokenType.LBRACE, "{");
                case '}': advance(); return new Token (TokenType.RBRACE, "}");
                case '/': return slashToken(); // fiz a função separada para identificar o token "/"
                case '"': return string(); // essa função também foi feita separadamente para reconhecer strings

                case '\0': return new Token (TokenType.EOF, "EOF");
                default: throw new Error("error léxico em: " + ch);


            }

    }



}
