package sample.Logic;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Created by jam on 9/13/16.
 */


public class InitialSetup {



    public boolean clicked = false;

    public  void display() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/sample/FXML_S/setup.fxml"));
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Initial Setup - Mushroom");    
        Scene scene = new Scene(root);
        window.setScene(scene);
        window.setResizable(false);
        window.show();
        // layout.setCenter(addGrid());

    }
}
