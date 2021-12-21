/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eep;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

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
    private ComboBox<?> jednotkyVstup;

    @FXML
    private TextField jmenoVstup;

    @FXML
    private ComboBox<?> kategorieVstup;

    @FXML
    private TextField mnozstviVstup;

    @FXML
    void ulozit(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            jmenoVstup.setText(potravina.jmeno);
            eanVstup.setText(potravina.ean);
        });
    }

}
