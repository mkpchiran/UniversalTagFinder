package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Main.class.getResource("/main.fxml"));
//root=FXMLLoader.load(new URL("src/main/sample.fxml"));
        primaryStage.setTitle("Universal Element Finder");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.setResizable(true);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
