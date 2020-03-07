package sample;

import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class Fryzjer extends Thread
{
    ReentrantLock lockfr ;
    Semaphore BinarySemaphore;
    Semaphore klient ;
    Semaphore fryzjer;
    Semaphore fotel;

    StackPane person;


    int index;
    volatile private int numerKlienta = 0;
    private volatile List<Klient> listaKlientowWPoczekalni;
    volatile Klient strzyzonyKlient;
    int iloscUslug;
    int numerFotela = 0;
    private int specjalizacja[];
    private int iloscFryzjerow;
    private volatile int [] tablicaFoteli;
    volatile int number;
    private Text legendaText;

    Fryzjer(Semaphore BinarySemaphore, Semaphore klient, Semaphore fryzjer,
            Semaphore fotel, int index, List<Klient> listaKlientowWPoczekalni, int iloscUslug, int iloscFryzjerow,
            int [] tablicaFoteli, StackPane person, ReentrantLock lock, Text fryzText)
    {
        this.BinarySemaphore = BinarySemaphore;
        this.klient = klient;
        this.fryzjer = fryzjer;
        this.fotel = fotel;
        this.index = index;
        this.listaKlientowWPoczekalni = listaKlientowWPoczekalni;
        this.iloscUslug = iloscUslug;
        this.iloscFryzjerow = iloscFryzjerow;
        this.tablicaFoteli = tablicaFoteli;
        this.person = person;
        person.setVisible(true);
        specjalizacja = new int[iloscUslug];
        lockfr = lock;
        legendaText = fryzText;
    }

    public void run()
    {
        String zmienna = "Fryzjer "+index+ " :";
        int pozycja =0;
        for(int i=0; i<specjalizacja.length;i++)
        {
            if(iloscUslug>=iloscFryzjerow)
            {
                if((i+1)%index ==0)
                    specjalizacja[i] = 1;

                else
                    specjalizacja[i] = 0;
            }
            else
            {
                if(index%(i+1)==0)
                    specjalizacja[i] = 1;
                else specjalizacja[i] = 0;
            }
        }
        for(int i=0; i<specjalizacja.length; i++)
        {
            if(specjalizacja[i] == 1) zmienna +=" " + (i+1) + ", ";
        }
        legendaText.setText(zmienna);
        for (int i=0; i<iloscUslug; i++)
            System.out.println(index + ". -> "+ specjalizacja[i]);
        while(true)
        {
            try
            {



                klient.acquire();
                int pozwolenie =0;
                number = listaKlientowWPoczekalni.size();
                for(int i=0; i<listaKlientowWPoczekalni.size();i++)
                {
                        number = listaKlientowWPoczekalni.size();
                        lockfr.lock();
                        if(i>=listaKlientowWPoczekalni.size())
                        {
                            lockfr.unlock();
                            break;
                        }
                        if (specjalizacja[listaKlientowWPoczekalni.get(i).getRodzajUslugi() - 1] == 1) {
                            pozycja = i;

                            System.out.println("WYRZUCILEM" +numerKlienta + " a wielkosc wynosi "+listaKlientowWPoczekalni.size());
                            if(specjalizacja[listaKlientowWPoczekalni.get(i).getRodzajUslugi()-1]==0)
                            {
                                i = 0;
                                lockfr.unlock();
                                continue;
                            }
                            pozwolenie = 1;

                            break;
                        }
                        lockfr.unlock();

                }
                if(pozwolenie==1) {
                    fotel.acquire();

                    BinarySemaphore.acquire();
                    for(int i=0; i<tablicaFoteli.length; i++)
                    {
                        if(tablicaFoteli[i] == 0)
                        {
                            tablicaFoteli[i] = 1;
                            numerFotela = i;
                            break;
                        }
                    }


                    System.out.println("Klient o numerze id " + numerKlienta + "siada na fotel "+numerFotela);


                    BinarySemaphore.release();

                    SequentialTransition trans = new SequentialTransition(
                            createTransition(person.getTranslateX(), person.getTranslateY()+40, 300),
                            createTransition(-290, person.getTranslateY()+40, index*300),
                            createTransition(-290, numerFotela*60-150, 1000),
                            createTransition(-310, numerFotela*60-150, 100)
                    );

                    Platform.runLater(()-> trans.play());


                    strzyzonyKlient =listaKlientowWPoczekalni.remove(pozycja);
                    strzyzonyKlient.zwolnijMiejsceWPoczekalni();
                    numerKlienta = strzyzonyKlient.getIndex();
                    lockfr.unlock();
                    strzyzonyKlient.move(-100+numerFotela*30,strzyzonyKlient.getPerson().getTranslateY(),1000);
                    sleep(1000);
                    strzyzonyKlient.move(-100+numerFotela*30,numerFotela*60-130,1000);
                    sleep(1000);
                    strzyzonyKlient.move(-350,numerFotela*60-130,1000);
                    sleep(1000);
                    strzyzonyKlient.move(-350,numerFotela*60-150,100);
                    System.out.println("Klient o numerze id " + numerKlienta + "zaczyna byc strzezony przez" + index);


                    strzyzenie();
                    SequentialTransition trans2 = new SequentialTransition(

                            createTransition((index-1)*35-250, -250,2000)
                    );


                    Platform.runLater(()-> trans2.play());
                    sleep(1500);
                    if(specjalizacja[strzyzonyKlient.getRodzajUslugi()-1]==0) throw new IndexOutOfBoundsException("BBBBBBBBBBBBBBB");
                    tablicaFoteli[numerFotela] = 0;
                    fotel.release();

                }
                else
                    klient.release();
            } catch (InterruptedException e)
            {
                System.out.println("Wyjatek fryzjera");
            }
        }
    }

    private void strzyzenie()
    {
        try{
            Random random = new Random();
            int los = random.nextInt(1000)+3000;
            strzyzonyKlient.colorize(los);
            sleep(los);
            System.out.println("Klient " + numerKlienta + "zostal obciety");
            strzyzonyKlient.setObciety(true);

        }catch (InterruptedException ex)
        {

        }
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
}
