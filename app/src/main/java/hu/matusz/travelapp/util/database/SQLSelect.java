package hu.matusz.travelapp.util.database;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * @author Máté
 * @deprecated 17.04 -- Android does not support it
 * This class is to get SQL data with SELECT statement. It is dependent on ConnectionManagement and remote SQL database.
 * @version v1 - written on the plane, so no connection to the database. But the code compiles
 */
public class SQLSelect {
    ResultSet res = null;
    ConnectionManager connectionManager = new ConnectionManager();
    Statement statement = null;
    public ResultSet getData(String selectStatement){


        Connection conn = connectionManager.databaseConnect();
        try {
            statement = conn.createStatement();
            res = statement.executeQuery(selectStatement);

        } catch (SQLException e) {
            throw new RuntimeException(e.toString()+" -- MM05");
        }
        if (res != null) return res;
        else return null;
    }
}
