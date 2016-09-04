package sample.Controllers;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.application.Platform.runLater;

/**
 * Created by jam on 9/3/16.
 */
public class SettingsMenuController implements Initializable {

    @FXML
    private Button MenuSettingImplementation;
    @FXML
    private Button MenuSettingDaikon;
    @FXML
    private Label MenuSettingImplementationPath;


    @Override
    public void initialize(URL location, ResourceBundle resources) {



        MenuSettingImplementation.setOnAction(event -> {
            System.out.print("hello");
        });

        MenuSettingDaikon.setOnAction(event -> {
            System.out.print("lol");
        });


    }

    @FXML
    private void MenuSettingImplementationLocateDirectory (ActionEvent event){
        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("ROS Implementation Directory");
        File selectedDirectory = directoryChooser.showDialog(stage);
        String directoryPath = selectedDirectory.getAbsolutePath();

        Path pathClass = new Path(directoryPath);

        SimpleStringProperty pathProperty = new SimpleStringProperty();
        pathProperty.bind(pathClass.returnPath());
        MenuSettingImplementationPath.textProperty().bind(new SimpleStringProperty(directoryPath));



        System.out.print("same shit");

    }


}

class Path{

    private SimpleStringProperty path_ = new SimpleStringProperty();

    public Path(String Path){
        System.out.print("old shit");
        path_.setValue(Path);
    }

    public SimpleStringProperty returnPath(){
        return path_;
    }


}



