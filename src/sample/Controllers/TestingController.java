package sample.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Logic.ArchitecturalInvariantInterpretation;
import sample.Logic.DatabaseOperations;
import sample.Logic.InputProcessingException;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jam on 9/17/16.
 */
public class TestingController implements Initializable {

    @FXML Text test_title = new Text();
    @FXML TextField testing_edittext_path = new TextField();
    private String test = "";
    private File post_DaikonFilteredFile;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setTest(String _test){
        test = _test;
        test_title.setText(_test + " Test");
    }

    private void chooseFile(Stage stage){
        try {

            FileChooser directoryChooser = new FileChooser();
            directoryChooser.setTitle("post-daikon filtered file path");
            File selectedFile = directoryChooser.showOpenDialog(stage);
            post_DaikonFilteredFile = selectedFile.getAbsoluteFile();
            testing_edittext_path.setText(post_DaikonFilteredFile.getAbsolutePath());
        }catch (Exception e){
            System.out.print("Path selection canceled");
            e.printStackTrace();
        }

    }

    @FXML
    public void testingLocatePath(ActionEvent event){
        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
        chooseFile(stage);
    }

    @FXML
    public void testingNext(ActionEvent event){
        ArchitecturalInvariantInterpretation architecturalInvariantInterpretation;
        try{
            architecturalInvariantInterpretation = new ArchitecturalInvariantInterpretation(post_DaikonFilteredFile);
            architecturalInvariantInterpretation.processData();
            System.out.print(architecturalInvariantInterpretation.getSpecificMapFilterData("/rec/arch_pub").get("Variable").get("/rosout"));
        }catch (InputProcessingException e){
            e.printStackTrace();
        }

    }
}
