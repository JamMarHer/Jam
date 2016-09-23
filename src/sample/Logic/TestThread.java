package sample.Logic;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.application.Platform;
import sample.Interfaces.ReportInterpretation;
import sample.Properties.TestingProgress;

import javax.script.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * Created by jam on 9/21/16.
 */
public class TestThread extends Thread {

    private String task;
    private boolean ROSPassed;
    private boolean PathPassed;
    private boolean FinalEnvironmentPassed;
    private TestingProgress testingProgress;
    private String ros_implementation;
    private String ROSTCPROSPPATH = "/opt/ros/indigo/lib/python2.7/dist-packages/rospy/impl/";
    private String ROSNODEHANDLECPATH = "/opt/ros/indigo/include/ros/";
    private String PROJECTPATH;
    private final String NONSUDO = "ERROR_NON_SUDO";

    public TestThread(ReportInterpretation RI, TestingProgress TP, String projectTestPath, String projectTestLaunchRos, String testType, String _task) {
        task = _task;
        ROSPassed = false;
        testingProgress = TP;
        DatabaseOperations databaseOperations = new DatabaseOperations();
        ros_implementation = databaseOperations.retrieveData("extDir");
        PROJECTPATH = System.getProperty("user.dir");
        //TP.setStateString("IN!!");
        TP.setState(0.2);
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

        }
        if (task.equals("ENV")){

        }
    }
}
