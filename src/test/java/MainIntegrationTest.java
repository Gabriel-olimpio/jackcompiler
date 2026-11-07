
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.*;

public class MainIntegrationTest {

    @TempDir
    Path pastaTemporaria;

    private Path criarArquivoJack(String nome) throws Exception {
        String codigo = """
            class %s {
                function void main() {
                    return;
                }
            }
            """.formatted(nome);

        Path arquivo = pastaTemporaria.resolve(nome + ".jack");

        Files.writeString(
            arquivo, codigo, StandardCharsets.UTF_8
        );

        return arquivo;
    }

    @Test
    void deveProcessarArquivoJackIndividual() throws Exception {
        Path entrada = criarArquivoJack("Main");

        Main.main(new String[]{entrada.toString()});

        Path saida = pastaTemporaria.resolve("MainP.xml");

        assertTrue(Files.exists(saida));

        String xml = Files.readString(saida);

        assertTrue(xml.startsWith("<class>"));
        assertTrue(xml.contains("<identifier> Main </identifier>"));
        assertTrue(xml.contains("</class>"));
    }

    @Test
    void deveProcessarPastaComDoisArquivos() throws Exception {
        criarArquivoJack("Primeiro");
        criarArquivoJack("Segundo");

        Main.main(new String[]{
            pastaTemporaria.toString()
        });

        Path primeiro = pastaTemporaria.resolve("PrimeiroP.xml");
        Path segundo = pastaTemporaria.resolve("SegundoP.xml");

        assertTrue(Files.exists(primeiro));
        assertTrue(Files.exists(segundo));

        String xmlPrimeiro = Files.readString(primeiro);
        String xmlSegundo = Files.readString(segundo);

        assertTrue(xmlPrimeiro.contains(
            "<identifier> Primeiro </identifier>"
        ));

        assertTrue(xmlSegundo.contains(
            "<identifier> Segundo </identifier>"
        ));
    }

    @Test
    void naoDeveSobrescreverXmlExistente() throws Exception {
        Path entrada = criarArquivoJack("Main");
        Path saida = pastaTemporaria.resolve("MainP.xml");

        Files.writeString(saida, "conteudo original");

        assertThrows(
            FileAlreadyExistsException.class,
            () -> Main.main(new String[]{entrada.toString()})
        );

        assertEquals(
            "conteudo original",
            Files.readString(saida)
        );
    }

    @Test
    void devePermitirNomeDeSaidaPersonalizado() throws Exception {
        Path entrada = criarArquivoJack("Main");
        Path saida = pastaTemporaria.resolve("resultado.xml");

        Main.main(new String[]{
            entrada.toString(),
            saida.toString()
        });

        assertTrue(Files.exists(saida));

        String xml = Files.readString(saida);

        assertTrue(xml.startsWith("<class>"));
        assertTrue(xml.contains(
            "<identifier> Main </identifier>"
        ));
    }
}
