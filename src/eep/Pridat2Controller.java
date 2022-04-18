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
public class Pridat2Controller implements Initializable {

    @FXML
    private DatePicker datumSpotrebyVstup;

    @FXML
    private TextField eanVstup;

    @FXML
    private ImageView ikonkaVystup;

    @FXML
    private ComboBox<String> jednotkyVstup;

    @FXML
    private ComboBox<String> kategorieVstup;

    @FXML
    private TextField mnozstviVstup;

    @FXML
    private TextField nazevVstup;

    @FXML
    void Pridat(ActionEvent event) {
        String spotreba = "0.0.0";
        try {
            LocalDate selectedDate = datumSpotrebyVstup.getValue();
            spotreba = selectedDate.getDayOfMonth() + "." + selectedDate.getMonthValue() + "." + selectedDate.getYear();
        } catch (Exception e) {

        }
        int mnozstvi;
        try {
            mnozstvi = Integer.parseInt(mnozstviVstup.getText());
        } catch (Exception e) {
            mnozstviVstup.setPromptText("Neplatná hodnota, vkládejte pouze celá čísla.");
            return;
        }
        // Obstarává dočasné ID
        int id = OfflineData.docasnaId[0] -1;
        OfflineData.docasnaId[0]--;
        Potravina kPridani = new Potravina("potravina", id, eanVstup.getText(), nazevVstup.getText(), kategorieVstup.getValue(), spotreba, mnozstvi, jednotkyVstup.getValue());

        OfflineData.pridat(kPridani);
        if (!jeUlozene) {
            OfflineData.pridatUlozenouPotravinu(kPridani);
        }
        Stage stage = (Stage) eanVstup.getScene().getWindow();
        stage.close();
    }

    String ean;
    boolean jeUlozene = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            /**
             * Zkusíme zjistit, zdali již potravinu nemáme uloženou
             */
            ean = OfflineData.pridatEan;
            boolean uloz = false;
            for (Potravina p : OfflineData.ulozenePotraviny) {
                if (p.ean.equals(ean)) {
                    jeUlozene = true;
                    nazevVstup.setText(p.jmeno);
                    jednotkyVstup.setValue(p.jednotky);
                    kategorieVstup.setValue(p.kategorie);

                    for (Kategorie kategorie : OfflineData.kategorie) {
                        if (!p.kategorie.equals(kategorie.nazev)) {
                            kategorieVstup.getItems().add(kategorie.nazev);
                        }
                    }
                    for (String jednotky : OfflineData.jednotky) {
                        if (!p.jednotky.equals(jednotky)) {
                            jednotkyVstup.getItems().add(jednotky);
                        }
                    }
                    ikonkaVystup.setImage(new Image("eep/pictures/fruits.png"));
                    String odkaz = "eep/pictures/" + p.kategorie + ".png";
                    odkaz = odkaz.toLowerCase();
                    try {
                        ikonkaVystup.setImage(new Image(odkaz));
                    } catch (Exception e) {
                        //System.out.println(odkaz);
                    }
                    uloz = true;
                    break;
                }
            }
            eanVstup.setText(ean);
            if (!uloz) {
                // Nastav hodnoty do možností výběru
                for (Kategorie kategorie : OfflineData.kategorie) {
                    kategorieVstup.getItems().add(kategorie.nazev);
                }
                for (String jednotky : OfflineData.jednotky) {
                    jednotkyVstup.getItems().add(jednotky);
                }
            }
        });
    }
}
