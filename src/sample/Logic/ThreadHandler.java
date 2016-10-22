package sample.Logic; /**
 * Created by jam on 8/24/16.
 */

import java.io.*;
import java.util.ArrayList;

public class ThreadHandler extends Thread {

    private String[] command;
    public boolean continuous;
    public String returnedData = "";
    private boolean sudo;
    public ArrayList<String> returnedContinouesArray;
    private BufferedWriter out;


    public ThreadHandler(String[] Command, boolean _sudo, boolean _continuous){
        command = Command;
        continuous = _continuous;
        sudo = _sudo;
        returnedContinouesArray = new ArrayList<>();

    }

    public void run() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            String line;
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            if (sudo) { //TODO Check logic
                process.waitFor();
                return;
            } else if (!continuous) {
                returnedData = in.readLine();
                process.destroy();
            }else {

                while ((line = in.readLine()) != null && continuous) {
                    returnedContinouesArray.add(line);
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                }
            }


            process.destroy();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void communicate(String command) throws IOException {
        out.write(command);
    }

    public void setContinuous(boolean state){
        continuous = state;

    }

}
