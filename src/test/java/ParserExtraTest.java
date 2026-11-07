import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ParserExtraTest {

    private String analisar(String codigo) {
        return new Parser(codigo.getBytes(StandardCharsets.UTF_8)).parse();
    }

    private String programa(String comandos) {
        return programa("var int x;", comandos);
    }

    private String programa(String declaracoes, String comandos) {
        return "class Teste {\n"
                + "function void main() {\n"
                + declaracoes + "\n"
                + comandos + "\n"
                + "return;\n"
                + "}\n"
                + "}\n";
    }

    private Document lerXml(String xml) throws Exception {
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return fabrica.newDocumentBuilder()
                .parse(new InputSource(new StringReader(xml)));
    }

    private List<Element> filhos(Element elemento) {
        List<Element> resultado = new ArrayList<>();
        NodeList nos = elemento.getChildNodes();
        for (int i = 0; i < nos.getLength(); i++) {
            if (nos.item(i).getNodeType() == Node.ELEMENT_NODE) {
                resultado.add((Element) nos.item(i));
            }
        }
        return resultado;
    }

    private void comparar(Element esperado, Element atual) {
        assertEquals(esperado.getTagName(), atual.getTagName(), "Tag diferente");

        List<Element> filhosEsperados = filhos(esperado);
        List<Element> filhosAtuais = filhos(atual);
        assertEquals(filhosEsperados.size(), filhosAtuais.size(),
                "Quantidade/ordem de elementos incorreta em <" + esperado.getTagName() + ">");

        if (filhosEsperados.isEmpty()) {
            assertEquals(esperado.getTextContent().strip(), atual.getTextContent().strip(),
                    "Valor incorreto em <" + esperado.getTagName() + ">");
        } else {
            for (int i = 0; i < filhosEsperados.size(); i++) {
                comparar(filhosEsperados.get(i), filhosAtuais.get(i));
            }
        }
    }

    // Verifica a subárvore inteira, inclusive ordem, tags, tokens e listas vazias.
    private void conferir(String xmlGerado, String caminhoXPath, String xmlEsperado)
            throws Exception {
        Document gerado = lerXml(xmlGerado);
        Node encontrado = (Node) XPathFactory.newInstance().newXPath()
                .evaluate(caminhoXPath, gerado, XPathConstants.NODE);
        assertNotNull(encontrado, "Caminho ausente: " + caminhoXPath);
        assertTrue(encontrado instanceof Element);
        comparar(lerXml(xmlEsperado).getDocumentElement(), (Element) encontrado);
    }

    @Test
    void deveAceitarMultiplosParametros() throws Exception {
        String xml = analisar("""
                class Teste {
                    function void main(int a, boolean b) {
                        return;
                    }
                }
                """);

        conferir(xml, "//subroutineDec/parameterList", """
                <parameterList>
                    <keyword> int </keyword>
                    <identifier> a </identifier>
                    <symbol> , </symbol>
                    <keyword> boolean </keyword>
                    <identifier> b </identifier>
                </parameterList>
                """);
    }

    @Test
    void deveAceitarChamadaSemArgumentos() throws Exception {
        String xml = analisar(programa("do Screen.clearScreen();"));

        conferir(xml, "//doStatement", """
                <doStatement>
                    <keyword> do </keyword>
                    <identifier> Screen </identifier>
                    <symbol> . </symbol>
                    <identifier> clearScreen </identifier>
                    <symbol> ( </symbol>
                    <expressionList></expressionList>
                    <symbol> ) </symbol>
                    <symbol> ; </symbol>
                </doStatement>
                """);
    }

    @Test
    void deveAceitarMultiplosArgumentos() throws Exception {
        String xml = analisar(programa("do Memory.poke(100, 42);"));

        conferir(xml, "//doStatement/expressionList", """
                <expressionList>
                    <expression><term><integerConstant> 100 </integerConstant></term></expression>
                    <symbol> , </symbol>
                    <expression><term><integerConstant> 42 </integerConstant></term></expression>
                </expressionList>
                """);
    }

    @Test
    void deveAceitarLeituraEEscritaEmArrays() throws Exception {
        String xml = analisar(programa(
                "var Array a;\nvar int x;",
                "let a[1] = 5;\nlet x = a[1];"));

        conferir(xml, "(//letStatement)[1]", """
                <letStatement>
                    <keyword> let </keyword>
                    <identifier> a </identifier>
                    <symbol> [ </symbol>
                    <expression><term><integerConstant> 1 </integerConstant></term></expression>
                    <symbol> ] </symbol>
                    <symbol> = </symbol>
                    <expression><term><integerConstant> 5 </integerConstant></term></expression>
                    <symbol> ; </symbol>
                </letStatement>
                """);

        conferir(xml, "(//letStatement)[2]", """
                <letStatement>
                    <keyword> let </keyword>
                    <identifier> x </identifier>
                    <symbol> = </symbol>
                    <expression>
                        <term>
                            <identifier> a </identifier>
                            <symbol> [ </symbol>
                            <expression><term><integerConstant> 1 </integerConstant></term></expression>
                            <symbol> ] </symbol>
                        </term>
                    </expression>
                    <symbol> ; </symbol>
                </letStatement>
                """);
    }

    @Test
    void deveAceitarOperadoresUnarios() throws Exception {
        String xml = analisar(programa("let x = -(3 + 4);\nlet x = ~false;"));

        conferir(xml, "(//letStatement)[1]/expression", """
                <expression>
                    <term>
                        <symbol> - </symbol>
                        <term>
                            <symbol> ( </symbol>
                            <expression>
                                <term><integerConstant> 3 </integerConstant></term>
                                <symbol> + </symbol>
                                <term><integerConstant> 4 </integerConstant></term>
                            </expression>
                            <symbol> ) </symbol>
                        </term>
                    </term>
                </expression>
                """);

        conferir(xml, "(//letStatement)[2]/expression", """
                <expression>
                    <term>
                        <symbol> ~ </symbol>
                        <term><keyword> false </keyword></term>
                    </term>
                </expression>
                """);
    }

    @Test
    void deveAceitarIfElseEWhile() throws Exception {
        String xml = analisar(programa("""
                while (x < 3) {
                    if (true) {
                        return;
                    } else {
                        return;
                    }
                }
                """));

        conferir(xml, "//whileStatement/expression", """
                <expression>
                    <term><identifier> x </identifier></term>
                    <symbol> &lt; </symbol>
                    <term><integerConstant> 3 </integerConstant></term>
                </expression>
                """);

        conferir(xml, "//whileStatement/statements/ifStatement", """
                <ifStatement>
                    <keyword> if </keyword>
                    <symbol> ( </symbol>
                    <expression><term><keyword> true </keyword></term></expression>
                    <symbol> ) </symbol>
                    <symbol> { </symbol>
                    <statements>
                        <returnStatement>
                            <keyword> return </keyword>
                            <symbol> ; </symbol>
                        </returnStatement>
                    </statements>
                    <symbol> } </symbol>
                    <keyword> else </keyword>
                    <symbol> { </symbol>
                    <statements>
                        <returnStatement>
                            <keyword> return </keyword>
                            <symbol> ; </symbol>
                        </returnStatement>
                    </statements>
                    <symbol> } </symbol>
                </ifStatement>
                """);
    }

    @Test
    void deveEscaparSimbolosXmlEReconhecerStrings() throws Exception {
        String xml = analisar(programa("""
                let x = 1 < 2 & 3 > 0;
                do Output.printString("A < B & C > D");
                """));

        conferir(xml, "//letStatement/expression", """
                <expression>
                    <term><integerConstant> 1 </integerConstant></term>
                    <symbol> &lt; </symbol>
                    <term><integerConstant> 2 </integerConstant></term>
                    <symbol> &amp; </symbol>
                    <term><integerConstant> 3 </integerConstant></term>
                    <symbol> &gt; </symbol>
                    <term><integerConstant> 0 </integerConstant></term>
                </expression>
                """);

        conferir(xml, "//doStatement/expressionList/expression/term", """
                <term><stringConstant> A &lt; B &amp; C &gt; D </stringConstant></term>
                """);

        assertTrue(xml.contains("&lt;"));
        assertTrue(xml.contains("&gt;"));
        assertTrue(xml.contains("&amp;"));
    }

    @Test
    void deveAceitarExpressoesComParenteses() throws Exception {
        String xml = analisar(programa("let x = (1 + 2) * 3;"));

        conferir(xml, "//letStatement/expression", """
                <expression>
                    <term>
                        <symbol> ( </symbol>
                        <expression>
                            <term><integerConstant> 1 </integerConstant></term>
                            <symbol> + </symbol>
                            <term><integerConstant> 2 </integerConstant></term>
                        </expression>
                        <symbol> ) </symbol>
                    </term>
                    <symbol> * </symbol>
                    <term><integerConstant> 3 </integerConstant></term>
                </expression>
                """);
    }

    @Test
    void deveRejeitarClasseSemFechamento() {
        String codigo = """
                class Teste {
                    function void main() {
                        return;
                    }
                """;

        IllegalStateException erro = assertThrows(
                IllegalStateException.class, () -> analisar(codigo));
        assertTrue(erro.getMessage().contains("Esperado: RBRACE"));
        assertTrue(erro.getMessage().contains("Encontrado: EOF"));
        assertTrue(erro.getMessage().matches("(?s).*linha: \\d+, coluna: \\d+.*"));
    }

    @Test
    void deveRejeitarExpressaoIncompleta() {
        String codigo = programa("let x = 1 + ;");

        IllegalStateException erro = assertThrows(
                IllegalStateException.class, () -> analisar(codigo));
        assertTrue(erro.getMessage().contains("Encontrado: SEMICOLON"));
        assertTrue(erro.getMessage().contains("lexema: ;"));
        assertTrue(erro.getMessage().matches("(?s).*linha: \\d+, coluna: \\d+.*"));
    }
}