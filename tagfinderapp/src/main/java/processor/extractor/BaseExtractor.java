package processor.extractor;

import model.Result;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by chiranz on 7/22/17.
 */
public abstract class BaseExtractor {

    protected String PATTERN = ".";

    public List<Element> getElementsList() {
        return elementsList;
    }

    public void setElementsList(List<Element> elementsList) {
        this.elementsList = elementsList;
    }

    public int getTotalFileCount() {
        return totalFileCount;
    }

    public void setTotalFileCount(int totalFileCount) {
        this.totalFileCount = totalFileCount;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public ArrayList<String> getContainingFileNames() {
        return containingFileNames;
    }

    public void setContainingFileNames(ArrayList<String> containingFileNames) {
        this.containingFileNames = containingFileNames;
    }

    protected List<Element> elementsList = new ArrayList<>();
    List<Result> results = new ArrayList<>();
    protected ArrayList<String> containingFileNames = new ArrayList<>();
    protected int totalFileCount=0;

    public List<File> getFiles(String path, String extention) throws IOException {
        return Files.walk(Paths.get(path)).filter(p -> p.toString().toUpperCase().endsWith("." + extention.toUpperCase())).distinct().
                map(n -> n.toFile()).collect(Collectors.toList());
    }

   public List<Result> getElements(String bookPath,
                                   String pattern,
                                   boolean withParent
                                            ) throws IOException{

        return null;
   }
}
