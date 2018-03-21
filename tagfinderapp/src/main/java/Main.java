import parser.Cli;

/**
 * Created by chiranz on 7/14/17.
 */
public class Main {
    public static void main(String[] args) {

        try {
            new Cli(args).parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
