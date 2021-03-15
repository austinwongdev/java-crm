/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import static DAO.DBQuery.setPreparedStatement;
import Model.Address;
import static Model.User.getCurrentUser;
import static Utilities.TimeFiles.dbStrNow;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @author Austin Wong
 */
public class AddressDaoImpl extends GeneralDaoImpl {    
    
    //<editor-fold defaultstate="collapsed" desc="data retrieval">
    
    // Retrieve address from DB using addressId
    public static Address getAddress(int addressId) throws SQLException  {
        
        Address addressResult;
        String sqlStatement = "SELECT * FROM address WHERE addressId = ?";
        PreparedStatement ps = setPreparedStatement(sqlStatement);
        ps.setInt(1, addressId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
            String address = rs.getString("address");
            String address2 = rs.getString("address2");
            int cityId = rs.getInt("cityId");
            String postalCode = rs.getString("postalCode");
            String phone = rs.getString("phone");
            getMetadata(rs);
            
            addressResult = new Address(addressId, address, address2, cityId, postalCode, phone, createDateLdt, createdBy, lastUpdateLdt, lastUpdateBy);
            return addressResult;
        }
        return null;        
    }
    
    // Retrieve address from DB using all fields except addressId
    public static Address getAddress(String address, String address2, int cityId, String postalCode, String phone) throws SQLException  {
        
        Address addressResult;
        String sqlStatement = "SELECT * FROM address WHERE LOWER(address) = LOWER(?) AND LOWER(address2) = LOWER(?) AND cityId = ? AND postalCode = ? AND phone = ?";
        PreparedStatement ps = setPreparedStatement(sqlStatement);
        ps.setString(1, address);
        ps.setString(2, address2);
        ps.setInt(3, cityId);
        ps.setString(4, postalCode);
        ps.setString(5, phone);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
            int addressId = rs.getInt("addressId");
            getMetadata(rs);
            addressResult = new Address(addressId, address, address2, cityId, postalCode, phone, createDateLdt, createdBy, lastUpdateLdt, lastUpdateBy);
            return addressResult;
        }
        
        return null;
    }
    
    //</editor-fold>
    
    // Insert address into DB
    public static Address insertAddress(String address, String address2, int cityId, String postalCode, String phone) throws SQLException {
        
        String createStatement = "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES(?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = setPreparedStatement(createStatement);
        
        String userName = getCurrentUser().getUserName();
        String time = dbStrNow();
        
        ps.setString(1, address);
        ps.setString(2, address2);
        ps.setInt(3, cityId);
        ps.setString(4,postalCode);
        ps.setString(5,phone);
        ps.setString(6, time);
        ps.setString(7, userName);
        ps.setString(8, time);
        ps.setString(9, userName);
        ps.execute();
        
        return getAddress(address, address2, cityId, postalCode, phone);
    }
}
