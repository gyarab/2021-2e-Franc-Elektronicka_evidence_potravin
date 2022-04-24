package eep;

import java.awt.Desktop;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xxx
 */
public class OfflineData {

    public static ArrayList<Potravina> potraviny = new ArrayList();
    public static ArrayList<Potravina> ulozenePotraviny = new ArrayList();

    private static ArrayList potravinyZPredeslehoKroku = null;
    public static ArrayList<Kategorie> kategorie = new ArrayList();
    public static ArrayList<String> jednotky = new ArrayList();
    public static int docasnaId[] = new int[3];
    public static boolean online = false;
    public static boolean zmena = false;
    public static String pridatEan;
    public static ArrayList<OfflineSQL> offlineSQL = new ArrayList();
    public static int[] tabulkaId = new int[10000];
    public static int[] tabulkaIdUlozenych = new int[10000];
    public static boolean barevne = false;

    public synchronized static void stahnoutOnlineData() {

        // Pojistka proti ztrátě dat, která se do databáze nenahrála.
        if (offlineSQL.size() == 0) {
            try {
                Class.forName(DB.driverName);
                Connection connection = DriverManager.getConnection(DB.url, DB.username, DB.password);
                Statement stmt = connection.createStatement();
                //Aktualizace seznamu potravin
                ResultSet rs = stmt.executeQuery("select * from " + Uzivatel.jmeno + " where typ='potraviny' ORDER BY jmeno;");
                System.out.println("Byly staženy potraviny z databáze");
                potraviny.clear();
                while (rs.next()) {
                    potraviny.add(new Potravina(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getInt(7), rs.getString(8)));
                }
                //Aktualizace seznamu kategorií
                rs = stmt.executeQuery("select * from " + Uzivatel.jmeno + " where typ='kategorie' ORDER BY jmeno;");
                kategorie.clear();
                while (rs.next()) {
                    kategorie.add(new Kategorie(rs.getString(4), rs.getString(9)));
                }
                //Aktualizace seznamu uložených potravin
                rs = stmt.executeQuery("select * from " + Uzivatel.jmeno + " where typ='ulozene' ORDER BY jmeno;");
                ulozenePotraviny.clear();
                while (rs.next()) {
                    ulozenePotraviny.add(new Potravina(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getInt(7), rs.getString(8)));
                }
                connection.close();
                online = true;
                zmena = true;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(OfflineData.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                online = false;
            }
        }
    }

    public synchronized static void synchronizace() {

        Thread t = new Thread(() -> {
            if (offlineSQL.size() > 0) {
                try {
                    Class.forName(DB.driverName);
                    Connection connection = DriverManager.getConnection(DB.url, DB.username, DB.password);

                    while (offlineSQL.size() != 0) {
                        System.out.println("- Operace v cyklu ID: " + offlineSQL.get(0).id);
                        int id = offlineSQL.get(0).id;
                        int aktualizovanyId = offlineSQL.get(0).id;
                        if (id < 0 && offlineSQL.get(0).vratitId == 0) {
                            if (offlineSQL.get(0).ulozenaPotravina) {
                                aktualizovanyId = (int) tabulkaIdUlozenych[Math.abs(id)];
                            } else {
                                aktualizovanyId = (int) tabulkaId[Math.abs(id)];

                            }
                        }
                        String sql = offlineSQL.get(0).sql;
                        sql = sql.replace("$ID$", aktualizovanyId + "");
                        PreparedStatement updateEXP = connection.prepareStatement(sql);
                        int updateEXP_done = updateEXP.executeUpdate();
                        // Případ, kdy do databáze ukládáme potravinu, které se přidělí id z online databáze
                        if (offlineSQL.get(0).vratitId == 1) {
                            Statement stmt = connection.createStatement();
                            ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID();");
                            while (rs.next()) {
                                int noveId = rs.getInt(1);
                                tabulkaId[Math.abs(id)] = noveId;
                                System.out.println("Pridani id hash mapy - key: " + id + " value: " + noveId);
                                for (int i = 0; i < potraviny.size(); i++) {
                                    if (potraviny.get(i).id == id) {
                                        potraviny.get(i).id = noveId;
                                        break;
                                    }
                                }
                            }
                        }
                        if (offlineSQL.get(0).vratitId == 2) {
                            Statement stmt = connection.createStatement();
                            ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID();");
                            while (rs.next()) {
                                int noveId = rs.getInt(1);
                                tabulkaIdUlozenych[Math.abs(id)] = noveId;
                                for (int i = 0; i < ulozenePotraviny.size(); i++) {
                                    if (ulozenePotraviny.get(i).id == id) {
                                        ulozenePotraviny.get(i).id = noveId;
                                        break;
                                    }
                                }
                            }
                        }
                        offlineSQL.remove(0);
                    }
                    connection.close();
                    docasnaId[0] = 0;
                    docasnaId[1] = 0;
                    docasnaId[2] = 0;
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(OfflineData.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    online = false;
                }
            }
            stahnoutOnlineData();
            ulozit();
        });
        t.start();

    }

    public synchronized static void ulozit() {
        FileOutputStream f = null;
        try {
            ArrayList ex = new ArrayList();
            ex.add(potraviny);
            ex.add(kategorie);
            ex.add(ulozenePotraviny);
            ex.add(jednotky);
            ex.add(offlineSQL);
            ex.add(docasnaId);
            ex.add(tabulkaId);
            ex.add(tabulkaIdUlozenych);

            f = new FileOutputStream("offlineData.xml");
            XMLEncoder encoder = new XMLEncoder(f);
            encoder.writeObject(ex);
            encoder.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Chyba při ukládání souboru");
        } finally {
            try {
                f.close();
            } catch (IOException ex) {
                Logger.getLogger(OfflineData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public synchronized static void nacist() {
        try {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream("offlineData.xml")));
            ArrayList in = new ArrayList();
            in = (ArrayList) decoder.readObject();
            potraviny = (ArrayList) in.get(0);
            kategorie = (ArrayList) in.get(1);
            ulozenePotraviny = (ArrayList) in.get(2);
            jednotky = (ArrayList) in.get(3);
            offlineSQL = (ArrayList) in.get(4);
            docasnaId = (int[]) in.get(5);
            tabulkaId = (int[]) in.get(6);
            tabulkaIdUlozenych = (int[]) in.get(7);

            System.out.println("Data: offlineData.xml byla úspěšně načtena");
        } catch (FileNotFoundException ex) {
            System.out.println("Soubor: offlineData.xml nenalezen");
        }
    }

    public static void upravit(Potravina potravina) {
        for (int i = 0; i < potraviny.size(); i++) {
            if (potraviny.get(i).id == potravina.id) {
                potraviny.set(i, potravina);
                zmena = true;
                // Podání požadavku k nahrání do databáze
                OfflineSQL task = new OfflineSQL("UPDATE " + Uzivatel.jmeno + " SET ean = '" + potravina.ean + "', jmeno = '" + potravina.jmeno + "', kategorie = '" + potravina.kategorie + "', spotreba = '" + potravina.spotreba + "', mnozstvi = '" + potravina.mnozstvi + "', jednotky = '" + potravina.jednotky + "' where id=$ID$;", potravina.id);
                offlineSQL.add(task);
                break;
            }
        }
    }

    public static void smazat(Potravina potravina) {
        for (int i = 0; i < potraviny.size(); i++) {
            if (potraviny.get(i).equals(potravina)) {
                if (potravina.jednotky.equals("ks") || potravina.jednotky.equals("kg")) {
                    if (potravina.mnozstvi > 1) {
                        potraviny.get(i).mnozstvi--;
                        //----  Nastavit příkaz pro úpravu potraviny  v databázi
                        OfflineSQL task = new OfflineSQL("UPDATE " + Uzivatel.jmeno + " SET mnozstvi = '" + (potravina.mnozstvi - 1) + "' where id=$ID$;", potravina.id);
                        offlineSQL.add(task);
                        zmena = true;
                        return;
                    }
                }
                potraviny.remove(i);
                //---- Nastavit příkaz k odstranění potraviny v databázi
                OfflineSQL task = new OfflineSQL("DELETE FROM " + Uzivatel.jmeno + "  WHERE id=$ID$;", potravina.id);
                offlineSQL.add(task);
                zmena = true;
            }
        }
    }

    public static void pridat(Potravina potravina) {
        potraviny.add(potravina);
        OfflineSQL task = new OfflineSQL("INSERT INTO " + Uzivatel.jmeno + " (typ, ean, jmeno, kategorie, spotreba, mnozstvi, jednotky) VALUES ('potraviny', '" + potravina.ean + "', '" + potravina.jmeno + "', '" + potravina.kategorie + "', '" + potravina.spotreba + "', '" + potravina.mnozstvi + "', '" + potravina.jednotky + "');");
        task.vratitId = 1;
        task.id = potravina.id;
        offlineSQL.add(task);
        zmena = true;
    }

    public static void pridatUlozenouPotravinu(Potravina potravina) {
        Potravina kPridani = new Potravina();
        int id = docasnaId[1] - 1;
        docasnaId[1]--;

        kPridani.id = id;
        kPridani.ean = potravina.ean;
        kPridani.kategorie = potravina.kategorie;
        kPridani.jmeno = potravina.jmeno;
        kPridani.typ = "ulozene";
        kPridani.jednotky = potravina.jednotky;
        ulozenePotraviny.add(kPridani);

        //-- doplnit onliene synchronizaci
        OfflineSQL task = new OfflineSQL("INSERT INTO " + Uzivatel.jmeno + " (typ, ean, jmeno, kategorie, jednotky) VALUES ('ulozene', '" + potravina.ean + "', '" + potravina.jmeno + "', '" + potravina.kategorie + "', '" + potravina.jednotky + "');");
        task.vratitId = 2;
        task.id = id;
        task.ulozenaPotravina = true;
        offlineSQL.add(task);
    }

    public static void generateExportHTML() {
        try {
            FileWriter myWriter = null;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd. MM. yyyy");
            LocalDateTime now = LocalDateTime.now();
            
            String html = "<html>\n"
                    + "    <head>\n"
                    + "        <title>Export seznamu potravin</title>\n"
                    + "        <meta charset=\"UTF-8\">\n"
                    + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                    + "        <link rel=\"stylesheet\" href=\"export.css\"> \n"
                    + "    </head>\n"
                    + "    <body onload=\"window.print()\">\n"
                    + "        <img src=\"paprikaStonek.png\" class=\"ikonka\" id=\"stonek\"/><img src=\"paprika.png\" class=\"ikonka\"/>\n"
                    + "    <center><div class=\"hlavicka\"><h1>" + Uzivatel.jmeno + "</h1><h3>" +dtf.format(now)+ "</h3></div></center>\n"
                    + "    <table>\n"
                    + "        <tr id=\"hlavicka\"><td id=\"nazev\">název</td><td id=\"kategorie\">kategorie</td><td id=\"spotreba\">spotřeba</td><td id=\"mnozstvi\">množství</td><td style=\"width: 130px;\">EAN</td></tr>\n"
                    + "";
            for (int i = 0; i < OfflineData.potraviny.size(); i++) {
                String spotreba = "";
                if (!OfflineData.potraviny.get(i).spotreba.equals("0.0.0")) {
                    spotreba = OfflineData.potraviny.get(i).spotreba;
                }
                html = html + "<tr id=\"polozka\"><td id=\"nazev\">" + OfflineData.potraviny.get(i).jmeno + "</td><td id=\"kategorie\">" + OfflineData.potraviny.get(i).kategorie + "</td><td id=\"spotreba\">"+spotreba+"</td><td id=\"mnozstvi\">" + OfflineData.potraviny.get(i).mnozstvi + " " + OfflineData.potraviny.get(i).jednotky + "</td><td id=\"ean\">" + OfflineData.potraviny.get(i).ean + "</td></tr>\n";
            }
            html = html + "</table></body></html>";

            //Uloz soubor
            myWriter = new FileWriter("export.html");
            myWriter.write(html);
            myWriter.close();

            //Otevri stranku
            String url = "export.html";
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
        } catch (IOException ex) {
            Logger.getLogger(OfflineData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
