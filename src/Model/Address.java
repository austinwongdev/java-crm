/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import DAO.CityDaoImpl;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 *
 * @author Austin Wong
 */
public class Address {
    
    //<editor-fold defaultstate="collapsed" desc="db variables">
    
    private int addressId;
    private String addressName;
    private String address2Name;
    private int cityId;
    private String postalCode;
    private String phone;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdateBy;
    
    //</editor-fold>
    
    private City cityObj; // City Object related to Address Object via cityId
    
    public Address(int addressId, String addressName, String address2Name, int cityId, String postalCode, String phone, LocalDateTime createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdateBy){
        this.addressId = addressId;
        this.addressName = addressName;
        this.address2Name = address2Name;
        this.cityId = cityId;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }
    
    //<editor-fold defaultstate="collapsed" desc="db variable setters and getters">
    
    public int getAddressId(){
        return addressId;
    }
    
    public void setAddressId(int addressId){
        this.addressId = addressId;
    }
    
    public String getAddressName(){
        return addressName;
    }
    
    public void setAddressName(String addressName){
        this.addressName = addressName;
    }
    
    public String getAddress2Name(){
        return address2Name;
    }
    
    public void setAddress2Name(String address2Name){
        this.address2Name = address2Name;
    }
    
    public int getCityId(){
        return cityId;
    }
    
    public void setCityId(int cityId){
        this.cityId = cityId;
    }
    
    public String getPostalCode(){
        return postalCode;
    }
    
    public void setPostalCode(String postalCode){
        this.postalCode = postalCode;
    }
    
    public String getPhone(){
        return phone;
    }
    
    public void setPhone(String phone){
        this.phone = phone;
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
    
    // </editor-fold>
    
    public City getCityObj(){
        return this.cityObj;
    }
    
    public void setCityObj() throws SQLException {
        this.cityObj = CityDaoImpl.getCity(cityId);
    }
    
}
