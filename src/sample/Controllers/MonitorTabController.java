package sample.Controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import sample.Interfaces.ReportInterpretation;
import sample.Logic.ROSNode;
import sample.Logic.TestThread;
import sample.Logic.ThreadHandler;
import sample.Properties.TestingProgress;
import sun.java2d.loops.GraphicsPrimitive;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.RunnableFuture;

/**
 * Created by ccc on 12/7/16.
 */
public class MonitorTabController implements Initializable {

    @FXML Button startsystem;
    @FXML Label MonitorNode;
    @FXML ListView<String> PublicationsMonitor;
    @FXML ListView<String> SubscriptionsMonitor;
    @FXML ListView<String> ServicesMonitor;

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
    private HashMap<String, ROSNode> generalNodesPrev;
    private HashMap<String, ROSNode> generalNodesPost;
    private TreeTableColumn<String, String> nodesMainTreeTable;
    private TreeTableView<String> MainTreeTable;
    private String tabID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        generalNodesPrev = new HashMap<>();
        generalNodesPost = new HashMap<>();

        Thread thread = new Thread();

        if(systemRunning){
            startsystem.setVisible(false);
            try {

                populateGeneralNodes();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {

            startsystem.setOnAction(event -> {
                try {
                    runThread = new TestThread(RI, testingProgress, projectTestPath, projectTestLaunchRun, ri, type, testName);
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
                    populateGeneralNodes();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startsystem.setVisible(false);
            });
        }
        MainTreeTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
                TreeItem<String> item = (TreeItem<String>) newValue;
                updateView(generalNodesPost.get(item.getValue()));
            }
        });
        /*
        UpdateROSInfo updateROSInfo = ne        w UpdateROSInfo(generalNodesPost);
        updateROSInfo.run();
        */
        //TODO Keep constantly updating the sytem

    }

    private void updateView(ROSNode node){
        MonitorNode.setText("Node: " + node.getName());
        ObservableList<String> itemsPub = FXCollections.observableArrayList(new ArrayList<>(node.getPublications()));
        ObservableList<String> itemsSub = FXCollections.observableArrayList(new ArrayList<>(node.getSubscriptions()));
        ObservableList<String> itemsSer = FXCollections.observableArrayList(new ArrayList<>(node.getServices()));
        PublicationsMonitor.setItems(itemsPub);
        SubscriptionsMonitor.setItems(itemsSub);
        ServicesMonitor.setItems(itemsSer);
    }

    public void setupMonitorTab(ReportInterpretation reportInterpretation, TestingProgress _testingProgress, String _projectTestPath,
                                File _projectTestLaunchTun, String _ri, String _type, String name, String[] _runnableCommand, String[] _runnableSupportCommand,
                                boolean _systemRunning, TreeTableView<String> _MainTreeTable, String _tabID ){
        RI = reportInterpretation;
        testingProgress = _testingProgress;
        projectTestLaunchRun = _projectTestLaunchTun; //TODO handle exception when skipping load of running system
        projectTestPath = _projectTestPath;
        testName = name;
        ri = _ri;
        type = _type;
        runnableCommand = _runnableCommand;
        runnableSupportCommand = _runnableSupportCommand;
        systemRunning = _systemRunning;
        MainTreeTable = _MainTreeTable;
        tabID = _tabID;

    }

    private void populateGeneralNodes() throws InterruptedException {
        String[] command = {"/bin/bash", "-c", "source /opt/ros/kinetic/setup.bash && rosnode list "};
        HashMap<String, ROSNode> generalNodes = new HashMap<>();
        ThreadHandler threadHandler = new ThreadHandler(command, false, true);
        threadHandler.run();
        threadHandler.join();

        for(String node : threadHandler.returnedContinouesArray){
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
                        tempPub.add(temp[2] + " **** Type: " + temp[3]);
                    }
                    if(state == 1){
                        String[] temp = outputLine.split(" ");
                        tempSub.add(temp[2] + " **** Type: " + temp[3]);
                    }
                    if(state == 2){
                        tempSev.add(outputLine.split(" ")[2]);
                    }
                }
                if(outputLine.contains("..."))
                    break;

            }
            ROSNode rosNode = new ROSNode();
            rosNode.populate(node,tempSev,tempSub,tempPub);
            generalNodes.put(node, rosNode);
        }
        generalNodesPrev = generalNodesPost;
        generalNodesPost = generalNodes;

        final TreeItem<String> root = new TreeItem<>("Nodes (" + generalNodesPost.size() + ")");
        root.setExpanded(true);
        for (String node : generalNodesPost.keySet()) {
            System.out.println(generalNodesPost.get(node).getName());
            final TreeItem<String> tempTreeItem = new TreeItem<>(generalNodes.get(node).getName());
            root.getChildren().add(tempTreeItem);
        }
        nodesMainTreeTable = new TreeTableColumn<>(tabID);
        nodesMainTreeTable.setCellValueFactory((TreeTableColumn.CellDataFeatures<String, String> p) -> new ReadOnlyStringWrapper(p.getValue().getValue()));
        MainTreeTable.setRoot(root);
        MainTreeTable.getColumns().add(nodesMainTreeTable);

    }

    private class UpdateROSInfo extends Thread {

        private boolean notDead = true;
        private Thread thread;
        HashMap<String, ROSNode> general;

        @Override
        public void run() {
            synchronized (generalNodesPost){
                try {
                    wait(1000);
                    populateGeneralNodes();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
        public void start(){
            if(thread == null){
                thread = new Thread(this, "UpdateROSInfo");
                thread.start();
            }
        }

        public UpdateROSInfo(HashMap<String, ROSNode> in ){
            general = in;
        }

    }
}
