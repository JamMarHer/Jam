package sample.Logic; /**
 * Created by jam on 8/24/16.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadHandler extends Thread {

    private String[] command;
    public boolean continuous;
    public String returnedData = "";

    public ThreadHandler(String[] Command, boolean Continuous){
        command = Command;
        continuous = Continuous;
    }

    public void run(){
        try {
            Process proc = Runtime.getRuntime().exec(command);

            // Read the output

            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (continuous){
                    System.out.print(line +" \n");
                }else {
                    returnedData += line + "\n";
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
