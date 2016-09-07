package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Controllers.SettingsMenuController;

import java.io.IOException;

/**
 * Created by jam on 9/1/16.
 */
public class MenuSettings {

    public boolean clicked = false;

    public  void display() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("settings.fxml"));

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Settings");
        window.setMinWidth(600);
        window.setMinHeight(450);
        Button toClose = new Button("Close");
        toClose.setOnAction(e -> window.close());


        BorderPane layout = new BorderPane();
        Scene scene = new Scene(root);
        window.setScene(scene);
        window.setResizable(false);
        window.show();
        // layout.setCenter(addGrid());

    }


}

