
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {

        if (args.length == 2) {
            processar(Path.of(args[0]), Path.of(args[1]));
            return;
        }

        if (args.length != 1) {
            System.err.println(
                "Uso: java Main <arquivo.jack | pasta> [saida.xml]"
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
        Path saida = entrada.resolveSibling(base + "P.xml");

        if (Files.exists(saida)) {
            throw new FileAlreadyExistsException(
                "XML ja existe; preservando arquivo: " + saida
            );
        }

        processar(entrada, saida);
    }

    private static void processar(Path entrada, Path saida)
            throws IOException {

        byte[] codigo = Files.readAllBytes(entrada);

        Parser parser = new Parser(codigo);
        String xml = parser.parse();

        Files.writeString(saida, xml, StandardCharsets.UTF_8);

        System.out.println("XML gerado: " + saida);
    }
}
