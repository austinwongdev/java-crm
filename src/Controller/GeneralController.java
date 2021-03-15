/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Utilities.RBMain;
import Utilities.WindowSizing;
import static Utilities.WindowSizing.setAlertCoordinates;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Commonly shared controller variables and methods
 * @author Austin Wong
 */
public class GeneralController {
    
    //<editor-fold desc="variables">
    private Stage stage;
    private Parent root;
    private Scene scene;
    
    protected String errorStr;
    
    protected static ResourceBundle rb;
    
    protected static enum AppScreen{VIEWCUSTOMERSCREEN, CALENDARSCREEN};
    protected static AppScreen lastScreen;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="screen navigation methods">
    
    // Primary method used to switch screens
    protected void displayScreen(Event event, String resourcePath) {
        stage = (Stage) ((Control) event.getSource()).getScene().getWindow();
        try{
            root = FXMLLoader.load(getClass().getResource(resourcePath), RBMain.getRb());
            scene = new Scene(root, WindowSizing.getWidth(stage), WindowSizing.getHeight(stage));
            scene.getStylesheets().add("Resources/generalStylesheet.css");
            root.requestFocus();
            stage.setScene(scene);
            stage.show();
        }
        // This shouldn't happen, so we log it and close the application
        catch(IOException e){
            Logger.getLogger("errorlog.txt").log(Level.SEVERE, null, e);
            throw new RuntimeException(e);
        }
    }
    
    // Returns to previous screen
    // Usually called by a "back" button
    protected void returnToLastScreen(Event event) {
        if(lastScreen.equals(AppScreen.CALENDARSCREEN))
            displayScreen(event,"/View/CalendarScreen.fxml");
        else if(lastScreen.equals(AppScreen.VIEWCUSTOMERSCREEN))
            displayScreen(event,"/View/ViewCustomerScreen.fxml");
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="user communication methods">
    
    // Sets a label's text to a temporary message in darkred font that fades out
    // Generally used for checking user input
    protected void displayMessage(Label lbl, String message) {
        lbl.setText(message);
        lbl.setTextFill(Color.DARKRED);
        FadeTransition ft = new FadeTransition(Duration.millis(4000), lbl);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
    }
    
    // Displays an Error Alert and waits for user to acknowledge it
    // Generally used for serious alerts
    protected void displayErrorAlert(String errorString){
        Alert alert = new Alert(Alert.AlertType.ERROR, errorString, ButtonType.OK);
        alert.showAndWait();
    }
    
    // Displays a temporary notification in lower-right corner of window
    // Generally used for success messages
    protected void displayNotification(Event event, String content){
        
        Stage stage = (Stage)((Control) event.getSource()).getScene().getWindow();
        
        // Create Alert
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setResult(ButtonType.OK); // Sets result since no buttons are present
        alert.setContentText(content);
        alert.setOnShown(e -> setAlertCoordinates(stage, alert)); // .setOnShown takes an EventHandler, but since this is a short and simple call to a procedure in another class, it makes sense to use a lambda here
                
        // Auto-close
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> alert.close()); // Again, short and simple so it was easier to use a lambda than write out a new eventhandler
        
        // Show alert
        alert.show();
        delay.play();
        
        // Close alert if X button is clicked
        Window window = alert.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(e -> alert.close());
    }
    
    //</editor-fold>
    
    // Check for empty fields, set errorStr if a field is empty
    // Returns True if an empty field exists, False if all fields are populated or no fields were passed in as a parameter
    protected Boolean checkFields(TextField...txtFields){
        
        rb = RBMain.getRb();
        for(TextField txt : txtFields){
            if(txt.getText().isEmpty()){
                if(txt.getPromptText().isEmpty())
                    errorStr = rb.getString("unknownemptyfield");
                else
                    errorStr = txt.getPromptText() + " " + rb.getString("knownemptyfield");
                return true;
            }
        }
        return false;
    }
}
