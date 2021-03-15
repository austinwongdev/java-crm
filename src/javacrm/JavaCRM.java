/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javacrm;

import DAO.DBConnection;
import Utilities.LogFiles;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Utilities.RBMain;
import Utilities.WindowSizing;
import static Utilities.WindowSizing.prepStage;
import java.io.IOException;

/**
 *
 * @author Austin Wong
 */
public class JavaCRM extends Application {
    
    @Override
    public void start(Stage stage) throws IOException {
        
        // Setup ResourceBundle
        RBMain.setRb();
        
        prepStage(stage);
        Parent root = FXMLLoader.load(getClass().getResource("/View/LoginScreen.fxml"),RBMain.getRb());
        Scene scene = new Scene(root,WindowSizing.getWidth(stage),WindowSizing.getHeight(stage));
        scene.getStylesheets().add("Resources/generalStylesheet.css");
        root.requestFocus(); // Prevent username textfield from being focused upon initialization
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        LogFiles.setupLogger();
        DBConnection.startConnection(); // Connect to MySQL DB
        //TestConditions.createTestCustomer(); //***AAW: TESTING
        launch(args);
        //TestConditions.cleanUp(); //***AAW: TESTING
        DBConnection.closeConnection(); // Disconnect from MySQL DB
        
    }
    
}
