package sample.Interfaces;

import sample.Logic.ROSNode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jam on 9/10/16.
 */
public interface NodeInteractionDependency {

    public void ROSNode();
    public String getName();
    public void populate(String name, ArrayList<String> services,ArrayList<String> subscriptions, ArrayList<String> publications);

    public ArrayList<String> getServices();
    public ArrayList<String> getSubscriptions();
    public ArrayList<String> getPublications();

}
