import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws java.io.IOException {

        String input = "class Main{}";
        Parser par = new Parser(input.getBytes(StandardCharsets.UTF_8));
        String resultado = par.parse();
        System.out.println(resultado);
//        if (args.length != 1) {
//            System.err.println("Uso: java Main <arquivo.jack>");
//            System.exit(1);
//        }
//
//        Scanner scan = new Scanner(Files.readAllBytes(Path.of(args[0])));
//
//        System.out.println("<tokens>");
//        for (Token tk = scan.nextToken(); tk.type != TokenType.EOF; tk = scan.nextToken()) {
//            System.out.println(tk);
//        }
//        System.out.println("</tokens>");



    }
}
