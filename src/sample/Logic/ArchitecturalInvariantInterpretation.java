package sample.Logic;

import sample.Interfaces.ReportInterpretation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jam on 9/17/16.
 */

// We assume that the only templates being implemented daikon are pub, sub, srv.
    // ::::::: = DaikonFilterMark | 7
    // :::::::::::::::::: = nodeMark | 18
    // :::::: = topic - service | 6
    // ::::: = min | 5
    // :::: = max | 4
    // ::: = minMax | 3

public class ArchitecturalInvariantInterpretation implements ReportInterpretation {

    private ArrayList<String> generalArrayInput;
    // 1) HashMap::: String:: recording node;
    //      2) HashMap:: String:: state;
    //          3) HashMap:: String:: topic/service;
    //              4) HashMap:: String:: min or max or minMax;
    //                  5) ArrayList:: data.
    private static final String ID = "AII";
    private HashMap<String, HashMap<String,HashMap<String, HashMap<String, ArrayList<String>>>>> generalMapInput;
    private String timeStamp;
    private String inputFile;
    private boolean processed;
    private int size;

    public ArchitecturalInvariantInterpretation(File file) throws InputProcessingException{
        generalArrayInput = inputProcess(file);
        generalMapInput = new HashMap<>();
        if(!(generalArrayInput.get(0).equals(" ::::::: Post-Dikon Filter ::: "))){
            throw new InputProcessingException("Input is not marked as a Post-Daikon filtered file");
        }
        if(!(generalArrayInput.get(2).split(" ")[0]).equals("Timestamp:")){
            throw new InputProcessingException("Input does not contain timestamp.");
        }else {
            timeStamp = generalArrayInput.get(2).split(" ")[1] + " " + generalArrayInput.get(2).split(" ")[2];
        }
        if(!((generalArrayInput.get(3).split(" ")[0]).equals("Input")) || !(generalArrayInput.get(3).split(" ")[1]).equals("file:")){
            throw  new InputProcessingException("Input does not contain file name.");
        }else{
            inputFile = generalArrayInput.get(3).split(" ")[2];
        }
        processed = false;
    }

    public String getID(){
        return ID;
    }

    public void processData(){
        String currentNode = "";
        String currentState = "";
        String currentTopicService = "";
        ArrayList<String> tempMin;
        ArrayList<String> tempMax;
        ArrayList<String> tempMinMax;
        for(String line : generalArrayInput){
            if(line.split(" ")[0].equals("::::::::::::::::::")){
                currentNode = line.split(" ")[1];
                generalMapInput.put(currentNode,new HashMap<>());
            }else if(line.split(" ")[0].equals("-------------------------[")){
                currentState = line.split(" ")[1];
                generalMapInput.get(currentNode).put(currentState, new HashMap<>());
            } else if(line.split(" ")[0].equals("::::::")){
                currentTopicService = line.split(" ")[1];
                generalMapInput.get(currentNode).get(currentState).put(currentTopicService, new HashMap<>());
            } else if(line.split(" ")[0].equals(":::::")){
                tempMin = processLineData(line);
                generalMapInput.get(currentNode).get(currentState).get(currentTopicService).put("Min", tempMin);
            } else if(line.split(" ")[0].equals("::::")){
                tempMax = processLineData(line);
                generalMapInput.get(currentNode).get(currentState).get(currentTopicService).put("Max", tempMax);
                size++;
            } else if(line.split(" ")[0].equals(":::")){
                tempMinMax = processLineData(line);
                generalMapInput.get(currentNode).get(currentState).get(currentTopicService).put("MinMax", tempMinMax);
                size++;
            }
        }
        processed = true;
    }

    private ArrayList<String> processLineData(String line){
        ArrayList<String> toReturn = new ArrayList<>();
        if(!(line.contains("'"))){
            toReturn.add("null");
            return toReturn;
        }
        String[] toAnalyze = line.split("'");
        for(int i = 1; i < toAnalyze.length; i++){
            toReturn.add(toAnalyze[i]);
            i++;
        }
        return toReturn;
    }

    public int getSize(){
        return size;
    }

    public String getTimeStamp(){
        return timeStamp;
    }

    public String getInputFile(){
        return inputFile;
    }

    public HashMap<String, HashMap<String,HashMap<String, HashMap<String, ArrayList<String>>>>> getGeneralMapFilterData()throws InputProcessingException{
        if(processed){
            return generalMapInput;
        }else {
            throw  new InputProcessingException("Data has not been processed");
        }
    }

    public HashMap<String,HashMap<String, HashMap<String, ArrayList<String>>>> getSpecificMapFilterData(String node) throws InputProcessingException{
        if(processed){
            return generalMapInput.get(node);
        }else {
            throw new InputProcessingException("Data has not been processed");
        }
    }

    public HashMap<String, HashMap<String, ArrayList<String>>> getSpecificMapFilterData(String node, String state) throws InputProcessingException{
        if(processed){
            return generalMapInput.get(node).get(state);
        }else {
            throw new InputProcessingException("Data has not been processed");
        }
    }

    public HashMap<String, ArrayList<String>> getSpecificMapFilterData(String node, String state, String topic) throws InputProcessingException{
        if(processed){
            return generalMapInput.get(node).get(state).get(topic);
        }else {
            throw new InputProcessingException("Data has not been processed");
        }
    }

    private ArrayList<String> inputProcess(File e){
        ArrayList<String> toReturn = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(e));
            String temp;
            while((temp = reader.readLine()) != null){
                toReturn.add(temp);
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return toReturn;
    }
}
