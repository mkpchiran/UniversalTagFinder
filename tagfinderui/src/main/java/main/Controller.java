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
//import org.apache.commons.io.FilenameUtils;
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
//    @FXML
//    TableColumn indexCol, fileNameCol, elementCountCol, elementCol, elementNumberCol;

    ArrayList<ResultModel> dataList;

    public void initialize() {
        result.setText("Set Directory Path and query to start the Search");
        type.getItems().addAll("XHTML",
                "text", "XHTMLComments");
        type.getSelectionModel().selectFirst();
        files.setVisible(false);
        files.getItems().add("Select");
        table.getItems().clear();
        table.getColumns().clear();
        show.setVisible(false);
//        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    protected void process() {
        table.getItems().clear();
        table.getColumns().clear();
        if (directory == null || directory.getText().equalsIgnoreCase("")
                || directory.getText().equalsIgnoreCase("/Example/Directory/Path")
                || directory.getText().equalsIgnoreCase("No Directory selected")) {

            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(new Popup());

                    if(selectedDirectory == null){
                    directory.setText("No Directory selected");
                    }else{
                    directory.setText(selectedDirectory.getAbsolutePath());
                    }

//            directory.setText(System.getProperty("user.home"));
        }

        if (selector.getText().trim().equalsIgnoreCase("")) {
            selector.setText(null);
            if (!type.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("XHTMLComments")) {
                Button[] buttons = new Button[1];
                buttons[0] = new Button();
                buttons[0].setCancelButton(true);
                Alert alert = new Alert(Alert.AlertType.ERROR);
            }
        }

        BaseExtractor extractor;

        if (type.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("text"))
            extractor = new TextExtractor();
        else if (type.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("XHTMLComments"))
            extractor = new CommentExtractor();
        else if (type.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("XHTML"))
            extractor = new XHTMLExtractor();
        else
            extractor = new XHTMLExtractor();

        try {
            List<Result> results = extractor.getElements(directory.getText(), selector.getText(), false);

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
//                result.setText(result.getText()+"\n"+result1.toString());
            });
            final ObservableList<ResultModel> data =
                    FXCollections.observableArrayList(models);
            this.dataList = models;
            table.setItems(data);
//            grid.add(table, 0, 1);
            int totalElements = models.size();
            int totalFileCount = extractor.getTotalFileCount();
            int totalContainingFileCount = extractor.getContainingFileNames().size();
            String totalContainingFileList = extractor.getContainingFileNames().toString();
            ArrayList<FileNameModel> fileNameModels = new ArrayList<>();
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
//            final ObservableList<FileNameModel> fileNames =
//                    FXCollections.observableArrayList(fileNameModels);
            String resultString = " Find " + totalElements + " elements from " + totalFileCount + " files . \n "
                    + totalContainingFileCount + " Files have elements with the selector "
                    + selector.getText() + " . \n " +
                    "File List " + totalContainingFileList;
            result.setText(resultString);
            table.setEditable(true);
            table.getColumns().remove(table.getColumns().size() -1 );
            files.getSelectionModel().selectFirst();
            System.out.println(files.getItems().toString());

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
        System.out.println("");
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
//            TableColumn indexCol = this.indexCol;
//            TableColumn fileNameCol = this.fileNameCol;
//            TableColumn elementCountCol = this.elementCountCol;
//            TableColumn elementCol = this.elementCol;
//            TableColumn elementNumber = this.elementNumberCol;

//            "fileName=" + fileName +
//                    ", elementCount=" + elementCount +
//                    ", element=" + element +
//                    ", elementNumber=" + elementNumber +
//        indexCol.setCellFactory(col -> new IntegerEditingCell());
        fileNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
//        elementCountCol.setCellFactory(TextFieldTableCell.forTableColumn());
        elementCol.setCellFactory(TextFieldTableCell.forTableColumn());
//        elementNumber.setCellFactory(TextFieldTableCell.forTableColumn());

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

/*

    Result :
    Filename : 'ch14-q07-assign02.xhtml'
    ElementCount : 2
    Elements : [<body dataList-chaucer-element-id="466489ae-6a74-49ba-9569-2d46e001f6dd">
 <div dataList-chaucer-element-id="5e91c6f5-276c-40d9-8b07-80eebe523285" class="main-pane" resource="http://purl.org/pearson/asset/ae1ee165c6822dafbc1eee88b68434484e92c8568">
  <section dataList-chaucer-element-id="7cd0d579-f92e-4270-b04f-08c4f5393eaa" dataList-is-assessment="true" class="lc_ec_majorminor practice level2 card" resource="http://purl.org/pearson/asset/ac6412b5d0afc515820e915a0c3c5d121ce7c8cd2">
   <div dataList-chaucer-element-id="b6a4831e-9e86-42be-8640-9ef2794c6f77" id="target1" class="brix" dataList-assignmenturl="http://repo.paf.pearsoncmg.com/paf-repo/resources/activities/3ec7ac05-b7f5-4f2d-97a5-a27375dfbc76" dataList-activityurl="http://repo.paf.pearsoncmg.com/paf-repo/resources/activities/f45c5014-26cd-45ff-b6ac-938e74c96edc" dataList-containerid="target1" dataList-module="assessment-item" dataList-type="brix" resource="http://purl.org/pearson/asset/a4b51290ef7de18fc6fe3320e3a2ad93139bcc3fc">
   </div>
  </section>
 </div>
</body>, <section dataList-chaucer-element-id="7cd0d579-f92e-4270-b04f-08c4f5393eaa" dataList-is-assessment="true" class="lc_ec_majorminor practice level2 card" resource="http://purl.org/pearson/asset/ac6412b5d0afc515820e915a0c3c5d121ce7c8cd2">
 <div dataList-chaucer-element-id="b6a4831e-9e86-42be-8640-9ef2794c6f77" id="target1" class="brix" dataList-assignmenturl="http://repo.paf.pearsoncmg.com/paf-repo/resources/activities/3ec7ac05-b7f5-4f2d-97a5-a27375dfbc76" dataList-activityurl="http://repo.paf.pearsoncmg.com/paf-repo/resources/activities/f45c5014-26cd-45ff-b6ac-938e74c96edc" dataList-containerid="target1" dataList-module="assessment-item" dataList-type="brix" resource="http://purl.org/pearson/asset/a4b51290ef7de18fc6fe3320e3a2ad93139bcc3fc">
 </div>
</section>]
            ]
    Disconnected from the target VM, address: '127.0.0.1:44972', transport: 'socket'

    Process finished with exit code 137 (interrupted by signal 9: SIGKILL)
*/

    public static class FileNameModel {
        public FileNameModel(String fileName) {
            this.fileName = new SimpleStringProperty(fileName);
        }

        public String getFileName() {
            return fileName.get();
        }

        public SimpleStringProperty fileNameProperty() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName.set(fileName);
        }

        private final SimpleStringProperty fileName;

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
