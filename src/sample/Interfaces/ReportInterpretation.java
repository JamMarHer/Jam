package sample.Interfaces;

import sample.Logic.InputProcessingException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jam on 9/19/16.
 */
public interface ReportInterpretation {
    public void processData();
    public String getInputFile();
    public String getTimeStamp();
    public String getID();
    public HashMap<String, HashMap<String,HashMap<String, HashMap<String, ArrayList<String>>>>> getGeneralMapFilterData()throws InputProcessingException;
}
