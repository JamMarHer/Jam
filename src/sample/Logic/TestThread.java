package sample.Logic;

import sample.Interfaces.ReportInterpretation;
import sample.Properties.TestingProgress;
import sun.awt.image.DataBufferNative;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jam on 9/21/16.
 */
public class TestThread extends Thread implements Serializable {

    private String task;
    private boolean ROSPassed;
    private boolean PathPassed;
    private boolean FinalEnvironmentPassed;
    private TestingProgress testingProgress;
    private String ros_implementation;
    private String ROSTCPROSPPATH = "/opt/ros/indigo/lib/python2.7/dist-packages/rospy/impl/";
    private String ROSNODEHANDLECPATH = "/opt/ros/indigo/include/ros/";
    public String PROJECTPATH;
    private String PROJECTTESTPATH;
    private File PROJECTTESTLAUNCHROS;
    private boolean allSetSucces;
    private String[] successfullRun;
    private String[] successfullSupportRun;
    public String testName;
    private String tempTestName;
    private ReportInterpretation report;
    private String testType;

    public TestThread(ReportInterpretation RI, TestingProgress TP, String projectTestPath, File projectTestLaunchRos, String _testType, String _task, String _temptestName) throws SQLException, ClassNotFoundException {
        task = _task;
        ROSPassed = false;
        testingProgress = TP;
        DatabaseOperations databaseOperations = new DatabaseOperations();
        ros_implementation = databaseOperations.retrieveData("extDir",null,"settings");
        PROJECTPATH = System.getProperty("user.dir");
        PROJECTTESTPATH = projectTestPath;
        PROJECTTESTLAUNCHROS = projectTestLaunchRos;
        allSetSucces =false;
        tempTestName = _temptestName;
        report = RI;
        testType = _testType;

    }


    public boolean getFinalEnvironmentPassed(){
        return FinalEnvironmentPassed;
    }
    public boolean getPathPassed(){
        return PathPassed;
    }
    public boolean getROSPassed(){
        return ROSPassed;
    }

    @Override
    public void run() {
        if(task.equals("ROS")){
            testingProgress.setStateString("Testing ROS Environment");
            String[] command = {"/bin/bash", "-c", "python " + PROJECTPATH + "/src/sample/PythonScripts/checkROSStatus.py " + PROJECTPATH + " " + ROSTCPROSPPATH + " " + ROSTCPROSPPATH + " " + "VERSE NON_SUDO"};
            String[] directCommand = {"/bin/bash", "-c", "gksudo cp " + PROJECTPATH + "/src/sample/ROSFiles/tcpros_service.py "+ROSTCPROSPPATH + " && cp "+PROJECTPATH +"/src/sample/ROSFilesMod/node_handle.h "+ ROSNODEHANDLECPATH};
            String[] command3 = {"/bin/bash", "-c", "python " + PROJECTPATH + "/src/sample/PythonScripts/checkROSStatus.py " + PROJECTPATH + " " + ROSTCPROSPPATH + " " + ROSTCPROSPPATH + " " + "VERSE NON_SUDO"};

            ThreadHandler threadHandler = new ThreadHandler(command,false, false); // Asks for ROS_MOD state
            ThreadHandler threadHandler1 = new ThreadHandler(directCommand, true, false);
            ThreadHandler threadHandler2 = new ThreadHandler(command3,false,false);
            threadHandler.start();
            try {
                threadHandler.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (threadHandler.returnedData.equals("NON_MOD_ROS")) {
                ROSPassed = true;
                System.out.print("Success by NON_MOD_ROS");
            } else {
                threadHandler1.start();
                try {
                    threadHandler1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                threadHandler2.start();
                try {
                    threadHandler2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (threadHandler2.returnedData.equals("NON_MOD_ROS")) {
                    ROSPassed = true;
                    System.out.print("Success by ROS_RESET");
                } else{
                    try {
                        throw new ROSEnvironmnetException("ERROR in copying file. Verify password");
                    } catch (ROSEnvironmnetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if(task.equals("PRO")){
            String[] sourceDevel = {"/bin/bash", "-c", "source "+PROJECTTESTPATH+"/devel/setup.bash"};
            String[] sourceDevelIso = {"/bin/bash", "-c", "source "+PROJECTTESTPATH+"/devel_isolated/setup.bash"};
            ThreadHandler threadHandler = new ThreadHandler(sourceDevel, false, false);
            threadHandler.start();
            try {
                threadHandler.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(threadHandler.returnedData == null){
                System.out.print("Passed with devel");
                PathPassed = true;
                return;
            }else if(threadHandler.returnedData.equals("bash: devel/setup.bash: No such file or directory")) {
                System.out.print("Not devel... Trying devel_isolated");
            }
            ThreadHandler threadHandler1 = new ThreadHandler(sourceDevelIso, false, false);
            threadHandler1.start();
            try {
                threadHandler1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(threadHandler1.returnedData == null){
                System.out.print("Passed with devel_isolated");
            }else if(threadHandler1.returnedData.equals("bash: devel_isolated/setup.bash: No such file or directory")){
                System.out.print("Wrong Path");
                return;
            }

        }
        if (task.equals("ENV") && !allSetSucces){
            char type = checkScriptLaunch(PROJECTTESTLAUNCHROS.getAbsolutePath());
            String[] tempRun = PROJECTTESTLAUNCHROS.getAbsolutePath().split("/");
            String[] requestedCommand = new String[]{};
            String preliminarCommand = "";
            if(type == 'x'){
                System.out.print("Not Supported");
                return;
            }else if(type == 'p'){
                RequestBox projectName = new RequestBox("Project information required","Please provide ros package (ex. beginner_tutorials)", false);
                preliminarCommand = "rosrun "+ projectName.requestUser() +" "+tempRun[tempRun.length-1];
            }else if(type == 'h'){
                RequestBox projectName = new RequestBox("Project information required","Please provide ros package (ex. beginner_tutorials)", false);
                preliminarCommand = "roslaunch "+ projectName.requestUser() +" "+tempRun[tempRun.length-1];
            }
            String[] setup = new String[]{"/bin/bash", "-c", "source " + PROJECTTESTPATH + "/devel/setup.bash && " + preliminarCommand};
            RequestBox requestBox = new RequestBox("Required commands", "Enter any command the system depends on.", true);
            String tempRequestedCommad = requestBox.requestUser();
            if(!tempRequestedCommad.equals("NON")){
                requestedCommand = new String[]{"/bin/bash", "-c", tempRequestedCommad};
                ThreadHandler requestedCommandExe = new ThreadHandler(requestedCommand,false,true);
                requestedCommandExe.start();
                try {
                    requestedCommandExe.join(10000); // time allowed for requested command to finalize meant to be changed by user
                    //requestedCommandExe.communicate("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //source /home/jam/Simulation/ros_catkin_ws/devel/setup.bash && cd /home/jam/Simulation/ardupilot/ArduCopter/ && ../Tools/autotest/sim_vehicle.sh -j 4 -f Gazebo --map --console from
            ThreadHandler threadHandler = new ThreadHandler(setup, false, true);
            threadHandler.start();
            try {
                threadHandler.join(10000);
                String[] rosverification = {"/bin/bash", "-c","rosnode list"};
                ThreadHandler threadHandler1 = new ThreadHandler(rosverification,false,true);
                threadHandler1.start();
                threadHandler1.join();
                if(threadHandler1.returnedContinouesArray.size() >1){
                    System.out.print("Test ready. Verified by nodes > 1**************");
                    testName= tempTestName +new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").format(new Date()).replace("/","_");
                    FinalEnvironmentPassed = true;
                    successfullRun = setup;
                    successfullSupportRun = requestedCommand;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(allSetSucces){
            ThreadHandler Command = new ThreadHandler(successfullRun, false, true);
            for (int i = 0; i<successfullRun.length; i++){
                System.out.println( "========="+successfullRun[i]);
            }
            successfullRun[1] = "-c";

            if(successfullSupportRun.length >= 3){
                successfullSupportRun[1] = "-c";
                ThreadHandler supportCommand = new ThreadHandler(successfullSupportRun, false, true);
                supportCommand.start();
                try {
                    supportCommand.join(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Command.start();
            try {
                Command.join(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print("Running program");
            startTest();

        }

    }

    public boolean startTest(){
        HashMap<String, Integer> record = new HashMap<>();
        HashMap<String, String> pubsubser = new HashMap<>();
        pubsubser.put("/rec/arch_pub", "PUBLISHER");
        pubsubser.put("/rec/arch_srvs", "SERVICE");
        pubsubser.put("/rec/arch_sub", "SUBSCRIBER");
        try {
            DatabaseOperations databaseOperations = new DatabaseOperations();
            String daikonExtension = databaseOperations.retrieveData("extDir", null, "settings");

            String[] initialize = {"/bin/bash", "-c", "source "+daikonExtension+"/catkin_ws/devel/setup.bash && rosrun recorder ArchitecturalTestServer.py"};
            ThreadHandler initializeATS = new ThreadHandler(initialize, false,true);
            initializeATS.start();
            initializeATS.join(1000);
            System.out.print("Starting Test...");
            if(testType.equals("AII")){
                System.out.println(" [AII]");
                ArchitecturalInvariantInterpretation AIIReport = (ArchitecturalInvariantInterpretation)report;
                HashMap<String, HashMap<String,HashMap<String, HashMap<String, ArrayList<String>>>>> generalMap = AIIReport.getGeneralMapFilterData();
                for( String recordingNode : generalMap.keySet()){
                    //System.out.println("In: " + recordingNode);
                    for( String state : generalMap.get(recordingNode).keySet()){
                        //System.out.println("In: " + state);
                        for( String topic_serivce : generalMap.get(recordingNode).get(state).keySet()){
                            //System.out.println("In: " + topic_serivce);
                            for( String min_max_minmax : generalMap.get(recordingNode).get(state).get(topic_serivce).keySet()){
                                //System.out.println("In: " + min_max_minmax);
                                if( generalMap.get(recordingNode).get(state).get(topic_serivce).get(min_max_minmax).get(0).equals("null"))
                                    continue;
                                System.out.println("DeAttaching "+ recordingNode+ "::::"+ state +":::"+ topic_serivce+"::"+
                                        min_max_minmax+":"+Arrays.toString(generalMap.get(recordingNode).get(state).get(topic_serivce).get(min_max_minmax).toArray()));
                                for(String node : generalMap.get(recordingNode).get(state).get(topic_serivce).get(min_max_minmax)){
                                    //System.out.println("Trying: " + pubsubser.get(recordingNode) + ", With: "+node+  ", And: "+topic_serivce);
                                    String[] command = {"/bin/bash", "-c", "source "+daikonExtension+"/catkin_ws/devel/setup.bash && rosservice call /deAttacher \"task: '"+pubsubser.get(recordingNode)+"'\n" +
                                            "nodeRequest: '"+node+"'\n" +
                                            "topic_serviceRequest: '"+topic_serivce+"'\""};
                                    ThreadHandler runK  = new ThreadHandler(command, false, true);
                                    runK.start();
                                    runK.join(1500);
                                    if(runK.returnedContinouesArray.get(0).equals("response: SUCCESS")){
                                        //System.out.print("SUCCESS");
                                        if(record.containsKey(pubsubser.get(recordingNode))){
                                            record.put(pubsubser.get(recordingNode), record.get(pubsubser.get(recordingNode))+1);
                                        }else {
                                            record.put(pubsubser.get(recordingNode), 1);
                                        }

                                        runK.setContinuous(false);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }catch (InputProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(record);
        return true;
    }

    //rosservice call /deAttacher "nodeRequest: '/gazebo' \n topicRequest: '/erlecopter/imu'"
    public void setAllSetSucces(boolean state){
        allSetSucces = state;
    }

    public void setRunCommand(String[] command){
        successfullRun = command;
    }
    public void setSupportCommand(String[] command){
        successfullSupportRun = command;
    }
    public String[] getRunCommand(){
        return successfullRun;
    }
    public String[] getSupportCommand(){
        return  successfullSupportRun;
    }


    private char checkScriptLaunch(String projectlaunchROS){
        String[] temp  = projectlaunchROS.split("\\.");
        System.out.print(Arrays.toString(temp));
        if(temp[temp.length-1].equals("py")){
            return 'p';
        }else if(temp[temp.length-1].equals("launch")){
            return 'h';
        }else {
            return 'x';
        }
    }
}