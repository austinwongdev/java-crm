/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.AppointmentDaoImpl;
import Model.Appointment;
import Model.User;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import static DAO.UserDaoImpl.getAllActiveUsers;

/**
 * Controls calendar logic
 *
 * @author Austin Wong
 */
public class CalendarScreenController extends GeneralController implements Initializable {

    //<editor-fold defaultstate="collapsed" desc="ui-variables">
    
    @FXML
    private TableColumn<?, ?> typeCol;

    @FXML
    private TableColumn<?, ?> titleCol;

    @FXML
    private TableColumn<?, ?> startCol;

    @FXML
    private TableColumn<?, ?> customerCol;

    @FXML
    private Button updateBtn;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ImageView leftArrow;

    @FXML
    private Button addBtn;

    @FXML
    private ComboBox<Period> periodComboBox;
    
    @FXML
    private ComboBox<User> userComboBox;

    @FXML
    private ImageView rightArrow;

    @FXML
    private Button deleteBtn;

    @FXML
    private TableColumn<?, ?> endCol;

    @FXML
    private TableColumn<?, ?> dateCol;

    @FXML
    private Button backBtn;

    @FXML
    private TableView<Appointment> appointmentTableView;
    
    //</editor-fold>
    
    // ENUM for time period to display appointments on calendar
    private static Period lastSelectedPeriod = Period.WEEK; // Default to WEEK
    private enum Period {
        
        WEEK("WEEK"),
        MONTH("MONTH"),
        ALL_APPOINTMENTS("ALL APPOINTMENTS");
        
        private final String displayName;
        
        Period(String displayName){
            this.displayName = displayName;
        }
        
        @Override
        public String toString(){
            return displayName;
        }
    };
    
    private static LocalDate lastSelectedDate = LocalDate.now();
    
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private TableView.TableViewSelectionModel<Appointment> tvSelAppointment;
    
    private ObservableList<User> userList = FXCollections.observableArrayList();

    //<editor-fold defaultstate="collapsed" desc="actions">
    
    @FXML
    void onActionAddAppointment(ActionEvent event) {
        displayScreen(event, "/View/AddAppointmentScreen.fxml");
    }

    @FXML
    void onActionUpdateAppointment(ActionEvent event) {
        if(currentUserSelected()) {
            if(tvSelAppointment.isEmpty()){
                displayErrorAlert("Select an appointment first");
            }
            else{
                Appointment.setCurrentAppointment(tvSelAppointment.getSelectedItem());
                displayScreen(event, "/View/UpdateAppointmentScreen.fxml");
            }
        } else
            displayErrorAlert("Cannot update other user's appointments");
    }

    @FXML
    void onActionDeleteAppointment(ActionEvent event) {
        if(currentUserSelected()){
            if(tvSelAppointment.isEmpty()){
                displayErrorAlert("Select an appointment first");
            }
            else{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete appointment?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait()
                        .filter(res -> res == ButtonType.YES) // Using lambda to handle unique event where user clicks yes on alert to agree to delete appointment
                        .ifPresent(res -> {
                            try {
                                if(AppointmentDaoImpl.deleteAppointment(tvSelAppointment.getSelectedItem().getAppointmentId())>0){
                                    refreshTable();
                                    displayNotification(event, "Appointment successfully deleted");
                                    Appointment.setCurrentAppointment(null);
                                }
                            }
                            catch (SQLException e) {
                                Logger.getLogger("errorlog.txt").log(Level.WARNING, null, e);
                                displayErrorAlert("Error occurred while deleting appointment from database");
                            }});
            }
        }
        else{
            displayErrorAlert("Cannot delete other user's appointments");
        }
    }

    // Return to main menu
    @FXML
    void onActionReturn(ActionEvent event) { 
        displayScreen(event, "/View/MainMenu.fxml");
    }
    
    // Filter table based on date and time period selected
    @FXML
    void onActionFilterPeriod(ActionEvent event) {
        lastSelectedPeriod = periodComboBox.getValue();
        lastSelectedDate = datePicker.getValue();
        refreshTable();
    }
    
    // Filter/refresh table when selected user is changed
    @FXML
    void onActionFilterUser(ActionEvent event) {
        refreshTable();
    }
    
    //</editor-fold>
    
    // Returns userId of user selected in ComboBox
    private int getSelectedUserId(){
        User user = userComboBox.getValue();
        int userId = user.getUserId();
        return userId;
    }
    
    // Returns true if Current User is selected, false otherwise
    private boolean currentUserSelected(){
        int currentUserId = User.getCurrentUser().getUserId();
        int selectedUserId = getSelectedUserId();
        return selectedUserId == currentUserId;
    }
    
    // Refreshes Data in Calendar
    private void refreshTable(){
        
        // Get userId for selected user
        int userId = getSelectedUserId();
        
        // Prepare Search Periods depending on which was selected
        Period period = periodComboBox.getValue();
        LocalDate ld = datePicker.getValue();
        LocalDateTime searchStart = ld.atStartOfDay();
        LocalDateTime searchEnd = searchStart; // Default is ALL where we pass in the same LocalDateTime for both searchStart and searchEnd
        
        if(period.equals(Period.WEEK)){
            TemporalField tf = WeekFields.of(Locale.getDefault()).dayOfWeek();
            searchEnd = searchEnd.with(tf,7);
            searchStart = searchStart.with(tf,1);
        }
        else if(period.equals(Period.MONTH)){
            searchEnd = searchEnd.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1); // Go to midnight of the first day of next month
            searchStart = searchStart.with(TemporalAdjusters.firstDayOfMonth());
        }
        
        appointments.clear();
        
        // Get Appointment Data
        try{
            appointments.addAll(AppointmentDaoImpl.getAppointmentList(userId,searchStart,searchEnd));
        }
        catch(SQLException e){
            Logger.getLogger("errorlog.txt").log(Level.WARNING,null,e);
            displayErrorAlert("Error loading appointments from database");
        }
        
        // Populate Table
        appointmentTableView.setItems(appointments);
        tvSelAppointment = appointmentTableView.getSelectionModel();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Set this screen as return screen
        lastScreen = AppScreen.CALENDARSCREEN;
        
        // Setup Calendar Columns
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        // Set Initial Date and Period
        datePicker.setValue(lastSelectedDate); // Default to today
        periodComboBox.getItems().setAll(Period.values());
        periodComboBox.setValue(lastSelectedPeriod);
           
        // Setup User ComboBox
        try{
            userList = getAllActiveUsers();
        }
        catch(SQLException e){
            Logger.getLogger("errorlog.txt").log(Level.WARNING,null,e);
            displayErrorAlert("Error loading users from database");
        }
        userComboBox.setItems(userList);
        userComboBox.setValue(User.getCurrentUser());
        
        // Populate Table with Data
        refreshTable();
        
        // Handle Image Buttons
        //    Moves calendar forward/backward by 1 week/month
        leftArrow.setOnMouseClicked((MouseEvent e) -> {
            LocalDate initialDate = datePicker.getValue();
            if(periodComboBox.getValue().equals(Period.WEEK))
                datePicker.setValue(initialDate.minusWeeks(1));
            else if(periodComboBox.getValue().equals(Period.MONTH))
                datePicker.setValue(initialDate.minusMonths(1));
        });
        
        rightArrow.setOnMouseClicked((MouseEvent e) -> {
            LocalDate initialDate = datePicker.getValue();
            if(periodComboBox.getValue().equals(Period.WEEK))
                datePicker.setValue(initialDate.plusWeeks(1));
            else if(periodComboBox.getValue().equals(Period.MONTH))
                datePicker.setValue(initialDate.plusMonths(1));
        });
    }
}
