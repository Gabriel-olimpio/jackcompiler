import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class CompilationEngineVmTest {

    private String compilar(String jack) {
        return new CompilationEngine(jack.getBytes(StandardCharsets.UTF_8)).compile();
    }

    @Test
    void deveTraduzirExpressaoAritmetica() {
        String vm = compilar("""
            class Exemplo {
                function void main() {
                    var int valor;
                    let valor = (2 + 3) * 4;
                    return;
                }
            }
            """);

        assertEquals("""
            function Exemplo.main 1
            push constant 2
            push constant 3
            add
            push constant 4
            call Math.multiply 2
            pop local 0
            push constant 0
            return
            """, vm);
    }

    @Test
    void deveGerarMultiplicacaoEDivisao() {
        String vm = compilar("""
            class Conta {
                function int calcular() {
                    return (8 * 2) / 4;
                }
            }
            """);

        assertTrue(vm.contains("call Math.multiply 2"));
        assertTrue(vm.contains("call Math.divide 2"));
    }

    @Test
    void deveGerarIfElse() {
        String vm = compilar("""
            class Exemplo {
                function int escolher(int valor) {
                    if (valor > 0) {
                        return 1;
                    } else {
                        return 2;
                    }
                }
            }
            """);

        assertTrue(vm.contains("if-goto Exemplo.escolher$IF_TRUE0"));
        assertTrue(vm.contains("goto Exemplo.escolher$IF_FALSE1"));
        assertTrue(vm.contains("label Exemplo.escolher$IF_END2"));
    }

    @Test
    void deveGerarWhile() {
        String vm = compilar("""
            class Exemplo {
                function void repetir() {
                    var int i;
                    let i = 0;
                    while (i < 3) {
                        let i = i + 1;
                    }
                    return;
                }
            }
            """);

        assertTrue(vm.contains("label Exemplo.repetir$WHILE_START0"));
        assertTrue(vm.contains("if-goto Exemplo.repetir$WHILE_BODY1"));
        assertTrue(vm.contains("goto Exemplo.repetir$WHILE_END2"));
        assertTrue(vm.contains("goto Exemplo.repetir$WHILE_START0"));
    }

    @Test
    void deveGerarConstrutor() {
        String vm = compilar("""
            class Ponto {
                field int x, y;
                constructor Ponto new() {
                    return this;
                }
            }
            """);

        assertEquals("""
            function Ponto.new 0
            push constant 2
            call Memory.alloc 1
            pop pointer 0
            push pointer 0
            return
            """, vm);
    }

    @Test
    void deveGerarMetodoComArgumentoImplicito() {
        String vm = compilar("""
            class Ponto {
                field int x;
                method void set(int valor) {
                    let x = valor;
                    return;
                }
            }
            """);

        assertEquals("""
            function Ponto.set 0
            push argument 0
            pop pointer 0
            push argument 1
            pop this 0
            push constant 0
            return
            """, vm);
    }

    @Test
    void deveGerarChamadaDeMetodoEmObjeto() {
        String vm = compilar("""
            class Exemplo {
                function void executar(Conta conta) {
                    do conta.depositar(10);
                    return;
                }
            }
            """);

        assertTrue(vm.contains("push argument 0\npush constant 10\ncall Conta.depositar 2"));
        assertTrue(vm.contains("call Conta.depositar 2\npop temp 0"));
    }

    @Test
    void deveGerarChamadaDeFuncaoDaClasse() {
        String vm = compilar("""
            class Exemplo {
                function void main() {
                    do Math.multiply(2, 3);
                    return;
                }
            }
            """);

        assertTrue(vm.contains("call Math.multiply 2"));
        assertFalse(vm.contains("push pointer 0"));
    }

    @Test
    void deveTraduzirString() {
        String vm = compilar("""
            class Exemplo {
                function void main() {
                    do Output.printString("Oi");
                    return;
                }
            }
            """);

        assertTrue(vm.contains("""
            push constant 2
            call String.new 1
            push constant 79
            call String.appendChar 2
            push constant 105
            call String.appendChar 2
            call Output.printString 1
            """));
    }

    @Test
    void deveLerValorDeArray() {
        String vm = compilar("""
            class Exemplo {
                function int ler(Array valores) {
                    return valores[2];
                }
            }
            """);

        assertEquals("""
            function Exemplo.ler 0
            push argument 0
            push constant 2
            add
            pop pointer 1
            push that 0
            return
            """, vm);
    }

    @Test
    void deveEscreverValorEmArray() {
        String vm = compilar("""
            class Exemplo {
                function void escrever(Array valores, int numero) {
                    let valores[1] = numero;
                    return;
                }
            }
            """);

        assertTrue(vm.contains("""
            push argument 0
            push constant 1
            add
            push argument 1
            pop temp 0
            pop pointer 1
            push temp 0
            pop that 0
            """));
    }

    @Test
    void deveGerarSegmentoStaticEThis() {
        String vm = compilar("""
            class Exemplo {
                static int total;
                field int valor;
                method void salvar(int numero) {
                    let valor = numero;
                    let total = valor;
                    return;
                }
            }
            """);

        assertTrue(vm.contains("pop this 0"));
        assertTrue(vm.contains("push this 0\npop static 0"));
    }

    @Test
    void deveGerarRotulosDiferentesParaIfsAninhados() {
        String vm = compilar("""
            class Exemplo {
                function void testar(boolean a, boolean b) {
                    if (a) {
                        if (b) {
                            do Output.println();
                        }
                    }
                    return;
                }
            }
            """);

        assertTrue(vm.contains("label Exemplo.testar$IF_TRUE0"));
        assertTrue(vm.contains("label Exemplo.testar$IF_TRUE3"));
        assertTrue(vm.contains("label Exemplo.testar$IF_END5"));
    }

    @Test
    void deveRejeitarVariavelNaoDeclarada() {
        String jack = """
            class Exemplo {
                function int calcular() {
                    return desconhecida;
                }
            }
            """;

        assertThrows(IllegalStateException.class, () -> compilar(jack));
    }
}
