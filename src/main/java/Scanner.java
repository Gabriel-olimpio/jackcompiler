import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Scanner {
    private byte[] input;
    private int  current;
    private int row = 1;
    private int column = 1;
    private int tokenRow = row;
    private int tokenColumn = column;

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
            if(ch == '\0') {
                return;
            }
            if(ch == '\r') {
                row++;
                column = 1;
            } else if (ch == '\n') {
                if(current == 0 || input[current - 1]  != '\r') {
                    row++;
                }
                column = 1;
            } else {
                column++;
            }
            current++;
    }

    private Token number() {
            int start = current;
            while(Character.isDigit(peek())) {
                advance();
            }
            String n = new String(input, start, current-start);
            return createToken(TokenType.NUMBER, n);
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
        return createToken(type, id);
    }

    private void skipWhiteSpace(){
            char ch = peek();
            while(ch == ' ' || ch ==  '\r' || ch == '\t' || ch == '\n'){
                advance();
                ch = peek();
            }
    }

    private void skipLineComments(){
            while (peek() != '\n' && peek() != '\0') {
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
            return createToken(TokenType.STRING, s);
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
            return createToken(TokenType.SLASH, "/");
        }
    }

    private Token createToken( TokenType type, String lexema) {
            return new Token(type, lexema, tokenRow, tokenColumn);
    }

    public Token nextToken() {
            skipWhiteSpace();
            tokenRow = row;
            tokenColumn = column;

            char ch = peek();

            if(isAlpha(ch)) {
                return identifier();
            }

            if(ch == '0') {
                advance();
                return createToken(TokenType.NUMBER, Character.toString(ch));
            } else if (Character.isDigit(ch)) {
                return number();
            }
            switch (ch) {
                case '+': advance(); return createToken(TokenType.PLUS, "+");
                case '-': advance(); return createToken(TokenType.MINUS, "-");
                case '=': advance(); return createToken(TokenType.EQ, "=");
                case '*': advance(); return createToken(TokenType.ASTERISK, "*");
                case '|': advance(); return createToken(TokenType.OR, "|");
                case '~': advance(); return createToken(TokenType.NOT, "~");
                case '.': advance(); return createToken(TokenType.DOT, ".");
                case '&': advance(); return createToken(TokenType.AND, "&");
                case '>': advance(); return createToken(TokenType.GT, ">");
                case '<': advance(); return createToken (TokenType.LT, "<");
                case '(': advance(); return createToken(TokenType.LPAREN, "(");
                case ')': advance(); return createToken(TokenType.RPAREN, ")");
                case '[': advance(); return createToken(TokenType.LBRACKET, "[");
                case ']': advance(); return createToken(TokenType.RBRACKET, "]");
                case ';': advance(); return createToken(TokenType.SEMICOLON, ";");
                case ',': advance(); return createToken(TokenType.COMMA, ",");
                case '{': advance(); return createToken(TokenType.LBRACE, "{");
                case '}': advance(); return createToken(TokenType.RBRACE, "}");
                case '/': return slashToken(); // fiz a função separada para identificar o token "/"
                case '"': return string(); // essa função também foi feita separadamente para reconhecer strings

                case '\0': return createToken(TokenType.EOF, "EOF");
                default: throw new Error("error léxico em: " + ch);


            }

    }



}
