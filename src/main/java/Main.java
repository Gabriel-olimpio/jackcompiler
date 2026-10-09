import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws java.io.IOException {

        byte[] input = java.nio.file.Files.readAllBytes(java.nio.file.Path.of("src/test/Main.jack")) ;
        Scanner scan = new Scanner(input);

        System.out.println("<tokens>");
        for (Token tk = scan.nextToken(); tk.type != TokenType.EOF; tk = scan.nextToken()) {
            System.out.println(tk);
        }
        System.out.println("</tokens>");
    }
}