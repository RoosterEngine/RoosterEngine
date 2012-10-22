package gameengine;

import gameengine.input.Action;
import gameengine.input.InputCode;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Stores all user preferences such as control bindings
 * @author davidrusu
 */
public class UserProfile {
    private String userName;
    private HashMap<Integer, Action> controls = new HashMap<Integer, Action>();
    private HashMap<String, Serializable> properties = new HashMap<String, Serializable>();
    
    /**
     * @param userName the name of the user
     */
    public UserProfile(String userName){
        this.userName = userName;
    }
    
    /**
     * Sets the specified {@link InputCode} to map to the specified {@link Action}
     * @param inputCode the input code to map
     * @param action the action to map
     */
    public void setInputBinding(int inputCode, Action action){
        controls.put(inputCode, action);
    }
    
    /**
     * Removes the binding for the specified {@link InputCode}
     * @param inputCode 
     */
    public void removeInputBinding(int inputCode){
        controls.remove(inputCode);
    }
    /**
     * Retrieves the {@link Action} that is mapped to the {@link InputCode}
     * @param inputCode
     * @return 
     */
    public Action getAction(int inputCode){
        return controls.get(inputCode);
    }
    
    /**
     * Sets the user name for this UserProfile
     * @param userName the new user name
     */
    public void setUserName(String userName){
        this.userName = userName;
    }
    
    /**
     * Retrieves the user name for this UserProfile
     * @return 
     */
    public String getUserName(){
        return userName;
    }
    
    /**
     * Maps a user defined property to a string, these properties can later be
     * retrieved with the property name
     * @param propertyName the property name
     * @param property the property to store
     */
    public void setProperty(String propertyName, Serializable property){
        properties.put(userName, property);
    }
    
    /**
     * Retrieves the property that is mapped to the specified property name
     * @param propertyName the name of the property to retrieve;
     * @return the user defined property or null if there is no property that
     * maps to the specified property name
     */
    public Object getProperty(String propertyName){
        return properties.get(propertyName);
    }
    
    /**
     * Returns an {@link Iterator} over the user controls
     * @return {@link Iterator}
     */
    public Iterator<Map.Entry<Integer, Action>>getControlsIterator(){
        return controls.entrySet().iterator();
    }
    
}