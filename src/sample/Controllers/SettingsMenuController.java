package sample.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sample.Logic.DatabaseOperations;

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

    private int tempScene = 0; // 0 = implementation, 1 = daikon

    DatabaseOperations databaseOperations;
    private String extPath;
    private String daikonPath;
    private static final String selectedColor = "#828E8E";
    private static final String normalColor = " #747474";


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        databaseOperations = new DatabaseOperations();
        extPath = databaseOperations.retrieveData("extDir");
        MenuSettingImplementationPath.setText(extPath);
        MenuSettingDaikonPath.setText(extPath);

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
        String directoryPath = "";
        // Continue here so that the name of Button can be compared to deciced which pathh to use
        chooseFile(stage);
        System.out.println(nameOfButton);

    }

    private void chooseFile(Stage stage){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if(tempScene == 0){
            directoryChooser.setTitle("ROS Implementation Directory");
        }
        if(tempScene == 1){
            directoryChooser.setTitle("Daikon Directory");
        }
        File selectedDirectory = directoryChooser.showDialog(stage);
        extPath = selectedDirectory.getAbsolutePath();
        if(tempScene == 0){ MenuSettingImplementationPath.setText(extPath);}
        if(tempScene == 1){ MenuSettingDaikonPath.setText(extPath);}
    }

    @FXML
    private void MenuSettingImplementationApply (ActionEvent event){
        if(tempScene == 0){ databaseOperations.updateData("extDir", extPath);}
        if(tempScene == 1){databaseOperations.updateData("extDaikon", extPath);}
        System.out.println("updating " + tempScene +" " + extPath);
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




