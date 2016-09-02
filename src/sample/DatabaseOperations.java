package sample;

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
            String sql = "CREATE TABLE Settings " +
                    "(EXT_DIR         TEXT    NOT NULL " +
                    ")";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        }catch (Exception e){
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
                String sql = "INSERT INTO Settings (EXT_DIR) VALUES ('"+data+"' );";
                stmt.executeUpdate(sql);
                created = true;
            }

            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": insertData " + e.getMessage() );
            System.exit(0);
        }
        return created;
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
            ResultSet rs = stmt.executeQuery( "SELECT * FROM Settings;" );
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
        System.out.println("Operation done successfully");
        return toReturn;
    }

}
