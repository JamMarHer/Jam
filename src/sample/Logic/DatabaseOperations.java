package sample.Logic;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by jam on 9/1/16.
 */
public class DatabaseOperations {


    public DatabaseOperations(){

    }

    public boolean checkDBPresent(String database){
        File file = new File("");
        if(database.equals("settings")){
            file = new File("settings.db");
        }
        if(database.equals("tests")){
            file = new File("tests.db");
        }

        return file.exists();
    }

    public void generateDatabase(){
        Connection c = null;
        Statement stmt = null;
        try{
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:settings.db");
            System.out.println("Opened database successfully");
            stmt = c.createStatement();
            String sql = "CREATE TABLE settings " +
                    "(ID INT     NOT NULL,"+
                    "EXT_DIR         TEXT, " +
                    "DAIKON_DIR         TEXT"+
                    ")";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        }catch (Exception e){
            System.out.println( e.getClass().getName() + ": generateDatabase " + e.getMessage());
            System.exit(0);
        }
        System.out.print("Settings Table created successfully");
    }

    public void generateTestsDatabase(){
        Connection c = null;
        Statement stmt = null;
        try{
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:tests.db");
            System.out.println("Opened test database successfully");
            stmt = c.createStatement();
            String sql = "CREATE TABLE tests " +
                    "(ID MEDIUMINT     NOT NULL AUTO_INCREMENT, "+
                    "TESTNAME         TEXT, " +
                    "FILENAME         TEXT"+
                    ")";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        }catch (Exception e){
            System.out.println( e.getClass().getName() + ": generateDatabase " + e.getMessage());
        }
        System.out.print(" Tests Table created successfully");
    }

    // arguments option = where to insert, data = what to insert
    // returns boolean = true if created false if it was not
    public boolean insertData(String option, String data, String database) throws ClassNotFoundException, SQLException {
        boolean created = false;
        Connection c = null;
        Statement stmt = null;
        if(database.equals("settings")) {
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:settings.db");
                c.setAutoCommit(false);
                System.out.println("Opened database successfully");

                stmt = c.createStatement();
                if (option.equals("extDir")) {
                    String sql = "INSERT INTO settings (ID, EXT_DIR) VALUES (1, '" + data + "' );";
                    stmt.executeUpdate(sql);
                    created = true;
                }
                if (option.equals("extDaikon")) {
                    String sql = "INSERT INTO settings (ID, DAIKON_DIR) VALUES (2, '" + data + "' );";
                    stmt.executeUpdate(sql);
                    created = true;
                }
                stmt.close();
                c.commit();
                c.close();
            } catch (Exception e) {
                System.out.println(e.getClass().getName() + ": insertData " + e.getMessage());
                System.exit(0);
            }
            return created;
        }
        if(database.equals("tests")) {
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:tests.db");
                c.setAutoCommit(false);
                System.out.println("Opened database successfully");

                stmt = c.createStatement();
                if (option.equals("testName")) {
                    String sql = "INSERT INTO tests (TESTNAME) VALUES ('" + data + "' );";
                    stmt.executeUpdate(sql);
                    created = true;
                }
                if (option.equals("fileName")) {
                    String sql = "INSERT INTO tests (ID, FILENAME) VALUES (2, '" + data + "' );";
                    stmt.executeUpdate(sql);
                    created = true;
                }

                stmt.close();
                c.commit();
                c.close();
            } catch (Exception e) {
                System.out.println(e.getClass().getName() + ": insertData " + e.getMessage());
                System.exit(0);
            }
        }
        return created;
    }

    public boolean updateData(String option, String data, String database){
        boolean updated = false;
        Connection c = null;
        Statement statement = null;
        if(database.equals("settings")) {
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:settings.db");
                c.setAutoCommit(false);
                System.out.println("Opened database successfully");

                statement = c.createStatement();
                if (option.equals("extDir")) {
                    String sql = "UPDATE settings set EXT_DIR = '" + data + "' WHERE ID=1;";
                    System.out.print(data + "From DB");
                    statement.executeUpdate(sql);
                    c.commit();
                    System.out.print("updates Ext");
                    updated = true;
                }
                if (option.equals("extDaikon")) {
                    String sql = "UPDATE settings set DAIKON_DIR = '" + data + "' WHERE ID=2;";
                    statement.executeUpdate(sql);
                    c.commit();
                    System.out.print("updates Daikon");
                    updated = true;
                }
                statement.close();
                c.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ":update " + e.getMessage());
            }
        }
        if(database.equals("tests")){
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:tests.db");
                c.setAutoCommit(false);
                System.out.println("Opened test database successfully");

                statement = c.createStatement();
                if (option.equals("testName")) {
                    String sql = "UPDATE tests set TESTNAME = '" + data + "' WHERE ID=1;";
                    System.out.print(data + "From DB");
                    statement.executeUpdate(sql);
                    c.commit();
                    System.out.print("updates Ext");
                    updated = true;
                }
                if (option.equals("fileName")) {

                    String sql = "UPDATE tests set FILENAME = '" + data + "' WHERE ID=2;";
                    System.out.print(data + "From DB");
                    statement.executeUpdate(sql);
                    c.commit();
                    System.out.print("updates Daikon");
                    updated = true;
                }
                statement.close();
                c.close();
            } catch (Exception e) {
                System.out.print("update");

                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }

        return updated;

    }

    public String retrieveData(String option, String database){

        Connection c = null;
        Statement stmt = null;
        Statement stmt2 = null;
        String toReturn =  "null";
        if(database.equals("settings")) {
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:settings.db");
                c.setAutoCommit(false);
                System.out.println("Opened database successfully");

                stmt = c.createStatement();
                stmt2 = c.createStatement();

                //while ( rs.next() ) {
                //    String extDir = rs.getString("EXT_DIR");
                //}
                if (option.equals("extDir")) {
                    ResultSet rs = stmt.executeQuery("SELECT * FROM settings WHERE ID=1;");
                    toReturn = rs.getString("EXT_DIR");
                    rs.close();
                }

                if (option.equals("extDaikon")) {
                    ResultSet rss = stmt2.executeQuery("SELECT * FROM settings WHERE ID=2;");
                    toReturn = rss.getString("DAIKON_DIR");
                    rss.close();
                }

                stmt.close();
                c.close();
            } catch (Exception e) {

                System.err.println(e.getClass().getName() + ": retrieveData " + e.getMessage());
                System.exit(0);
            }
        }
        if(database.equals("tests")){
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:tests.db");
                c.setAutoCommit(false);
                System.out.println("Opened database successfully");

                stmt = c.createStatement();
                stmt2 = c.createStatement();

                if (option.equals("testName")) {
                    ResultSet rs = stmt.executeQuery("SELECT * FROM tests;");
                    toReturn = rs.getArray("TESTNAME").toString();//TODO Generate a better solution for the complete return of tests

                    rs.close();
                }

                if (option.equals("fileName")) {
                    ResultSet rss = stmt2.executeQuery("SELECT * FROM tests WHERE ID=2;");
                    toReturn = rss.getString("FILENAME");
                    rss.close();
                }
                stmt.close();
                c.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": retrieveData " + e.getMessage());
                System.exit(0);
            }
        }
        System.out.println("Operation done successfully returning " + toReturn);
        return toReturn;
    }

}
