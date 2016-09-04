package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

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
            menuSettings.display();
            System.out.println("It works");
        });
    }
}
