package model;

import java.util.ArrayList;

/**
 * Created by chiranz on 7/15/17.
 */
public class Result {
    @Override
    public String toString() {
        return "\nResult : \n" +
                "Filename : '" + filename + '\'' +
                "\nElementCount : " + elementCount +
                "\nElements : " + elements +
                '\n';
    }

    String filename;

    public int getElementCount() {
        return elementCount;
    }

    public void setElementCount(int elementCount) {
        this.elementCount = elementCount;
    }

    int elementCount;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ArrayList<String> getElements() {
        return elements;
    }

    public void setElements(ArrayList<String> elements) {
        this.elements = elements;
    }

    ArrayList<String> elements;
}
