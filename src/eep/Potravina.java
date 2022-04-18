/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eep;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 *
 * @author xxx
 */
public class Potravina {

    String typ;
    int id;
    String ean = "";
    String jmeno = "";
    String kategorie = "";
    String spotreba = "";
    int mnozstvi = 1;
    String jednotky = "";
    String poznamky = "";
    int odebrano;

    public Potravina(String typ, int id, String ean, String jmeno, String kategorie, String spotreba, int mnozstvi, String jednotky) {
        this.typ = typ;
        this.id = id;
        this.ean = ean;
        this.jmeno = jmeno;
        this.kategorie = kategorie;
        this.spotreba = spotreba;
        this.mnozstvi = mnozstvi;
        this.jednotky = jednotky;
    }

    public Potravina(String typ, int id, String jmeno, String kategorie) {
        this.typ = typ;
        this.id = id;
        this.jmeno = jmeno;
        this.kategorie = kategorie;
    }

    public Potravina() {
    }

    public int dniDoExpirace() {
        if (spotreba.length() == 0) {
            return -1;
        }
        String[] datum = spotreba.split(".");
        if (datum.length != 2) {
            return -1;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date1 = LocalDate.parse(spotreba, dtf);
        LocalDate date2 = LocalDate.parse(java.time.LocalDate.now().getMonthValue() + "." + java.time.LocalDate.now().getDayOfMonth() + "." + java.time.LocalDate.now().getYear(), dtf);
        return (int) Duration.between(date1, date2).toDays();

    }

    public AnchorPane potravinaPane() {
        Potravina tatoPotravina = this;
        AnchorPane potravina = new AnchorPane();
        potravina.setMinWidth(130);
        potravina.setMaxWidth(130);
        potravina.setMinHeight(170);
        potravina.setStyle("-fx-background-color: white;");
        Label nadpis = new Label();
        nadpis.setCursor(Cursor.HAND);
        nadpis.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Upravit.fxml"));
                    Parent upravit = loader.load();
                    Stage oknoUpravit = new Stage();
                    oknoUpravit.setTitle("Upravit");
                    oknoUpravit.setResizable(false);
                    Scene scenaUpravit = new Scene(upravit);
                    oknoUpravit.setScene(scenaUpravit);
                    oknoUpravit.show();
                    UpravitController controller = loader.getController();
                    controller.potravina = tatoPotravina;
                } catch (IOException ex) {
                    Logger.getLogger(Potravina.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        Label spotreba = new Label();
        Label mnozstvi = new Label();
        Button zavrit = new Button();
        zavrit.setId("" + this.id);
        zavrit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //System.out.println("smazat");
                OfflineData.smazat(tatoPotravina);
            }
        });
        zavrit.setPrefSize(25, 25);
        zavrit.setMaxSize(25, 25);
        zavrit.setStyle("-fx-background-color: transparent;");
        zavrit.setCursor(Cursor.HAND);
        AnchorPane.setTopAnchor(zavrit, 0.0);
        AnchorPane.setRightAnchor(zavrit, 0.0);

        Image img = new Image("eep/pictures/close.png");
        ImageView krizek = new ImageView(img);
        krizek.setFitHeight(18);
        krizek.setFitWidth(18);
        zavrit.setGraphic(krizek);

        nadpis.setText(this.jmeno);
        if (!this.spotreba.equals("0.0.0")) {
            spotreba.setText(this.spotreba);

        }
        mnozstvi.setText(this.mnozstvi + " " + this.jednotky);
        AnchorPane.setTopAnchor(spotreba, 140.0);
        AnchorPane.setLeftAnchor(spotreba, 7.0);
        AnchorPane.setRightAnchor(spotreba, 7.0);
        spotreba.setFont(Font.font("Calibri", 16));
        mnozstvi.setFont(Font.font("Calibri", 16));
        spotreba.setAlignment(Pos.TOP_CENTER);
        spotreba.setTextAlignment(TextAlignment.CENTER);
        spotreba.setOpacity(0.52);
        ImageView ikonka = new ImageView(new Image("eep/pictures/fruits.png"));
        String odkaz = "eep/pictures/" + this.kategorie + ".png";
        odkaz = odkaz.toLowerCase();
        try {

            ikonka = new ImageView(new Image(odkaz));
        } catch (Exception e) {
            //System.out.println(odkaz);
        }
        ikonka.setFitHeight(75);
        ikonka.setFitWidth(75);
        ikonka.setPreserveRatio(true);
        AnchorPane.setTopAnchor(ikonka, 18.0);
        AnchorPane.setLeftAnchor(ikonka, 28.0);

        AnchorPane.setTopAnchor(nadpis, 100.0);
        AnchorPane.setLeftAnchor(nadpis, 7.0);
        AnchorPane.setRightAnchor(nadpis, 7.0);

        AnchorPane.setTopAnchor(mnozstvi, 5.0);
        AnchorPane.setLeftAnchor(mnozstvi, 7.0);
        nadpis.setWrapText(true);
        nadpis.setFont(Font.font("Calibri", 16));
        nadpis.setAlignment(Pos.TOP_CENTER);
        nadpis.setTextAlignment(TextAlignment.CENTER);
        nadpis.setLineSpacing(0);
        nadpis.setMaxHeight(44);
        nadpis.setTextOverrun(OverrunStyle.ELLIPSIS);
        potravina.getChildren().add(nadpis);
        potravina.getChildren().add(mnozstvi);
        potravina.getChildren().add(zavrit);
        potravina.getChildren().add(spotreba);
        potravina.getChildren().add(ikonka);
        AnchorPane.setTopAnchor(potravina, 5.0);
        AnchorPane.setLeftAnchor(potravina, 50.0);
        return potravina;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Potravina other = (Potravina) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.mnozstvi != other.mnozstvi) {
            return false;
        }
        if (this.odebrano != other.odebrano) {
            return false;
        }
        if (!Objects.equals(this.typ, other.typ)) {
            return false;
        }
        if (!Objects.equals(this.ean, other.ean)) {
            return false;
        }
        if (!Objects.equals(this.jmeno, other.jmeno)) {
            return false;
        }
        if (!Objects.equals(this.kategorie, other.kategorie)) {
            return false;
        }
        if (!Objects.equals(this.spotreba, other.spotreba)) {
            return false;
        }
        if (!Objects.equals(this.jednotky, other.jednotky)) {
            return false;
        }
        if (!Objects.equals(this.poznamky, other.poznamky)) {
            return false;
        }
        return true;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public String getSpotreba() {
        return spotreba;
    }

    public void setSpotreba(String spotreba) {
        this.spotreba = spotreba;
    }

    public int getMnozstvi() {
        return mnozstvi;
    }

    public void setMnozstvi(int mnozstvi) {
        this.mnozstvi = mnozstvi;
    }

    public String getJednotky() {
        return jednotky;
    }

    public void setJednotky(String jednotky) {
        this.jednotky = jednotky;
    }

    public String getPoznamky() {
        return poznamky;
    }

    public void setPoznamky(String poznamky) {
        this.poznamky = poznamky;
    }

    public int getOdebrano() {
        return odebrano;
    }

    public void setOdebrano(int odebrano) {
        this.odebrano = odebrano;
    }

}
