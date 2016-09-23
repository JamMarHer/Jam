package sample.Logic; /**
 * Created by jam on 8/24/16.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class ThreadHandler extends Thread {

    private String[] command;
    public boolean continuous;
    public String returnedData = "";
    private boolean sudo;

    public ThreadHandler(String[] Command, boolean _sudo, boolean _continuous){
        command = Command;
        continuous = _continuous;
        sudo = _sudo;

    }

    public void run() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            String line;
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            if (sudo) {
                process.waitFor();
                return;
            } else if (!continuous) {
                returnedData = in.readLine();
                process.destroy();
            }


            process.destroy();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
