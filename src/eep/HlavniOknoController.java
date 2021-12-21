package eep;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author xxx
 */
public class HlavniOknoController implements Initializable {

    /**
     * mode = 0 => bez filtrování mode = 1 => hledání a řazení podle jména mode
     * = 2 => filtrování podle kategorie
     */
    int mode = 0;
    int polozkyNaSirku = 10;
    int sirkaKarty = 148;

    @FXML
    private AnchorPane content;

    @FXML
    private TextField eanInput;

    @FXML
    public Label indikator;

    @FXML
    private Label jmeno;

    @FXML
    private AnchorPane menu;

    @FXML
    private Pane nastaveni;

    Scene scena;

    /**
     * Stará se o stahování aktualit z databáze keždých 30s. Pokud se nedaří
     * připojit, tak se jen pokusí o připojení.
     */
    @FXML

    void odhlasit(ActionEvent event) throws IOException {
        File f = new File("uzivatel.xml");
        if (f.exists()) {
            f.delete();
        }
        Stage aktualniOkno = (Stage) jmeno.getScene().getWindow();
        Parent prihlaseni = FXMLLoader.load(getClass().getResource("Prihlasovani.fxml"));
        Stage oknoPrihlasovani = new Stage();
        oknoPrihlasovani.setTitle("Přihlášení");
        Scene ScenaPrihlasovani = new Scene(prihlaseni);
        oknoPrihlasovani.setScene(ScenaPrihlasovani);
        aktualniOkno.close();
        oknoPrihlasovani.show();
    }

    @FXML
    void otevriMenu(ActionEvent event) {
        menu.setVisible(true);
    }

    @FXML
    void zavriMenu(ActionEvent event) {
        menu.setVisible(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        scena = menu.getScene();
        menu.setVisible(false);
        jmeno.setText(Uzivatel.jmeno);

        /**
         *
         */
        ScheduledService service = new SynchronizaceService(this);
        service.setPeriod(Duration.seconds(10)); // The interval between executions.
        service.start();
        /**
         * Obsah se vyvolá po načtení celého dokumentu. Jinak by zde nebylo
         * možné získávat paramtery javaFx objektů.
         */
        Platform.runLater(() -> {
            OfflineData.Synchronizovat();
            Stage stage = (Stage) menu.getScene().getWindow();
            polozkyNaSirku = (int) stage.getWidth() / sirkaKarty;
            zobraz();
            /**
             * Hlídá změnu velikosti okna. Pokud se velikost změní příliš, tak
             * zaktualizuje zobrazované karty s potravinami.
             */
            stage.widthProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.intValue() / sirkaKarty != polozkyNaSirku) {
                    polozkyNaSirku = newVal.intValue() / sirkaKarty;
                    //System.out.println("Je potřeba update");
                    zobraz();
                }
            });

            menu.getScene().setOnKeyReleased(new EventHandler<KeyEvent>() {

                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode().compareTo(KeyCode.ENTER) == 0) {
                        hledatPodleEan(new ActionEvent());
                    }
                }
            });
            /**
             * Když dojde k zavření okna, tak ukonči vlákna
             */
            stage.setOnCloseRequest(event -> {
                System.out.println("Stage is closing");
                service.cancel();
                Platform.exit();
            });

        });

    }

    @FXML
    void hledatPodleEan(ActionEvent event) {
        mode = 1;
        zobraz();
    }

    public synchronized void zobraz() {
        ArrayList<Potravina> potraviny = OfflineData.potraviny;
        content.getChildren().clear();
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        AnchorPane.setTopAnchor(pane, 10.0);
        AnchorPane.setLeftAnchor(pane, 10.0);
        AnchorPane.setRightAnchor(pane, 10.0);
        AnchorPane.setBottomAnchor(pane, 10.0);
        //System.out.println(polozkyNaSirku);

        if (mode == 0) {
            for (int i = 1; i <= potraviny.size(); i++) {
                pane.add(potraviny.get(i - 1).potravinaPane(), i % polozkyNaSirku, i / polozkyNaSirku);
                //System.out.println("i: "+i+" sirka: "+i%polozkyNaSirku+" vyska: "+i/polozkyNaSirku);
            }
        }
        if (mode == 1) {
            String eanHledany = eanRepareInput(eanInput.getText());
            Label zprava = new Label();
            zprava.setFont(Font.font("Calibri", 23));
            AnchorPane.setTopAnchor(zprava, 10.0);
            AnchorPane.setLeftAnchor(zprava, 20.0);
            zprava.setStyle("-fx-text-fill: white;");
            if (eanHledany.length() == 0) {
                mode = 0;
                zobraz();
                return;
            }
            int j = 1;
            for (int i = 0; i < potraviny.size(); i++) {
                if (potraviny.get(i).ean.equals(eanHledany)) {
                    pane.add(potraviny.get(i).potravinaPane(), j % polozkyNaSirku, j / polozkyNaSirku);
                    j++;
                }
            }
            if (j == 1) {
                zprava.setText("S kódem "+eanHledany+" nebyla nalezena žádná potravina.");
                content.getChildren().add(zprava);
                return;
            }
        }

        content.getChildren().add(pane);
    }

    public String eanRepareInput(String input) {
        char[] zamenitZ = {'+', 'ě', 'š', 'č', 'ř', 'ž', 'ý', 'á', 'í', 'é'};
        char[] zamenitNa = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        for (int i = 0; i < zamenitZ.length; i++) {
            input = input.replace(zamenitZ[i], zamenitNa[i]);
        }
        return input;
    }
}