import diagram.Diagram;
import diagram.Parser;

import java.io.IOException;
import java.nio.file.Paths;

public class Verifier {
    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        final Diagram d = parser.parse(Paths.get("/home/nikita/development/ltl/src/main/resources/diagram1.xml"));
        System.out.println(d.toString());
        System.out.println(d.getName());

    }
}