/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.AppointmentDaoImpl;
import static DAO.AppointmentDaoImpl.checkAppointmentOverlap;
import Model.Appointment;
import Model.Customer;
import Model.User;
import Utilities.BusinessException;
import static Utilities.TimeFiles.createLocalDateTime;
import static Utilities.TimeFiles.getAvailableAppointmentTimes;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import static DAO.CustomerDaoImpl.getAllActiveCustomers;
import java.time.LocalTime;
import javafx.event.Event;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Controls screen for adding new appointments
 *
 * @author Austin Wong
 */
public class AddAppointmentScreenController extends GeneralController implements Initializable {
    
    //<editor-fold defaultstate="collapsed" desc="ui-variables">
    @FXML
    private TextField contactTxt;

    @FXML
    private ComboBox<String> endTimeCombo;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label errorLbl;

    @FXML
    private TextField locationTxt;

    @FXML
    private TextField typeTxt;

    @FXML
    private TextField titleTxt;

    @FXML
    private Button backBtn;

    @FXML
    private ComboBox<String> startTimeCombo;

    @FXML
    private TextField urlTxt;

    @FXML
    private TextField descriptionTxt;

    @FXML
    private Button saveBtn;
    
    @FXML
    private ComboBox<Customer> customerComboBox;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="actions">
    
    // Create a new appointment and save to DB
    @FXML
    void onActionSave(ActionEvent event) {
        submit(event);
    }

    // Return to previous screen
    @FXML
    void onActionReturn(ActionEvent event) {
        back(event);
    }
    
    @FXML
    void onKeyPressedSubmit(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            submit(event);
        }
    }
    //</editor-fold>
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Populate ComboBoxes for Appointment Times
        startTimeCombo.setItems(getAvailableAppointmentTimes());
        endTimeCombo.setItems(getAvailableAppointmentTimes());
        
        // Pre-select times
        startTimeCombo.getSelectionModel().selectFirst();
        endTimeCombo.getSelectionModel().select(2);
        
        // Populate Customer ComboBox
        try{
            customerComboBox.setItems(getAllActiveCustomers());
        }
        catch(SQLException e){
            handleSQLException(e);
        }
        
        // Pre-select Customer
        Customer customer = Customer.getCurrentCustomer();
        if (customer instanceof Customer)
            customerComboBox.setValue(customer);
        
    }
    
    private void handleSQLException(SQLException e){
        Logger.getLogger("errorlog.txt").log(Level.WARNING, null, e);
        displayErrorAlert("Error adding appointment to database");
    }
    
    private void submit(Event event){
        // No Blank Text Fields Allowed
        if (checkFields(typeTxt)){
            displayMessage(errorLbl,errorStr);
            return;
        }

        // Appointment date must be selected
        if(datePicker.getValue() == null){
            displayMessage(errorLbl,"Select a date");
            return;
        }
        
        // Customer must be selected
        Customer customerComboBoxSelection = customerComboBox.getValue();
        if(customerComboBoxSelection == null){
            displayMessage(errorLbl,"Select a customer");
            return;
        }
        
        // Get selected customer, date, and time info
        int customerId = customerComboBoxSelection.getCustomerId();
        LocalDate date = datePicker.getValue();
        String startTime = startTimeCombo.getValue();
        String endTime = endTimeCombo.getValue();
        LocalDateTime startDateTime = createLocalDateTime(date,startTime);
        LocalDateTime endDateTime = createLocalDateTime(date,endTime);
        
        // Validate times
        if(!(startDateTime.isBefore(endDateTime))){
            displayMessage(errorLbl,"Start time must be before end time");
            return;
        }
        
        // Appointment Times should be during business hours (8am-5pm)
        // In this application, weekend hours are allowed (within the same 8am-5pm timeframe)
        assert (!startDateTime.isBefore(LocalDateTime.of(date, LocalTime.of(8,0))));
        assert (!startDateTime.isAfter(LocalDateTime.of(date,LocalTime.of(17, 0))));
        assert (!endDateTime.isAfter(LocalDateTime.of(date,LocalTime.of(17,0))));

        // Gather input text
        String location = locationTxt.getText();
        String type = typeTxt.getText();
        String title = titleTxt.getText();
        String contact = contactTxt.getText();
        String url = urlTxt.getText();
        String description = descriptionTxt.getText();
        
        // Try to add appointment to DB
        try{  
            
            // Check for overlapping appointments
            if(checkAppointmentOverlap(User.getCurrentUser().getUserId(), customerId, startDateTime,endDateTime,false))
                throw new BusinessException("Appointment time overlaps with existing appointment");
            
            Customer.setCurrentCustomer(customerComboBoxSelection);
            
            // Check for existing duplicate appointment. If none exist, add appointment.
            Appointment appointment = AppointmentDaoImpl.getAppointment(startDateTime, endDateTime);
            if(!(appointment instanceof Appointment)){
                AppointmentDaoImpl.insertAppointment(title, description, location, contact, type, url, startDateTime, endDateTime);
                displayNotification(event, "Appointment successfully added!");
                back(event);
            }
            else
                throw new BusinessException("Appointment already exists");
        }
        catch(SQLException e){
            handleSQLException(e);
        }
        catch(BusinessException e){
            displayMessage(errorLbl,e.getMessage());
        }
    }
    
    private void back(Event event){
        Customer.setCurrentCustomer(null);
        Appointment.setCurrentAppointment(null);
        returnToLastScreen(event);
    }
}
