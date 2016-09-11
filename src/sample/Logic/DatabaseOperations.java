package sample.Logic;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by jam on 9/1/16.
 */
public class DatabaseOperations {

    ArrayList<String> options = new ArrayList<>();

    public DatabaseOperations(){
        options.add("extDir");

    }

    public boolean checkDBPresent(){
        File file = new File("settings.db");
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
                    "(ID INT PRIMARY KEY    NOT NULL,"+
                    "EXT_DIR         TEXT, " +
                    "DAIKON_DIR         TEXT"+
                    ")";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        }catch (Exception e){
            System.out.print("generate");
            System.out.println( e.getClass().getName() + ": generateDatabase " + e.getMessage());
            System.exit(0);
        }

        System.out.print("Table created successfully");
    }

    // arguments option = where to insert, data = what to insert
    // returns boolean = true if created false if it was not
    public boolean insertData(String option, String data){
        boolean created = false;
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:settings.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            if(option.equals("extDir")){
                String sql = "INSERT INTO settings (ID, EXT_DIR) VALUES (1, '"+data+"' );";
                stmt.executeUpdate(sql);
                created = true;
            }
            if(option.equals("daikonDir")){
                String sql = "INSERT INTO settings (ID, DAIKON_DIR) VALUES (2, '"+data+"' );";
                stmt.executeUpdate(sql);
                created =true;
            }

            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.out.print("insert");
            System.err.println( e.getClass().getName() + ": insertData " + e.getMessage() );
            System.exit(0);
        }
        return created;
    }

    public boolean updateData(String option, String data){
        boolean updated = false;
        Connection c = null;
        Statement statement = null;

        try{
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:settings.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            statement = c.createStatement();
            if (option.equals("extDir")) {
                String sql = "UPDATE settings set EXT_DIR = '" + data +"' WHERE ID=1;";
                System.out.print(data + "From DB");
                statement.executeUpdate(sql);
                c.commit();
                updated = true;
            }
            statement.close();
            c.close();
        } catch ( Exception e ) {
            System.out.print("update");

            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }

        return updated;

    }



    public String retrieveData(String option){

        Connection c = null;
        Statement stmt = null;
        String toReturn =  "null";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:settings.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM settings WHERE ID=1;" );
            //while ( rs.next() ) {
            //    String extDir = rs.getString("EXT_DIR");
            //}
            if(option.equals("extDir")) {
                toReturn = rs.getString("EXT_DIR");
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {

            System.err.println( e.getClass().getName() + ": retrieveData " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Operation done successfully returning " + toReturn);
        return toReturn;
    }

}
