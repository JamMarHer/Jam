package sample.Controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import sample.Logic.ArchitecturalInvariantInterpretation;
import sample.Logic.Reaction;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by ccc on 10/20/16.
 */


public class ReportController implements Initializable {

    @FXML private ListView<String> reportTestListView;
    @FXML private Label reportTestStatus;
    @FXML private Label reportTestName;
    @FXML private Label reportTestTime;
    @FXML private Label reportTestTopic;
    @FXML private Label reportTestTaskID;
    @FXML private Label reportTestConfirmationBy;
    @FXML private Label reportTestInvariantStatus;

    final CategoryAxis xAxys = new CategoryAxis();
    final NumberAxis yAxys = new NumberAxis();
    @FXML private BarChart<String, Number> BarChartReport = new BarChart<String, Number>(xAxys,yAxys);

    private String name;
    private String date;
    private HashMap<String, String> generalOutcome;
    private HashMap<String, HashMap<Integer, Integer>> generalSuccessFailure;
    private HashMap<String,Reaction> reactions;
    private int fails;
    private int success;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadReport();
    }

    public void loadReport(){
        System.out.println("Name: " + name + "Date: " + date);
        reportTestName.setText(name);
        reportTestTime.setText(date);
        ObservableList<String> items = FXCollections.observableArrayList(new ArrayList<>(generalOutcome.keySet()));
        reportTestListView.setItems(items);
        reportTestListView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        reportTestTopic.setText(reactions.get(newValue).getTopic());
                        reportTestTaskID.setText(reactions.get(newValue).getTaskID());
                        reportTestStatus.setText(generalOutcome.get(newValue));
                        reportTestConfirmationBy.setText(reactions.get(newValue).getCauseOfFail());
                        reportTestInvariantStatus.setText(String.valueOf(reactions.get(newValue).getCrash()));

                    }
                }
        );
        initializeTest();
    }
    public void initializeTest(){

        xAxys.setLabel("Recording Node");
        yAxys.setLabel("Quantity");
        XYChart.Series series = new XYChart.Series();
        series.setName("Success");
        series.getData().add(new XYChart.Data<String,Number>("Subscribers", generalSuccessFailure.get("/rec/arch_sub").get(1)));
        series.getData().add(new XYChart.Data<String,Number>("Publishers", generalSuccessFailure.get("/rec/arch_pub").get(1)));
        series.getData().add(new XYChart.Data<String,Number>("Services", generalSuccessFailure.get("/rec/arch_srvs").get(1)));
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Failure");
        series2.getData().add(new XYChart.Data<String,Number>("Subscribers", generalSuccessFailure.get("/rec/arch_sub").get(0)));
        series2.getData().add(new XYChart.Data<String,Number>("Publishers", generalSuccessFailure.get("/rec/arch_pub").get(0)));
        series2.getData().add(new XYChart.Data<String,Number>("Services", generalSuccessFailure.get("/rec/arch_srvs").get(0)));

        BarChartReport.getData().setAll(series, series2);
        BarChartReport.setVisible(true);
        BarChartReport.impl_updatePeer();

    }




    public void setReport(String _name, String _date, HashMap<String, String> _generalOutome, HashMap<String, HashMap<Integer, Integer>> _generalSuccessFailure, HashMap<String, Reaction> _reactions){

        name = _name;
        date = _date;
        generalOutcome = _generalOutome;
        generalSuccessFailure = _generalSuccessFailure;
        reactions = _reactions;
    }
}
