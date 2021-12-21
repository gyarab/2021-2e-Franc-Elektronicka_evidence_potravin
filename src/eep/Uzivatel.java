package eep;

import java.util.ArrayList;
import javafx.stage.Stage;

/**
 * <h1>Práce s uživatelskými údaji, správa a ukládání nastavení.</h1>
 * @author Vojtěch Franc
 */
public class Uzivatel {
    public static int id;
    public static String jmeno;
    public static String email;
    public static String heslo;
    public static Stage stage;
    public static boolean notifikace;

    public static void load(ArrayList a){
        Uzivatel.id = (int) a.get(0);
        Uzivatel.jmeno = (String) a.get(1);
        Uzivatel.email = (String) a.get(2);
        Uzivatel.heslo = (String) a.get(3);
        
    }
    public static ArrayList export(){
        ArrayList arr = new ArrayList();
        arr.add(id);
        arr.add(jmeno);
        arr.add(email);
        arr.add(heslo);
        return arr;
    }
    
}
