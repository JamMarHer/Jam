package sample.Controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Interfaces.ReportInterpretation;
import sample.Logic.*;
import sample.Main;
import sample.Properties.TestingProgress;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class Controller implements Initializable, Serializable {

    private boolean environmentSetup = false;

    @FXML private javafx.scene.control.MenuItem settings;
    @FXML private MenuItem close;
    @FXML private Line mainEnvironmentNotSetupLine = new Line();
    @FXML private Text mainEnvironmentNotSetupLabel = new Text();
    @FXML private MenuItem architecturalInvariantTest = new MenuItem();
    @FXML private MenuItem loadPreviousTest = new MenuItem();
    @FXML private MenuItem loadPreviousReport = new MenuItem();
    @FXML private BorderPane MainPane = new BorderPane();
    @FXML private TabPane MainTesting = new TabPane();
    @FXML private StackPane MainStackPane = new StackPane();
    @FXML private Label TestsStatusTest = new Label();
    @FXML private Button ButtonTestLoadPreviousSystem = new Button();
    @FXML private Label TestLoadPreviousSystemLabel = new Label();
    @FXML private Button TestsStartTest = new Button();
    final CategoryAxis xAxys = new CategoryAxis();
    final NumberAxis yAxys = new NumberAxis();
    final TestingProgress testingProgress = new TestingProgress();
    @FXML private BarChart<String, Number> BarChartII = new BarChart<String, Number>(xAxys,yAxys);
    private ReportInterpretation RI;
    private String projectTestPath;
    private File projectTestLaunchRun;
    private TestThread testThreadROS;
    private TestThread testThreadPoject;
    private TestThread testThreadEnvi;
    private boolean recuperatedTestThreadEnv;
    private DatabaseOperations databaseOperations;
    private String[] RunableCommand;
    private String[] RunableSupportCommand;
    private String testName;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseOperations = new DatabaseOperations();
        TestsStatusTest.setText("...");
        try {
            testThreadEnvi = new TestThread(null,null,null,null,null,null,null); // to obtain PAth
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        if(!(databaseOperations.retrieveData("extDir", null,"settings").equals("/...") || databaseOperations.retrieveData("extDaikon", null, "settings").equals("/..."))) {
            environmentSetup = true;
            mainEnvironmentNotSetupLine.setVisible(false);
            mainEnvironmentNotSetupLabel.setVisible(false);
        }

        if(!databaseOperations.checkDBPresent("tests")){
            ButtonTestLoadPreviousSystem.setVisible(false);
            TestLoadPreviousSystemLabel.setVisible(false);
        }
        ButtonTestLoadPreviousSystem.setOnAction(event -> loadPreviousTest());

        loadPreviousTest.setOnAction(event -> loadPreviousTest());



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
            RI = testSuite.display();
            if(RI.getID().equals("AII")){
                //**************************************************************************************
                TestTabController testTabController = new TestTabController();
                testTabController.setupTestTab(RI, "",  recuperatedTestThreadEnv, null, null, new DatabaseOperations());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/FXML_S/test.fxml"));
                loader.setController(testTabController);
                MainStackPane.setVisible(false);
                MainTesting.setVisible(true);

                try {
                    MainTesting.getTabs().add( loader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //initializeTest((ArchitecturalInvariantInterpretation)RI);
            }
        });


        loadPreviousReport.setOnAction(event -> loadReport());

    }

    private void loadReport(){
        databaseOperations = new DatabaseOperations();
        RequestBox requestBox = new RequestBox("Report Selection", "Select a report", false, databaseOperations);
        RunableCommand = new String[]{};
        try {
            RunableCommand = requestBox.retrieveTTE();
            databaseOperations = new DatabaseOperations();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(RunableCommand !=null) {
            recuperatedTestThreadEnv = true;

            testName = requestBox.testName;
             try {
                 FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir") + "/src/sample/SavedReports/" + testName+ ".ser");
                 ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                 TestReport testReport = (TestReport) objectInputStream.readObject();
                 testReport.displayReport();
                 objectInputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }


    private void loadPreviousTest(){
        databaseOperations = new DatabaseOperations();
        RequestBox requestBox = new RequestBox("Pick a System", "Select a previous system", false, databaseOperations);
        RunableCommand = new String[]{};
        try {
            RunableCommand = requestBox.retrieveTTE();
            databaseOperations = new DatabaseOperations();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(RunableCommand !=null) {
            recuperatedTestThreadEnv = true;
            RunableSupportCommand = requestBox.supportCommand;
            testName = requestBox.testName;

            RequestBox requestBox2 = new RequestBox("Previous post-daikon report", "There is an available post-daikon report. Do you want to use it?");
            if(requestBox2.requestPass()){
                try {
                    FileInputStream fileInputStream = new FileInputStream(testThreadEnvi.PROJECTPATH + "/src/sample/SavedTests/" + testName+ ".ser");
                    System.out.println(testThreadEnvi.PROJECTPATH + "/src/sample/SavedTests/" + testName+ ".ser");
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    RI = (ArchitecturalInvariantInterpretation) objectInputStream.readObject();

                    objectInputStream.close();
                    TestTabController testTabController = new TestTabController();
                    testTabController.setupTestTab(RI, testName, recuperatedTestThreadEnv, RunableCommand, RunableSupportCommand, new DatabaseOperations());
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/FXML_S/test.fxml"));
                    loader.setController(testTabController);
                    MainStackPane.setVisible(false);
                    MainTesting.setVisible(true);

                    try {
                        MainTesting.getTabs().add( loader.load());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else {
            recuperatedTestThreadEnv = false;
        }
    }

}
