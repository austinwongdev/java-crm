/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.AppointmentDaoImpl;
import static DAO.AppointmentDaoImpl.checkAppointmentOverlap;
import DAO.CustomerDaoImpl;
import Model.Appointment;
import Model.Customer;
import Model.User;
import Utilities.BusinessException;
import static Utilities.TimeFiles.createLocalDateTime;
import static Utilities.TimeFiles.getAvailableAppointmentTimes;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import static Utilities.TimeFiles.localDateTimeToUITime;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static DAO.CustomerDaoImpl.getAllActiveCustomers;
import java.time.LocalTime;
import javafx.event.Event;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Controls screen for updating existing appointments
 *
 * @author Austin Wong
 */
public class UpdateAppointmentScreenController extends GeneralController implements Initializable {

    //<editor-fold defaultstate="collapsed" desc="ui-variables">
    @FXML
    private TextField titleTxt;
    @FXML
    private TextField descriptionTxt;
    @FXML
    private TextField locationTxt;
    @FXML
    private TextField contactTxt;
    @FXML
    private TextField typeTxt;
    @FXML
    private TextField urlTxt;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> startTimeCombo;
    @FXML
    private ComboBox<String> endTimeCombo;
    @FXML
    private ComboBox<Customer> customerComboBox;
    @FXML
    private Button saveBtn;
    @FXML
    private Button backBtn;
    @FXML
    private Label errorLbl;
    //</editor-fold>
    
    // Other variables
    private Appointment appt;

    //<editor-fold defaultstate="collapsed" desc="actions">
    
    // Attempt to update appointment in DB
    @FXML
    private void onActionSave(ActionEvent event) {
        submit(event);
    }

    // Return to previous screen
    @FXML
    private void onActionReturn(ActionEvent event) {
        back(event);
    }
    
    @FXML
    private void onKeyPressedSubmit(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            submit(event);
        }
    }
    //</editor-fold>
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Pre-populate fields with existing appointment data
        appt = Appointment.getCurrentAppointment();
        titleTxt.setText(appt.getTitle());
        descriptionTxt.setText(appt.getDescription());
        locationTxt.setText(appt.getLocation());
        contactTxt.setText(appt.getContact());
        typeTxt.setText(appt.getType());
        urlTxt.setText(appt.getUrl());
        datePicker.setValue(appt.getStart().toLocalDate());
        startTimeCombo.setItems(getAvailableAppointmentTimes());
        endTimeCombo.setItems(getAvailableAppointmentTimes());
        startTimeCombo.setValue(localDateTimeToUITime(appt.getStart()));
        endTimeCombo.setValue(localDateTimeToUITime(appt.getEnd()));
        
        // Populate Customer ComboBox
        int customerId = appt.getCustomerId();
        Customer customer;
        try{
            customerComboBox.setItems(getAllActiveCustomers());
            customer = CustomerDaoImpl.getCustomer(customerId);
            customerComboBox.setValue(customer);
        }
        catch(SQLException e){
            Logger.getLogger("errorlog.txt").log(Level.WARNING,null,e);
            displayErrorAlert("Error loading data from database");
        }
    }
    
    private void submit(Event event){
        
        // Validate text fields
        if (checkFields(typeTxt)){
            displayMessage(errorLbl,errorStr);
            return;
        }

        // Validate date field
        if(datePicker.getValue() == null){
            displayMessage(errorLbl,"Select a date");
            return;
        }
        
        // Validate Customer field
        Customer customerComboBoxSelection = customerComboBox.getValue();
        if(customerComboBoxSelection == null){
            displayMessage(errorLbl,"Select a customer");
            return;
        }
        int customerId = customerComboBoxSelection.getCustomerId();

        // Get appt date and time values
        LocalDate date = datePicker.getValue();
        String startTime = startTimeCombo.getValue();
        String endTime = endTimeCombo.getValue();
        LocalDateTime startDateTime = createLocalDateTime(date,startTime);
        LocalDateTime endDateTime = createLocalDateTime(date,endTime);
        
        // Validate appt times
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
        int appointmentId = Appointment.getCurrentAppointment().getAppointmentId();
        
        // Attempt to update appointment in DB
        try{  
            
            // Check for overlapping appointments
            if(checkAppointmentOverlap(User.getCurrentUser().getUserId(), customerId, startDateTime,endDateTime,true))
                throw new BusinessException("Appointment time overlaps with existing appointment");
            
            Customer.setCurrentCustomer(CustomerDaoImpl.getCustomer(customerId));
            
            // Update appointment
            AppointmentDaoImpl.updateAppointment(title, description, location, contact, type, url, startDateTime, endDateTime, customerId, appointmentId);
            displayNotification(event, "Appointment successfully updated");
            back(event);
        }
        catch(BusinessException e){
            displayMessage(errorLbl,e.getMessage());
        }
        catch(SQLException e){
            Logger.getLogger("errorlog.txt").log(Level.WARNING,null,e);
            displayErrorAlert("Error updating appointment to database");
        }
    }
    
    private void back(Event event){
        Appointment.setCurrentAppointment(null);
        Customer.setCurrentCustomer(null);
        returnToLastScreen(event);
    }
    
}
