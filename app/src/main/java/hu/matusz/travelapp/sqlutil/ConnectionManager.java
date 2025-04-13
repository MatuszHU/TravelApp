package hu.matusz.travelapp.sqlutil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class ConnectionManager {
        public static void loader() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            } catch (Exception ex) {
                System.err.println("Failed to load Connection Manager Driver! MM01 -- "+ex.toString());
            }
        }

    public void databaseConnect(){
        Connection dataConnection = null;
        try {
            dataConnection =
                    DriverManager.getConnection("bodma.dyndns.org:5155");
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
    }
}
