package sample.Controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Interfaces.ReportInterpretation;
import sample.Logic.*;
import sample.Properties.TestingProgress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * Created by ccc on 11/3/16.
 */
public class TestTabController implements Initializable {


    @FXML private Tab TestTab = new Tab();
    @FXML private StackPane MainStackPane = new StackPane();
    @FXML private Label InputStimeStamp = new Label();
    @FXML private Label InputText = new Label();
    @FXML private Label InputTotalPub = new Label();
    @FXML private Label InputTotalSub = new Label();
    @FXML private Label InputTotalServ = new Label();
    @FXML private Label InputTotal = new Label();
    @FXML private TextField testing_prokect_edittext_path = new TextField();
    @FXML private TextField testing_prokect_edittext_path_launch_run = new TextField();
    @FXML private ProgressIndicator TestingProgressIndicator = new ProgressIndicator();
    @FXML private Label TestsStatusTest = new Label();
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
        initializeTest((ArchitecturalInvariantInterpretation) RI);
        System.out.println("In Initialize");

        TestsStartTest.setOnAction(event -> {
            try {
                StartTest();
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public void initializeTest(ArchitecturalInvariantInterpretation AII){
        System.out.println("In Initialize test");
        InputStimeStamp.setText(AII.getTimeStamp());
        InputText.setText(AII.getInputFile());

        TestTab.setText("Architecture Invariant Test");
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
                                databaseOperations.insertData("Full", testThreadEnvi.testName, Arrays.toString(testThreadEnvi.getRunCommand()),Arrays.toString(testThreadEnvi.getSupportCommand()));
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

    public void setupTestTab(ReportInterpretation _RI, String _testName,boolean recuperated, String[] _runnableCommand, String[] _runnableSupportCommand, DatabaseOperations _databaseOperations){
        databaseOperations = _databaseOperations;
        RI = _RI;
        recuperatedTestThreadEnv = recuperated;
        RunableCommand = _runnableCommand;
        RunableSupportCommand = _runnableSupportCommand;
        testName = _testName;

        if(recuperated){
            testing_prokect_edittext_path_launch_run.setText("ALL SET");
            testing_prokect_edittext_path_launch_run.setEditable(false);
            testing_prokect_edittext_path.setText("ALL SET");
            testing_prokect_edittext_path.setEditable(false);
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
}
