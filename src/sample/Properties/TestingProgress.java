package sample.Properties;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by jam on 9/20/16.
 */
public class TestingProgress {
    private DoubleProperty state;
    private StringProperty stateString;

    public final double getState(){
        if(state != null)
            return state.get();
        return 0;

    }
    public final String getStateString(){
        if(stateString != null){
            return stateString.get();
        }else {
            return " ";
        }
    }
    public final void setState(double number){
        this.stateProperty().set(number);
    }
    public final void setStateString(String state){
        this.stateStringProperty().set(state);
    }

    public final DoubleProperty stateProperty(){
        if(state == null){
            state = new SimpleDoubleProperty(0);
        }
        return state;
    }
    public final StringProperty stateStringProperty(){
        if(stateString == null){
            stateString = new SimpleStringProperty(" ");
        }
        return stateString;
    }
}
