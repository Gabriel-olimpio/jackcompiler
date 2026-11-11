
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class MainIntegrationTest {

    @TempDir
    Path pasta;

    // Cria um arquivo Jack simples para os testes
    private Path criarArquivoJack(String nome) throws Exception {
        String codigo = """
            class %s {
                function void main() {
                    return;
                }
            }
            """.formatted(nome);

        Path arquivo = pasta.resolve(nome + ".jack");
        Files.writeString(arquivo, codigo);

        return arquivo;
    }

    @Test
    void deveProcessarArquivoJackIndividual() throws Exception {
        Path arquivo = criarArquivoJack("Main");

        Main.main(new String[]{arquivo.toString()});

        Path vm = pasta.resolve("Main.vm");

        assertTrue(Files.exists(vm));
        assertTrue(Files.readString(vm).contains("function Main.main"));
    }

    @Test
    void deveProcessarPastaComDoisArquivos() throws Exception {
        criarArquivoJack("Primeiro");
        criarArquivoJack("Segundo");

        Main.main(new String[]{pasta.toString()});

        Path primeiro = pasta.resolve("Primeiro.vm");
        Path segundo = pasta.resolve("Segundo.vm");

        assertTrue(Files.exists(primeiro));
        assertTrue(Files.exists(segundo));

        assertTrue(Files.readString(primeiro).contains("function Primeiro.main"));
        assertTrue(Files.readString(segundo).contains("function Segundo.main"));
    }

    @Test
    void naoDeveAlterarXmlExistente() throws Exception {
        Path arquivo = criarArquivoJack("Main");
        Path xml = pasta.resolve("MainP.xml");

        Files.writeString(xml, "conteudo original");

        Main.main(new String[]{arquivo.toString()});

        assertEquals("conteudo original", Files.readString(xml));
        assertTrue(Files.exists(pasta.resolve("Main.vm")));
    }

    @Test
    void deveAtualizarVmAoRecompilar() throws Exception {
        Path jack = criarArquivoJack("Main");
        Path vm = pasta.resolve("Main.vm");

        Main.main(new String[]{jack.toString()});
        String antes = Files.readString(vm);

        Files.writeString(jack, """
            class Main {
                function void main() {
                    do Output.printInt(7);
                    return;
                }
            }
            """);

        Main.main(new String[]{jack.toString()});

        String depois = Files.readString(vm);
        assertNotEquals(antes, depois);
        assertTrue(depois.contains("call Output.printInt 1"));
    }

    @Test
    void naoDeveApagarVmSeCompilacaoFalhar() throws Exception {
        Path jack = criarArquivoJack("Main");
        Path vm = pasta.resolve("Main.vm");

        Main.main(new String[]{jack.toString()});
        String original = Files.readString(vm);

        Files.writeString(jack, """
            class Main {
                function void main() {
                    let x = ;
                    return;
                }
            }
            """);

        assertThrows(IllegalStateException.class,
                () -> Main.main(new String[]{jack.toString()}));
        assertEquals(original, Files.readString(vm));
    }

    @Test
    void deveRecompilarPastaComArquivosVmExistentes() throws Exception {
        criarArquivoJack("Primeiro");
        criarArquivoJack("Segundo");
        Path primeiro = pasta.resolve("Primeiro.vm");
        Path segundo = pasta.resolve("Segundo.vm");

        Main.main(new String[]{pasta.toString()});
        Main.main(new String[]{pasta.toString()});

        assertTrue(Files.exists(primeiro));
        assertTrue(Files.exists(segundo));
    }

    @Test
    void devePermitirNomeDeSaidaPersonalizado() throws Exception {
        Path arquivo = criarArquivoJack("Main");
        Path xml = pasta.resolve("resultado.xml");

        Main.main(new String[]{arquivo.toString(), xml.toString()});

        assertTrue(Files.exists(xml));
        assertTrue(Files.readString(xml).startsWith("<class>"));
        assertTrue(Files.readString(xml).contains("<identifier> Main </identifier>"));
    }
}
