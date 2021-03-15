/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import static DAO.DBQuery.setPreparedStatement;
import Model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Austin Wong
 */
public class UserDaoImpl extends GeneralDaoImpl {

    //Compare username and password to those in DB
    public static Boolean logIn(String userName, String password) throws SQLException {
        
        String sqlStatement = "select * FROM user WHERE userName = ? AND password = ? AND active=TRUE";
        PreparedStatement ps = setPreparedStatement(sqlStatement);

        ps.setString(1, userName);
        ps.setString(2, password);
        ps.execute();
        ResultSet rs = ps.getResultSet();

        if (rs.next()){
            //Set currentUser
            rs.getMetaData();
            User.setCurrentUser(new User(rs.getInt("userId"),userName,"",true,createDateLdt,createdBy,lastUpdateLdt,lastUpdateBy)); // No need to hang onto password
            return true;
        }
        else
            return false;
    }
    
    // Returns a list of all active users
    public static ObservableList<User> getAllActiveUsers() throws SQLException {
        
        ObservableList<User> allUsers = FXCollections.observableArrayList();
        
        String sqlStatement = "SELECT * FROM user WHERE active = true ORDER BY userName";
        PreparedStatement ps = setPreparedStatement(sqlStatement);

        ps.execute();
        ResultSet rs = ps.getResultSet();

        while(rs.next()){
            int userId = rs.getInt("userId");
            String userName = rs.getString("userName");
            String password = ""; // Leave password blank - no need for it
            Boolean active = rs.getBoolean("active");
            rs.getMetaData();
            User user = new User(userId,userName,password,active,createDateLdt,createdBy,lastUpdateLdt,lastUpdateBy);
            
            allUsers.add(user);
        }
        
        return allUsers;
    }
}