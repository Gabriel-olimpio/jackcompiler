
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SymbolTableTest {

    private SymbolTable tabela = new SymbolTable();

    @Test
    void deveRegistrarVariavelField() {
        tabela.define("saldo", "int", SymbolKind.FIELD);

        Symbol simbolo = tabela.resolve("saldo");

        assertNotNull(simbolo);
        assertEquals("int", simbolo.type);
        assertEquals(SymbolKind.FIELD, simbolo.kind);
        assertEquals(0, simbolo.index);
    }

    @Test
    void deveAtribuirIndicesSequenciaisParaField() {
        tabela.define("idade", "int", SymbolKind.FIELD);
        tabela.define("nota", "int", SymbolKind.FIELD);
        tabela.define("altura", "int", SymbolKind.FIELD);

        assertEquals(0, tabela.resolve("idade").index);
        assertEquals(1, tabela.resolve("nota").index);
        assertEquals(2, tabela.resolve("altura").index);
    }

    @Test
    void deveManterIndicesIndependentesParaStaticEField() {
        tabela.define("idade", "int", SymbolKind.FIELD);
        tabela.define("total", "int", SymbolKind.STATIC);
        tabela.define("nota", "int", SymbolKind.FIELD);
        tabela.define("quantidade", "int", SymbolKind.STATIC);

        assertEquals(0, tabela.resolve("idade").index);
        assertEquals(0, tabela.resolve("total").index);
        assertEquals(1, tabela.resolve("nota").index);
        assertEquals(1, tabela.resolve("quantidade").index);
    }

    @Test
    void deveContarVariaveisFieldCorretamente() {
        tabela.define("idade", "int", SymbolKind.FIELD);
        tabela.define("nota", "int", SymbolKind.FIELD);
        tabela.define("total", "int", SymbolKind.STATIC);

        assertEquals(2, tabela.varCount(SymbolKind.FIELD));
    }

    @Test
    void deveContarVariaveisLocaisCorretamente() {
        tabela.define("numero", "int", SymbolKind.ARG);
        tabela.define("soma", "int", SymbolKind.VAR);
        tabela.define("resultado", "int", SymbolKind.VAR);

        assertEquals(2, tabela.varCount(SymbolKind.VAR));
    }

    @Test
    void deveAtribuirIndicesSequenciaisParaArgumentos() {
        tabela.define("a", "int", SymbolKind.ARG);
        tabela.define("b", "int", SymbolKind.ARG);
        tabela.define("c", "int", SymbolKind.ARG);

        assertEquals(0, tabela.resolve("a").index);
        assertEquals(1, tabela.resolve("b").index);
        assertEquals(2, tabela.resolve("c").index);
        assertEquals(3, tabela.varCount(SymbolKind.ARG));
    }

    @Test
    void deveReiniciarEscopoDaSubrotina() {
        tabela.define("a", "int", SymbolKind.ARG);
        tabela.define("x", "int", SymbolKind.VAR);

        tabela.startSubroutine();

        assertEquals(0, tabela.varCount(SymbolKind.ARG));
        assertEquals(0, tabela.varCount(SymbolKind.VAR));

        assertNull(tabela.resolve("a"));
        assertNull(tabela.resolve("x"));

        tabela.define("b", "int", SymbolKind.ARG);
        tabela.define("y", "int", SymbolKind.VAR);

        assertEquals(0, tabela.resolve("b").index);
        assertEquals(0, tabela.resolve("y").index);
    }

    @Test
    void devePreservarVariaveisDaClasseAposReiniciarSubrotina() {
        tabela.define("idade", "int", SymbolKind.FIELD);
        tabela.define("total", "int", SymbolKind.STATIC);
        tabela.define("x", "int", SymbolKind.VAR);

        tabela.startSubroutine();

        assertNotNull(tabela.resolve("idade"));
        assertNotNull(tabela.resolve("total"));
        assertNull(tabela.resolve("x"));

        assertEquals(0, tabela.resolve("idade").index);
        assertEquals(0, tabela.resolve("total").index);
        assertEquals(1, tabela.varCount(SymbolKind.FIELD));
        assertEquals(1, tabela.varCount(SymbolKind.STATIC));
    }

    @Test
    void devePriorizarVariavelLocalSobreVariavelDaClasse() {
        tabela.define("valor", "int", SymbolKind.FIELD);

        tabela.startSubroutine();
        tabela.define("valor", "int", SymbolKind.VAR);

        Symbol simbolo = tabela.resolve("valor");

        assertNotNull(simbolo);
        assertEquals(SymbolKind.VAR, simbolo.kind);
        assertEquals(0, simbolo.index);
        assertEquals(1, tabela.varCount(SymbolKind.FIELD));
        assertEquals(1, tabela.varCount(SymbolKind.VAR));
    }

    @Test
    void deveRejeitarVariaveisDuplicadasNoMesmoEscopo() {
        tabela.define("valor", "int", SymbolKind.VAR);

        assertThrows(IllegalArgumentException.class,
            () -> tabela.define("valor", "boolean", SymbolKind.VAR));

        assertEquals(1, tabela.varCount(SymbolKind.VAR));
        assertEquals("int", tabela.resolve("valor").type);
    }
}
