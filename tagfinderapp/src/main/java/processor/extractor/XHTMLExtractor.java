package processor.extractor;

import model.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chiranz on 7/22/17.
 */
public class XHTMLExtractor extends BaseExtractor {

    @Override
    public List<Result> getElements(String bookPath, String pattern, boolean withParent) throws IOException {

        PATTERN = pattern;
        if (pattern == null || pattern.equalsIgnoreCase("")) {
            return results;
        }
        List<File> files = getFiles(bookPath, "XHTML");
        this.setTotalFileCount(files.size());
        files.stream().forEach(p -> {
                    try {
                        Document doc = Jsoup.parse(p, "UTF-8");
                        Elements elements = doc.select(PATTERN);
                        Result result = new Result();
                        result.setFilename(p.getName());
                        elementsList.addAll(elements);
                        ArrayList<String> strings = new ArrayList<>();
                        elements.forEach(element -> {
                            if (withParent) {
                                if (element.parent() != null)
                                    strings.add(element.parent().toString());
                                else
                                    strings.add(element.toString());

                            } else {
                                strings.add(element.toString());
                            }
                            result.setElements(strings);
                        });
                        if (elements.size() > 0) {
                            result.setElementCount(elements.size());
                            results.add(result);
                            containingFileNames.add(p.getName());

                        }
                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                }
        );
        return results;
    }
}
