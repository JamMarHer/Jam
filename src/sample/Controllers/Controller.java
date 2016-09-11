package sample.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import sample.Logic.MenuSettings;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private javafx.scene.control.MenuItem settings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert settings != null : "fx:id=\"settings\" was not injected: check your FXML file 'sample.fxml'.";
        MenuSettings menuSettings = new MenuSettings();
        settings.setOnAction(e ->{
            try {
                menuSettings.display();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("It works");
        });

        /*MenuImplementation.setOnAction(event -> {
            System.out.print("hello");
        });
        */
    }
}
