/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import static DAO.DBQuery.setPreparedStatement;
import Model.City;
import static Model.User.getCurrentUser;
import static Utilities.TimeFiles.dbStrNow;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Austin Wong
 */
public class CityDaoImpl extends GeneralDaoImpl{
    
    //<editor-fold defaultstate="collapsed" desc="data retrieval">
    
    // Retrieve city from DB using cityId
    public static City getCity(int cityId) throws SQLException {
        
        City cityResult;
        String sqlStatement = "SELECT * FROM city WHERE cityId = ?";
        PreparedStatement ps = setPreparedStatement(sqlStatement);
        ps.setInt(1, cityId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
            String city = rs.getString("city");
            int countryId = rs.getInt("countryId");
            getMetadata(rs);
            
            cityResult = new City(cityId, city, countryId, createDateLdt, createdBy, lastUpdateLdt, lastUpdateBy);
            return cityResult;
        }
        return null;        
    }
    
    // Retrieve city from DB using cityName and countryId
    public static City getCity(String cityName, int countryId) throws SQLException {
        
        City cityResult;
        String sqlStatement = "select * FROM city WHERE LOWER(city) = LOWER(?) AND countryId = ?";
        PreparedStatement ps = setPreparedStatement(sqlStatement);
        ps.setString(1, cityName);
        ps.setInt(2, countryId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
            int cityId = rs.getInt("cityId");
            String city = rs.getString("city");
            getMetadata(rs);
            cityResult = new City(cityId, city, countryId, createDateLdt, createdBy, lastUpdateLdt, lastUpdateBy);
            return cityResult;
        }
        
        return null;
    }
    
    //</editor-fold>
    
    // Insert city into DB
    public static City insertCity(String cityName, int countryId) throws SQLException {
        
        String createStatement = "INSERT INTO city (city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES(?,?,?,?,?,?)";
        PreparedStatement ps = setPreparedStatement(createStatement);
        
        String userName = getCurrentUser().getUserName();
        String time = dbStrNow();
        
        ps.setString(1, cityName);
        ps.setInt(2, countryId);
        ps.setString(3, time);
        ps.setString(4, userName);
        ps.setString(5, time);
        ps.setString(6, userName);
        ps.execute();
        
        return getCity(cityName, countryId);
    }
}
