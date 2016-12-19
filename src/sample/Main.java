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
        if(!databaseOperations.checkDBPresent("settings")){
            databaseOperations.generateDatabase();
            databaseOperations.insertData("extDir", "/...", "settings");
            databaseOperations.insertData("extDaikon", "/...", "settings");
            InitialSetup initialSetup = new InitialSetup();

            initialSetup.display();
        }else {
            Parent root = FXMLLoader.load(getClass().getResource("/sample/FXML_S/sample.fxml"));
            Scene scene = new Scene(root);
            primaryStage.getIcons().add(new Image("/sample/images/post_icon.png"));
            primaryStage.setTitle("Mushroom");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();
        }
    }

}
