/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eep;

/**
 *
 * @author xxx
 */
public class Kategorie {
    String nazev;
    String odkazNaIkonku;

    public Kategorie(String nazev, String odkazNaIkonku) {
        this.nazev = nazev;
        this.odkazNaIkonku = odkazNaIkonku;
    }

    public Kategorie() {
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public String getOdkazNaIkonku() {
        return odkazNaIkonku;
    }

    public void setOdkazNaIkonku(String odkazNaIkonku) {
        this.odkazNaIkonku = odkazNaIkonku;
    }
    
}
