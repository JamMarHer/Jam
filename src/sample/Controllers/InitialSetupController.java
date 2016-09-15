package sample.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jdk.nashorn.internal.runtime.ECMAException;
import sample.Logic.DatabaseOperations;
import sample.Logic.InitialSetup;

import java.io.File;
import java.net.URL;
import java.security.spec.ECField;
import java.util.ResourceBundle;

/**
 * Created by jam on 9/13/16.
 */
public class InitialSetupController implements Initializable {


    @FXML ImageView it = new ImageView();
    @FXML CheckBox setup_checkbox_local = new CheckBox();
    @FXML CheckBox setup_checkbox_shh = new CheckBox();
    @FXML Button setup_button_begin = new Button();
    @FXML BorderPane setup_ros = new BorderPane();
    @FXML Button setup_ros_button_locatedirectory = new Button();
    @FXML TextField setup_ros_edittext_path = new TextField();
    @FXML Button setup_ros_button_next = new Button();
    @FXML Button setup_ros_button_back = new Button();
    @FXML BorderPane setup_daikon = new BorderPane();
    @FXML Button setup_daikon_button_locatedirectory = new Button();
    @FXML TextField setup_daikon_edittext_path = new TextField();
    @FXML Button setup_daikon_button_next = new Button();
    @FXML Button setup_daikon_button_back = new Button();
    @FXML HBox setup_test = new HBox();

    FXMLLoader welcomeScene = new FXMLLoader(getClass().getResource("/sample/FXML_S/setup.fxml"));
    FXMLLoader rosScene = new FXMLLoader(getClass().getResource("/sample/FXML_S/setup_ros.fxml"));
    FXMLLoader daikonScene = new FXMLLoader(getClass().getResource("/sample/FXML_S/setup_daikon.fxml"));
    FXMLLoader testScene = new FXMLLoader(getClass().getResource("/sample/FXML_S/setup_test.fxml"));


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        it.setImage(new Image("file:src/sample/images/post_icon.png"));

        setup_ros_button_back.setOnAction(event -> updateScene(welcomeScene, setup_ros_button_back));
        setup_daikon_button_back.setOnAction(event -> updateScene(rosScene, setup_daikon_button_back));
        //setup_test_button_back.setOnAction(event -> updateScene(daikonScene));
        setup_button_begin.setOnAction(event -> updateScene(rosScene, setup_button_begin));
        setup_ros_button_next.setOnAction(event -> updateScene(daikonScene, setup_ros_button_next));
        setup_daikon_button_next.setOnAction(event -> updateScene(testScene, setup_daikon_button_next));

    }


    private void updateScene(FXMLLoader loader, Button fromButton){
        try{
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) fromButton.getScene().getWindow();
            currentStage.setScene(scene);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private  void setupDB(DatabaseOperations databaseOperations, String ExtDir, String DaikonDir){
        System.out.println("DB not present");
        databaseOperations.generateDatabase();
        databaseOperations.insertData("extDir",ExtDir);
        databaseOperations.insertData("daikonDir", DaikonDir);
    }

    //param load ~/Simulation/ardupilot/Tools/Frame_params/Erle-Copter.param



    @FXML
    public void being(ActionEvent event){

    }
}
