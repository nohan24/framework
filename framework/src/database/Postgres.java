package database;

import java.sql.*;

public class Postgres {
    public static Connection connect(){
        Connection c = null;
        try {
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres","postgres", "root");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }   
}
