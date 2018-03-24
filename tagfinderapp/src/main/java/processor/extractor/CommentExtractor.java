package processor.extractor;

import model.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chiranz on 7/22/17.
 */
public class CommentExtractor extends BaseExtractor {

    @Override
    public List<Result> getElements(String bookPath, String pattern, boolean withParent) throws IOException {
        PATTERN = pattern;
        List<File> files = getFiles(bookPath, "XHTML");
        this.setTotalFileCount(files.size());
        files.stream().forEach(p -> {
            try {
                Result result = new Result();
                result.setFilename(p.getName());
                Document doc = Jsoup.parse(p, "UTF-8");
                List<Node> nodes = doc.childNodes();
                ArrayList<String> strings = new ArrayList<>();
                List<String> commentedElements = new ArrayList<>();
                nodes.forEach(node -> {
                    List<String> commentsList = getComments(node);
                    commentedElements.addAll(commentsList);

                });

                commentedElements.forEach(s -> {
                    if (pattern == null) {
                        strings.add(s);

                    } else if (pattern != "") {
                        doc.body().html(s);
                        Elements elements = doc.select(pattern);
                        elements.forEach(element -> {
                            strings.add(element.toString());
                        });
                    } else {
                        strings.add(s);
                    }

                });
                result.setElementCount(strings.size());
                result.setFilename(p.getName());
                result.setFilePath(p.getAbsolutePath());
                result.setElements(strings);
                if (strings.size() > 0) {
                    results.add(result);
                    containingFileNames.add(p.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        return results;
    }

    private List<String> getComments(Node node) {
        List<String> comments = new ArrayList<>();
        int i = 0;
        while (i < node.childNodes().size()) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment")) {
                comments.add(child.attr("comment"));
//                comments.add(child.toString());
            } else {
                comments.addAll(getComments(child));
            }
            i++;
        }
        return comments;
    }

}
