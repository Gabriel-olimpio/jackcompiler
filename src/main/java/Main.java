import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws java.io.IOException {

        if(args.length != 2) {
            System.err.println("Uso: java Main <entrada.jack> <saida.xml>");
            System.exit(1);
        }

        Path inputPath = Path.of(args[0]);
        Path outputPath = Path.of(args[1]);

        byte[] bytesRead = Files.readAllBytes(inputPath);

        Parser par = new Parser(bytesRead);
        String result = par.parse();
        Files.writeString(outputPath, result, StandardCharsets.UTF_8);
        System.out.println("XML foi gerado; path: " + outputPath);


    }
}
