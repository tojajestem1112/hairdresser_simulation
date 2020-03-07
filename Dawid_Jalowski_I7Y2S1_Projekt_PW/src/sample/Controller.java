package sample;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;


import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

import javafx.fxml.Initializable;
import javafx.scene.text.Text;

public class Controller implements Initializable
{
    int pojemnoscPoczekalni;
    int liczbaFoteli;
    int liczbaKlientow = 100;
    int iloscUslug = 11;
    int liczbaFryzjerow;

    int [] tablicaPoczekalni;
    int [] tablicaFoteli;

    Semaphore BinarySemaphore = new Semaphore(1);
    Semaphore klient = new Semaphore(0);
    Semaphore fryzjer = new Semaphore (0);
    Semaphore fotel;
    ReentrantLock lockfr = new ReentrantLock();
    ReentrantLock lockkl = new ReentrantLock();
    StackPane[] kwadratyKlientow;
    StackPane[] kwadratyFryzjerow;
    Text []service;
    Rectangle [] rectangles;
    Text [] legendaFryzjerow;
    @FXML
    Button startButton;
    @FXML
    BorderPane mainPane;
    @FXML
    StackPane centerPane;
    @FXML
    TextField Lfryz;
    @FXML
    ComboBox Lfo;
    @FXML
    ComboBox Lpo;
    @FXML
    TextField Lus;
    @FXML
    TextField Lkl;
    @FXML
    VBox LegFryz;
    @FXML
    VBox poczekalnia;
    @FXML
    VBox lustra;
    @FXML
    VBox Fotele;
    @FXML
void onStartButtonPressed()
{
    int test;
    if((test =checknumbers())==1) {
        Lpo.setDisable(true);
        Lfo.setDisable(true);
        Lus.setDisable(true);
        Lkl.setDisable(true);
        Lfryz.setDisable(true);

        startButton.setVisible(false);
        new Thread(() ->
        {
            List<Klient> listaKlientowWPoczekalni = new LinkedList<Klient>();

            Random los = new Random();
            for (int i = 0; i < liczbaFryzjerow; i++) {
                Fryzjer fryz = new Fryzjer(BinarySemaphore, klient,
                        fryzjer, fotel, i + 1, listaKlientowWPoczekalni,
                        iloscUslug, liczbaFryzjerow, tablicaFoteli, kwadratyFryzjerow[i], lockfr, legendaFryzjerow[i]);
                fryz.start();
            }
            for (int i = 0; i < liczbaKlientow; i++) {

                try {
                    sleep(los.nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Klient klie = new Klient(BinarySemaphore, klient, fryzjer, fotel, i + 1,
                        pojemnoscPoczekalni, listaKlientowWPoczekalni, iloscUslug,
                        tablicaPoczekalni, kwadratyKlientow[i], lockkl, service[i], rectangles[i]);
                klie.start();
            }
        }
        ).start();
    }
    else
    {
        System.out.println("ERROR");
        if(test == -1)
        {
            ConfirmWindow.display("Bląd", "WNieprawidłowa ilość fryzjerów", "OK");
        }
        if(test == 0)
        {
            ConfirmWindow.display("Bląd", "Dane można wprowadzać\nwyłącznie w postaci licbowej", "OK");
        }
        if(test == -2)
        {
            ConfirmWindow.display("Bląd", "Nieprawidłowa liczba klientów", "OK");
        }
        if(test == -3)
        {
            ConfirmWindow.display("Bląd", "Nieprawidłowa liczba usług", "OK");
        }
        if(test == -4)
        {
            ConfirmWindow.display("Bląd", "Wybierz dane z listy", "OK");
        }
        if(test == -5)
        {
            ConfirmWindow.display("Bląd", "Liczba foteli musi być\nmniejsza od liczby fryzjerów.", "OK");
        }
        if(test == -6)
        {
            ConfirmWindow.display("Bląd", "Ilość usług nie\n może przekraczać 10", "OK");
        }
    }
}

public void initialize(URL var1, ResourceBundle var2)
{
    for(int i=0; i<6; i++)
    {
        Lpo.getItems().add(""+(i+1));
        Lfo.getItems().add(""+(i+1));
    }


}
    void prepareGUI()
    {
        for(int i=0; i<liczbaFryzjerow; i++)
        {
            legendaFryzjerow[i] = new Text("A");
            legendaFryzjerow[i].setFill(Color.WHITESMOKE);
            LegFryz.getChildren().add(legendaFryzjerow[i]);
            kwadratyFryzjerow[i] = new StackPane();
            Text number = new Text(""+(i+1));
            Rectangle fryz = new Rectangle(25, 25);
            fryz.setFill(Color.AZURE);
            kwadratyFryzjerow[i].setVisible(false);
            kwadratyFryzjerow[i].getChildren().addAll(fryz, number);
            centerPane.getChildren().add(kwadratyFryzjerow[i]);
            kwadratyFryzjerow[i].setTranslateX(i*35-250);
            kwadratyFryzjerow[i].setTranslateY(-250);
        }
        for(int i=0; i<liczbaKlientow; i++)
        {
            kwadratyKlientow[i] = new StackPane();
            Text number1 = new Text((i+1)+"");

            Text number = new Text(""+(i+1));
            service[i] = number;
            Rectangle fryz = new Rectangle(25, 25);
            rectangles[i] = fryz;
            fryz.setFill(Color.BLUE);
            kwadratyKlientow[i].setVisible(false);
            number1.setTranslateY(-7);
            number.setTranslateY(5);
            kwadratyKlientow[i].getChildren().addAll(fryz, number,number1);
            centerPane.getChildren().add(kwadratyKlientow[i]);
            kwadratyKlientow[i].setTranslateX(200);
            kwadratyKlientow[i].setTranslateY(250);
        }
        for (int i=0; i<pojemnoscPoczekalni; i++)
        {
            Rectangle fotel = new Rectangle(30,30);
            fotel.setFill(Color.TOMATO);
            poczekalnia.getChildren().add(fotel);
        }
        for(int i=0; i<liczbaFoteli; i++)
        {
            Rectangle fotel = new Rectangle(10,30);
            fotel.setFill(Color.LIGHTBLUE);
            Rectangle fotel2 = new Rectangle(30, 30);
            fotel2.setFill(Color.TOMATO);
            lustra.getChildren().add(fotel);
            Fotele.getChildren().add(fotel2);
        }
    }
    int checknumbers()
    {

        if(Lpo.getSelectionModel().getSelectedItem()==null || Lfo.getSelectionModel().getSelectedItem()==null)
            return -4;

        pojemnoscPoczekalni = Integer.parseInt((String)Lpo.getSelectionModel().getSelectedItem());
        System.out.println(pojemnoscPoczekalni);
        liczbaFoteli = Integer.parseInt((String)Lfo.getSelectionModel().getSelectedItem());

        try {
            liczbaFryzjerow = Integer.parseInt(Lfryz.getText());
            liczbaKlientow = Integer.parseInt(Lkl.getText());
            iloscUslug = Integer.parseInt(Lus.getText());
        }catch(NumberFormatException e) {
            return 0;
        }
        if(liczbaFryzjerow<=0 || liczbaFryzjerow >= 14)
        {
            return -1;
        }
        if(liczbaKlientow<=0)
        {
            return -2;
        }
        if(iloscUslug<=0)
        {
            return -3;
        }
        if(liczbaFoteli>=liczbaFryzjerow)
        {
            return -5;
        }
        if(iloscUslug>10)
        {
            return -6;
        }
        tablicaPoczekalni = new int [pojemnoscPoczekalni];
        tablicaFoteli = new int [liczbaFoteli];
        kwadratyKlientow= new StackPane[liczbaKlientow];
        kwadratyFryzjerow= new StackPane[liczbaFryzjerow];
        service = new Text [liczbaKlientow];
        rectangles = new Rectangle [liczbaKlientow];
        fotel = new Semaphore(liczbaFoteli);
        legendaFryzjerow = new Text[liczbaFryzjerow];
        prepareGUI();
        return 1;
    }

}
