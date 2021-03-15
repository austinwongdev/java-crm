/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import DAO.CustomerDaoImpl;
import static Utilities.TimeFiles.localDateTimeToUIDate;
import static Utilities.TimeFiles.localDateTimeToUITime;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 *
 * @author Austin Wong
 */
public class Appointment {
    
    private static Appointment currentAppointment;
    
    //<editor-fold defaultstate="collapsed" desc="db variables">
    
    private int appointmentId;
    private int customerId;
    private int userId;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private String url;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdateBy;
    
    //</editor-fold>
    
    public Appointment(int appointmentId, int customerId, int userId, String title, String description, String location, String contact, String type, String url, LocalDateTime start, LocalDateTime end, LocalDateTime createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdateBy){
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.url = url;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }
    
    //<editor-fold defaultstate="collapsed" desc="db variable setters and getters">
    
    public static Appointment getCurrentAppointment(){
        return currentAppointment;
    }
    
    public static void setCurrentAppointment(Appointment appt){
        currentAppointment = appt;
    }
    
    public int getAppointmentId(){
        return appointmentId;
    }
    
    public void setAppointmentId(int appointmentId){
        this.appointmentId = appointmentId;
    }
    
    public int getCustomerId(){
        return customerId;
    }
    
    public void setCustomerId(int customerId){
        this.customerId = customerId;
    }
    
    public int getUserId(){
        return userId;
    }
    
    public void setUserId(int userId){
        this.userId = userId;
    }
    
    public String getTitle(){
        return title;
    }
    
    public void setTitle(String title){
        this.title = title;
    }
    
    public String getDescription(){
        return description;
    }
    
    public void setDescription(String description){
        this.description = description;
    }
    
    public String getLocation(){
        return location;
    }
    
    public void setLocation(String location){
        this.location = location;
    }
    
    public String getContact(){
        return contact;
    }
    
    public void setContact(String contact){
        this.contact = contact;
    }
    
    public String getType(){
        return type;
    }
    
    public void setType(String type){
        this.type = type;
    }
    
    public String getUrl(){
        return url;
    }
    
    public void setUrl(String url){
        this.url = url;
    }
    
    public LocalDateTime getStart(){
        return start;
    }
    
    public void setStart(LocalDateTime start){
        this.start = start;
    }
    
    public LocalDateTime getEnd(){
        return end;
    }
    
    public void setEnd(LocalDateTime end){
        this.end = end;
    }
    
    public LocalDateTime getCreateDate(){
        return createDate;
    }
    
    public void setCreateDate(LocalDateTime createDate){
        this.createDate = createDate;
    }
    
    public String getCreatedBy(){
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy){
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getLastUpdate(){
        return lastUpdate;
    }
    
    public void setLastUpdate(LocalDateTime lastUpdate){
        this.lastUpdate = lastUpdate;
    }
    
    public String getLastUpdateBy(){
        return lastUpdateBy;
    }
    
    public void setLastUpdateBy(String lastUpdateBy){
        this.lastUpdateBy = lastUpdateBy;
    }
    
    //</editor-fold>

    // Returns customer name of customer associated with appointment
    public String getCustomerName() throws SQLException {
        Customer customer = CustomerDaoImpl.getCustomer(customerId);
        String customerName = customer.getCustomerName();
        return customerName;
    }
    
    // Returns user-friendly date of appointment
    // Used in calendar table
    public String getDate(){
        return localDateTimeToUIDate(start);
    }
    
    // Returns user-friendly start time of appointment
    // Used in calendar table
    public String getStartTime(){
        return localDateTimeToUITime(start);
    }
    
    // Returns user-friendly end time of appointment
    // Used in calendar table
    public String getEndTime(){
        return localDateTimeToUITime(end);
    }
    
}
