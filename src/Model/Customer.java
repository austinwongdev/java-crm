/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import DAO.AddressDaoImpl;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 *
 * @author Austin Wong
 */
public class Customer {
    
    //<editor-fold defaultstate="collapsed" desc="db variables">
    private int customerId;
    private String customerName;
    private int addressId;
    private boolean active;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdateBy;
    //</editor-fold>
    
    private Address addressObj;
    
    //<editor-fold defaultstate="collapsed" desc="static variables and methods">
    private static Customer currentCustomer;
    
    public static Customer getCurrentCustomer(){
        return currentCustomer;
    }
    
    public static void setCurrentCustomer(Customer customer){
        currentCustomer = customer;
    }
    //</editor-fold>
    
    // Constructor
    public Customer(int customerId, String customerName, int addressId, boolean active, LocalDateTime createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdateBy){
        this.customerId = customerId;
        this.customerName = customerName;
        this.addressId = addressId;
        this.active = active;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }
    
    //<editor-fold defaultstate="collapsed" desc="db variable setters and getters">
    
    public int getCustomerId(){
        return customerId;
    }
    
    public void setCustomerId(int customerId){
        this.customerId = customerId;
    }
    
    public String getCustomerName(){
        return customerName;
    }
    
    public void setCustomerName(String customerName){
        this.customerName = customerName;
    }
    
    public int getAddressId(){
        return addressId;
    }
    
    public void setAddressId(int addressId){
        this.addressId = addressId;
    }
    
    public boolean isActive(){
        return active;
    }
    
    public void setActive(boolean active){
        this.active = active;
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
    
    //<editor-fold defaultstate="collapsed" desc="address getters">
    public String getAddress(){
        return this.addressObj.getAddressName();
    }
    
    public String getPhone(){
        return this.addressObj.getPhone();
    }
    
    public String getCity(){
        return this.addressObj.getCityObj().getCityName();
    }
    
    public String getAddress2(){
        return this.addressObj.getAddress2Name();
    }
    
    public String getPostalCode(){
        return this.addressObj.getPostalCode();
    }
    
    public String getCountry(){
        return this.addressObj.getCityObj().getCountryObj().getCountryName();
    }
    
    //</editor-fold>
    
    public void setAddressObj() throws SQLException {
        this.addressObj = AddressDaoImpl.getAddress(addressId);
        this.addressObj.setCityObj();
        this.addressObj.getCityObj().setCountryObj();
    }
    
    // Override for ComboBox display
    @Override
    public String toString(){
        String displayName = customerName + " [" + customerId + "]";
        return displayName;
    }    
}
