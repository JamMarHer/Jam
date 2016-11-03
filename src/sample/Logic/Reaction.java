package sample.Logic;

import sample.Controllers.ReportController;

import java.io.Serializable;

/**
 * Created by ccc on 10/27/16.
 */
public class Reaction implements Serializable {
    private String topic;
    private boolean failSuccess;
    private String causeOfFail; // In this case failure means that Mushroom succeed at un-registering
    private boolean crash;
    private String taskID;


    public Reaction(String _topic, String _taskID, boolean _failureSuccess){
        topic = _topic;
        failSuccess =_failureSuccess;
        taskID = _taskID;
    }
    public void setCauseOfFail(String cause){
        causeOfFail = cause;
    }
    public void setCrash(boolean _crash){
        crash = _crash;
    }
    public String getTopic(){
        return topic;
    }
    public String getCauseOfFail(){
        return causeOfFail;
    }
    public String getTaskID(){
        return taskID;
    }
    public boolean getCrash(){
        return crash;
    }
}
