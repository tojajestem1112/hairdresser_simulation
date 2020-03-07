package sample;


import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Klient extends Thread
{
    private Semaphore BinarySemaphore;
    private Semaphore klient ;
    private Semaphore fryzjer;
    private Semaphore fotel;
    private ReentrantLock lockkl;

    private StackPane person;
    private Text tekst;
    private int index;
    private int czekanie;
    private volatile int numerPoczekalni=0;
    private int poj_poczek;
    private volatile boolean obciety = false;
    private boolean readyForHairDresser = false;
    private volatile List<Klient> listaKlientowWPoczekalni;
    private int rodzaj_uslugi;
    private volatile int tablicaPoczekalni[];
    private volatile boolean koniecAnimacji = false;
    ReentrantLock lock;
    Rectangle rectangle;
    Klient(Semaphore BinarySemaphore, Semaphore klient, Semaphore fryzjer,
           Semaphore fotel, int index, int poj_poczek, List<Klient> listaKlientowWPoczekalni,int iloscUslug,
           int[] tablicaPoczekalni, StackPane person, ReentrantLock lock, Text text, Rectangle client)
    {

        tekst = text;
        this.BinarySemaphore = BinarySemaphore;
        this.klient = klient;
        this.fryzjer = fryzjer;
        this.fotel = fotel;
        this.index = index;
        this.poj_poczek = poj_poczek;
        this.listaKlientowWPoczekalni =listaKlientowWPoczekalni;
        this.tablicaPoczekalni = tablicaPoczekalni;
        this.person = person;
        Random los = new Random();
        rodzaj_uslugi = los.nextInt(iloscUslug) + 1;
        lockkl = lock;
        tekst.setText("U:"+rodzaj_uslugi);
        rectangle = client;
    }

    public void run()
    {
    Thread.currentThread().setName("K"+index);
        SequentialTransition seq = new SequentialTransition(
                createTransition(40,250,2000),
                createTransition(40,150,1000)

        );
        Platform.runLater( () ->
        {
            person.setVisible(true);
            seq.play();
        });
        seq.setOnFinished(e ->
        {
            koniecAnimacji = true;
        });
        czekaj();
        int pozwolenie = 0;
        koniecAnimacji = false;
        try {
            BinarySemaphore.acquire();

            if(listaKlientowWPoczekalni.size()<poj_poczek) {
                listaKlientowWPoczekalni.add(this);

                lockkl.lock();
                for (int i = 0; i < listaKlientowWPoczekalni.size(); i++) {
                    System.out.println(index);

                    if (tablicaPoczekalni[i] == 0) {
                        System.out.println("Ja " + index + "Wybieram " + i);
                        pozwolenie = 1;
                        tablicaPoczekalni[i] = 1;
                        numerPoczekalni = i;
                        lockkl.unlock();
                        break;
                    }

                }

                if (pozwolenie == 0) {
                    lockkl.unlock();

                    BinarySemaphore.release();
                }
                else {
                    klient.release();
                    System.out.println("Klient o indeksie " + index + "siada w poczekalni nr " + numerPoczekalni + " i rzada uslugi nr " + rodzaj_uslugi);
                    BinarySemaphore.release();
                    TranslateTransition wejscieDoPoczekalni = createTransition(person.getTranslateX(), person.getTranslateY() - 50 * numerPoczekalni - 60, 200);
                    Platform.runLater(() -> wejscieDoPoczekalni.play());
                    wejscieDoPoczekalni.setOnFinished(e ->
                    {
                        koniecAnimacji = true;
                        readyForHairDresser = true;
                    });
                    czekaj();
                    fryzjer.acquire();


                    while (!obciety) {
                        //Strzyzenie
                    }

                }
            }
            else
            {
                FillTransition ft = new FillTransition(Duration.millis(3000), rectangle, Color.RED, Color.ORANGE);
                ft.setCycleCount(0);
                ft.setAutoReverse(true);

                System.out.println("Brak miejsca w poczekalni do klienta o indeksie "+index);
                BinarySemaphore.release();
                SequentialTransition leaving = new SequentialTransition();
                leaving.getChildren().addAll(createTransition(10,150, 500),
                        createTransition(10,250, 1000),
                        createTransition(-410,250, 2000)
                );
                Platform.runLater( () ->
                {
                    leaving.play();
                    ft.play();
                });
                leaving.setOnFinished(e ->
                {
                    koniecAnimacji = true;
                });

            }
        } catch (InterruptedException e)
        {
            System.out.println("Exception!");
        }


        czekaj();

        System.out.println("Klient " + index + "zostal wyszedl z salonu");
        Platform.runLater( () ->
        {
            person.setVisible(false);
        });
    }


    public int getIndex()
    {
        return index;
    }
    public boolean isObciety()
    {
        return obciety;
    }
    public void setObciety (boolean obciety)
    {
        System.out.println("Zmiana stanu! " + obciety);
        this.obciety = obciety;


        SequentialTransition leaving = new SequentialTransition();
        leaving.getChildren().addAll(
                createTransition(person.getTranslateX()-10,person.getTranslateY(), 400),
                createTransition(person.getTranslateX()-10,person.getTranslateY()+20, 400),
                createTransition(person.getTranslateX()+70,person.getTranslateY()+20, 400),
                createTransition(person.getTranslateX()+70,150, 400),
                createTransition(person.getTranslateX()+200,150, 1000),
                createTransition(10,150, 1000),
                createTransition(10,250, 1000),
                createTransition(-410,250, 2000)
        );
        Platform.runLater( () ->
        {
            leaving.play();
        });
        leaving.setOnFinished(e ->
        {
            koniecAnimacji = true;
            Platform.runLater( () ->
            {
                person.setVisible(false);
            });
        });

    }
    public int getRodzajUslugi()
    {
        return rodzaj_uslugi;
    }
    private TranslateTransition createTransition(double x, double y, int time )
    {
        TranslateTransition trans = new TranslateTransition();
        trans.setDuration(Duration.millis(time));
        trans.setToX(x);
        trans.setToY(y);
        trans.setNode(person);
        return trans;
    }
    private void czekaj()
    {
        while(!koniecAnimacji)
        {
            czekanie = 1;
            try {

                sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            czekanie = 0;
        }
        koniecAnimacji = false;
    }
    public void move(double x, double y, int time)
    {
        TranslateTransition temp = this.createTransition(x,y,time);
        Platform.runLater(()-> temp.play());
    }
    public boolean getReadyforHairDresser()
    {
        return readyForHairDresser;
    }
    public void zwolnijMiejsceWPoczekalni()
    {
        tablicaPoczekalni[numerPoczekalni] = 0;
        numerPoczekalni = 0;
    }
    public StackPane getPerson()
    {
        return person;
    }
    public void colorize(int los)
    {
        FillTransition ft = new FillTransition(Duration.millis(los), rectangle, Color.BLUE, Color.LIGHTGREEN);
        ft.setCycleCount(0);
        ft.setAutoReverse(true);
        Platform.runLater(()-> ft.play());

    }
}
