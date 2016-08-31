package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;



public class Main extends Application {

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        startSystem();
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Mushroom");
        Button startSystem = new Button("Start");
        StackPane initialLayout = new StackPane();
        initialLayout.getChildren().add(startSystem);
        Scene scene = new Scene(initialLayout, 300, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void startSystem(){
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

    private static void serviceHandler(){
        String[] command = {"/bin/bash","-c","python2.7 /home/jam/daikon-ext/catkin_ws/src/recorder/scripts/service_handler.py"};
        ThreadHandler TH3 = new ThreadHandler(command, false);
        TH3.start();
        System.out.print(TH3.returnedData);
        System.out.print("done. \nServiceHandler running...");

    }



}
