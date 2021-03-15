/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import static DAO.DBQuery.setPreparedStatement;
import Model.Customer;
import static Model.User.getCurrentUser;
import static Utilities.TimeFiles.dbStrNow;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Austin Wong
 */
public class CustomerDaoImpl extends GeneralDaoImpl{
    
    //<editor-fold defaultstate="collapsed" desc="data retrieval">
    
    // Retrieve customer from DB with customerId
    public static Customer getCustomer(int customerId) throws SQLException {
        
        Customer customerResult;
        String sqlStatement = "SELECT * FROM customer WHERE customerId = ?";
        PreparedStatement ps = setPreparedStatement(sqlStatement);
        ps.setInt(1, customerId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
            String customerName = rs.getString("customerName");
            int addressId = rs.getInt("addressId");
            Boolean active = rs.getBoolean("active");
            getMetadata(rs);
            customerResult = new Customer(customerId, customerName, addressId, active, createDateLdt, createdBy, lastUpdateLdt, lastUpdateBy);
            return customerResult;
        }
        
        return null;
    }
    
    
    // Retrieve customer from DB with customerName and addressId
    public static Customer getCustomer(String customerName, int addressId) throws SQLException {
        
        Customer customerResult;
        String sqlStatement = "SELECT * FROM customer WHERE LOWER(customerName) = LOWER(?) AND addressId = ?";
        PreparedStatement ps = setPreparedStatement(sqlStatement);
        ps.setString(1, customerName);
        ps.setInt(2, addressId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
            int customerId = rs.getInt("customerId");
            Boolean active = rs.getBoolean("active");
            getMetadata(rs);
            customerResult = new Customer(customerId, customerName, addressId, active, createDateLdt, createdBy, lastUpdateLdt, lastUpdateBy);
            return customerResult;
        }
        
        return null;
    }
    
    // Get a list of all active customers
    public static ObservableList<Customer> getAllActiveCustomers() throws SQLException {
        ObservableList<Customer> allCustomers=FXCollections.observableArrayList();
        
        String selectStatement="SELECT * FROM customer WHERE active = true ORDER by customerId";
        PreparedStatement ps = setPreparedStatement(selectStatement);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
            int customerId = rs.getInt("customerId");
            String customerName = rs.getString("customerName");
            int addressId = rs.getInt("addressId");
            Boolean active = rs.getBoolean("active");
            getMetadata(rs);
            Customer customerResult = new Customer(customerId, customerName, addressId, active, createDateLdt, createdBy, lastUpdateLdt, lastUpdateBy);
            customerResult.setAddressObj();
            allCustomers.add(customerResult);
        }
        
        return allCustomers;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="data setting">
    
    // Insert customer into DB
    public static Customer insertCustomer(String customerName, int addressId) throws SQLException {
        
        String createStatement = "INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES(?,?,?,?,?,?,?)";
        PreparedStatement ps = setPreparedStatement(createStatement);
        
        String userName = getCurrentUser().getUserName();
        String time = dbStrNow();
        
        ps.setString(1, customerName);
        ps.setInt(2, addressId);
        ps.setBoolean(3, true);
        ps.setString(4, time);
        ps.setString(5, userName);
        ps.setString(6, time);
        ps.setString(7, userName);
        ps.execute();
        
        return getCustomer(customerName, addressId);
    }    
    
    // Delete customer and linked appointments
    public static int deleteCustomer(int customerId) throws SQLException {
        String deleteStatement = "DELETE FROM appointment WHERE customerId = ?";
        PreparedStatement ps = setPreparedStatement(deleteStatement);

        ps.setInt(1, customerId);
        ps.execute();
        
        deleteStatement = "DELETE FROM customer WHERE customerId = ?";
        ps = setPreparedStatement(deleteStatement);
        ps.setInt(1, customerId);
        ps.execute();
        
        return ps.getUpdateCount();
    }
    
    // Update customer
    public static int updateCustomer(String customerName, int addressId, int customerId) throws SQLException {
        String updateStatement = "UPDATE customer SET customerName = ?, addressId = ? WHERE customerId = ?";
        PreparedStatement ps = setPreparedStatement(updateStatement);
        ps.setString(1, customerName);
        ps.setInt(2, addressId);
        ps.setInt(3, customerId);
        ps.execute();
        return ps.getUpdateCount();
    }
    
    //</editor-fold>
    
}
