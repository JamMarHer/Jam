package sample.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import sample.Interfaces.ReportInterpretation;
import sample.Logic.ArchitecturalInvariantInterpretation;
import sample.Logic.DatabaseOperations;
import sample.Logic.MenuSettings;
import sample.Logic.TestSuite;
import sample.Main;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private boolean environmentSetup = false;

    @FXML private javafx.scene.control.MenuItem settings;
    @FXML private MenuItem close;
    @FXML private Line mainEnvironmentNotSetupLine = new Line();
    @FXML private Text mainEnvironmentNotSetupLabel = new Text();
    @FXML private MenuItem architecturalInvariantTest = new MenuItem();
    @FXML private BorderPane MainPane = new BorderPane();
    @FXML private TabPane MainTesting = new TabPane();
    @FXML private StackPane MainStackPane = new StackPane();
    @FXML private Label InputStimeStamp = new Label();
    @FXML private Label InputText = new Label();
    final CategoryAxis xAxys = new CategoryAxis();
    final NumberAxis yAxys = new NumberAxis();
    @FXML private BarChart<String, Number> BarChartII = new BarChart<String, Number>(xAxys,yAxys);
    private ReportInterpretation RI;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DatabaseOperations databaseOperations = new DatabaseOperations();

        if(!(databaseOperations.retrieveData("extDir").equals("/...") || databaseOperations.retrieveData("extDaikon").equals("/..."))){
            environmentSetup = true;
            mainEnvironmentNotSetupLine.setVisible(false);
            mainEnvironmentNotSetupLabel.setVisible(false);
        }

        assert settings != null : "fx:id=\"settings\" was not injected: check your FXML file 'sample.fxml'.";
        MenuSettings menuSettings = new MenuSettings();
        settings.setOnAction(e ->{
            try {
                menuSettings.display();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("It works");
        });
        close.setOnAction(event -> System.exit(0));
        architecturalInvariantTest.setOnAction(event -> {
            TestSuite testSuite = new TestSuite("Architecture");
            RI = testSuite.display();
            if(RI.getID().equals("AII")){
                initializeTest((ArchitecturalInvariantInterpretation)RI);
            }
        });
    }

    public void initializeTest(ArchitecturalInvariantInterpretation AII){
        InputStimeStamp.setText(AII.getTimeStamp());
        InputText.setText(AII.getInputFile());
        MainTesting.setVisible(true);
        MainTesting.getTabs().get(0).setText("Architecture Invariant Test");
        MainStackPane.setVisible(false);
        xAxys.setLabel("Recording Node");
        yAxys.setLabel("Quantity");
        XYChart.Series series = new XYChart.Series();
        series.setName("Static");
        series.getData().add(new XYChart.Data<String,Number>("Publisher", AII.getSize("/rec/arch_pub", "Static")));
        series.getData().add(new XYChart.Data<String,Number>("Subscriber", AII.getSize("/rec/arch_sub", "Static")));
        series.getData().add(new XYChart.Data<String,Number>("Services", AII.getSize("/rec/arch_srvs", "Static")));
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Variable");
        series2.getData().add(new XYChart.Data<String,Number>("Publisher", AII.getSize("/rec/arch_pub", "Variable")));
        series2.getData().add(new XYChart.Data<String,Number>("Subscriber", AII.getSize("/rec/arch_sub", "Variable")));
        series2.getData().add(new XYChart.Data<String,Number>("Services", AII.getSize("/rec/arch_srvs", "Variable")));
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Restricted Variable");
        series3.getData().add(new XYChart.Data<String,Number>("Publisher", AII.getSize("/rec/arch_pub", "Restricted")));
        series3.getData().add(new XYChart.Data<String,Number>("Subscriber", AII.getSize("/rec/arch_sub", "Restricted")));
        series3.getData().add(new XYChart.Data<String,Number>("Services", AII.getSize("/rec/arch_srvs", "Restricted")));

        BarChartII.getData().addAll(series,series2,series3);
        BarChartII.setVisible(true);
        BarChartII.impl_updatePeer();

    }
}
