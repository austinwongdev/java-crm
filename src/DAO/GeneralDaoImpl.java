/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import static Utilities.TimeFiles.dbStrToLocalDateTime;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Common variables and methods for DAO classes
 * @author Austin Wong
 */
public class GeneralDaoImpl {
    
    protected static String createDate;
    protected static String createdBy;
    protected static String lastUpdate;
    protected static String lastUpdateBy;
    protected static LocalDateTime createDateLdt;
    protected static LocalDateTime lastUpdateLdt;
    
    protected static void getMetadata(ResultSet rs) throws SQLException {
        createDate = rs.getString("createDate");
        createdBy = rs.getString("createdBy");
        lastUpdate = rs.getString("lastUpdate");
        lastUpdateBy = rs.getString("lastUpdateBy");
        createDateLdt = dbStrToLocalDateTime(createDate);
        lastUpdateLdt = dbStrToLocalDateTime(lastUpdate);
    }    
}
