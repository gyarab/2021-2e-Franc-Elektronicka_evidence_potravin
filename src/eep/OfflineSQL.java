/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eep;

/**
 *
 * @author Vojtěch Franc
 */
public class OfflineSQL {
    String sql;
    int vratitId = 0;// 1 - potravina 2 - ulozena potravina
    int id;
    boolean ulozenaPotravina = false;
    public OfflineSQL(String sql) {
        this.sql = sql;
    }

    public OfflineSQL() {
    }

    public OfflineSQL(String sql, int id) {
        this.sql = sql;
        this.id = id;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public int getVratitId() {
        return vratitId;
    }

    public void setVratitId(int vratitId) {
        this.vratitId = vratitId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isUlozenaPotravina() {
        return ulozenaPotravina;
    }

    public void setUlozenaPotravina(boolean ulozenaPotravina) {
        this.ulozenaPotravina = ulozenaPotravina;
    }
    
    
}
