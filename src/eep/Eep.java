package eep;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Vojta Franc
 */
public class Eep extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        /**
         * Načítání uložených údajů o uživateli (pokud existují)
         */
        File f = new File("uzivatel.xml");
        if (f.exists()) {
            try {
                XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream("uzivatel.xml")));
                Uzivatel.load((ArrayList) decoder.readObject());

                Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
                Stage hlavni = new Stage();
                hlavni.initStyle(StageStyle.DECORATED);
                hlavni.setTitle("Elektronicá evidence potravin");
                hlavni.setMinHeight(705);
                hlavni.setMinWidth(719);
                hlavni.resizableProperty().setValue(Boolean.TRUE);
                Scene scene = new Scene(root);
                scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                hlavni.setScene(scene);
                Uzivatel.stage = hlavni;
                hlavni.show();
            } catch (FileNotFoundException ex) {
                System.out.println("Nepodařilo se načíst předuložené přihlášení");
            }
            /**
             * Pokud není profil uživatele v počítači uložen, vykresli okno pro
             * přihlášení.
             */
        } else {
            Parent prihlaseni = FXMLLoader.load(getClass().getResource("Prihlasovani.fxml"));
            Stage oknoPrihlasovani = new Stage();
            oknoPrihlasovani.setTitle("Přihlášení");
            Scene ScenaPrihlasovani = new Scene(prihlaseni);
            oknoPrihlasovani.setScene(ScenaPrihlasovani);
            oknoPrihlasovani.show();
        }
        
        OfflineData.jednotky.add("ks");
        OfflineData.jednotky.add("kg");
        OfflineData.jednotky.add("ml");
        OfflineData.jednotky.add("l");
        OfflineData.jednotky.add("%");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
