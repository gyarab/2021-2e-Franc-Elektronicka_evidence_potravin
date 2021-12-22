/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eep;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author xxx
 */
public class UpravitController implements Initializable {

    public Potravina potravina;

    @FXML
    private DatePicker datumSpotrebyVstup;

    @FXML
    private TextField eanVstup;

    @FXML
    private ImageView ikonkaVystup;

    @FXML
    private ComboBox<String> jednotkyVstup;

    @FXML
    private TextField jmenoVstup;

    @FXML
    private ComboBox<String> kategorieVstup;

    @FXML
    private TextField mnozstviVstup;

    @FXML
    void ulozit(ActionEvent event) {
        potravina.ean = eanVstup.getText();
        potravina.jmeno = jmenoVstup.getText();
        potravina.kategorie = kategorieVstup.getValue();
        potravina.jednotky = jednotkyVstup.getValue();
        try {
            LocalDate selectedDate = datumSpotrebyVstup.getValue();
            potravina.spotreba = selectedDate.getDayOfMonth() + "." + selectedDate.getMonthValue() + "." + selectedDate.getYear();
        } catch (Exception e) {

        }

        try {
            potravina.mnozstvi = Integer.parseInt(mnozstviVstup.getText());
        } catch (Exception e) {
            mnozstviVstup.setPromptText("Neplatná hodnota, vkládejte pouze celá čísla.");
            return;
        }
        OfflineData.upravit(potravina);
        OfflineData.Synchronizovat();
        Stage stage = (Stage) eanVstup.getScene().getWindow();
        potravina.poznamky = "aktualizovat";
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            jmenoVstup.setText(potravina.jmeno);
            eanVstup.setText(potravina.ean);
            mnozstviVstup.setText(Integer.toString(potravina.mnozstvi));
            try {
                String[] datum = potravina.spotreba.split("\\.");
                int dny = Integer.parseInt(datum[0]);
                int mesice = Integer.parseInt(datum[1]);
                int roky = Integer.parseInt(datum[2]);
                System.out.println(dny + "." + mesice + "." + roky);
                datumSpotrebyVstup.setValue(LocalDate.of(roky, mesice, dny));
            } catch (Exception e) {

            }

            for (Kategorie kategorie : OfflineData.kategorie) {
                if (!potravina.kategorie.equals(kategorie.nazev)) {
                    kategorieVstup.getItems().add(kategorie.nazev);
                }
            }
            kategorieVstup.setValue(potravina.kategorie);
            jednotkyVstup.setValue(potravina.jednotky);
            for (String jednotky : OfflineData.jednotky) {
                if (!potravina.jednotky.equals(jednotky)) {
                    jednotkyVstup.getItems().add(jednotky);
                }
            }
            ikonkaVystup.setImage(new Image("eep/pictures/fruits.png"));
            String odkaz = "eep/pictures/" + potravina.kategorie + ".png";
            odkaz = odkaz.toLowerCase();
            try {
                ikonkaVystup.setImage(new Image(odkaz));
            } catch (Exception e) {
                //System.out.println(odkaz);
            }
        });
    }

}
