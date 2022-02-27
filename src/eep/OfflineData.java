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

    public static ArrayList<Potravina> potraviny = new ArrayList();
    private static ArrayList potravinyZPredeslehoKroku = null;
    public static ArrayList<Kategorie> kategorie = new ArrayList();
    public static ArrayList<String> jednotky = new ArrayList();
    public static boolean online = false;
    public static boolean zmena;

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
            if (potravina.mnozstvi == 1 && potravina.jednotky.equals("ks") || potravina.equals("kg")) {
                sql = "DELETE FROM " + Uzivatel.jmeno + "  WHERE id=" + potravina.id + ";";
                for(int i = 0; i < potraviny.size();i++){
                    if(potraviny.get(i).equals(potravina)){
                        potraviny.remove(i);
                        break;
                    }
                }
            }else{
                sql = "UPDATE " + Uzivatel.jmeno + " SET ean = '" + potravina.ean + "', jmeno = '" + potravina.jmeno + "', kategorie = '" + potravina.kategorie + "', spotreba = '" + potravina.spotreba + "', mnozstvi = '" + (potravina.mnozstvi-1) + "', jednotky = '" + potravina.jednotky + "' where id=" + potravina.id + ";";
                potravina.mnozstvi --;
            }
            zmena = true;
            PreparedStatement updateEXP = connection.prepareStatement(sql);
            int updateEXP_done = updateEXP.executeUpdate();
            connection.close();
        } catch (ClassNotFoundException ex) {

        } catch (SQLException ex) {
            System.out.println("nepodařilo se přpojt");
        }
    }
}
