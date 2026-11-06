
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class ScannerXmlTest {

    private String normalizarXml(String xml) {
        return xml.lines()
                .map(String::stripLeading)
                .collect(Collectors.joining("\n"))
                .strip();
    }

    private void validarScanner(String nome) throws Exception {

        Path pasta = Path.of("src/test");

        byte[] codigo = Files.readAllBytes(
                pasta.resolve(nome + ".jack")
        );

        Scanner scanner = new Scanner(codigo);
        XmlWriter writer = new XmlWriter();

        for (Token token = scanner.nextToken();
             token.type != TokenType.EOF;
             token = scanner.nextToken()) {

            writer.writeToken(token);
        }

        String xmlGerado =
                "<tokens>\n" +
                writer.getOutput() +
                "</tokens>\n";

        String xmlOficial = Files.readString(
                pasta.resolve(nome + "T.xml")
        );

        assertEquals(
                normalizarXml(xmlOficial),
                normalizarXml(xmlGerado),
                "Scanner diferente da referencia: " + nome
        );
    }

    @Test
    void compararMain() throws Exception {
        validarScanner("Main");
    }

    @Test
    void compararSquare() throws Exception {
        validarScanner("Square");
    }

    @Test
    void compararSquareGame() throws Exception {
        validarScanner("SquareGame");
    }
}
