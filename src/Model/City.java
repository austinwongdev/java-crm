/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import DAO.CountryDaoImpl;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 *
 * @author Austin Wong
 */
public class City {
    
    //<editor-fold defaultstate="collapsed" desc="db variables">
    private int cityId;
    private String cityName;
    private int countryId;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdateBy;
    //</editor-fold>
    
    private Country countryObj; // Country Object related to City Object via countryId
    
    public City(int cityId, String cityName, int countryId, LocalDateTime createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdateBy){
        this.cityId = cityId;
        this.cityName = cityName;
        this.countryId = countryId;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }
    
    //<editor-fold defaultstate="collapsed" desc="db variable setters and getters">
    
    public int getCityId(){
        return cityId;
    }
    
    public void setCityId(int cityId){
        this.cityId = cityId;
    }
    
    public String getCityName(){
        return cityName;
    }
    
    public void setCityName(String cityName){
        this.cityName = cityName;
    }
    
    public int getCountryId(){
        return countryId;
    }
    
    public void setCountryId(int countryId){
        this.countryId = countryId;
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
    
    public Country getCountryObj(){
        return this.countryObj;
    }
    
    public void setCountryObj() throws SQLException {
        this.countryObj = CountryDaoImpl.getCountry(countryId);
    }
    
}
