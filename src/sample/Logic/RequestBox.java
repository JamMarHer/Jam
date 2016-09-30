package sample.Logic;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by jam on 9/23/16.
 */
public class RequestBox  {

    static String returned;
    static String title;
    static String message;
    static boolean extraButton;
    static DatabaseOperations databaseOperations;
    String currentSelection = "";
    String[] retrieved;
    public String[] supportCommand;
    public String testName;
    boolean accept = false;

    public RequestBox(String _title, String _message){
        title = _title;
        message = _message;
    }

    public RequestBox(String _title, String _message, boolean _extraButton){
        title = _title;
        message = _message;
        extraButton = _extraButton;
   }
    public RequestBox(String _title, String _message, boolean _extraButton, DatabaseOperations _databaseOperations){
        title = _title;
        message = _message;
        extraButton = _extraButton;
        databaseOperations = _databaseOperations;
    }

    public boolean requestPass(){
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setResizable(false);
        window.setMinWidth(350);
        Label Message = new Label(message);


        Button apply = new Button("Load");
        Button skip = new Button("Skip");

        apply.setOnAction(event -> {
            accept = true;
            window.close();
        });
        skip.setOnAction(event -> {
            accept = false;
            window.close();
        });

        VBox vBox = new VBox(10);
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(apply,skip);
        vBox.getChildren().addAll(Message, hBox);

        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox);

        window.setScene(scene);
        window.showAndWait();
        return accept;
    }

    public String requestUser(){

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setResizable(false);
        window.setMinWidth(350);
        Label Message = new Label(message);

        TextField textField = new TextField();

        Button apply = new Button("Apply");
        Button skip = new Button("Skip");

        apply.setOnAction(event -> {
            if(textField.getText() != null){
                returned = textField.getText();
                window.close();
            }else {
                textField.setText("Please provide input");
            }
        });
        skip.setOnAction(event -> {
            returned = "NON";
            window.close();
        });

        VBox vBox = new VBox(10);
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(apply,skip);
        if(extraButton){
            vBox.getChildren().addAll(Message,textField,hBox);
        }else {
            vBox.getChildren().addAll(Message, textField, apply);
        }
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox);

        window.setScene(scene);
        window.showAndWait();
        return returned;
    }

    public String[] retrieveTTE() throws Exception{
        if(databaseOperations == null){
            throw new Exception("RequestBox has not been initialized with DatabaseOperator");
        }

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setResizable(false);
        window.setMinWidth(350);
        window.setWidth(700);
        Label Message = new Label(message);

        TextField textField = new TextField();

        Button apply = new Button("Apply");

        ListView<String> listView = new ListView<>();

        ObservableList<String> items = FXCollections.observableArrayList(translateTestsToArray(databaseOperations.retrieveData("testName", null,"tests")));
        listView.setItems(items);
        listView.setPrefHeight(500);
        listView.setPrefWidth(320);
        listView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> ov,
                                        String old_val, String new_val) {
                        currentSelection  = new_val.replace(" ", "");
                    }
                });
        apply.setOnAction(event -> {
            if(currentSelection != null){
                try {
                    retrieved = translateTestsToArray(databaseOperations.retrieveData("command", currentSelection,"tests"));
                    supportCommand = translateTestsToArray(databaseOperations.retrieveData("supportCommand", currentSelection, "tests"));
                    testName = currentSelection;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                window.close();
            }else {
                System.out.print("Not Selection Made");
            }
        });

        VBox vBox = new VBox(10);
        VBox listBox = new VBox(2);
        VBox.setVgrow(listBox, Priority.ALWAYS);
        listBox.getChildren().addAll(listView);
        vBox.getChildren().addAll(Message,listBox,apply);
        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox);

        window.setScene(scene);
        window.showAndWait();
        return retrieved;
    }

    private String[] translateTestsToArray(String data){
        String toReturn = data.replace("[", "").replace("]","");
        return toReturn.split(",");

    }

}
