package processor;

import model.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by chiranz on 7/14/17.
 */
public class Extactor {
    private static final Logger log = Logger.getLogger(Extactor.class.getName());

    private String PATTERN = ".";

    public List<Element> getElements(String bookPath, String pattern, boolean withparent, boolean withresult,Type type) throws Exception {
        PATTERN = pattern;
        final int[] total = {0};
        final int[] totalFilecount = {0};
        List<Element> elementsList = new ArrayList<>();
        List<Result> results = new ArrayList<>();
        ArrayList<String> containigFileName = new ArrayList<>();
        List<File> files = getFiles(bookPath, "XHTML");
        List<Comment> comments = new ArrayList<Comment>();

        if (type==Type.text) {
            final String[] subQuery = {null};
            subQuery[0] = pattern;
            files.stream().forEach(p -> {
                ArrayList<String> strings = new ArrayList<>();

                totalFilecount[0] += 1;

                try {
                    Result result = new Result();
                    result.setFilename(p.getName());
                    Document doc = Jsoup.parse(p, "UTF-8");
                    String str = doc.html();
                    ArrayList<String> matchStrings = getCount(str, subQuery[0]);
                    matchStrings.forEach(s -> {
                        if (matchStrings.size() > 0) {
                            System.out.println("*************************************************************");

                            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

                            log.log(Level.INFO, p.getName() + " : \n" + s);
                            strings.add(s);

                            containigFileName.add(p.getName());
                            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//                            System.out.println(p.getName()+" : "+element.toString());
                            result.setElements(strings);
                        }
                        if (matchStrings.size() > 0)
                            System.out.println("*************************************************************");


                    });

                    if (matchStrings.size() > 0) {
                        total[0] += matchStrings.size();
                        result.setElementCount(matchStrings.size());
                        results.add(result);
                        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//                          System.out.println(p.getName()+" has  "+elements.size()+" elements");
                        log.log(Level.INFO, p.getName() + " has  " + matchStrings.size() + " Strings ");
                        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } else if (type==Type.comment||pattern.startsWith("#comment")) {

            final String[] subQuery = {null};
            subQuery[0] = pattern;//.replace("#comment", "");
            files.stream().forEach(p -> {
                totalFilecount[0] += 1;
                try {
                    Result result = new Result();
                    result.setFilename(p.getName());
                    Document doc = Jsoup.parse(p, "UTF-8");
                    List<Node> nodes = doc.childNodes();
                    ArrayList<String> strings = new ArrayList<>();
                    int commentsListSize = 0;
                    List<String> fileCommentList = new ArrayList<>();
                    nodes.forEach(node -> {
                        List<String> commentsList = getComments(node, p, strings);
                        fileCommentList.addAll(commentsList);

                    });

                    ArrayList<Element> commentedElements = new ArrayList<>();
                    fileCommentList.forEach(s -> {
                        if (subQuery[0] != ""||subQuery[0] !=null) {
                            doc.body().html(s);
                            Elements elements = doc.select(subQuery[0]);
                            commentedElements.addAll(elements);
                        }

                    });
                    result.setElementCount(commentedElements.size());
//                    result.setElements(strings);
                    commentedElements.forEach(element -> {
                        if (commentedElements.size() > 0) {
                            System.out.println("*************************************************************");

                            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                            if (withparent) {
                                log.log(Level.INFO, p.getName() + " : \n" + element.parent().toString());
                                strings.add(element.parent().toString());
                            } else {
                                log.log(Level.INFO, p.getName() + " : \n" + element.toString());
                                strings.add(element.toString());
                            }
                            containigFileName.add(p.getName());
                            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//                            System.out.println(p.getName()+" : "+element.toString());
                            result.setElements(strings);
                        }
                        if (commentedElements.size() > 0)
                            System.out.println("*************************************************************");
                    });
                    if (commentedElements.size() > 0) {
                        total[0] += commentedElements.size();
                        result.setElementCount(commentedElements.size());
                        results.add(result);
                        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//                          System.out.println(p.getName()+" has  "+elements.size()+" elements");
                        log.log(Level.INFO, p.getName() + " has  " + commentedElements.size() + " commented elements");
                        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

        } else {

            files.stream().forEach(p -> {
                        try {
                            totalFilecount[0] += 1;
                            Document doc = Jsoup.parse(p, "UTF-8");
                            Elements elements = doc.select(PATTERN);
                            if (elements.size() > 0)
                                System.out.println("*************************************************************");

//                        List<String> ids = divs.stream().map(n ->
//                                n.attr("id")
//                        ).collect(Collectors.toList());
                            Result result = new Result();
                            result.setFilename(p.getName());
                            elementsList.addAll(elements);
                            ArrayList<String> strings = new ArrayList<>();
                            elements.forEach(element -> {
                                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                                if (withparent) {
                                    log.log(Level.INFO, p.getName() + " : \n" + element.parent().toString());
                                    strings.add(element.parent().toString());
                                } else {
                                    log.log(Level.INFO, p.getName() + " : \n" + element.toString());
                                    strings.add(element.toString());
                                }
                                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//                            System.out.println(p.getName()+" : "+element.toString());
                                result.setElements(strings);
                            });
                            total[0] += elements.size();
                            if (elements.size() > 0) {
                                result.setElementCount(elements.size());
                                results.add(result);
                                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//                        System.out.println(p.getName()+" has  "+elements.size()+" elements");
                                log.log(Level.INFO, p.getName() + " has  " + elements.size() + " elements");
                                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                                containigFileName.add(p.getName());

                            }
                            if (elements.size() > 0)
                                System.out.println("*************************************************************");
                        } catch (IOException e) {
                            log.log(Level.SEVERE, e.getMessage());

                            e.printStackTrace();
                        }

                    }
            );
        }
//        System.out.println(bookPath + " has  " + total[0] + " elements");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        if (withresult) {
            System.out.println("@@@@@@@@         FINAL              RESULT @@@@@@@@@@@@@@@@@@");
            log.log(Level.INFO, bookPath + " Result  " + results.toString());
        }
        System.out.println("@@@@@@@@@@@@@@@@      SUMMARY   @@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        log.log(Level.INFO, bookPath + " has  " + total[0] + " Elements");
        log.log(Level.INFO, bookPath + " has  " + results.size() + " XHTML files with relevant elements");
        log.log(Level.INFO, bookPath + " has  " + containigFileName.size() + " XHTML files with elements equal with duplication occurancew");
        log.log(Level.INFO, bookPath + " containing files are " + containigFileName.toString());
        log.log(Level.INFO, bookPath + " has  " + totalFilecount[0] + " XHTMLFiles");
        log.log(Level.INFO, " For more info about css selectors visit " + new URL("https://www.w3schools.com/cssref/css_selectors.asp"));

        return elementsList;
    }

    public List<File> getFiles(String path, String extention) throws IOException {
        return Files.walk(Paths.get(path)).filter(p -> p.toString().toUpperCase().endsWith("." + extention.toUpperCase())).distinct().
                map(n -> n.toFile()).collect(Collectors.toList());
    }

    private List<String> getComments(Node node, File p, ArrayList<String> strings) {
//        List<Comment> comments = new ArrayList<Comment>();
        List<String> comments = new ArrayList<String>();
        int i = 0;
        while (i < node.childNodes().size()) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment")) {
//                comments.add((Comment) child);

                comments.add(child.attr("comment"));
//                log.log(Level.INFO, p.getName() + " : \n" + child.toString());
                strings.add(child.toString());
            } else {
                comments.addAll(getComments(child, p, strings));
            }
            i++;
        }
        return comments;
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

//    public static void main(String[] args) {
//        Extactor extactor = new Extactor();
//        try {
//            extactor.getElements("/home/chiranz/Documents/CITE/packages/ford_brown-dkc_v5_cite/OPS/", "#comment.img", true, false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
