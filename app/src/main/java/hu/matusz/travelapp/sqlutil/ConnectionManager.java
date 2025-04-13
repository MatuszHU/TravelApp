package hu.matusz.travelapp.sqlutil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class ConnectionManager {
    static void load() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception e) {
            System.err.println("Failed to load Connection Manager Driver! MM01 -- " + e.toString());
        }
    }

    Connection databaseConnect() {
        Connection dataConnection;
        try {
            dataConnection = DriverManager.getConnection("https://bodma.dyndns.org:5155");
            return dataConnection;
        } catch (SQLException e) {
            System.out.println("SQLException: MM02 -- " + e.getMessage());
            System.out.println("SQLState: MM03 --" + e.getSQLState());
            System.out.println("VendorError: MM04 --" + e.getErrorCode());
            return null;
        }
    }
}
