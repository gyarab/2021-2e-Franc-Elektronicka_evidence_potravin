/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eep;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xxx
 */
public class OfflineData {

    static class OfflineSQL {

        String sql;
        String type;

        public OfflineSQL(String sql) {
            this.sql = sql;
        }

        public OfflineSQL(String sql, String type) {
            this.sql = sql;
            this.type = type;
        }
    }

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
            System.out.println("Data: offlineData.xml byla úspěšně načtena");
        } catch (FileNotFoundException ex) {
            System.out.println("Soubor: offlineData.xml nenalezen");
        }
    }

    /*public synchronized static void Synchronizovat() {
        try {
            Class.forName(DB.driverName);
            Connection connection = DriverManager.getConnection(DB.url, DB.username, DB.password);
            Statement stmt = connection.createStatement();

            ResultSet rs = stmt.executeQuery("select * from " + Uzivatel.jmeno + " where typ='potraviny' ORDER BY jmeno;");
            ArrayList puvodni = (ArrayList) potraviny.clone();
            potraviny.clear();
            while (rs.next()) {
                potraviny.add(new Potravina(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getInt(7), rs.getString(8)));
            }
            rs = stmt.executeQuery("select * from " + Uzivatel.jmeno + " where typ='kategorie' ORDER BY jmeno;");
            kategorie.clear();
            while (rs.next()) {
                kategorie.add(new Kategorie(rs.getString(4), rs.getString(9)));
            }
            rs = stmt.executeQuery("select * from " + Uzivatel.jmeno + " where typ='ulozene' ORDER BY jmeno;");
            ulozenePotraviny.clear();
            while (rs.next()) {
                ulozenePotraviny.add(new Potravina(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getInt(7), rs.getString(8)));
            }
            connection.close();
            if (puvodni.equals(potraviny)) {
                zmena = false;
            } else {
                zmena = true;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OfflineData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            System.out.println("Nepodařilo se připojit");
            if (potravinyZPredeslehoKroku == null) {
                potravinyZPredeslehoKroku = (ArrayList) potraviny.clone();
                zmena = true;
            } else {
                if (potravinyZPredeslehoKroku.equals(potraviny)) {
                    zmena = false;
                } else {
                    zmena = true;
                }
            }
            online = false;
            potravinyZPredeslehoKroku = (ArrayList) potraviny.clone();
            return;
        }
        online = true;
    }*/
    public static void upravit(Potravina potravina) {
        for(int i = 0; i < potraviny.size(); i++){
            if(potraviny.get(i).id == potravina.id){
                potraviny.set(i, potravina);
                zmena = true;
                //--  Nastavit příkaz k úpravě
                break;
            }
        }
    }
    
    /*
    try {
            Class.forName(DB.driverName);
            Connection connection = DriverManager.getConnection(DB.url, DB.username, DB.password);
            String sql = "UPDATE " + Uzivatel.jmeno + " SET ean = '" + potravina.ean + "', jmeno = '" + potravina.jmeno + "', kategorie = '" + potravina.kategorie + "', spotreba = '" + potravina.spotreba + "', mnozstvi = '" + potravina.mnozstvi + "', jednotky = '" + potravina.jednotky + "' where id=" + potravina.id + ";";
            PreparedStatement updateEXP = connection.prepareStatement(sql);
            int updateEXP_done = updateEXP.executeUpdate();
            connection.close();
            zmena = true;
        } catch (ClassNotFoundException ex) {

        } catch (SQLException ex) {
            System.out.println("nepodařilo se přpojt");
        }
    */

    public static void smazat(Potravina potravina) {
        for (int i = 0; i < potraviny.size(); i++) {
            if (potraviny.get(i).equals(potravina)) {
                if (potravina.jednotky.equals("ks") || potravina.jednotky.equals("kg")) {
                    if (potravina.mnozstvi > 1) {
                        potraviny.get(i).mnozstvi--;
                        //----  Nastavit příkaz pro úpravu potraviny
                        zmena = true;
                        return;
                    }
                }
                potraviny.remove(i);
                //---- Nastavit příkaz k odstranění potraviny
                zmena = true;
            }
        }
        /*try {

            Class.forName(DB.driverName);
            Connection connection = DriverManager.getConnection(DB.url, DB.username, DB.password);
            String sql;

            if (potravina.mnozstvi <= 1) {
                sql = "DELETE FROM " + Uzivatel.jmeno + "  WHERE id=" + potravina.id + ";";
                for (int i = 0; i < potraviny.size(); i++) {
                    if (potraviny.get(i).equals(potravina)) {
                        potraviny.remove(i);
                        break;
                    }
                }
            } else {
                sql = "UPDATE " + Uzivatel.jmeno + " SET ean = '" + potravina.ean + "', jmeno = '" + potravina.jmeno + "', kategorie = '" + potravina.kategorie + "', spotreba = '" + potravina.spotreba + "', mnozstvi = '" + (potravina.mnozstvi - 1) + "', jednotky = '" + potravina.jednotky + "' where id=" + potravina.id + ";";
                for (int i = 0; i < potraviny.size(); i++) {
                    if (potraviny.get(i).equals(potravina)) {
                        potraviny.get(i).mnozstvi--;
                        break;
                    }
                }
            }

            zmena = true;
            PreparedStatement updateEXP = connection.prepareStatement(sql);
            int updateEXP_done = updateEXP.executeUpdate();
            connection.close();

        } catch (ClassNotFoundException ex) {

        } catch (SQLException ex) {
            if (potravina.mnozstvi <= 1) {
                offlineSQL.add(new OfflineSQL("DELETE FROM " + Uzivatel.jmeno + "  WHERE id=" + potravina.id + ";"));
            } else {
                offlineSQL.add(new OfflineSQL("UPDATE " + Uzivatel.jmeno + " SET ean = '" + potravina.ean + "', jmeno = '" + potravina.jmeno + "', kategorie = '" + potravina.kategorie + "', spotreba = '" + potravina.spotreba + "', mnozstvi = '" + (potravina.mnozstvi - 1) + "', jednotky = '" + potravina.jednotky + "' where id=" + potravina.id + ";"));
            }
            zmena = true;

            System.out.println("nepodařilo se přpojt");
        }*/
    }

    public static void pridat(Potravina potravina) {
        potraviny.add(potravina);
        zmena = true;
        // -- doplnit online
        /*try {
            Class.forName(DB.driverName);
            Connection connection = DriverManager.getConnection(DB.url, DB.username, DB.password);
            String sql = "INSERT INTO " + Uzivatel.jmeno + " (typ, ean, jmeno, kategorie, spotreba, mnozstvi, jednotky) VALUES ('potraviny', '" + potravina.ean + "', '" + potravina.jmeno + "', '" + potravina.kategorie + "', '" + potravina.spotreba + "', '" + potravina.mnozstvi + "', '" + potravina.jednotky + "');";
            PreparedStatement updateEXP = connection.prepareStatement(sql);
            int updateEXP_done = updateEXP.executeUpdate();

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID();");
            potraviny.clear();
            while (rs.next()) {
                potravina.id = rs.getInt(1);
                System.out.println(potravina.id);
            }

            connection.close();
            zmena = true;
        } catch (ClassNotFoundException ex) {

        } catch (SQLException ex) {
            System.out.println("nepodařilo se přpojt");
        }
        return potravina;*/
    }

    public static void pridatUlozenouPotravinu(Potravina potravina) {
        Potravina kPridani = new Potravina();
        int id = docasnaId[1] -1;
        docasnaId[1] --;
        
        kPridani.id = id;
        kPridani.ean = potravina.ean;
        kPridani.kategorie = potravina.kategorie;
        kPridani.jmeno = potravina.jmeno;
        kPridani.typ = "ulozene";
        kPridani.jednotky = potravina.jednotky;
        ulozenePotraviny.add(kPridani);
        
        //-- doplnit onliene synchronizaci
        /*try {
            Class.forName(DB.driverName);
            Connection connection = DriverManager.getConnection(DB.url, DB.username, DB.password);
            String sql = "INSERT INTO " + Uzivatel.jmeno + " (typ, ean, jmeno, kategorie, jednotky) VALUES ('ulozene', '" + potravina.ean + "', '" + potravina.jmeno + "', '" + potravina.kategorie + "', '" + potravina.jednotky + "');";
            PreparedStatement updateEXP = connection.prepareStatement(sql);
            int updateEXP_done = updateEXP.executeUpdate();

            connection.close();
            zmena = true;
        } catch (ClassNotFoundException ex) {

        } catch (SQLException ex) {
            System.out.println("nepodařilo se přpojt");
        }*/
    }

}
