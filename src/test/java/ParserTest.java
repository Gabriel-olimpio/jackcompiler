
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class ParserTest {

    private static final Path BASE =
        Path.of("src/test/parser/Square");


    private String gerarXml(String nome) throws Exception {
        byte[] codigo = Files.readAllBytes(
            BASE.resolve(nome + ".jack")
        );

        Parser parser = new Parser(codigo);
        String xml = parser.parse();

        Path saida = Path.of("target/parser-tests");
        Files.createDirectories(saida);

        Files.writeString(
            saida.resolve(nome + "-geradoP.xml"),
            xml,
            StandardCharsets.UTF_8
        );

        return xml;
    }

    private Element lerXml(String xml) throws Exception {
        var fabrica = DocumentBuilderFactory.newInstance();

        fabrica.setFeature(
            "http://apache.org/xml/features/disallow-doctype-decl",
            true
        );

        var documento = fabrica.newDocumentBuilder().parse(
            new ByteArrayInputStream(
                xml.getBytes(StandardCharsets.UTF_8)
            )
        );

        return documento.getDocumentElement();
    }

    private List<Element> filhos(Element elemento) {
        List<Element> lista = new ArrayList<>();
        NodeList nos = elemento.getChildNodes();

        for (int i = 0; i < nos.getLength(); i++) {
            if (nos.item(i) instanceof Element filho) {
                lista.add(filho);
            }
        }

        return lista;
    }

    private void comparar(Element esperado, Element obtido) {
        assertEquals(
            esperado.getTagName(),
            obtido.getTagName(),
            "Tag XML diferente"
        );

        List<Element> a = filhos(esperado);
        List<Element> b = filhos(obtido);

        assertEquals(
            a.size(), b.size(),
            "Estrutura diferente em " + esperado.getTagName()
        );

        if (a.isEmpty()) {
            assertEquals(
                esperado.getTextContent().strip(),
                obtido.getTextContent().strip(),
                "Token diferente em " + esperado.getTagName()
            );
        }

        for (int i = 0; i < a.size(); i++) {
            comparar(a.get(i), b.get(i));
        }
    }

    
    private void validar(String nome) throws Exception {
        String gerado = gerarXml(nome);

        String esperado = Files.readString(
            BASE.resolve(nome + "P.xml"),
            StandardCharsets.UTF_8
        );

        comparar(lerXml(esperado), lerXml(gerado));
    }

    @Test
    void testeMain() throws Exception {
        validar("Main");
    }

    @Test
    void testeSquare() throws Exception {
        validar("Square");
    }

    @Test
    void testeSquareGame() throws Exception {
        validar("SquareGame");
    }
}