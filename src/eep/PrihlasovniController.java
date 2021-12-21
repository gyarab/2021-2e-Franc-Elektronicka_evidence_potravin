package eep;

import java.awt.Desktop;
import java.beans.XMLEncoder;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;


public class PrihlasovniController {

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @FXML
    private CheckBox zustatPrihlasen;

    @FXML
    private Label errorMsg;

    @FXML
    void prihlasit(ActionEvent event) throws IOException {

        try {
            Class.forName(DB.driverName);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PrihlasovniController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Connection connection = DriverManager.getConnection(DB.url, DB.username, DB.password);
            String heslo = this.password.getText();
            SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
            byte[] digest = digestSHA3.digest(heslo.getBytes());

            heslo =  Hex.toHexString(digest);

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from uzivatele");
            boolean uspech = false;
            while (rs.next()) {
                if (this.username.getText().equals(rs.getString(2)) && heslo.equals(rs.getString(4))) {
                    uspech = true;
                    Uzivatel.id = rs.getInt(1);
                    Uzivatel.jmeno = rs.getString(2);
                    Uzivatel.heslo = rs.getString(4);
                    Uzivatel.email = rs.getString(3);
                    break;
                }
            }
            connection.close();
            if (!uspech || this.username.getText().equals("") || heslo.equals("")) {
                errorMsg.setText("Špatné jméno nebo heslo");
            } else {
                if (zustatPrihlasen.isSelected()) {
                    FileOutputStream f = new FileOutputStream("uzivatel.xml");
                    XMLEncoder encoder = new XMLEncoder(f);
                    encoder.writeObject(Uzivatel.export());
                    encoder.close();
                }
                otevri();
            }
        } catch (SQLException ex) {
            System.out.println("Nepodařilo se připojit");
            errorMsg.setText("Nepodařilo se připojit");
        }
    }

    @FXML
    void registrovat(ActionEvent event
    ) {
        String url = "https://evidencepotravin.000webhostapp.com/vytvoritnovyucet.php";

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
    }

    public void otevri() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Stage stage = new Stage();
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("Elektronicá evidence potravin");
        stage.setMinHeight(705);
        stage.setMinWidth(719);
        stage.resizableProperty().setValue(Boolean.TRUE);
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        stage.setScene(scene);

        Stage aktualniOkno = (Stage) this.errorMsg.getScene().getWindow();
        aktualniOkno.close();
        Uzivatel.stage = stage;
        stage.show();
    }
}
