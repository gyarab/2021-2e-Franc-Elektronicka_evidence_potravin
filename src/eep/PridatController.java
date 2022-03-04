/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eep;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
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
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author xxx
 */
public class PridatController implements Initializable {

    @FXML
    private TextField ean;

    @FXML
    private Label errorCode;

    @FXML
    void dalsi(ActionEvent event) {
        String eanStr = ean.getText();
        if (eanStr.length() == 0) {
            errorCode.setText("Nebyl zadán EAN kód");
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Pridat2.fxml"));
                //Pridat2Controller controller = (Pridat2Controller) loader.getController();
                OfflineData.pridatEan = eanRepareInput(eanStr);
                Parent upravit = loader.load();
                Stage oknoPridat = new Stage();
                oknoPridat.setTitle("Pridat - zadejte podrobnosti");
                oknoPridat.setResizable(false);
                Scene scenaUpravit = new Scene(upravit);
                oknoPridat.setScene(scenaUpravit);
                oknoPridat.show();
            } catch (IOException ex) {
                Logger.getLogger(PridatController.class.getName()).log(Level.SEVERE, null, ex);
            }
            Stage stage = (Stage) errorCode.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    void vytvoritNovy(ActionEvent event) {

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Reakce na stisknutí klávesy Enter volá finkci tlačítka pokračovat s Ean kódem
        Platform.runLater(() -> {
            ean.getScene().setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode().compareTo(KeyCode.ENTER) == 0) {
                        dalsi(new ActionEvent());
                    }
                }
            });
        });
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
