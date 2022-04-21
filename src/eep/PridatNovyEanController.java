/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eep;

import java.awt.Desktop;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author xxx
 */
public class PridatNovyEanController implements Initializable {

    @FXML
    private DatePicker datumSpotrebyVstup;

    @FXML
    private ComboBox<String> jednotkyVstup;

    @FXML
    private ComboBox<String> kategorieVstup;

    @FXML
    private TextField mnozstviVstup;

    @FXML
    private TextField nazevVstup;

    @FXML
    private AnchorPane root;

    @FXML
    void Pridat(ActionEvent event) {
        FileWriter myWriter = null;
        try {
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
            if(nazevVstup.getText().length() < 1){
            nazevVstup.setPromptText("Je nutné zadat název potrviny");
            return;
        }
            // Obstarává dočasné ID
            int id = OfflineData.docasnaId[0] - 1;
            OfflineData.docasnaId[0]--;
            //Výpočet EAN kódu

            String ean = "2";
            try {
                int idUzi = Uzivatel.id;
                for (int i = 0; i < 4; i++) {
                    if (idUzi % 10 == 0) {
                        ean = ean + "0";
                    } else {
                        ean = ean + idUzi % 10;
                    }
                    idUzi /= 10;
                }
                int idKategorie = 0;
                for (int i = 0; i < OfflineData.kategorie.size(); i++) {
                    if (kategorieVstup.getValue().equals(OfflineData.kategorie.get(i).nazev)) {
                        idKategorie = i % 100;
                        System.out.println("Nalezeno");
                        break;
                    }
                }
                System.out.println("Kategorie id "+ idKategorie);
                for (int i = 0; i < 2; i++) {
                    if (idKategorie % 10 == 0) {
                        ean = ean + "0";
                    } else {
                        ean = ean + idKategorie % 10;
                    }
                    idKategorie /= 10;
                }
                int spotrebaHash = Math.abs(spotreba.hashCode() % 100);
                System.out.println("Spotreba hash " + spotrebaHash);
                for (int i = 0; i < 2; i++) {
                    if (spotrebaHash % 10 == 0) {
                        ean = ean + "0";
                    } else {
                        ean = ean + spotrebaHash % 10;
                    }
                    spotrebaHash /= 10;
                }
                int jmenoHash = Math.abs(nazevVstup.getText().hashCode() % 1000);
                System.out.println("Jmeno hash" + jmenoHash);
                for (int i = 0; i < 3; i++) {
                    if (jmenoHash % 10 == 0) {
                        ean = ean + "0";
                    } else {
                        ean = ean + jmenoHash % 10;
                    }
                    jmenoHash /= 10;
                }
                int kontrolniCislice = 10 - ((3 * Character.getNumericValue(ean.charAt(0)) + Character.getNumericValue(ean.charAt(1)) + 3 * Character.getNumericValue(ean.charAt(2)) + Character.getNumericValue(ean.charAt(3)) + 3 * Character.getNumericValue(ean.charAt(4)) + Character.getNumericValue(ean.charAt(5)) + 3 * Character.getNumericValue(ean.charAt(6)) + Character.getNumericValue(ean.charAt(7)) + 3 * Character.getNumericValue(ean.charAt(8)) + Character.getNumericValue(ean.charAt(9)) + 3 * Character.getNumericValue(ean.charAt(10) + Character.getNumericValue(ean.charAt(11)))) % 10);
                if(kontrolniCislice == 10){
                    kontrolniCislice = 0;
                }
                ean = ean + kontrolniCislice;
                System.out.println("Vygenerovaný ean kód: "+ean);
            } catch (Exception e) {
                System.out.println("Převod na Ean se nepovedl");
            }

            Potravina kPridani = new Potravina("potravina", id, String.valueOf(ean), nazevVstup.getText(), kategorieVstup.getValue(), spotreba, mnozstvi, jednotkyVstup.getValue());
            OfflineData.pridat(kPridani);
            // Tisk ean kódu
            String html = "<!DOCTYPE html>\n"
                    + "<html>\n"
                    + "    <head>\n"
                    + "        <title>Tisk EAN kódu</title>\n"
                    + "        <meta charset=\"UTF-8\">\n"
                    + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                    + "        <style>\n"
                    + "            @font-face {\n"
                    + "                font-family: ean;\n"
                    + "                src: url(EAN13.ttf);\n"
                    + "            }\n"
                    + "            p {\n"
                    + "                background-color: transparent;\n"
                    + "                font-family: ean;\n"
                    + "                font-size: 70px;\n"
                    + "                margin: 0px;\n"
                    + "                position: absolute;\n"
                    + "                bottom: 5px;\n"
                    + "                right: 5px;\n"
                    + "            }\n"
                    + "            .etiketa{\n"
                    + "                position: relative;\n"
                    + "                float: left;\n"
                    + "                display: box;\n"
                    + "                margin: 2px;\n"
                    + "                background-color: #CAE3E2;\n"
                    + "                width: 225px;\n"
                    + "                height: 140px;\n"
                    + "            }\n"
                    + "            h1{\n"
                    + "                font-family: arial;\n"
                    + "                font-size: 23px;\n"
                    + "                position: relative;\n"
                    + "                top: -5px;\n"
                    + "                left: 10px;\n"
                    + "                background-color: #CAE3E2;\n"
                    + "                margin-bottom: 0px;\n"
                    + "                width: 215px;\n"
                    + "                max-height: 55px;\n"
                    + "                width: 215px;\n"
                    + "                width: 215px;\n"
                    + "                overflow: hidden;\n"
                    + "            }\n"
                    + "            h2{\n"
                    + "                font-family: arial;\n"
                    + "                font-size: 15px;\n"
                    + "                left: 10px;\n"
                    + "                position: relative;\n"
                    + "            }\n"
                    + "        </style>\n"
                    + "    </head>\n"
                    + "    <body onload=\"window.print()\">\n";
            if (!jednotkyVstup.getValue().equals("ks") && !jednotkyVstup.getValue().equals("kg")) {
                mnozstvi = 1;
            }
            if (spotreba.equals("0.0.0")) {
                spotreba = "";
            }
            for (int i = 0; i < mnozstvi; i++) {
                html = html + "<div class=\"etiketa\"><p>" + ean + "</p><h1>" + nazevVstup.getText() + "</h1><h2>" + spotreba + "</h2></div>";
            }
            html = html + "    </body></html>";

            myWriter = new FileWriter("index.html");
            myWriter.write(html);
            myWriter.close();

            //Zobrazení kóodu pro tisk
            String url = "index.html";
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI(url));
                } catch (IOException | URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("xdg-open " + url);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            Stage stage = (Stage) nazevVstup.getScene().getWindow();
            stage.close();
        } catch (IOException ex) {
            Logger.getLogger(PridatNovyEanController.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                myWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(PridatNovyEanController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    String ean;
    boolean jeUlozene = false;

    @Override
    public void initialize(URL url, ResourceBundle rb
    ) {
        Platform.runLater(() -> {
            /**
             * Zkusíme zjistit, zdali již potravinu nemáme uloženou
             */
            // Nastav hodnoty do možností výběru
            for (Kategorie kategorie : OfflineData.kategorie) {
                kategorieVstup.getItems().add(kategorie.nazev);
            }
            for (String jednotky : OfflineData.jednotky) {
                jednotkyVstup.getItems().add(jednotky);
            }
        });
    }
}
