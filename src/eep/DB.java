package eep;

/**
 * <h1>Správa základních údajů o připojení k databázi</h1>
 * @author Vojtěch Franc
 */
public class DB {
    public static String driverName = "com.mysql.jdbc.Driver";
    public static String serverName = "localhost";
    public static String mydatabase = "id14303150_eep";
    public static String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
    public static String username = "root";
    public static String password = "";
            
}
