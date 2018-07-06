package main;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Popup;
import model.Result;
import org.apache.commons.io.FilenameUtils;
import processor.extractor.BaseExtractor;
import processor.extractor.CommentExtractor;
import processor.extractor.TextExtractor;
import processor.extractor.XHTMLExtractor;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    Button search;

    @FXML
    ComboBox type;

    @FXML
    ComboBox files;

    @FXML
    TextField selector;

    @FXML
    TextField directory;

    @FXML
    GridPane grid;

    @FXML
    Label result;

    @FXML
    TableView table;

    @FXML
    Button show;

    @FXML
    Button browse;

    @FXML
    ComboBox extension;

    ArrayList<ResultModel> dataList;

    public void initialize() {
        result.setText("Set Directory Path and query to start the Search");
        type.getItems().addAll("Search Type","XHTML",
                                         "Text",
                                         "XHTMLComments");
        extension.getItems().addAll("File Type","XHTML","HTML","XML");
        type.getSelectionModel().selectFirst();
        extension.getSelectionModel().selectFirst();
        files.setVisible(false);
        files.getItems().add("Select");
        table.getItems().clear();
        table.getColumns().clear();
        show.setVisible(false);
    }

    @FXML
    protected void process() {
        table.getItems().clear();
        table.getColumns().clear();
        if (directory == null
                || directory.getText().equalsIgnoreCase("")
                || directory.getText().equalsIgnoreCase("/Example/Directory/Path")
                || directory.getText().equalsIgnoreCase("No Directory Is Selected")) {

            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(new Popup());

                if(selectedDirectory == null){
                    directory.setText("No Directory Is Selected");
                }else{
                    directory.setText(selectedDirectory.getAbsolutePath());
                }

        }

        if (selector.getText().trim().equalsIgnoreCase("")) {
            selector.setText(null);

            if (!type.getSelectionModel()
                    .getSelectedItem().toString()
                    .equalsIgnoreCase("XHTMLComments")) {

                Button[] buttons = new Button[1];
                buttons[0] = new Button();
                buttons[0].setCancelButton(true);
                Alert alert = new Alert(Alert.AlertType.ERROR);
            }
        }

        BaseExtractor extractor;

        if (type.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("Text"))
            extractor = new TextExtractor();
        else if (type.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("XHTMLComments"))
            extractor = new CommentExtractor();
        else if (type.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("XHTML"))
            extractor = new XHTMLExtractor();
        else
            extractor = new XHTMLExtractor();

        try {
            List<Result> results = extractor.getElements(
                    directory.getText(),
                    selector.getText(),
                    false,
                    extension.getSelectionModel().getSelectedItem().toString());

            ArrayList<ResultModel> models = new ArrayList<>();
            this.setColumns();
            System.out.println(results.toString());
            int index[] = {0, 0};
            results.forEach(result1 -> {
                index[1] = 0;

                result1.getElements().forEach(e -> {
                    ResultModel model = new ResultModel(
                            result1.getFilename(),
                            result1.getElementCount(),
                            e.replaceAll("\" ", "\"\n"),
                            index[1] += 1,
                            index[0] += 1,
                            result1.getFilePath()
                            );
                    models.add(model);
                });

                ListCell node = new ListCell();
                node.setText(result1.toString());

            });

            final ObservableList<ResultModel> data =
                    FXCollections.observableArrayList(models);
            this.dataList = models;
                table.setItems(data);

            int totalElements = models.size();
            int totalFileCount = extractor.getTotalFileCount();
            int totalContainingFileCount = extractor.getContainingFileNames().size();

            String totalContainingFileList = extractor.getContainingFileNames().toString();

            files.setVisible(true);
            files.getItems().clear();
            if (extractor.getContainingFileNames() != null
                    && extractor.getContainingFileNames().size() != 0)
                files.getItems().add("Select");
            extractor.getContainingFileNames().forEach(name -> {
                if (!files.getItems().contains(name)) {
                    files.getItems().add((name));

                }
            });

            String resultString = " Find " + totalElements + " elements from " + totalFileCount + " files . \n "
                    + totalContainingFileCount + " Files have elements with the selector "
                    + selector.getText() + " . \n " +
                    "File List " + totalContainingFileList;

            result.setText(resultString);
            table.setEditable(true);
            table.getColumns().remove(table.getColumns().size() -1 );
            files.getSelectionModel().selectFirst();

         } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void filter() {

        ArrayList<ResultModel> filteredList = new ArrayList<>();
        String fileName = null;
        if (files != null && files.getSelectionModel() != null
                && files.getSelectionModel().getSelectedItem() != null)
            fileName = files.getSelectionModel().getSelectedItem().toString();
        table.getItems().clear();
        table.getColumns().clear();
        if (fileName != null && !fileName.equalsIgnoreCase("Select")) {
            String finalFileName = fileName;
            dataList.forEach(m -> {
                if (finalFileName.contains(m.getFileName()))
                    filteredList.add(m);

            });
            final ObservableList<ResultModel> data =
                    FXCollections.observableArrayList(filteredList);
            this.setColumns();
            table.setItems(data);
            show.setVisible(true);
            show.setText("Open "+ FilenameUtils.getName(fileName));
            show.setId("Open "+ fileName);
        } else {
            final ObservableList<ResultModel> data =
                    FXCollections.observableArrayList(dataList);
            this.setColumns();
            table.setItems(data);
            show.setVisible(false);

        }

    }

    @FXML
    protected void open(){
        File file=new File(show.getId().replace("Open ",""));
        if( Desktop.isDesktopSupported() )
            {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
    }

    @FXML
    protected void browse(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(new Popup());
            if(selectedDirectory == null){
                directory.setText("No Directory selected");
            }else{
                directory.setText(selectedDirectory.getAbsolutePath());
            }
        }

    private void setColumns() {

        TableColumn indexCol = new TableColumn();
        TableColumn fileNameCol = new TableColumn();
        TableColumn elementCountCol = new TableColumn();
        TableColumn elementCol = new TableColumn();
        TableColumn elementNumber = new TableColumn();

        fileNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        elementCol.setCellFactory(TextFieldTableCell.forTableColumn());

        indexCol.setText("Index");
        fileNameCol.setText("File Name");
        elementCountCol.setText("Element Count");
        elementCol.setText("Element");
        elementNumber.setText("Element Number");
        elementCol.setMaxWidth(750);
        indexCol.setCellValueFactory
                (new PropertyValueFactory<ResultModel, Integer>("index"));
        fileNameCol.setCellValueFactory
                (new PropertyValueFactory<ResultModel, String>("fileName"));
        elementCountCol.setCellValueFactory
                (new PropertyValueFactory<ResultModel, Integer>("elementCount"));
        elementCol.setCellValueFactory
                (new PropertyValueFactory<ResultModel, String>("element"));
        elementNumber.setCellValueFactory
                (new PropertyValueFactory<ResultModel, Integer>("elementNumber"));
        elementCol.setEditable(true);
        fileNameCol.setEditable(true);
        elementCountCol.setEditable(true);
        elementNumber.setEditable(true);

        this.table.getColumns().clear();
        this.table.getColumns().addAll(
                indexCol,
                fileNameCol,
                elementCountCol,
                elementCol,
                elementNumber);
        table.setEditable(true);
    }

    public static class ResultModel {
        public String getFileName() {
            return fileName.get();
        }

        public SimpleStringProperty fileNameProperty() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName.set(fileName);
        }

        public String getElement() {
            return element.get();
        }

        public SimpleStringProperty elementProperty() {
            return element;
        }

        public int getElementCount() {
            return elementCount.get();
        }

        public SimpleIntegerProperty elementCountProperty() {
            return elementCount;
        }

        public int getElementNumber() {
            return elementNumber.get();
        }

        public SimpleIntegerProperty elementNumberProperty() {
            return elementNumber;
        }

        public void setElement(String element) {
            this.element.set(element);
        }

        @Override
        public String toString() {
            return "ResultModel{" +
                    "  index=" + index +
                    ", fileName=" + fileName +
                    ", elementCount=" + elementCount +
                    ", element=" + element +
                    ", elementNumber=" + elementNumber +
                    '}';
        }

        public SimpleStringProperty getFilePathProperty() {
            return filePath;
        }

        public void setFilePathProperty(SimpleStringProperty filePath) {
            this.filePath = filePath;
        }

        SimpleStringProperty filePath;
        public ResultModel(String fileName,
                           int elementCount,
                           String element,
                           int elementNumber,
                           int index,
                           String filePath) {
            this.fileName = new SimpleStringProperty(fileName);
            this.elementCount = new SimpleIntegerProperty(elementCount);
            this.element = new SimpleStringProperty(element);
            this.elementNumber = new SimpleIntegerProperty(elementNumber);
            this.index = new SimpleIntegerProperty(index);
            this.filePath = new SimpleStringProperty(filePath);
        }

        public int getIndex() {
            return index.get();
        }

        public SimpleIntegerProperty indexProperty() {
            return index;
        }

        public void setIndex(int index) {
            this.index.set(index);
        }

        public void setElementCount(int elementCount) {
            this.elementCount.set(elementCount);
        }

        public void setElementNumber(int elementNumber) {
            this.elementNumber.set(elementNumber);
        }

        private final SimpleIntegerProperty index;
        private final SimpleStringProperty fileName;
        private final SimpleIntegerProperty elementCount;
        private final SimpleStringProperty element;
        private final SimpleIntegerProperty elementNumber;


    }

}
