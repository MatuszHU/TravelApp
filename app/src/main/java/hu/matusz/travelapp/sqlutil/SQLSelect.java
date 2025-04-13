package hu.matusz.travelapp.sqlutil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * @author Máté Majoros
 * This class is to get SQL data with SELECT statement. It is dependent on ConnectionManagement and remote SQL database.
 * @version v1 - written on the plane, so no connection to the database. But the code compiles
 */
public class SQLSelect {
    ResultSet res = null;
    ConnectionManager connectionManager = new ConnectionManager();

    public ResultSet getData(String selectStatement){

        connectionManager.load();
        Connection conn = connectionManager.databaseConnect();
        try {
            res = conn.createStatement().executeQuery(selectStatement);

        } catch (SQLException e) {
            throw new RuntimeException(e.toString()+" -- MM05");
        }
        if (res != null) return res;
        else return null;
    }
}
