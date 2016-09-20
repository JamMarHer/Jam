package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sample.Logic.DatabaseOperations;
import sample.Logic.InitialSetup;
import sample.Logic.ThreadHandler;


public class Main extends Application {

    private DatabaseOperations databaseOperations;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        databaseOperations = new DatabaseOperations();
        if(!databaseOperations.checkDBPresent()){
            databaseOperations.generateDatabase();
            databaseOperations.insertData("extDir", "/...");
            databaseOperations.insertData("extDaikon", "/...");
            InitialSetup initialSetup = new InitialSetup();
            initialSetup.display();
        }else {
            Parent root = FXMLLoader.load(getClass().getResource("/sample/FXML_S/sample.fxml"));
            Scene scene = new Scene(root);
            primaryStage.getIcons().add(new Image("/sample/images/post_icon.png"));
            primaryStage.setTitle("Mushroom");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        }
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
