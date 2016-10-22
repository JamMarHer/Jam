package sample.Logic;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Controllers.ReportController;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by ccc on 10/18/16.
 */
public class TestReport implements Serializable {

    public String name;
    private String timeStamp;
    private HashMap<String, String> generalOutcome;
    private HashMap<String, HashMap<Integer, Integer>> generalSuccessFailure;



    public TestReport(String _name){
        name = _name;
        generalOutcome = new HashMap<>();
        generalSuccessFailure = new HashMap<>();
        generalSuccessFailure.put("/rec/arch_sub",new HashMap<>());
        generalSuccessFailure.get("/rec/arch_sub").put(0,0);
        generalSuccessFailure.get("/rec/arch_sub").put(1,0);

        generalSuccessFailure.put("/rec/arch_pub", new HashMap<>());
        generalSuccessFailure.get("/rec/arch_pub").put(0,0);
        generalSuccessFailure.get("/rec/arch_pub").put(1,0);

        generalSuccessFailure.put("/rec/arch_srvs", new HashMap<>());
        generalSuccessFailure.get("/rec/arch_srvs").put(0,0);
        generalSuccessFailure.get("/rec/arch_srvs").put(1,0);

    }

    public void displayReport() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/FXML_S/report.fxml"));
        ReportController reportController = new ReportController();
        reportController.setReport(name,timeStamp,generalOutcome, generalSuccessFailure);
        loader.setController(reportController);
        Parent root = loader.load();

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Report");
        Scene scene = new Scene(root);
        window.setScene(scene);
        window.setResizable(false);
        window.show();


        System.out.println("##############" + generalOutcome);



    }

    public void setTimeStamp(String _timeStamp){
        timeStamp = _timeStamp;
    }

    public void insertOutCome(String task, String outome){
        generalOutcome.put(task, outome);
    }

    public void setSucces(String node, int in){
        generalSuccessFailure.get(node).put(1, in);
    }
    public void setFailure(String node, int in){
        generalSuccessFailure.get(node).put(0,in);
    }
    public int getSucces(String node){
        return generalSuccessFailure.get(node).get(1);
    }
    public int getFailure(String node){
        return generalSuccessFailure.get(node).get(0);
    }

}
