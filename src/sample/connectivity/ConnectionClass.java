package sample.connectivity;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionClass {
    public Connection connection;

    public Connection getConnection() {
        String dbName = "beltline2";
        String username = "root";
        String pass = "fake!";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbName,username,pass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
