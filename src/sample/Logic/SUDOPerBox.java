package sample.Logic;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class SUDOPerBox {

    private static String _password;

    public static String display(){

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("SUDO Permission required");
        window.setMinWidth(300);
        Label message = new Label("SUDO permission required to reset ROS");

        PasswordField password = new PasswordField();
        password.setText("password");

        Button apply = new Button("Reset ROS");

        apply.setOnAction(event -> {
            if(!(password.getText() == null)){
                 _password =password.getText();
                window.close();
            }else {
                password.setText("Provide Password");
            }
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(message,password,apply);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        return _password;
    }
}
