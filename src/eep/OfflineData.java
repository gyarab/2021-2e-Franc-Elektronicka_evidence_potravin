/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eep;

import java.sql.Connection;
import java.sql.DriverManager;
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

    public static ArrayList potraviny = new ArrayList();
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
                potraviny.add(new Potravina(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getInt(7),rs.getString(8)));
            }
            rs = stmt.executeQuery("select * from " + Uzivatel.jmeno + " where typ='kategorie' ORDER BY jmeno;");
            kategorie.clear();
            while (rs.next()) {
                kategorie.add(new Kategorie(rs.getString(4), rs.getString(9)));
            }
            connection.close();
            if(puvodni.equals(potraviny)){
                zmena = false;
            }else{
                zmena = true;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OfflineData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            System.out.println("Nepodařilo se připojit");
            online  = false;
            return;
        }
        online = true;
    }
}
