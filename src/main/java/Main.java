import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {

        // Modo XML explícito: --xml entrada.jack saida.xml
        if (args.length == 3 && args[0].equals("--xml")) {
            processarXml(Path.of(args[1]), Path.of(args[2]));
            return;
        }

        // Dois argumentos: entrada.jack saida.xml
        if (args.length == 2) {
            processarXml(Path.of(args[0]), Path.of(args[1]));
            return;
        }

        // Modo VM: um argumento
        if (args.length != 1) {
            System.err.println(
                    "Uso:\n"
                            + "  java Main <arquivo.jack | pasta>             (gera .vm)\n"
                            + "  java Main <arquivo.jack> <saida.xml>         (gera XML)\n"
                            + "  java Main --xml <arquivo.jack> <saida.xml>   (gera XML)"
            );
            System.exit(1);
            return;
        }

        Path entrada = Path.of(args[0]);

        if (Files.isRegularFile(entrada)) {
            processarAutomaticamente(entrada);

        } else if (Files.isDirectory(entrada)) {

            List<Path> arquivos;

            try (Stream<Path> lista = Files.list(entrada)) {
                arquivos = lista
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".jack"))
                        .sorted()
                        .toList();
            }

            for (Path arquivo : arquivos) {
                processarAutomaticamente(arquivo);
            }

            System.out.println(
                    "Arquivos Jack encontrados: " + arquivos.size()
            );

        } else {
            throw new IllegalArgumentException(
                    "Arquivo ou pasta inexistente: " + entrada
            );
        }
    }

    private static void processarAutomaticamente(Path entrada)
            throws IOException {

        String nome = entrada.getFileName().toString();

        if (!nome.endsWith(".jack")) {
            throw new IllegalArgumentException(
                    "Arquivo precisa ter extensao .jack"
            );
        }

        String base = nome.substring(0, nome.length() - 5);
        Path saida = entrada.resolveSibling(base + ".vm");

        if (Files.exists(saida)) {
            throw new FileAlreadyExistsException(
                    "VM ja existe; preservando arquivo: " + saida
            );
        }

        processar(entrada, saida);
    }

    // Modo VM
    private static void processar(Path entrada, Path saida)
            throws IOException {

        byte[] codigo = Files.readAllBytes(entrada);

        CompilationEngine engine = new CompilationEngine(codigo);
        String vm = engine.compile();

        Files.writeString(saida, vm, StandardCharsets.UTF_8);

        System.out.println("VM gerado: " + saida);
    }

    // Modo XML
    private static void processarXml(Path entrada, Path saida)
            throws IOException {

        byte[] codigo = Files.readAllBytes(entrada);

        Parser parser = new Parser(codigo);
        String xml = parser.parse();

        Files.writeString(saida, xml, StandardCharsets.UTF_8);

        System.out.println("XML gerado: " + saida);
    }
}