package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Main extends Application {

    private DatabaseOperations databaseOperations;

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        Scene scene = new Scene(root, 1200, 700);

        databaseOperations = new DatabaseOperations();
        if(!databaseOperations.checkDBPresent()){
            System.out.println("DB not present");
            databaseOperations.generateDatabase();
            databaseOperations.insertData("extDir","...");
            databaseOperations.insertData("daikonDir", "...");
        }


        primaryStage.setTitle("Mushroom");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private  void startSystem(){
        System.out.println("Initializing roscore...");
        roscoreInit();
        System.out.println("Initializing ServiceHandler...");
        serviceHandler();
    }

    private static void roscoreInit(){
        String[] command = {"/bin/bash","-c","roscore"};
        String[] command2 = {"/bin/bash","-c","rosservice list"};
        ThreadHandler TH = new ThreadHandler(command, false);
        TH.start();
        try{ Thread.sleep(4000); }catch(Exception e){ e.printStackTrace(); }

        ThreadHandler TH2 = new ThreadHandler(command2, false);
        TH2.start();
        try{ Thread.sleep(3000); }catch(Exception e){ e.printStackTrace(); }
        if (TH2.returnedData != null){
            System.out.println("done.");
        } else {
            System.out.println("failed.");
        }
    }

    private  void serviceHandler(){
        String[] command = {"/bin/bash","-c","python2.7" + databaseOperations.retrieveData("extDir") + "scripts/service_handler.py"};
        ThreadHandler TH3 = new ThreadHandler(command, false);
        TH3.start();
        System.out.print(TH3.returnedData);
        System.out.print("done. \nServiceHandler running...");

    }



}
