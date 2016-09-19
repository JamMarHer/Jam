package sample.Logic;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Controllers.TestingController;

/**
 * Created by jam on 9/17/16.
 */
public class TestSuite {

    private String test = "";

    public TestSuite(String _test){
        test = _test;

    }

    public void display(){
        try{
            if(test.equals("Architecture")){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/FXML_S/testing_interface.fxml"));
                Parent root = loader.load();
                Stage window = new Stage();

                window.initModality(Modality.APPLICATION_MODAL);
                window.setTitle("ArchitectureTest");
                Scene scene = new Scene(root);
                TestingController controller =  loader.getController();
                controller.setTest("Architecture");
                window.setScene(scene);
                window.setResizable(false);
                window.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
