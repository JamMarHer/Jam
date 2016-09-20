package sample.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import sample.Logic.ArchitecturalInvariantInterpretation;
import sample.Logic.DatabaseOperations;
import sample.Logic.MenuSettings;
import sample.Logic.TestSuite;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private boolean environmentSetup = false;

    @FXML private javafx.scene.control.MenuItem settings;
    @FXML private MenuItem close;
    @FXML private Line mainEnvironmentNotSetupLine = new Line();
    @FXML private Text mainEnvironmentNotSetupLabel = new Text();
    @FXML private MenuItem architecturalInvariantTest = new MenuItem();

    private ArchitecturalInvariantInterpretation AII;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DatabaseOperations databaseOperations = new DatabaseOperations();


        if(!(databaseOperations.retrieveData("extDir").equals("/...") || databaseOperations.retrieveData("extDaikon").equals("/..."))){
            environmentSetup = true;
            mainEnvironmentNotSetupLine.setVisible(false);
            mainEnvironmentNotSetupLabel.setVisible(false);
        }

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
        close.setOnAction(event -> System.exit(0));
        architecturalInvariantTest.setOnAction(event -> {
            TestSuite testSuite = new TestSuite("Architecture");
            testSuite.display();
            while (!(testSuite.Obtainable())){
                if(testSuite.closed()){
                    break;
                }
            }
            AII = testSuite.reTrieveAII();
            System.out.print("In");
        });
    }



}
