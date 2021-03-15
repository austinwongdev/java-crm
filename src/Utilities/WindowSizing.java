/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author Austin Wong
 */
public class WindowSizing {
    
    // Gets screen that window is currently on using center of window
    private static Screen getCurrentScreen(Stage stage){
        ObservableList<Screen> screens = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
        
        double stageMinX = stage.getX();
        double stageCenterX = (2*stageMinX + stage.getWidth())/2;
        
        for(Screen screen : screens){
            if((stageCenterX >= screen.getVisualBounds().getMinX()) && (stageCenterX <= screen.getVisualBounds().getMaxX())){
                return screen;
            }
        }
        return Screen.getPrimary(); // Default to primary screen as fallback
    }
    
    public static double getWidth(Stage stage){
        Screen screen = getCurrentScreen(stage);
        return screen.getVisualBounds().getWidth();
    }
    
    public static double getHeight(Stage stage){
        Screen screen = getCurrentScreen(stage);
        return screen.getVisualBounds().getHeight();
    }
    
    public static void setMinimums(Stage stage){
        stage.setMinHeight(800);
        stage.setMinWidth(800);
    }
    
    public static void prepStage(Stage stage){
        setMinimums(stage);
        stage.setMaximized(true);
    }
    
    // Sets alert coordinates such that it displays in lower-right corner of screen that the stage is in
    public static void setAlertCoordinates(Stage stage, Alert alert){
        
        Screen screen = getCurrentScreen(stage);
        double screenMaxX = screen.getVisualBounds().getMaxX();
        double screenMaxY = screen.getVisualBounds().getMaxY();

        alert.setX(screenMaxX - alert.getWidth());
        alert.setY(screenMaxY - alert.getHeight());     
    }
}
