
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;

public class ParserErroTest {

    private String verificarErro(String arquivo) throws Exception {
        byte[] codigo = Files.readAllBytes(
            Path.of("src/test/parser/Square/invalid", arquivo)
        );

        Parser parser = new Parser(codigo);

        IllegalStateException erro = assertThrows(
            IllegalStateException.class,
            parser::parse
        );

        assertNotNull(erro.getMessage());
        assertTrue(erro.getMessage().contains("linha:"));
        assertTrue(erro.getMessage().contains("coluna:"));

        return erro.getMessage();
    }

    @Test
    void deveRejeitarPontoEVirgulaAusente() throws Exception {
        String mensagem = verificarErro("SemPonto_virgula.jack");

        assertTrue(mensagem.contains("SEMICOLON"));
        assertTrue(mensagem.contains("RETURN"));
    }

    @Test
    void deveRejeitarParentesesAusente() throws Exception {
        String mensagem = verificarErro("SemParantese.jack");

        assertTrue(mensagem.contains("LBRACE"));
    }
}
