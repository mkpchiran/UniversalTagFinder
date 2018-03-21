package parser;

import org.junit.Assert;
import org.junit.Test;
import processor.ExtractorTest;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by chiranz on 7/15/17.
 */

public class CliTest {

    @Test
    public void shouldparseTest() throws MalformedURLException {
        URL path = ExtractorTest.class.getClassLoader().getResource("test-files");

        String[] args={"-p",path.getPath(),"-q","div"};
        Cli cli=new Cli(args);
       Assert.assertEquals(cli.parse(),true);

    }
}
