
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ParserOfficialTest {

    private List<String> normalizar(String xml) {
        return xml.lines()
                .map(String::strip)
                .filter(linha -> !linha.isEmpty())
                .toList();
    }

    private void validar(String conjunto, String nome)
            throws Exception {

        Path pasta = Path.of(
                "src/test/parser", conjunto
        );

        byte[] codigo = Files.readAllBytes(
                pasta.resolve(nome + ".jack")
        );

        Parser parser = new Parser(codigo);
        String xmlGerado = parser.parse();

        String xmlOficial = Files.readString(
                pasta.resolve(nome + "P.xml")
        );

        assertEquals(
                normalizar(xmlOficial),
                normalizar(xmlGerado),
                "XML diferente: " + conjunto + "/" + nome
        );
    }

    @Test
    void testarExpressionLessMain() throws Exception {
        validar("ExpressionLessSquare", "Main");
    }

    @Test
    void testarExpressionLessSquare() throws Exception {
        validar("ExpressionLessSquare", "Square");
    }

    @Test
    void testarExpressionLessSquareGame() throws Exception {
        validar("ExpressionLessSquare", "SquareGame");
    }

    @Test
    void testarArrayTest() throws Exception {
        validar("ArrayTest", "Main");
    }
}
