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

    public OfflineSQL(String sql, int id) {
        this.sql = sql;
        this.id = id;
    }

    public int isVratitId() {
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
    
    
}
