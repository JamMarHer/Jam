package sample.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sample.DatabaseOperations;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by jam on 9/3/16.
 */
public class SettingsMenuController implements Initializable {

    @FXML private Button MenuSettingImplementation;
    @FXML private Button MenuSettingDaikon;
    //--------------------------Implementation
    @FXML private Button MenuSettingImplementationApply;
    @FXML private Button MenuSettingImplementationLocateDirectory;
    @FXML private TextField MenuSettingImplementationPath;
    //--------------------------Daikon
    @FXML private Button MenuSettingDaikonApply;
    @FXML private Button MenuSettingDaikonLocateDirectory;
    @FXML private TextField MenuSettingDaikonPath;
    //--------------------------------
    @FXML private VBox ImplementationSettings;
    @FXML private VBox DaikonSettings;

    private int tempScene = 0;

    DatabaseOperations databaseOperations;
    private String extPath;
    private String daikonPath;
    private static final String selectedColor = "#828E8E";
    private static final String normalColor = " #A3A3A3";


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        databaseOperations = new DatabaseOperations();
        extPath = databaseOperations.retrieveData("extDir");
        MenuSettingImplementationPath.setText(extPath);

        MenuSettingImplementation.setOnAction(event -> {
            MenuSettingImplementation.setStyle("-fx-background-color: " + selectedColor +";");
            resetColorsBut("MenuSettingImplementation");
            if (tempScene  == 1){ DaikonSettings.setVisible(false); tempScene = 0;}
            ImplementationSettings.setVisible(true);

        });

        MenuSettingDaikon.setOnAction(event -> {
            MenuSettingDaikon.setStyle("-fx-background-color: " + selectedColor +";");
            resetColorsBut("MenuSettingDaikon");
            System.out.print("lol");
            if(tempScene == 0){ ImplementationSettings.setVisible(false); tempScene = 1; }
            DaikonSettings.setVisible(true);

        });


    }

    // Recode the Below method to handle events from all buttons in the left pane

    @FXML
    private void MenuSettingImplementationLocateDirectory (ActionEvent event){
        System.out.print(event.getEventType().getName());
        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
        String nameOfButton = ((Button)event.getSource()).getId();
        // Continue here so that the name of Button can be compared to deciced which pathh to use
        System.out.print(nameOfButton);
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("ROS Implementation Directory");
        File selectedDirectory = directoryChooser.showDialog(stage);
        String directoryPath = selectedDirectory.getAbsolutePath();
        extPath = directoryPath;


        MenuSettingImplementationPath.setText(directoryPath);

    }

    @FXML
    private void MenuSettingImplementationApply (ActionEvent event){
        extPath = MenuSettingImplementationPath.getText();
        System.out.print("updating rxtPath to" + extPath);
        databaseOperations.updateData("extDir", extPath);

    }

    private void resetColorsBut(String notTo){
        if(notTo.equals("MenuSettingDaikon")){
            MenuSettingImplementation.setStyle("-fx-background-color: " + normalColor +";");
        }
        if(notTo.equals("MenuSettingImplementation")){
            MenuSettingDaikon.setStyle("-fx-background-color: " + normalColor +";");
        }
    }

}




