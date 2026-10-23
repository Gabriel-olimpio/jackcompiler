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
            default ->  throw new IllegalStateException("Esperado: int, char, boolean ou nome de classe; "  + "; Encontrado: " + currentToken.type + " e lexema: " + currentToken.lexeme + ", linha: " + currentToken.row + ", coluna: " + currentToken.column);
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
            throw new IllegalStateException("Categorias esperadas: METHOD, FUNCTION ou CONSTRUCTOR ");
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
        output.openTag("subRoutineBody");

        output.writeToken(consume(TokenType.LBRACE));

        output.openTag("statements");
        output.closeTag("statements");

        output.writeToken(consume(TokenType.RBRACE));

        output.closeTag("subRoutineBody");
    }

}
