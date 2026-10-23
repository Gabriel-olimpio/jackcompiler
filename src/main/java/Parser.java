public class Parser {
    private Scanner scanner;
    private Token currentToken;
    private XmlWriter output = new XmlWriter();


    public Parser(byte[] input) {
        scanner = new Scanner(input);
        currentToken = scanner.nextToken();
    }

    public Token peek(){
        return currentToken;
    }

    public Token advance(){
        Token tk = currentToken;
        if (tk.type != TokenType.EOF ) {
           currentToken = scanner.nextToken();
        }
        return tk;
    }

    public boolean match(TokenType typePretend){
        if(currentToken.type == typePretend) {
            advance();
            return true;
        }
        return false;
    }

    public Token consume(TokenType typePretend) {
        if(currentToken.type == typePretend) {
            return advance();
        } else {
            throw new IllegalStateException("Esperado: " + typePretend + "; Encontrado: "
                    + currentToken.type + ", lexema: "
                    + currentToken.lexeme + ", linha: " + currentToken.row
                    + ", coluna: " + currentToken.column);
        }
    }

    private boolean isOp(TokenType type) {
        if(type == TokenType.PLUS || type == TokenType.MINUS
                || type == TokenType.SLASH || type == TokenType.ASTERISK
                || type == TokenType.AND || type == TokenType.OR
                || type == TokenType.LT || type == TokenType.GT
                || type == TokenType.EQ) {
            return true;
        }
        return false;
    }

    private void parseClass(){
        output.openTag("class");

        output.writeToken(consume(TokenType.CLASS));

        output.writeToken(consume(TokenType.IDENT));

        output.writeToken(consume(TokenType.LBRACE));

        while(currentToken.type == TokenType.STATIC || currentToken.type == TokenType.FIELD) {
            parseClassVarDec();
        }

        while(currentToken.type == TokenType.CONSTRUCTOR || currentToken.type == TokenType.FUNCTION || currentToken.type == TokenType.METHOD) {
            parseSubroutineDec();
        }

        output.writeToken(consume(TokenType.RBRACE));

        output.closeTag("class");
    }

    public String parse(){
        parseClass();
        consume(TokenType.EOF);
        return output.getOutput();
    }

    private void parseType(){
        TokenType tk = currentToken.type;
        switch (tk){
            case TokenType.INT -> output.writeToken(consume(TokenType.INT));
            case TokenType.CHAR -> output.writeToken(consume(TokenType.CHAR));
            case TokenType.BOOLEAN -> output.writeToken(consume(TokenType.BOOLEAN));
            case TokenType.IDENT -> output.writeToken(consume(TokenType.IDENT));
            default ->  throw new IllegalStateException("Esperado: int, char, boolean ou nome de classe; "
                    + "; Encontrado: "
                    + currentToken.type
                    + " e lexema: " + currentToken.lexeme
                    + ", linha: " + currentToken.row
                    + ", coluna: "
                    + currentToken.column);
        }

    }

    private void parseClassVarDec(){
        output.openTag("classVarDec");

        if (currentToken.type == TokenType.STATIC) {
            output.writeToken(consume(TokenType.STATIC));
        } else {
            output.writeToken(consume(TokenType.FIELD));
        }

        parseType();
        output.writeToken(consume(TokenType.IDENT));

        while(currentToken.type == TokenType.COMMA) {
            output.writeToken(consume(TokenType.COMMA));
            output.writeToken(consume(TokenType.IDENT));
        }
        output.writeToken(consume(TokenType.SEMICOLON));

        output.closeTag("classVarDec");
    }

    private void parseParameterList(){
        output.openTag("parameterList");

        if(currentToken.type != TokenType.RPAREN) {
            parseType();
            output.writeToken(consume(TokenType.IDENT));

            while(currentToken.type == TokenType.COMMA) {
                output.writeToken(consume(TokenType.COMMA));
                parseType();
                output.writeToken(consume(TokenType.IDENT));
            }
        }

        output.closeTag("parameterList");

    }

    private void parseSubroutineDec(){
        output.openTag("subroutineDec");

        if(currentToken.type == TokenType.CONSTRUCTOR || currentToken.type == TokenType.METHOD || currentToken.type == TokenType.FUNCTION){
            output.writeToken(consume(currentToken.type));
        } else {
            throw new IllegalStateException("Esperado: CONSTRUCTOR, METHOD OU FUNCTION; " + "Encotrado: " + currentToken.type + ", lexema: " + currentToken.lexeme
            + ", linha: " + currentToken.row + ", coluna: " + currentToken.column);
        }
        if(currentToken.type == TokenType.VOID){
            output.writeToken(consume(TokenType.VOID));
        } else {
            parseType();
        }

        output.writeToken(consume(TokenType.IDENT));
        output.writeToken(consume(TokenType.LPAREN));
        parseParameterList();
        output.writeToken(consume(TokenType.RPAREN));

        parseSubroutineBody();

        output.closeTag("subroutineDec");
    }

    private void parseSubroutineBody(){
        output.openTag("subroutineBody");

        output.writeToken(consume(TokenType.LBRACE));

        while(currentToken.type == TokenType.VAR) {
            parseVarDec();
        }

        parseStatements(); // abertura dos <statements>


        output.writeToken(consume(TokenType.RBRACE));

        output.closeTag("subroutineBody");
    }

    private void parseVarDec() {
        output.openTag("varDec");

        output.writeToken(consume(TokenType.VAR));

        parseType();

        output.writeToken(consume(TokenType.IDENT));

        while(currentToken.type == TokenType.COMMA) {
            output.writeToken(consume(TokenType.COMMA));
            output.writeToken(consume(TokenType.IDENT));
        }

        output.writeToken(consume(TokenType.SEMICOLON));

        output.closeTag("varDec");

    }

    private void parseReturn() {
        output.openTag("returnStatement");

        output.writeToken(consume(TokenType.RETURN));

        if(currentToken.type != TokenType.SEMICOLON){
            parseExpression();
        }
        output.writeToken(consume(TokenType.SEMICOLON));

        output.closeTag("returnStatement");
    }

    private void parseStatements(){
        output.openTag("statements");

        while(currentToken.type == TokenType.RETURN || currentToken.type == TokenType.LET ) {
            if(currentToken.type == TokenType.RETURN){
                parseReturn();
            } else {
                parseLet();
            }

        }

        output.closeTag("statements");
    }

    private void parseExpression() {
        output.openTag("expression");

        parseTerm();

        while(isOp(currentToken.type)) {
            output.writeToken(consume(currentToken.type));
            parseTerm();
        }

        output.closeTag("expression");
    }

    private void caseLparen(){
        output.writeToken(consume(TokenType.LPAREN));
        parseExpression();
        output.writeToken(consume(TokenType.RPAREN));
    }

    private void caseMinusOrNot(TokenType type){
        output.writeToken(consume(type));
        parseTerm();
    }

    private void parseTerm(){
        output.openTag("term");

        TokenType type = currentToken.type;
        switch (type) {
            case TokenType.NUMBER -> output.writeToken(consume(TokenType.NUMBER));
            case TokenType.STRING -> output.writeToken(consume(TokenType.STRING));
            case TokenType.IDENT -> output.writeToken(consume(TokenType.IDENT));
            case TokenType.TRUE -> output.writeToken(consume(TokenType.TRUE));
            case TokenType.FALSE -> output.writeToken(consume(TokenType.FALSE));
            case TokenType.NULL -> output.writeToken(consume(TokenType.NULL));
            case TokenType.THIS -> output.writeToken(consume(TokenType.THIS));
            case TokenType.LPAREN -> caseLparen();
            case TokenType.NOT, TokenType.MINUS -> caseMinusOrNot(type);

            default -> throw new IllegalStateException("Esperado: NUMBER, STRING, IDENT, TRUE, FALSE, NULL, THIS, LPAREN, MINUS ou NOT; "
                    + "; Encontrado: " + currentToken.type
                    + " e lexema: " + currentToken.lexeme
                    + ", linha: " + currentToken.row
                    + ", coluna: " + currentToken.column);

        }
        output.closeTag("term");
    }

    private void parseLet(){
        output.openTag("letStatement");

        output.writeToken(consume(TokenType.LET));
        output.writeToken(consume(TokenType.IDENT));

        if(currentToken.type == TokenType.LBRACKET) {
            output.writeToken(consume(TokenType.LBRACKET));
            parseExpression();
            output.writeToken(consume(TokenType.RBRACKET));
        }

        output.writeToken(consume(TokenType.EQ));
        parseExpression();
        output.writeToken(consume(TokenType.SEMICOLON));

        output.closeTag("letStatement");
    }
}
