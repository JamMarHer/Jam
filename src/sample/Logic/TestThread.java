package sample.Logic;

import sample.Interfaces.ReportInterpretation;
import sample.Properties.TestingProgress;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

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

    public TestThread(ReportInterpretation RI, TestingProgress TP, String projectTestPath, File projectTestLaunchRos, String testType, String _task) {
        task = _task;
        ROSPassed = false;
        testingProgress = TP;
        DatabaseOperations databaseOperations = new DatabaseOperations();
        ros_implementation = databaseOperations.retrieveData("extDir",null,"settings");
        PROJECTPATH = System.getProperty("user.dir");
        PROJECTTESTPATH = projectTestPath;
        PROJECTTESTLAUNCHROS = projectTestLaunchRos;
        allSetSucces =false;

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
                    FinalEnvironmentPassed = true;
                    successfullRun = setup;
                    successfullSupportRun = requestedCommand;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(allSetSucces){
            ThreadHandler Command = new ThreadHandler(successfullRun, false, true);
            if(successfullSupportRun != null){
                ThreadHandler supportCommand = new ThreadHandler(successfullSupportRun, false, true);
                supportCommand.start();
                try {
                    supportCommand.join(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Command.start();
            System.out.print("Running program");
            try {
                Command.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

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
