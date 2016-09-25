package sample.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sample.Logic.DatabaseOperations;

import java.io.File;
import java.net.URL;
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
    @FXML Button setup_ros_button_next = new Button();
    @FXML Button setup_ros_button_back = new Button();
    @FXML BorderPane setup_daikon = new BorderPane();
    @FXML Button setup_daikon_button_locatedirectory = new Button();
    @FXML TextField setup_daikon_edittext_path = new TextField();
    @FXML Button setup_daikon_button_next = new Button();
    @FXML Button setup_daikon_button_back = new Button();
    @FXML BorderPane setup_test = new BorderPane();
    @FXML TextField setup_ros_edittext_path = new TextField();
    @FXML Button setup_test_button_next = new Button();

    private FXMLLoader welcomeScene = new FXMLLoader(getClass().getResource("/sample/FXML_S/setup.fxml"));
    private FXMLLoader rosScene = new FXMLLoader(getClass().getResource("/sample/FXML_S/setup_ros.fxml"));
    private FXMLLoader daikonScene = new FXMLLoader(getClass().getResource("/sample/FXML_S/setup_daikon.fxml"));
    private FXMLLoader testScene = new FXMLLoader(getClass().getResource("/sample/FXML_S/setup_test.fxml"));
    private int currentScene = 0; // 0 = welcome, 1 = ros, 2 = daikon, 3 = test;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DatabaseOperations databaseOperations  = new DatabaseOperations();
        setup_ros_edittext_path.setText(databaseOperations.retrieveData("extDir",null, "settings"));
        setup_daikon_edittext_path.setText(databaseOperations.retrieveData("extDaikon", null, "settings"));

        it.setImage(new Image("file:src/sample/images/post_icon.png"));


        setup_ros_button_back.setOnAction(event ->{
                updateScene(welcomeScene, setup_ros_button_back, 0);
        });
        setup_daikon_button_back.setOnAction(event ->{
                currentScene = 1;
                updateScene(rosScene, setup_daikon_button_back, 1);
        });
        setup_button_begin.setOnAction(event ->{
                currentScene = 1;
                System.out.print(currentScene);
                updateScene(rosScene, setup_button_begin, 1);
        });
        setup_ros_button_next.setOnAction(event ->{
                currentScene = 2;
                updateScene(daikonScene, setup_ros_button_next, 2);
        });
        setup_daikon_button_next.setOnAction(event ->{
                currentScene = 3;
                updateScene(testScene, setup_daikon_button_next, 3);
        });
        /*
        setup_test_button_back.setOnAction(event ->{
                currentScene
                updateScene(daikonScene);
        });
        */
        setup_test_button_next.setOnAction(e->{
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/sample/FXML_S/sample.fxml"));
                Scene scene = new Scene(root, 1200, 700);
                Stage currentStage = (Stage) setup_test_button_next.getScene().getWindow();
                currentStage.setTitle("Mushroom");
                currentStage.setScene(scene);
                currentStage.show();
            }catch (Exception exception){
                exception.printStackTrace();
            }
        });
    }

    @FXML
    private void InitialSettingImplementationLocateDirectory (ActionEvent event){
        System.out.print(event.getEventType().getName());
        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
        String nameOfButton = ((Button)event.getSource()).getId();
        String directoryPath = "";
        // Continue here so that the name of Button can be compared to deciced which pathh to use
        chooseFile(stage);
        System.out.println(nameOfButton);

    }

    private void chooseFile(Stage stage){
        System.out.print(currentScene);
        DatabaseOperations databaseOperations = new DatabaseOperations();
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(stage);
            if(currentScene == 1){
                directoryChooser.setTitle("ROS Implementation Directory");
            }
            if(currentScene == 2){
                directoryChooser.setTitle("Daikon Directory");
            }
            if(currentScene == 1){
                setup_ros_edittext_path.setText(selectedDirectory.getAbsolutePath());
                databaseOperations.updateData("extDir",selectedDirectory.getAbsolutePath(), "settings");
            }
            if(currentScene == 2){
                setup_daikon_edittext_path.setText(selectedDirectory.getAbsolutePath());
                databaseOperations.updateData("extDaikon",selectedDirectory.getAbsolutePath(), "settings");
            }
        }catch (Exception e){
            System.out.print("Path selection canceled");
            e.printStackTrace();
        }

    }


    private void updateScene(FXMLLoader loader, Button fromButton, int cScene){
        try{
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) fromButton.getScene().getWindow();
            InitialSetupController controller =  loader.getController();
            controller.setScene(cScene);
            currentStage.setScene(scene);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //param load ~/Simulation/ardupilot/Tools/Frame_params/Erle-Copter.param

    public void setScene(int scene){
        currentScene = scene;
    }

    @FXML
    public void being(ActionEvent event){

    }
}
