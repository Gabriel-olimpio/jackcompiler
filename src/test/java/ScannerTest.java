import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ScannerTest {

    @Test
    void deveReconhecerPalavraReservada() {
        Scanner scanner = new Scanner("while".getBytes());

        Token token = scanner.nextToken();

        assertEquals(TokenType.WHILE, token.type);
        assertEquals("while", token.lexeme);
    }
    
@Test
void deveReconhecerSimbolos() {
    Scanner scanner = new Scanner("+ = ;".getBytes());

    assertEquals(TokenType.PLUS, scanner.nextToken().type);
    assertEquals(TokenType.EQ, scanner.nextToken().type);
    assertEquals(TokenType.SEMICOLON, scanner.nextToken().type);
    assertEquals(TokenType.EOF, scanner.nextToken().type);
}


@Test
void deveIgnorarComentarios() {
    Scanner scanner = new Scanner(
        "// comentario de linha\n/* comentario de bloco */ let".getBytes()
    );

    assertEquals(TokenType.LET, scanner.nextToken().type);
    assertEquals(TokenType.EOF, scanner.nextToken().type);
}

}
