/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eep;

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
    public static boolean online = false;
    public static boolean zmena;
    public static String pridatEan;
    public static ArrayList<OfflineSQL> offlineSQL = new ArrayList();

    public synchronized static void Synchronizovat() {
        try {
            Class.forName(DB.driverName);
            Connection connection = DriverManager.getConnection(DB.url, DB.username, DB.password);
            Statement stmt = connection.createStatement();

            /*
            ResultSet rs = stmt.executeQuery("select * from " + Uzivatel.jmeno + " where typ='potraviny'");
            while (rs.next()) {
                aktualniPot.add(new Potravina(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)));
            }*/
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
    }

    public static void upravit(Potravina potravina) {
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
    }

    public static void smazat(Potravina potravina) {
        try {

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
        }
    }

    public static Potravina pridat(Potravina potravina) {
        try {
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
        return potravina;
    }

    public static void pridatUlozenouPotravinu(Potravina potravina) {
        try {
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
        }
    }

}
