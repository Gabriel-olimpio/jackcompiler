public class CompilationEngine {
    private Scanner scanner;
    private Token currentToken;
    private XmlWriter output = new XmlWriter();
    private SymbolTable symbolTable = new SymbolTable();
    private VMWriter vm = new VMWriter();
    private boolean generatingVm;
    private String className;
    private String subroutineName;
    private TokenType subroutineKind;
    private String returnType;
    private int labelCount;


    public CompilationEngine(byte[] input) {
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

        Token classToken = consume(TokenType.IDENT);
        className = classToken.lexeme;
        output.writeToken(classToken);

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

    public String compile(){
        generatingVm = true;
        parseClass();
        consume(TokenType.EOF);
        return vm.getOutput();
    }

    private String parseType(){
        TokenType tk = currentToken.type;
        switch (tk){
            case TokenType.INT -> {
                Token intToken = consume(TokenType.INT);
                output.writeToken(intToken);
                return intToken.lexeme;
            }
            case TokenType.CHAR -> {
                Token charToken = consume(TokenType.CHAR);
                output.writeToken(charToken);
                return charToken.lexeme;
            }
            case TokenType.BOOLEAN -> {
                Token boolToken = consume(TokenType.BOOLEAN);
                output.writeToken(boolToken);
                return boolToken.lexeme;
            }
            case TokenType.IDENT -> {
                Token identToken = consume(TokenType.IDENT);
                output.writeToken(identToken);
                return identToken.lexeme;
            }
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
        SymbolKind category;
        if (currentToken.type == TokenType.STATIC) {
            category = SymbolKind.STATIC;
            output.writeToken(consume(TokenType.STATIC));
        } else {
            category = SymbolKind.FIELD;
            output.writeToken(consume(TokenType.FIELD));
        }

        String tipoDeclarado = parseType();
        Token identToken = consume(TokenType.IDENT);
        output.writeToken(identToken);
        symbolTable.define(identToken.lexeme, tipoDeclarado, category);

        while(currentToken.type == TokenType.COMMA) {
            output.writeToken(consume(TokenType.COMMA));
            identToken = consume(TokenType.IDENT);
            output.writeToken(identToken);
            symbolTable.define(identToken.lexeme, tipoDeclarado, category);
        }
        output.writeToken(consume(TokenType.SEMICOLON));

        output.closeTag("classVarDec");
    }

    private void parseParameterList(){
        output.openTag("parameterList");

        if(currentToken.type != TokenType.RPAREN) {
            String tipoDeclarado = parseType();
            Token identToken = consume(TokenType.IDENT);
            output.writeToken(identToken);
            symbolTable.define(identToken.lexeme, tipoDeclarado, SymbolKind.ARG);

            while(currentToken.type == TokenType.COMMA) {
                output.writeToken(consume(TokenType.COMMA));
                tipoDeclarado = parseType();
                identToken = consume(TokenType.IDENT);
                output.writeToken(identToken);
                symbolTable.define(identToken.lexeme, tipoDeclarado, SymbolKind.ARG);
            }
        }

        output.closeTag("parameterList");

    }

    private void parseSubroutineDec(){
        output.openTag("subroutineDec");
        symbolTable.startSubroutine();
        labelCount = 0;
        if(currentToken.type == TokenType.CONSTRUCTOR || currentToken.type == TokenType.METHOD || currentToken.type == TokenType.FUNCTION){
            subroutineKind = currentToken.type;
            output.writeToken(consume(currentToken.type));
        } else {
            throw new IllegalStateException("Esperado: CONSTRUCTOR, METHOD OU FUNCTION; " + "Encotrado: " + currentToken.type + ", lexema: " + currentToken.lexeme
            + ", linha: " + currentToken.row + ", coluna: " + currentToken.column);
        }
        if(currentToken.type == TokenType.VOID){
            returnType = currentToken.lexeme;
            output.writeToken(consume(TokenType.VOID));
        } else {
            returnType = parseType();
        }

        Token name = consume(TokenType.IDENT);
        subroutineName = className + "." + name.lexeme;
        output.writeToken(name);
        if (generatingVm && subroutineKind == TokenType.METHOD) {
            symbolTable.define("this", className, SymbolKind.ARG);
        }
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

        if (generatingVm) {
            vm.writeFunction(subroutineName, symbolTable.varCount(SymbolKind.VAR));
            if (subroutineKind == TokenType.CONSTRUCTOR) {
                vm.writePush(Segment.CONSTANT, symbolTable.varCount(SymbolKind.FIELD));
                vm.writeCall("Memory.alloc", 1);
                vm.writePop(Segment.POINTER, 0);
            } else if (subroutineKind == TokenType.METHOD) {
                vm.writePush(Segment.ARGUMENT, 0);
                vm.writePop(Segment.POINTER, 0);
            }
        }

        parseStatements(); // abertura dos <statements>


        output.writeToken(consume(TokenType.RBRACE));

        output.closeTag("subroutineBody");
    }

    private void parseVarDec() {
        output.openTag("varDec");

        output.writeToken(consume(TokenType.VAR));

        String tipoDeclarado = parseType();

        Token identToken = consume(TokenType.IDENT);
        output.writeToken(identToken);
        symbolTable.define(identToken.lexeme, tipoDeclarado, SymbolKind.VAR);

        while(currentToken.type == TokenType.COMMA) {
            output.writeToken(consume(TokenType.COMMA));
            identToken = consume(TokenType.IDENT);
            output.writeToken(identToken);
            symbolTable.define(identToken.lexeme, tipoDeclarado, SymbolKind.VAR);
        }

        output.writeToken(consume(TokenType.SEMICOLON));

        output.closeTag("varDec");

    }

    private void parseReturn() {
        output.openTag("returnStatement");

        Token keyword = consume(TokenType.RETURN);
        output.writeToken(keyword);

        boolean hasValue = currentToken.type != TokenType.SEMICOLON;
        if (generatingVm && hasValue == returnType.equals("void")) {
            throw error(keyword, "Retorno incompatível com o tipo " + returnType);
        }
        if(hasValue){
            parseExpression();
        } else if (generatingVm) {
            vm.writePush(Segment.CONSTANT, 0);
        }
        output.writeToken(consume(TokenType.SEMICOLON));
        if (generatingVm) {
            vm.writeReturn();
        }

        output.closeTag("returnStatement");
    }

    private void parseStatements(){
        output.openTag("statements");
        while(currentToken.type == TokenType.RETURN
                || currentToken.type == TokenType.LET
                || currentToken.type == TokenType.IF
                || currentToken.type == TokenType.WHILE
                || currentToken.type == TokenType.DO) {
            switch(currentToken.type) {
                case TokenType.RETURN -> parseReturn();
                case TokenType.LET -> parseLet();
                case TokenType.IF -> parseIf();
                case TokenType.WHILE -> parseWhile();
                case TokenType.DO -> parseDo();
            }

        }

        output.closeTag("statements");
    }

    private void parseExpression() {
        output.openTag("expression");

        parseTerm();

        while(isOp(currentToken.type)) {
            Token operator = consume(currentToken.type);
            output.writeToken(operator);
            parseTerm();
            if (generatingVm) {
                writeBinary(operator.type);
            }
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
        if (generatingVm) {
            vm.writeArithmetic(type == TokenType.MINUS ? ArithmeticCommand.NEG : ArithmeticCommand.NOT);
        }
    }

    private void caseIdent(){
        Token name = consume(TokenType.IDENT);
        output.writeToken(name);

        if(currentToken.type == TokenType.LBRACKET){
            if (generatingVm) {
                pushVariable(name);
            }
            output.writeToken(consume(TokenType.LBRACKET));
            parseExpression();
            output.writeToken(consume(TokenType.RBRACKET));
            if (generatingVm) {
                vm.writeArithmetic(ArithmeticCommand.ADD);
                vm.writePop(Segment.POINTER, 1);
                vm.writePush(Segment.THAT, 0);
            }
        } else if (currentToken.type == TokenType.LPAREN || currentToken.type == TokenType.DOT) {
            parseCallAfterName(name);
        } else if (generatingVm) {
            pushVariable(name);
        }

    }

    private void parseTerm(){
        output.openTag("term");

        TokenType type = currentToken.type;
        switch (type) {
            case TokenType.NUMBER -> {
                Token number = consume(TokenType.NUMBER);
                output.writeToken(number);
                if (generatingVm) {
                    vm.writePush(Segment.CONSTANT, Integer.parseInt(number.lexeme));
                }
            }
            case TokenType.STRING -> {
                Token string = consume(TokenType.STRING);
                output.writeToken(string);
                if (generatingVm) {
                    writeString(string);
                }
            }
            case TokenType.IDENT -> caseIdent();
            case TokenType.TRUE, TokenType.FALSE, TokenType.NULL -> {
                output.writeToken(consume(type));
                if (generatingVm) {
                    vm.writePush(Segment.CONSTANT, 0);
                    if (type == TokenType.TRUE) {
                        vm.writeArithmetic(ArithmeticCommand.NOT);
                    }
                }
            }
            case TokenType.THIS -> {
                Token keyword = consume(TokenType.THIS);
                output.writeToken(keyword);
                if (generatingVm) {
                    requireObject(keyword);
                    vm.writePush(Segment.POINTER, 0);
                }
            }
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
        Token name = consume(TokenType.IDENT);
        output.writeToken(name);

        boolean array = currentToken.type == TokenType.LBRACKET;
        if(array) {
            if (generatingVm) {
                pushVariable(name);
            }
            output.writeToken(consume(TokenType.LBRACKET));
            parseExpression();
            output.writeToken(consume(TokenType.RBRACKET));
            if (generatingVm) {
                vm.writeArithmetic(ArithmeticCommand.ADD);
            }
        }

        output.writeToken(consume(TokenType.EQ));
        parseExpression();
        output.writeToken(consume(TokenType.SEMICOLON));
        if (generatingVm) {
            if (array) {
                // O endereço fica na pilha enquanto o lado direito pode alterar pointer 1.
                vm.writePop(Segment.TEMP, 0);
                vm.writePop(Segment.POINTER, 1);
                vm.writePush(Segment.TEMP, 0);
                vm.writePop(Segment.THAT, 0);
            } else {
                Symbol symbol = requireSymbol(name);
                vm.writePop(segmentOf(symbol.kind), symbol.index);
            }
        }

        output.closeTag("letStatement");
    }

    private void parseIf(){
        output.openTag("ifStatement");
        String trueLabel = newLabel("IF_TRUE");
        String falseLabel = newLabel("IF_FALSE");
        String endLabel = newLabel("IF_END");

        // if(){} -> if + ( + expr + ) + { + statements + }
        output.writeToken(consume(TokenType.IF));
        output.writeToken(consume(TokenType.LPAREN));
        parseExpression();
        output.writeToken(consume(TokenType.RPAREN));
        if (generatingVm) {
            vm.writeIf(trueLabel);
            vm.writeGoto(falseLabel);
            vm.writeLabel(trueLabel);
        }

        output.writeToken(consume(TokenType.LBRACE));
        parseStatements();
        output.writeToken(consume(TokenType.RBRACE));
        if (generatingVm) {
            vm.writeGoto(endLabel);
            vm.writeLabel(falseLabel);
        }

        // else{} -> else + { + statements + }
        if(currentToken.type == TokenType.ELSE) {
            output.writeToken(consume(TokenType.ELSE));
            output.writeToken(consume(TokenType.LBRACE));
            parseStatements();
            output.writeToken(consume(TokenType.RBRACE));

        }
        if (generatingVm) {
            vm.writeLabel(endLabel);
        }

        output.closeTag("ifStatement");

    }

    private void parseWhile() {
        output.openTag("whileStatement");
        String startLabel = newLabel("WHILE_START");
        String bodyLabel = newLabel("WHILE_BODY");
        String endLabel = newLabel("WHILE_END");
        if (generatingVm) {
            vm.writeLabel(startLabel);
        }

        // while(){} -> while + ( + expr + ) + { + statements + }
        output.writeToken(consume(TokenType.WHILE));
        output.writeToken(consume(TokenType.LPAREN));
        parseExpression();
        output.writeToken(consume(TokenType.RPAREN));
        if (generatingVm) {
            vm.writeIf(bodyLabel);
            vm.writeGoto(endLabel);
            vm.writeLabel(bodyLabel);
        }

        output.writeToken(consume(TokenType.LBRACE));
        parseStatements();
        output.writeToken(consume(TokenType.RBRACE));
        if (generatingVm) {
            vm.writeGoto(startLabel);
            vm.writeLabel(endLabel);
        }

        output.closeTag("whileStatement");
    }

    private int parseExpressionList(){
        output.openTag("expressionList");
        int count = 0;

        if(currentToken.type != TokenType.RPAREN) {
            parseExpression();
            count++;

            while(currentToken.type == TokenType.COMMA){
                output.writeToken(consume(TokenType.COMMA));
                parseExpression();
                count++;
            }
        }



        output.closeTag("expressionList");
        return count;
    }

    private void parseCallAfterName(Token name){
        String target = className + "." + name.lexeme;
        int implicitArgs = 0;
        if(currentToken.type == TokenType.DOT){
            output.writeToken(consume(TokenType.DOT));
            Token method = consume(TokenType.IDENT);
            output.writeToken(method);
            if (generatingVm) {
                Symbol receiver = symbolTable.resolve(name.lexeme);
                if (receiver == null) {
                    target = name.lexeme + "." + method.lexeme;
                } else {
                    pushVariable(name);
                    target = receiver.type + "." + method.lexeme;
                    implicitArgs = 1;
                }
            }
        } else if (generatingVm) {
            requireObject(name);
            vm.writePush(Segment.POINTER, 0);
            implicitArgs = 1;
        }

        output.writeToken(consume(TokenType.LPAREN));
        int count = parseExpressionList();
        output.writeToken(consume(TokenType.RPAREN));
        if (generatingVm) {
            vm.writeCall(target, count + implicitArgs);
        }
    }


    private void parseDo(){
        output.openTag("doStatement");

        output.writeToken(consume(TokenType.DO));
        Token name = consume(TokenType.IDENT);
        output.writeToken(name);

        parseCallAfterName(name);

        output.writeToken(consume(TokenType.SEMICOLON));
        if (generatingVm) {
            vm.writePop(Segment.TEMP, 0);
        }

        output.closeTag("doStatement");
    }

    private String newLabel(String purpose) {
        return subroutineName + "$" + purpose + labelCount++;
    }

    private IllegalStateException error(Token token, String message) {
        return new IllegalStateException(message + "; lexema: " + token.lexeme
                + ", linha: " + token.row + ", coluna: " + token.column);
    }

    private void requireObject(Token token) {
        if (subroutineKind == TokenType.FUNCTION) {
            throw error(token, "Function não possui objeto this implícito");
        }
    }

    private Symbol requireSymbol(Token name) {
        Symbol symbol = symbolTable.resolve(name.lexeme);
        if (symbol == null) {
            throw error(name, "Variável não declarada");
        }
        if (symbol.kind == SymbolKind.FIELD) {
            requireObject(name);
        }
        return symbol;
    }

    private Segment segmentOf(SymbolKind kind) {
        return switch (kind) {
            case STATIC -> Segment.STATIC;
            case FIELD -> Segment.THIS;
            case ARG -> Segment.ARGUMENT;
            case VAR -> Segment.LOCAL;
        };
    }

    private void pushVariable(Token name) {
        Symbol symbol = requireSymbol(name);
        vm.writePush(segmentOf(symbol.kind), symbol.index);
    }

    private void writeBinary(TokenType operator) {
        switch (operator) {
            case PLUS -> vm.writeArithmetic(ArithmeticCommand.ADD);
            case MINUS -> vm.writeArithmetic(ArithmeticCommand.SUB);
            case AND -> vm.writeArithmetic(ArithmeticCommand.AND);
            case OR -> vm.writeArithmetic(ArithmeticCommand.OR);
            case EQ -> vm.writeArithmetic(ArithmeticCommand.EQ);
            case LT -> vm.writeArithmetic(ArithmeticCommand.LT);
            case GT -> vm.writeArithmetic(ArithmeticCommand.GT);
            case ASTERISK -> vm.writeCall("Math.multiply", 2);
            case SLASH -> vm.writeCall("Math.divide", 2);
            default -> throw new IllegalArgumentException("Operador inválido: " + operator);
        }
    }

    private void writeString(Token token) {
        String value = token.lexeme;
        if (value.length() > 32767) {
            throw error(token, "String excede 32767 caracteres");
        }
        vm.writePush(Segment.CONSTANT, value.length());
        vm.writeCall("String.new", 1);
        for (int i = 0; i < value.length(); i++) {
            int character = value.charAt(i);
            if (character <= 32767) {
                vm.writePush(Segment.CONSTANT, character);
            } else {
                vm.writePush(Segment.CONSTANT, 65535 - character);
                vm.writeArithmetic(ArithmeticCommand.NOT);
            }
            vm.writeCall("String.appendChar", 2);
        }
    }
}
