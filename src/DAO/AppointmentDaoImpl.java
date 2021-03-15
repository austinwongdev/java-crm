/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import static DAO.DBQuery.setPreparedStatement;
import Model.Appointment;
import Model.Customer;
import Model.User;
import static Model.User.getCurrentUser;
import Utilities.RBMain;
import static Utilities.TimeFiles.dbStrNow;
import static Utilities.TimeFiles.dbStrToLocalDateTime;
import static Utilities.TimeFiles.localDateTimeToDBStr;
import static Utilities.TimeFiles.localDateTimeToUITime;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Austin Wong
 */
public class AppointmentDaoImpl extends GeneralDaoImpl{
    
    //<editor-fold defaultstate="collapsed" desc="data retrieval">
    
    // Retrieve appointment from DB with customerId, userId, start, and end
    public static Appointment getAppointment(LocalDateTime start, LocalDateTime end) throws SQLException {
        
        Appointment appointmentResult;
        String sqlStatement = "SELECT * FROM appointment WHERE customerId = ? AND userId = ? AND start = ? AND end = ?";
        PreparedStatement ps = setPreparedStatement(sqlStatement);
        ps.setInt(1, Customer.getCurrentCustomer().getCustomerId());
        ps.setInt(2, User.getCurrentUser().getUserId());
        ps.setString(3, localDateTimeToDBStr(start));
        ps.setString(4, localDateTimeToDBStr(end));
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
            int appointmentId = rs.getInt("appointmentId");
            String title = rs.getString("title");
            String description = rs.getString("description");
            String location = rs.getString("location");
            String contact = rs.getString("contact");
            String type = rs.getString("type");
            String url = rs.getString("url");
            
            getMetadata(rs);
            appointmentResult = new Appointment(appointmentId, Customer.getCurrentCustomer().getCustomerId(), User.getCurrentUser().getUserId(), title, description, location, contact, type, url, start, end, createDateLdt, createdBy, lastUpdateLdt, lastUpdateBy);
            return appointmentResult;
        }
        
        return null;
    }
    
    // Get a list of appointments for currentUser between two LocalDateTimes
    public static ObservableList<Appointment> getAppointmentList(int userId, LocalDateTime searchStart, LocalDateTime searchEnd) throws SQLException {
        ObservableList<Appointment> apptList =FXCollections.observableArrayList();
        String selectStatement;
        PreparedStatement ps;
        
        // if LocalDateTimes are the same, return ALL appointments for currentUser
        if(searchStart.equals(searchEnd)){
           selectStatement = "SELECT * FROM appointment WHERE userId = ? ORDER BY start";
           ps = setPreparedStatement(selectStatement);
        }
        else{
            selectStatement="SELECT * FROM appointment WHERE userId = ? AND start BETWEEN ? AND ? ORDER BY start";
            ps = setPreparedStatement(selectStatement);
            ps.setString(2, localDateTimeToDBStr(searchStart));
            ps.setString(3, localDateTimeToDBStr(searchEnd));
        }
        ps.setInt(1, userId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
            
            int appointmentId = rs.getInt("appointmentId");
            int customerId = rs.getInt("customerId");
            String title = rs.getString("title");
            String description = rs.getString("description");
            String location = rs.getString("location");
            String contact = rs.getString("contact");
            String type = rs.getString("type");
            String url = rs.getString("url");
            String startStr = rs.getString("start");
            String endStr = rs.getString("end");
            
            LocalDateTime start = dbStrToLocalDateTime(startStr);
            LocalDateTime end = dbStrToLocalDateTime(endStr);
            getMetadata(rs);
            
            Appointment appointmentResult = new Appointment(appointmentId, customerId, userId, title, description, location, contact, type, url, start, end, createDateLdt, createdBy, lastUpdateLdt, lastUpdateBy);
            apptList.add(appointmentResult);
        }
        
        return apptList;
    }
    
    // Creates a report of the number of various types of appointments each month in a given time period
    public static String getTypeReport(ObservableList<String> searchTimes, Integer userId) throws SQLException {
        
        String report = "";
        String startSearch = searchTimes.get(0);
        String endSearch = searchTimes.get(1);
        String currentMonth = "";
        String selectStatement;
        PreparedStatement ps;
        
        // Report for specific user
        if(userId instanceof Integer){
            selectStatement="SELECT CONCAT(MONTHNAME(start),\" \",YEAR(start)) \"apptperiod\", type, count(type) \"appointments\" FROM appointment " + 
                                "WHERE userId = ? AND start BETWEEN ? AND ? " +
                                "GROUP BY apptperiod, type " +
                                "ORDER BY ANY_VALUE(start)";
            ps = setPreparedStatement(selectStatement);
            ps.setInt(1, userId);
            ps.setString(2, startSearch);
            ps.setString(3, endSearch);
        }
        // Report for ALL users
        else{
            selectStatement="SELECT CONCAT(MONTHNAME(start),\" \",YEAR(start)) \"apptperiod\", type, count(type) \"appointments\" FROM appointment " + 
                            "WHERE start BETWEEN ? AND ? " +
                            "GROUP BY apptperiod, type " +
                            "ORDER BY ANY_VALUE(start)";
            ps = setPreparedStatement(selectStatement);
            ps.setString(1, startSearch);
            ps.setString(2, endSearch);
        }
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
            String month = rs.getString("apptperiod");
            if(!(month.equals(currentMonth))){
                currentMonth = month;
                report = report + "\n" + "   " + month + "\n";
            }
            String type = rs.getString("type");
            int appointments = rs.getInt("appointments");
            report = report + type + ": " + appointments + "\n";
        }
        
        report = report.replaceFirst("\n", "");
        
        return report;
    }
    
    // Creates a report of the average # of appointments among all consultants by month in a given time period
    public static String getAvgApptReport(ObservableList<String> searchTimes) throws SQLException {
        
        String report = "";
        String startSearch = searchTimes.get(0);
        String endSearch = searchTimes.get(1);
        String selectStatement;
        PreparedStatement ps;

        selectStatement="SELECT totappts.apptperiod, avg(totappts.appointments) \"appointments\" FROM " +
                           "(SELECT count(*) \"appointments\", userId, ANY_VALUE(start) \"start\", concat(MONTHNAME(start),\" \",year(start)) \"apptperiod\" " +
                           "FROM appointment WHERE start BETWEEN ? AND ? " + 
                           "GROUP BY userId, apptperiod) totappts " + 
                        "GROUP BY totappts.apptperiod ORDER BY totappts.start";
        ps = setPreparedStatement(selectStatement);
        ps.setString(1, startSearch);
        ps.setString(2, endSearch);
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
            String month = rs.getString("apptperiod");
            double appointments = rs.getDouble("appointments");
            
            report = report + month + ": " + appointments + "\n";
        }
        
        return report;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="data setting">
    
    // Insert appointment into DB
    public static Appointment insertAppointment(String title, String description, String location, String contact, String type, String url, LocalDateTime start, LocalDateTime end) throws SQLException {
        
        String createStatement = "INSERT INTO appointment (customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = setPreparedStatement(createStatement);
        
        String userName = getCurrentUser().getUserName();
        String time = dbStrNow();
        
        ps.setInt(1, Customer.getCurrentCustomer().getCustomerId());
        ps.setInt(2, User.getCurrentUser().getUserId());
        ps.setString(3, title);
        ps.setString(4, description);
        ps.setString(5, location);
        ps.setString(6, contact);
        ps.setString(7, type);
        ps.setString(8, url);
        ps.setString(9, localDateTimeToDBStr(start));
        ps.setString(10, localDateTimeToDBStr(end));
        ps.setString(11, time);
        ps.setString(12, userName);
        ps.setString(13, time);
        ps.setString(14, userName);
        ps.execute();
        
        return getAppointment(start, end);
    }
        
    // Delete appointment from DB
    public static int deleteAppointment(int appointmentId) throws SQLException {
        String deleteStatement = "DELETE FROM appointment WHERE appointmentId = ?";
        PreparedStatement ps = setPreparedStatement(deleteStatement);
        ps.setInt(1, appointmentId);
        ps.execute();
        return ps.getUpdateCount();
    }
    
    // Update appointment in DB
    public static int updateAppointment(String title, String description, String location, String contact, String type, String url, LocalDateTime start, LocalDateTime end, int customerId, int appointmentId) throws SQLException {
        String updateStatement = "UPDATE appointment SET title = ?, description = ?, location = ?, contact = ?, type = ?, url = ?, start = ?, end = ?, lastUpdate = ?, lastUpdateBy = ?, customerId = ? WHERE appointmentId = ?";
        PreparedStatement ps = setPreparedStatement(updateStatement);
        
        String userName = getCurrentUser().getUserName();
        String time = dbStrNow();
        
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, contact);
        ps.setString(5, type);
        ps.setString(6, url);
        ps.setString(7, localDateTimeToDBStr(start));
        ps.setString(8, localDateTimeToDBStr(end));
        ps.setString(9, time);
        ps.setString(10, userName);
        ps.setInt(11, customerId);
        ps.setInt(12, appointmentId);
        ps.execute();
        return ps.getUpdateCount();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="validation">
    
    // Returns true if appointment dates overlap with existing appointment, false otherwise
    public static boolean checkAppointmentOverlap(int userId, int customerId, LocalDateTime searchStart, LocalDateTime searchEnd, Boolean checkId) throws SQLException {
        
        String selectStatement;
        PreparedStatement ps;
        
        // For 'start BETWEEN ? AND ?'
        String startStr1 = localDateTimeToDBStr(searchStart);
        LocalDateTime searchEndAdj = searchEnd.minusSeconds(1);
        String endStr1 = localDateTimeToDBStr(searchEndAdj);
        
        // For 'end BETWEEN ? AND ?'
        LocalDateTime searchStartAdj = searchStart.plusSeconds(1);
        String startStr2 = localDateTimeToDBStr(searchStartAdj);
        String endStr2 = localDateTimeToDBStr(searchEnd);

        // Because BETWEEN is inclusive, we need to ensure that:
        // if DB appt starts when Local appt ends, that is okay
        // If DB appt ends when Local appt starts, that is okay
        // So  start BETWEEN searchStart AND searchEnd - 1 ms
        // and end BETWEEN searchStart + 1 ms AND searchEnd
        // both satisfy this
        
        // Check for appointments that overlap for a given user or a given customer
        selectStatement="SELECT appointmentId FROM appointment WHERE (userId = ? OR customerId = ?) AND (start BETWEEN ? AND ? OR end BETWEEN ? and ?)";
        
        ps = setPreparedStatement(selectStatement);
        ps.setInt(1, userId);
        ps.setInt(2, customerId);
        ps.setString(3, startStr1);
        ps.setString(4, endStr1);
        ps.setString(5, startStr2);
        ps.setString(6, endStr2);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        // Looking for any overlapping appointment besides the one being updated
        if(checkId){
            int currentAppointmentId = Appointment.getCurrentAppointment().getAppointmentId();
            while(rs.next()){
                int dbAppointmentId = rs.getInt("appointmentId");
                if (dbAppointmentId != currentAppointmentId)
                    return true;
            }
            return false;
        }
        // Looking for ANY overlapping appointment (adding, not updating appointment)
        else{
            while(rs.next()){
                return true;
            }
            return false;
        }
    }  
    
    /**
     * Checks for an appointment within 15 minutes of current time
     * @return Alert message (String) or empty string if no appointment found
     * @throws SQLException 
     */
    public static String checkUpcomingAppt() throws SQLException {
        
        String selectStatement;
        PreparedStatement ps;
        int userId = User.getCurrentUser().getUserId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startLdt = now.plusMinutes(15);
        String startSearch = localDateTimeToDBStr(startLdt);
        String endSearch = localDateTimeToDBStr(now);

        selectStatement="select customerName, start from appointment " + 
                        "inner join customer on customer.customerId = appointment.customerId " +
                        "where userId = ? AND start <= ? AND end > ?";
        ps = setPreparedStatement(selectStatement);
        ps.setInt(1, userId);
        ps.setString(2, startSearch);
        ps.setString(3, endSearch);
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        if(rs.next()){
            
            String customerName = rs.getString("customerName");
            String start = rs.getString("start");
            LocalDateTime apptTime = dbStrToLocalDateTime(start);
            long minuteDiff = ChronoUnit.MINUTES.between(now,apptTime);
            long secondDiff = ChronoUnit.SECONDS.between(now,apptTime);
            ResourceBundle rb = RBMain.getRb();
            String alertMsg;
            
            if(secondDiff > 0){
                alertMsg = rb.getString("upcomingappt1") + customerName + rb.getString("upcomingappt2");          
                if(secondDiff >= 60)
                    alertMsg = alertMsg + minuteDiff + rb.getString("upcomingappt3min");
                else
                    alertMsg = alertMsg + secondDiff + rb.getString("upcomingappt3sec");
            }
            else{
                alertMsg = rb.getString("recentappt1") + customerName + rb.getString("recentappt2");
                if(secondDiff <= -60)
                    alertMsg = alertMsg + minuteDiff*-1 + rb.getString("recentappt3min");
                else
                    alertMsg = alertMsg + secondDiff*-1 + rb.getString("recentappt3sec");
            }
            
            alertMsg = alertMsg + localDateTimeToUITime(apptTime);
            
            return alertMsg;
        }
        
        return "";
    }
    
    //</editor-fold>
    
}
