package eep;

import java.util.ArrayList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * <h1>Třída starající se o zobrazování tabulky potravin</h1>
 * <p>Tato třída slouží pro práci s dynamickým obsahem. Má na starosti panel s kartami potravin, které jsou static, tudíž dostupné odkudkoliv v programu.</p>
 * @author vojtech Franc
 */
public class Zobrazeni {
    /** Slouží k ukládání základní panelu, kam se generují jednotlivé položky potravin. */
    public static AnchorPane zaklad;
    
    /**  Tato část programu se stará o vytvoření grafického objektu zaklad, do kterého se budou následně generovat panely potravin.
     * @param arr Pole potravin pro vypsání
     * @return Vrací vygenerovanou tabulku s potravinymi */
    public static GridPane zobraz(ArrayList<Potravina> arr) {
        GridPane pozadi = new GridPane();
        int i = 0;
        pozadi.setHgap(10);
        pozadi.setVgap(10);
        AnchorPane.setTopAnchor(pozadi, 10.0);
        AnchorPane.setLeftAnchor(pozadi, 10.0);
        AnchorPane.setRightAnchor(pozadi, 10.0);
        AnchorPane.setBottomAnchor(pozadi, 10.0);
        for (Potravina p : arr) {
            pozadi.add(p.potravinaPane(), i, 0);
            i++;
        }
        return pozadi;
    }
    
    public static void smazat(int id){// 180 px
        
    }
}
