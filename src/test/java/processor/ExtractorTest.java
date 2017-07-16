
package processor;

import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.util.List;


/**
 * Created by chiranz on 7/15/17.
 */

public class ExtractorTest {

    @Test(expected = Exception.class)
    public void getElements() throws Exception {
        URL path = ExtractorTest.class.getClassLoader().getResource("test-files/knowledgeCheck");
        Extactor extactor = new Extactor();
        List<Element> elements = extactor.getElements(path.getPath(),"p",true,false);
        Assert.assertTrue(elements.size()>0);

    }
}

