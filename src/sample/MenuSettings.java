package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by jam on 9/1/16.
 */
public class MenuSettings {

    public boolean clicked = false;

    public  void display(){
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Settings");
        window.setMinWidth(600);
        window.setMinHeight(450);
        Button toClose = new Button("Close");
        toClose.setOnAction(e -> window.close());


        BorderPane layout = new BorderPane();
        layout.setLeft(addVBox());
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
        // layout.setCenter(addGrid());

    }

    public  VBox addVBox (){
        VBox toReturn = new VBox();
        Hyperlink options[] = new Hyperlink[] {
                new Hyperlink("Sales"),
                new Hyperlink("Marketing"),
                new Hyperlink("Distribution"),
                new Hyperlink("Costs")};

        for (int i=0; i<4; i++) {
            VBox.setMargin(options[i], new Insets(0, 0, 0, 8));
            toReturn.getChildren().add(options[i]);
        }
        Button trial = new Button("Cli");
        toReturn.getChildren().addAll(trial);

        trial.setOnAction(e -> {
            System.out.print("Clicked");
        });
        return toReturn;
    }
}

