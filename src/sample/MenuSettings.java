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

    public static void display(){
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Settings");
        window.setMinWidth(300);
        Button toClose = new Button("Close");
        toClose.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent e){
                window.close();
            }
        });


        BorderPane layout = new BorderPane();
        layout.setLeft(addVBox());
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
        // layout.setCenter(addGrid());

    }

    public static VBox addVBox (){
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
        return toReturn;
    }
}

