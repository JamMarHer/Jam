package sample.Logic;

import sample.Interfaces.NodeInteractionDependency;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jam on 9/10/16.
 */
public class ROSNode implements NodeInteractionDependency {

    private HashMap<String, ArrayList<String>> characteristics;
    String name;

    @Override
    public void ROSNode(String _name, ArrayList<String> _services, ArrayList<String> _subscriptions, ArrayList<String> _publications) {
        name = _name;
        characteristics.put("services",_services);
        characteristics.put("subscriptions",_subscriptions);
        characteristics.put("publications",_publications);
    }

    // true = replace Array, false = update array
    public void updateCharacteristics(String option, ArrayList<String> updatedServices, boolean remove_add){
        if(remove_add){
            characteristics.put(option,updatedServices);
        }else{
            characteristics.put(option,updateArray(characteristics.get(option),updatedServices));
        }

    }

    // true = add service;  false = remove service
    public void updateCharacteristics(String option, String service, boolean remove_add){
        ArrayList<String> temp = characteristics.get(option);
        if(remove_add){
            temp.add(service);
        }else{
            temp.remove(service);
        }
        characteristics.put(option,temp);
    }

    private ArrayList<String> updateArray(ArrayList<String> prior, ArrayList<String> post){
        for(String currentVal : post){
            if(!(prior.contains(currentVal))){
                prior.add(currentVal);
            }
        }
        return prior;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ArrayList<String> getServices() {
        return characteristics.get("services");
    }

    @Override
    public ArrayList<String> getSubscriptions() {
        return characteristics.get("subscriptions");
    }

    @Override
    public ArrayList<String> getPublications() {
        return characteristics.get("publications");
    }
}
