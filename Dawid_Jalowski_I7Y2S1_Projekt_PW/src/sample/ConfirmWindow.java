package sample;


import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmWindow
{
    static boolean anwser = false;
    public static boolean display (String title, String question, String positiveButton)

    {
        Stage window = new Stage();
        VBox mainPane = new VBox();
        mainPane.spacingProperty().setValue(20);
        mainPane.setMinWidth(270);
        mainPane.setAlignment(Pos.CENTER);
        mainPane.setStyle("-fx-background-color: #2d2d2d");
        window.setTitle(title);
        Scene scene = new Scene (mainPane, 270, 200);
        Text komunikat = new Text(question);
        komunikat.setFont(Font.font("Calibri", 22));
        komunikat.setFill(Color.WHITE);


        Button yesButton = new Button(positiveButton);
        yesButton.setTranslateX(100);
        yesButton.setOnAction( e-> {
            anwser = true;
            window.close();
        });

        mainPane.getChildren().addAll(komunikat,yesButton);




        window.setScene(scene);
        window.showAndWait();
        return anwser;
    }


}
