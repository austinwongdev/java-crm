/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import static DAO.DBQuery.setPreparedStatement;
import Model.Country;
import static Model.User.getCurrentUser;
import static Utilities.TimeFiles.dbStrNow;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Austin Wong
 */
public class CountryDaoImpl extends GeneralDaoImpl {
    
    //<editor-fold defaultstate="collapsed" desc="data retrieval">
    
    // Retrieve country from DB using countryId
    public static Country getCountry(int countryId) throws SQLException {
        
        Country countryResult;
        String sqlStatement = "select * FROM country WHERE countryId = ?";
        PreparedStatement ps = setPreparedStatement(sqlStatement);
        ps.setInt(1, countryId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
            String country = rs.getString("country");
            getMetadata(rs);
            countryResult = new Country(countryId, country, createDateLdt, createdBy, lastUpdateLdt, lastUpdateBy);
            return countryResult;
        }
        
        return null;
        
    }
    
    // Retrieve country from DB using countryName
    public static Country getCountry(String countryName) throws SQLException {
        
        Country countryResult;
        String sqlStatement = "select * FROM country WHERE lower(country) = lower(?)";
        PreparedStatement ps = setPreparedStatement(sqlStatement);
        ps.setString(1, countryName);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
            int countryId = rs.getInt("countryId");
            String country = rs.getString("country");
            getMetadata(rs);
            countryResult = new Country(countryId, country, createDateLdt, createdBy, lastUpdateLdt, lastUpdateBy);
            return countryResult;
        }
        
        return null;
    }
    
    //</editor-fold>
    
    // Insert country into DB
    public static Country insertCountry(String countryName) throws SQLException {
        
        String createStatement = "INSERT INTO country (country,createDate,createdBy,lastUpdate,lastUpdateBy) VALUES(?,?,?,?,?)";
        PreparedStatement ps = setPreparedStatement(createStatement);
        
        String userName = getCurrentUser().getUserName();
        String time = dbStrNow();
        
        ps.setString(1, countryName);
        ps.setString(2, time);
        ps.setString(3, userName);
        ps.setString(4, time);
        ps.setString(5, userName);
        ps.execute();
        
        return getCountry(countryName);
        
    }
}
