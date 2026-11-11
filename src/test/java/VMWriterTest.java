
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VMWriterTest {

    private VMWriter writer = new VMWriter();

    @Test
    void deveGerarComandoPush() {
        writer.writePush(Segment.CONSTANT, 5);
        assertEquals("push constant 5\n", writer.getOutput());
    }

    @Test
    void deveGerarComandoPop() {
        writer.writePop(Segment.LOCAL, 0);
        assertEquals("pop local 0\n", writer.getOutput());
    }

    @Test
    void deveGerarComandoAdd() {
        writer.writeArithmetic(ArithmeticCommand.ADD);
        assertEquals("add\n", writer.getOutput());
    }

    @Test
    void deveGerarSequenciaDeComandos() {
        writer.writePush(Segment.CONSTANT, 5);
        writer.writePush(Segment.CONSTANT, 3);
        writer.writeArithmetic(ArithmeticCommand.ADD);

        String esperado = "push constant 5\n"
                        + "push constant 3\n"
                        + "add\n";

        assertEquals(esperado, writer.getOutput());
    }

    @Test
    void deveGerarComandoLabel() {
        writer.writeLabel("LOOP_START");
        assertEquals("label LOOP_START\n", writer.getOutput());
    }

    @Test
    void deveGerarComandoGoto() {
        writer.writeGoto("LOOP_START");
        assertEquals("goto LOOP_START\n", writer.getOutput());
    }

    @Test
    void deveGerarComandoIfGoto() {
        writer.writeIf("IF_TRUE");
        assertEquals("if-goto IF_TRUE\n", writer.getOutput());
    }

    @Test
    void deveGerarComandoCall() {
        writer.writeCall("Math.abs", 1);
        assertEquals("call Math.abs 1\n", writer.getOutput());
    }

    @Test
    void deveGerarComandoFunction() {
        writer.writeFunction("Main.main", 2);
        assertEquals("function Main.main 2\n", writer.getOutput());
    }

    @Test
    void deveGerarComandoReturn() {
        writer.writeReturn();
        assertEquals("return\n", writer.getOutput());
    }

    @Test
    void deveRejeitarIndiceNegativoNoPush() {
        assertThrows(IllegalArgumentException.class,
            () -> writer.writePush(Segment.CONSTANT, -1));

        assertEquals("", writer.getOutput());
    }

    @Test
    void deveRejeitarPopNoSegmentoConstant() {
        assertThrows(IllegalArgumentException.class,
            () -> writer.writePop(Segment.CONSTANT, 5));

        assertEquals("", writer.getOutput());
    }

    @Test
    void deveRejeitarQuantidadeNegativaDeArgumentos() {
        assertThrows(IllegalArgumentException.class,
            () -> writer.writeCall("Math.abs", -1));

        assertEquals("", writer.getOutput());
    }

    @Test
    void deveRejeitarQuantidadeNegativaDeVariaveisLocais() {
        assertThrows(IllegalArgumentException.class,
            () -> writer.writeFunction("Main.main", -1));

        assertEquals("", writer.getOutput());
    }
}
