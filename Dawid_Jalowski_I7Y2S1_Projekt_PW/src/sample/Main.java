package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.concurrent.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));


        primaryStage.setTitle("Hello World");
        Scene mainScene = new Scene(root, 1300, 625);
        primaryStage.setMaxHeight(625);
        primaryStage.setMaxWidth(1300);
        primaryStage.setMinHeight(625);
        primaryStage.setMinWidth(1300);

        primaryStage.setScene(mainScene);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
