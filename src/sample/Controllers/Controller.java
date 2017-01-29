package sample.Controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
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
    @FXML private MenuItem instrumentROS;
    @FXML private MenuItem close;
    @FXML private Line mainEnvironmentNotSetupLine = new Line();
    @FXML private Text mainEnvironmentNotSetupLabel = new Text();
    @FXML private MenuItem architecturalInvariantTest = new MenuItem();
    @FXML private MenuItem rosMonitor = new MenuItem();
    @FXML private MenuItem loadPreviousTest = new MenuItem();
    @FXML private MenuItem loadPreviousReport = new MenuItem();
    @FXML private MenuItem undoInstrumentROS = new MenuItem();
    @FXML private BorderPane MainPane = new BorderPane();
    @FXML private TabPane MainTesting = new TabPane();
    @FXML private StackPane MainStackPane = new StackPane();
    @FXML private Label TestsStatusTest = new Label();
    @FXML private Button ButtonTestLoadPreviousSystem = new Button();
    @FXML private Label TestLoadPreviousSystemLabel = new Label();
    @FXML private Button TestsStartTest = new Button();
    @FXML private TreeTableView<String> MainTreeTable;
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
    private int numberOfMonitors;
    private String verse_reverse;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        numberOfMonitors = 0;
        System.out.println("NOT HERE");
        verse_reverse = "REVERSE";
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
        ButtonTestLoadPreviousSystem.setOnAction(event -> loadPreviousTest(false));

        loadPreviousTest.setOnAction(event -> loadPreviousTest(false));



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

        rosMonitor.setOnAction(event -> {
            boolean loadSkip = false;
            MonitorTabController monitorTabController = new MonitorTabController();
            String[] command = {"/bin/bash", "-c", "source /opt/ros/kinetic/setup.bash && rosnode list "};
            ThreadHandler threadHandler = new ThreadHandler(command, false, true);
            threadHandler.run();
            boolean currentSystemRunning = threadHandler.returnedContinouesArray.get(0).equals("ERROR: Unable to communicate with master!");
            if(!currentSystemRunning) {
                RequestBox requestBox = new RequestBox("System already running", "There is a ROS system already running. Do you want to monitor such system?", true);
                loadSkip = requestBox.requestPass();
                if(loadSkip){
                    monitorTabController.setupMonitorTab(null, null,null,null,"MON","RUN",null, null,null, true, MainTreeTable, "Monitor " + numberOfMonitors);
                }
            }else {
                loadPreviousTest(true);
                monitorTabController.setupMonitorTab(RI,testingProgress,projectTestPath,projectTestLaunchRun,"MON","RUN",testName, RunableCommand,RunableSupportCommand, false, MainTreeTable, "Monitor " + numberOfMonitors);
            }


            try {
                FXMLLoader loader =  new FXMLLoader(getClass().getResource("/sample/FXML_S/sample_live_monitor_tab.fxml"));
                loader.setController(monitorTabController);
                Tab tab = loader.load();
                tab.setText("Monitor " + numberOfMonitors);
                MainStackPane.setVisible(false);
                MainTesting.setVisible(true);

                numberOfMonitors++;
                MainTesting.getTabs().add(tab);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        loadPreviousReport.setOnAction(event -> loadReport());
        int toastMsgTime = 3500; //3.5 seconds
        int fadeInTime = 500; //0.5 seconds
        int fadeOutTime= 500; //0.5 seconds
        instrumentROS.setOnAction(event -> {

            String[] command = {"/bin/bash", "-c", "python " + System.getProperty("user.dir") + "/src/sample/PythonScripts/checkROSStatus.py " + System.getProperty("user.dir") + " " + databaseOperations.retrieveData("extROS", null,"settings")+"/lib/python2.7/dist-packages/rospy/impl/" + " " + databaseOperations.retrieveData("extROS", null,"settings")+"/include/ros/" + " REVERSE NON_SUDO"};
            String[]  NonModToMod = {"/bin/bash", "-c", "gksudo cp " + System.getProperty("user.dir") + "/src/sample/ROSFilesMod/tcpros_service.py "+databaseOperations.retrieveData("extROS", null,"settings")+"/lib/python2.7/dist-packages/rospy/impl/" + " && cp "+System.getProperty("user.dir") +"/src/sample/ROSFilesMod/node_handle.h "+ databaseOperations.retrieveData("extROS", null,"settings")+"/include/ros/"};
            System.out.println(Arrays.toString(NonModToMod));
            ThreadHandler threadHandler = new ThreadHandler(command,false, false); // Asks for ROS_MOD state
            ThreadHandler threadHandler2 = new ThreadHandler(NonModToMod, true, false);

            threadHandler2.run();
            try {
                threadHandler2.join();
                threadHandler.run();
                threadHandler.join();
                System.out.println(threadHandler.returnedData);
                if(threadHandler.returnedData.equals("MOD_ROS")){
                    Toast.makeText((Stage) MainStackPane.getScene().getWindow(), "ROS Instrumented", toastMsgTime, fadeInTime, fadeOutTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        undoInstrumentROS.setOnAction(event -> {
            String[] ModToNonMod = {"/bin/bash", "-c", "gksudo cp " + System.getProperty("user.dir") + "/src/sample/ROSFiles/tcpros_service.py "+databaseOperations.retrieveData("extROS", null,"settings")+"/lib/python2.7/dist-packages/rospy/impl/" + " && cp "+System.getProperty("user.dir") +"/src/sample/ROSFiles/node_handle.h "+ databaseOperations.retrieveData("extROS", null,"settings")+"/include/ros/"};
            String[] command = {"/bin/bash", "-c", "python " + System.getProperty("user.dir") + "/src/sample/PythonScripts/checkROSStatus.py " + System.getProperty("user.dir") + " " + databaseOperations.retrieveData("extROS", null,"settings")+"/lib/python2.7/dist-packages/rospy/impl/" + " " + databaseOperations.retrieveData("extROS", null,"settings")+"/include/ros/" + " VERSE NON_SUDO"};

            ThreadHandler threadHandler = new ThreadHandler(command,false, false); // Asks for ROS_MOD state
            ThreadHandler threadHandler1 = new ThreadHandler(ModToNonMod, true, false);

            threadHandler1.run();
            try {
                threadHandler1.join();
                threadHandler.run();
                threadHandler.join();
                System.out.println(threadHandler.returnedData);
                if(threadHandler.returnedData.equals("NON_MOD_ROS")){
                    Toast.makeText((Stage) MainStackPane.getScene().getWindow(), "ROS Instrumentation Removed", toastMsgTime, fadeInTime, fadeOutTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

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

    // This method covers the recovery of previous systems that have been ran ( Also used for LiveMonitor)
    private void loadPreviousTest(boolean liveMonitor){
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


            boolean dummyPass = true; //TODO needed if it is planned to let other reports be loaded with previous systems.
            if(dummyPass){
                try {
                    FileInputStream fileInputStream = new FileInputStream(testThreadEnvi.PROJECTPATH + "/src/sample/SavedTests/" + testName+ ".ser");
                    System.out.println(testThreadEnvi.PROJECTPATH + "/src/sample/SavedTests/" + testName+ ".ser");
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    RI = (ArchitecturalInvariantInterpretation) objectInputStream.readObject();
                    if(liveMonitor)
                        return;
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
