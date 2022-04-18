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
    boolean vratitId = false;

    public OfflineSQL(String sql) {
        this.sql = sql;
    }

    public OfflineSQL() {
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public boolean isVratitId() {
        return vratitId;
    }

    public void setVratitId(boolean vratitId) {
        this.vratitId = vratitId;
    }
    
}
