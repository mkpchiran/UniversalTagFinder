package processor.extractor;

import model.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.lang.model.util.Elements;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniqueElement extends BaseExtractor{

    public Map<String,String> getElementsMap(String bookPath) throws IOException {

        List<File> files = getFiles(bookPath, "XHTML");

        Map<String,String> stringMap=new HashMap<>();
        files.stream().forEach(p -> {
            try {
                Document doc = Jsoup.parse(p, "UTF-8");
               org.jsoup.select.Elements elements= doc.body().getAllElements();

               elements.forEach(element -> {
                   stringMap.put(element.cssSelector(),element.html());

               });

            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        return stringMap;
    }
//
//    public static void main(String[] args) {
//        try {
//            new UniqueElement().getElementsMap("/home/chiran/Documents/Test/Titles/ORIGINAL/T03/hibbeler10-062316-pr");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
