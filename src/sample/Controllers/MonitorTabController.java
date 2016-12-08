package sample.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeTableColumn;
import sample.Interfaces.ReportInterpretation;
import sample.Logic.ROSNode;
import sample.Logic.TestThread;
import sample.Logic.ThreadHandler;
import sample.Properties.TestingProgress;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by ccc on 12/7/16.
 */
public class MonitorTabController implements Initializable {

    @FXML Button startsystem;
    private TestThread runThread;
    private ReportInterpretation RI;
    private TestingProgress testingProgress;
    private String projectTestPath;
    private File projectTestLaunchRun;
    private String testName;
    private String ri;
    private String type;
    private String[] runnableCommand;
    private String[] runnableSupportCommand;
    private boolean systemRunning;
    private ArrayList<ROSNode> generalNodesPrev;
    private ArrayList<ROSNode> generalNodesPost;
    private TreeTableColumn nodesMainTreeTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        generalNodesPrev = new ArrayList<>();
        generalNodesPost = new ArrayList<>();
        if(systemRunning){
            startsystem.setVisible(false);
            try {

                populateGeneralNodes();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        startsystem.setOnAction(event -> {
            try {
                runThread = new TestThread(RI, testingProgress, projectTestPath, projectTestLaunchRun, ri, type,testName);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            runThread.setRunCommand(runnableCommand);
            runThread.setSupportCommand(runnableSupportCommand);
            runThread.setAllSetSucces(true);
            runThread.setMonitor(true);
            runThread.start();
            try {
                runThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


    }

    public void setupMonitorTab(ReportInterpretation reportInterpretation, TestingProgress _testingProgress, String _projectTestPath,
                                File _projectTestLaunchTun, String _ri, String _type, String name, String[] _runnableCommand, String[] _runnableSupportCommand,
                                boolean _systemRunning, TreeTableColumn _nodesMainTreeTable){
        RI = reportInterpretation;
        testingProgress = _testingProgress;
        projectTestLaunchRun = _projectTestLaunchTun;
        projectTestPath = _projectTestPath;
        testName = name;
        ri = _ri;
        type = _type;
        runnableCommand = _runnableCommand;
        runnableSupportCommand = _runnableSupportCommand;
        systemRunning = _systemRunning;
        nodesMainTreeTable = _nodesMainTreeTable;

    }

    private void populateGeneralNodes() throws InterruptedException {
        String[] command = {"/bin/bash", "-c", "source /opt/ros/kinetic/setup.bash && rosnode list "};
        ArrayList<ROSNode> generalNodes = new ArrayList<>();
        ThreadHandler threadHandler = new ThreadHandler(command, false, true);
        threadHandler.run();
        threadHandler.join();

        for(String node : threadHandler.returnedContinouesArray){
            System.out.print(node);
            String[] commandTwo = {"/bin/bash", "-c", "source /opt/ros/kinetic/setup.bash && rosnode info " + node};
            ThreadHandler threadHandler1 = new ThreadHandler(commandTwo, false,true);
            ArrayList<String> tempPub = new ArrayList<>();
            ArrayList<String> tempSub = new ArrayList<>();
            ArrayList<String> tempSev = new ArrayList<>();
            threadHandler1.run();
            threadHandler1.join();
            int state = -1; // 0 = pub ; 1 = sub ; 2 = serv
            for(String outputLine : threadHandler1.returnedContinouesArray){
                if(outputLine.contains("Publications:")) {
                    state = 0;
                    continue;
                }
                if(outputLine.contains("Subscriptions:")) {
                    state = 1;
                    continue;
                }
                if(outputLine.contains("Services:")){
                    state =2;
                    continue;
                }
                if(outputLine.contains("*")){
                    if(state == 0){

                        String[] temp = outputLine.split(" ");
                        tempPub.add(temp[2] + " *** " + temp[3]);
                    }
                    if(state == 1){
                        String[] temp = outputLine.split(" ");
                        tempPub.add(temp[2] + " *** " + temp[3]);
                    }
                    if(state == 2){
                        tempSev.add(outputLine.split(" ")[1]);
                    }
                }
                if(outputLine.contains("..."))
                    break;

            }
            ROSNode rosNode = new ROSNode();
            rosNode.populate(node,tempSev,tempSub,tempPub);
            generalNodes.add(rosNode);
        }
        generalNodesPrev = generalNodesPost;
        generalNodesPost = generalNodes;

    }
}
