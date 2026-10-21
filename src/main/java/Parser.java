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


        Token tokenClass = consume(TokenType.CLASS);
        output.writeToken(tokenClass);

        Token tokenIdent = consume(TokenType.IDENT);
        output.writeToken(tokenIdent);

        Token tokenLbrace = consume(TokenType.LBRACE);
        output.writeToken(tokenLbrace);

        while(currentToken.type == TokenType.STATIC || currentToken.type == TokenType.FIELD) {
            parseClassVarDec();
        }

        Token tokenRbrace = consume(TokenType.RBRACE);
        output.writeToken(tokenRbrace);

        output.closeTag("class");
    }

    public String parse(){
        parseClass();
        consume(TokenType.EOF);
        return output.getOutput();
    }

    public void parseType(){
        TokenType tk = currentToken.type;
        switch (tk){
            case TokenType.INT -> consume(TokenType.INT);
            case TokenType.CHAR -> consume(TokenType.CHAR);
            case TokenType.BOOLEAN -> consume(TokenType.BOOLEAN);
            case TokenType.IDENT -> consume(TokenType.IDENT);
            default ->  throw new IllegalStateException("Esperado: " + tk + "; Encontrado: " + currentToken + ", linha: " + currentToken.row + ", coluna: " + currentToken.column);
        }

    }

    public void parseClassVarDec(){
        output.openTag("classVarDec");

        if (currentToken.type == TokenType.STATIC) {
            Token tokenStatic = consume(currentToken.type);
            output.writeToken(tokenStatic);
        } else {
            Token tokenField = consume(TokenType.FIELD);
            output.writeToken(tokenField);
        }
        parseType();
        consume(TokenType.IDENT);

        while(currentToken.type == TokenType.COMMA) {
            Token tokenComma = consume(TokenType.COMMA);
            output.writeToken(tokenComma);
            Token tokenIdent = consume(TokenType.IDENT);
            output.writeToken(tokenIdent);
        }
        Token tokenSemicolon = consume(TokenType.SEMICOLON);
        output.writeToken(tokenSemicolon);

        output.closeTag("classVarDec");
    }
}
