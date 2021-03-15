/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import static DAO.AppointmentDaoImpl.checkUpcomingAppt;
import DAO.DBConnection;
import DAO.UserDaoImpl;
import Utilities.BusinessException;
import static Utilities.LogFiles.logUserActivity;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Handles logic for login screen
 * @author Austin Wong
 */
public class LoginScreenController extends GeneralController implements Initializable {
    
    //<editor-fold defaultstate="collapsed" desc="ui-variables">
    @FXML
    private Label titleLbl;
    
    @FXML
    private Button loginBtn;

    @FXML
    private Button exitBtn;
    
    @FXML
    private TextField userNameTxt;

    @FXML
    private PasswordField passwordTxt;
    
    @FXML
    private Label errorLbl;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="actions">

    // Exit Program
    @FXML
    void onActionExit(ActionEvent event) {
        DBConnection.closeConnection();
        System.exit(0);
    }
    
    // Attempt Login (triggered by clicking button)
    @FXML
    void onActionLogin(ActionEvent event) {
        login(event);
    }
    
    // Attempt Login (triggered by ENTER button)
    @FXML
    void onKeyPressedLogin(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER){
            login(event);
        }
    }
    
    //</editor-fold>
    
    @Override
    public void initialize(URL url, ResourceBundle initialRb) {
        
        // Translate log-in text
        rb = initialRb;
        userNameTxt.setPromptText(rb.getString("username"));
        passwordTxt.setPromptText(rb.getString("password"));
        loginBtn.setText(rb.getString("loginverb"));
        exitBtn.setText(rb.getString("exit"));
        titleLbl.setText(rb.getString("loginnoun"));
    } 
    
    // Attempt Login
    private void login(Event event){
        
        //Check for empty fields
        if (checkFields(userNameTxt,passwordTxt)){
            displayMessage(errorLbl,errorStr);
            return;
        }
        
        try{
            if(UserDaoImpl.logIn(userNameTxt.getText(), passwordTxt.getText())){
                
                logUserActivity();

                // Alert if appt in 15 mins from login
                String alertMsg = checkUpcomingAppt();
                if(!(alertMsg.isEmpty()))
                    displayNotification(event, alertMsg);

                // Go to main menu
                displayScreen(event,"/View/MainMenu.fxml");
            }
            else
                throw new BusinessException(rb.getString("notfound"));
        }
        catch (SQLException e){
            Logger.getLogger("errorlog.txt").log(Level.WARNING, null, e);
            displayErrorAlert(rb.getString("loginsqlerror"));
        }
        catch (BusinessException e){
            displayErrorAlert(e.getMessage());
        }
    }
}
