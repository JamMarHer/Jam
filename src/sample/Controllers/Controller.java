package sample.Controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    @FXML private Label InputStimeStamp = new Label();
    @FXML private Label InputText = new Label();
    @FXML private Label InputTotalPub = new Label();
    @FXML private Label InputTotalSub = new Label();
    @FXML private Label InputTotalServ = new Label();
    @FXML private Label InputTotal = new Label();
    @FXML private Button testing_project_button_locatedirectory = new Button();
    @FXML private TextField testing_prokect_edittext_path = new TextField();
    @FXML private Button testing_project_button_locate_launch_run = new Button();
    @FXML private TextField testing_prokect_edittext_path_launch_run = new TextField();
    @FXML private ProgressBar TestingProgressBar = new ProgressBar();
    @FXML private ProgressIndicator TestingProgressIndicator = new ProgressIndicator();
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
                initializeTest((ArchitecturalInvariantInterpretation)RI);
            }
        });

        TestsStartTest.setOnAction(event -> {
            try {
                StartTest();
            } catch (Exception e) {
                e.printStackTrace();
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
            testing_prokect_edittext_path_launch_run.setText("ALL SET");
            testing_prokect_edittext_path_launch_run.setEditable(false);
            testing_prokect_edittext_path.setText("ALL SET");
            testing_prokect_edittext_path.setEditable(false);
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
                    initializeTest((ArchitecturalInvariantInterpretation)RI);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else {
            recuperatedTestThreadEnv = false;
        }
    }

    public void initializeTest(ArchitecturalInvariantInterpretation AII){
        InputStimeStamp.setText(AII.getTimeStamp());
        InputText.setText(AII.getInputFile());
        MainTesting.setVisible(true);
        MainTesting.getTabs().get(0).setText("Architecture Invariant Test");
        MainStackPane.setVisible(false);
        xAxys.setLabel("Recording Node");
        yAxys.setLabel("Quantity");
        XYChart.Series series = new XYChart.Series();
        series.setName("Static");
        series.getData().add(new XYChart.Data<String,Number>("Publisher", AII.getSize("/rec/arch_pub", "Static")));
        series.getData().add(new XYChart.Data<String,Number>("Subscriber", AII.getSize("/rec/arch_sub", "Static")));
        series.getData().add(new XYChart.Data<String,Number>("Services", AII.getSize("/rec/arch_srvs", "Static")));
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Variable");
        series2.getData().add(new XYChart.Data<String,Number>("Publisher", AII.getSize("/rec/arch_pub", "Variable")));
        series2.getData().add(new XYChart.Data<String,Number>("Subscriber", AII.getSize("/rec/arch_sub", "Variable")));
        series2.getData().add(new XYChart.Data<String,Number>("Services", AII.getSize("/rec/arch_srvs", "Variable")));
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Restricted Variable");
        series3.getData().add(new XYChart.Data<String,Number>("Publisher", AII.getSize("/rec/arch_pub", "Restricted")));
        series3.getData().add(new XYChart.Data<String,Number>("Subscriber", AII.getSize("/rec/arch_sub", "Restricted")));
        series3.getData().add(new XYChart.Data<String,Number>("Services", AII.getSize("/rec/arch_srvs", "Restricted")));
        BarChartII.getData().setAll(series,series2,series3);
        BarChartII.setVisible(true);
        BarChartII.impl_updatePeer();

        InputTotalPub.setText("/rec/arch_pub:   "+String.valueOf(AII.getSize("/rec/arch_pub", "Static")+AII.getSize("/rec/arch_pub", "Variable")+AII.getSize("/rec/arch_pub", "Restricted")));
        InputTotalSub.setText("/rec/arch_sub:   "+String.valueOf(AII.getSize("/rec/arch_sub", "Variable")+AII.getSize("/rec/arch_sub", "Static")+AII.getSize("/rec/arch_sub", "Restricted")));
        InputTotalServ.setText("/rec/arch_srvs:  "+String.valueOf(AII.getSize("/rec/arch_srvs", "Static")+AII.getSize("/rec/arch_srvs", "Variable")+AII.getSize("/rec/arch_srvs", "Restricted")));
        InputTotal.setText("Total:                "+String.valueOf(AII.getSize()));
        TestingProgressIndicator.progressProperty().bind(testingProgress.stateProperty());



    }


    @FXML
    public void testingLocatePath(ActionEvent event){
        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
        chooseFile(stage, "project");

    }
    @FXML
    public void testingLocateLaunchRun(ActionEvent event){
        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
        chooseFile(stage, "launch_run");
    }

    private void chooseFile(Stage stage, String from){
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            FileChooser fileChooser = new FileChooser();
            if(from.equals("project")) {
                directoryChooser.setTitle("Project path");
                File selectedDirectory = directoryChooser.showDialog(stage);
                projectTestPath = selectedDirectory.getAbsolutePath();
                testing_prokect_edittext_path.setText(selectedDirectory.getAbsolutePath());

            }
            if(from.equals("launch_run")) {
                fileChooser.setTitle("Launch/Run");
                File selectedDirectory = fileChooser.showOpenDialog(stage);
                projectTestLaunchRun= selectedDirectory.getAbsoluteFile();
                testing_prokect_edittext_path_launch_run.setText(selectedDirectory.getAbsolutePath());
            }

        }catch (Exception e){
            System.out.print("Path selection canceled");
            e.printStackTrace();
        }
    }

    public void StartTest () throws InterruptedException, SQLException, ClassNotFoundException, IOException {
        if(recuperatedTestThreadEnv){
            System.out.print(testName);

            System.out.print(RI.getID());
            testThreadEnvi = new TestThread(RI, testingProgress, projectTestPath, projectTestLaunchRun, "AII", "ENV",testName);
            testThreadEnvi.setRunCommand(RunableCommand);
            testThreadEnvi.setSupportCommand(RunableSupportCommand);
            TestsStatusTest.setText("All Set");
            testThreadEnvi.setAllSetSucces(true);
            testThreadEnvi.start();
            testThreadEnvi.join();
            TestReport testReport = testThreadEnvi.getTestReport();
            if( testReport != null){
                System.out.println("Reporting");
                FileOutputStream outputStream = new FileOutputStream(testThreadEnvi.PROJECTPATH + "/src/sample/SavedReports/" + testThreadEnvi.testName + ".ser");
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(testReport);
                objectOutputStream.close();
                testReport.displayReport();
            }else{
                System.out.println("Error: Not reporting");
            }


        }else if(projectTestPath == null || projectTestLaunchRun == null){
            TestsStatusTest.setText("Project Path || Project Launch/Run not set!");
        }else{
            try {
                System.out.print("IN START TEST");
                RequestBox successfullrun = new RequestBox("Save System?", "Do you want to save the system if is ran without errors?", true);
                String tempRequestedSave = successfullrun.requestUser();

                testThreadROS = new TestThread(RI, testingProgress, projectTestPath, projectTestLaunchRun, "AII", "ROS", null);
                processStatusIndicator("Analyzing ROS", testThreadROS);
                testThreadROS.run();
                testThreadROS.join();
                if(testThreadROS.getROSPassed()) {
                    testThreadPoject = new TestThread(RI, testingProgress, projectTestPath, projectTestLaunchRun, "AII", "PRO", null);
                    processStatusIndicator("Analizing Project", testThreadPoject);
                    testThreadPoject.run();
                    testThreadPoject.join();
                    if(testThreadPoject.getPathPassed()){
                        processStatusIndicator("Analyzing Environment", testThreadEnvi);
                        testThreadEnvi = new TestThread(RI, testingProgress, projectTestPath, projectTestLaunchRun, "AII", "ENV", tempRequestedSave);
                        testThreadEnvi.run();
                        testThreadEnvi.join();
                        if(testThreadEnvi.getFinalEnvironmentPassed()){
                            if(!tempRequestedSave.equals("NON")){
                                //if(!databaseOperations.checkDBPresent("tests") || !databaseOperations.checkTablePresent("tests", "tests")){ // We assume that
                                    databaseOperations.generateTestsDatabase();
                                //}
                                databaseOperations.insertData("Full", testThreadEnvi.testName,Arrays.toString(testThreadEnvi.getRunCommand()),Arrays.toString(testThreadEnvi.getSupportCommand()));
                                if(RI != null) {
                                    FileOutputStream outputStream = new FileOutputStream(testThreadEnvi.PROJECTPATH + "/src/sample/SavedTests/" + testThreadEnvi.testName + ".ser");
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                                    objectOutputStream.writeObject(RI);
                                    objectOutputStream.close();
                                }
                                testThreadEnvi.startTest();
                                testThreadEnvi.join();
                                if(testThreadEnvi.getTestReport() != null){
                                    FileOutputStream outputStream = new FileOutputStream(testThreadEnvi.PROJECTPATH + "/src/sample/SavedReports/" + testThreadEnvi.testName + ".ser");
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                                    objectOutputStream.writeObject(testThreadEnvi.getTestReport());
                                    objectOutputStream.close();
                                    testThreadEnvi.getTestReport().displayReport();
                                }else {
                                    System.out.println("Test Fail");
                                }

                            }
                            System.out.print("ALLSET");

                        }else {
                            System.out.print("DEAD IN ENV");
                        }
                    }else {
                        System.out.print("DEAD IN PRO");
                    }
                }else {
                    System.out.print("DEAD IN ROS");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }

    private void processStatusIndicator(String currentStatus, TestThread testThread){
        Task<Void> task = new Task<Void>() {
            @Override public Void call() {
                int numberDots = 0;
                String update = currentStatus;
                String nonUpdate = currentStatus;
                while(!testThread.getROSPassed()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.interrupted();

                        break;
                    }

                    if(numberDots == 3){
                        numberDots =0;
                        updateMessage(nonUpdate);
                        update = nonUpdate;
                    }else{
                        update += ".";
                        updateMessage(update);
                        numberDots++;
                    }
                }
                return null;
            }
        };
        TestsStatusTest.textProperty().bind(task.messageProperty());
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

    }

    private void analizePath(String path){

    }
}
