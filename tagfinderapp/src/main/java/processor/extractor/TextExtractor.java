package processor.extractor;

import model.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chiranz on 7/22/17.
 */
public class TextExtractor extends BaseExtractor {
    @Override
    public List<Result> getElements(String bookPath, String pattern, boolean withParent) throws IOException {

        List<File> files = getFiles(bookPath, "XHTML");
        this.setTotalFileCount(files.size());
        if(pattern==null||pattern.equalsIgnoreCase("")){
            return results;
        }
        files.stream().forEach(p -> {
            ArrayList<String> strings = new ArrayList<>();
            try {
                Result result = new Result();
                result.setFilename(p.getName());
                Document doc = Jsoup.parse(p, "UTF-8");
                String str = doc.html();
                ArrayList<String> matchStrings = getCount(str, pattern);
                matchStrings.forEach(s -> {
                    if (matchStrings.size() > 0) {
                        strings.add(s);
                        result.setElements(strings);
                    }

                });

                if (matchStrings.size() > 0) {
                    result.setFilename(p.getName());
                    result.setFilePath(p.getAbsolutePath());
                    result.setElementCount(matchStrings.size());
                    results.add(result);
                    containingFileNames.add(p.getAbsolutePath());

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return results;
    }

    private ArrayList<String> getCount(String str, String match) {

        ArrayList<String> strings = new ArrayList();

        int lastindex = 0;
        int count = 0;
        while (lastindex != -1) {
            lastindex = str.indexOf(match, lastindex);

            if (lastindex != -1) {
                if (lastindex != 0 && str.length() >= lastindex + match.length() + 3) {
//                    System.out.println(str.substring(lastindex - 1, lastindex + match.length() + 3));
                    strings.add(str.substring(lastindex - 1, lastindex + match.length() + 3));
                }
                count++;
                lastindex += match.length();
            }
        }
        return strings;
    }

}
