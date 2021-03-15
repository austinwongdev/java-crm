/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import static DAO.AppointmentDaoImpl.getAvgApptReport;
import static DAO.AppointmentDaoImpl.getTypeReport;
import Model.Report;
import Model.User;
import static Utilities.TimeFiles.getSearchTimes;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

/**
 * Controls logic for report screen
 *
 * @author Austin Wong
 */
public class ReportScreenController extends GeneralController implements Initializable {

    //<editor-fold defaultstate="collapsed" desc="ui-variables">
    
    @FXML
    private TextArea reportTextArea;

    @FXML
    private ComboBox<Report> reportComboBox;

    @FXML
    private Button backBtn;
    
    //</editor-fold>
    
    private final ObservableList<Report> reportList = FXCollections.observableArrayList();
    private final int userId = User.getCurrentUser().getUserId();

    //<editor-fold defaultstate="collapsed" desc="actions">
    
    @FXML
    private void onActionGenerateReport(ActionEvent event) {
        
        try{
            Report selectedReport = reportComboBox.getValue();
            LocalDateTime start = LocalDateTime.now();
            ObservableList<String> rollingYear = getSearchTimes(start,true);
            ObservableList<String> calendarYear = getSearchTimes(start, false);

            switch (selectedReport.getReportId()) {
                case 1:
                    reportTextArea.setText(getTypeReport(rollingYear,userId));
                    break;
                case 2:
                    reportTextArea.setText(getTypeReport(calendarYear,userId));
                    break;
                case 3:
                    reportTextArea.setText(getTypeReport(rollingYear,null));
                    break;
                case 4:
                    reportTextArea.setText(getTypeReport(calendarYear,null));
                    break;
                case 5:
                    reportTextArea.setText(getAvgApptReport(rollingYear));
                    break;
                case 6:
                    reportTextArea.setText(getAvgApptReport(calendarYear));
                    break;
                default:
                    break;
            }
        }
        catch(SQLException e){
            Logger.getLogger("errorlog.txt").log(Level.WARNING,null,e);
            displayErrorAlert("Could not retrieve report from database");
        }
    }

    // Return to main menu
    @FXML
    private void onActionReturn(ActionEvent event) {
        displayScreen(event, "/View/MainMenu.fxml");
    }
    
    //</editor-fold>

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Create reports and add to ComboBox
        // Note: I did not add a report for a schedule for each consultant here to avoid duplication because that functionality is already covered in the calendar screen
        reportList.add(new Report("User Appointment Types by Month (Rolling Year)",1));
        reportList.add(new Report("User Appointment Types by Month (Calendar Year)", 2));
        reportList.add(new Report("All Appointment Types by Month (Rolling Year)",3));
        reportList.add(new Report("All Appointment Types by Month (Calendar Year)",4));
        reportList.add(new Report("Organization Average Appointments by Month (Rolling Year)",5));
        reportList.add(new Report("Organization Average Appointments by Month (Calendar Year)",6));
        
        reportComboBox.setItems(reportList);
    }
}
