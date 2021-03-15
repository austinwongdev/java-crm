/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Commonly used functions for creating, adjusting, and formatting LocalDateTimes for both application and database use
 * @author Austin Wong
 */
public class TimeFiles {
    
    private static final DateTimeFormatter DBDATETIMEFORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    private static final DateTimeFormatter UITIMEFORMAT = DateTimeFormatter.ofPattern("h:mm a");
    private static final DateTimeFormatter UIDATEFORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
    
    // Takes a LocalDate and a String representing time in "h:mm a" format
    // Returns a LocalDateTime
    public static LocalDateTime createLocalDateTime(LocalDate date, String time12){
        LocalDateTime ldt = LocalDateTime.of(date, LocalTime.parse(time12, UITIMEFORMAT));
        return ldt;
    }
    
    // Takes a LocalDateTime
    // Returns a DB-ready String representing timestamp in UTC
    public static String localDateTimeToDBStr(LocalDateTime ldtUser){
        ZonedDateTime zdtUser = ZonedDateTime.of(ldtUser, TimeZone.getDefault().toZoneId());
        ZonedDateTime zdtUtc = zdtUser.withZoneSameInstant(ZoneId.of("UTC"));
        String dbStr = zdtUtc.format(DBDATETIMEFORMAT);
        return dbStr;
    }
    
    // Takes a String representing DB timestamp in UTC
    // Returns a LocalDateTime in User's timezone, automatically taking into account Daylight vs Standard time
    public static LocalDateTime dbStrToLocalDateTime(String dbStr){
        LocalDateTime ldtUtc = LocalDateTime.parse(dbStr, DBDATETIMEFORMAT);
        ZonedDateTime zdtUtc = ZonedDateTime.of(ldtUtc, ZoneId.of("UTC"));
        ZonedDateTime zdtUser = zdtUtc.withZoneSameInstant(TimeZone.getDefault().toZoneId());
        LocalDateTime ldtUser = zdtUser.toLocalDateTime();
        return ldtUser;
    }
    
    // Takes a LocalDateTime
    // Returns a String representing time in "h:mm a" format
    public static String localDateTimeToUITime(LocalDateTime ldt){
        String uiTime = ldt.format(UITIMEFORMAT);
        return uiTime;
    }
    
    // Takes a LocalDateTime
    // Returns a String representing date in "M/d/yyyy" format
    public static String localDateTimeToUIDate(LocalDateTime ldt){
        String uiDate = ldt.format(UIDATEFORMAT);
        return uiDate;
    }
    
    // Returns a list of appointment times from 8:00am - 5:00pm in half-hour increments
    public static ObservableList<String> getAvailableAppointmentTimes(){
        ObservableList<String> ltList = FXCollections.observableArrayList();
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(17, 1); // .isBefore on next line is exclusive (< not <=) so we add a minute to include 17:00
        
        for(LocalTime lt = startTime; lt.isBefore(endTime); lt = lt.plusMinutes(30)){
            ltList.add(lt.format(UITIMEFORMAT));
        }
        return ltList;
    }
    
    // Returns a string representing current date and time in UTC in DB format
    public static String dbStrNow(){
        return localDateTimeToDBStr(LocalDateTime.now());
    }
    
    // Takes a LocalDateTime representing start date of search period
    // Returns a list of Strings representing start and end LocalDateTimes with either a rolling year or a calendar year
    // 
    // Rolling year: FROM 1 year before start TO start
    // Calendar year: FROM 1st day of start year TO last day of start year
    //
    // Note: rolling year calculates up to today, not through the end of the month
    public static ObservableList<String> getSearchTimes(LocalDateTime start, Boolean rolling){
        
        ObservableList<String> searchTimes = FXCollections.observableArrayList();
        String startSearch;
        String endSearch;

        // Rolling Year
        if(rolling == true){
            startSearch = localDateTimeToDBStr(start.minusYears(1));
            endSearch = localDateTimeToDBStr(start);
        }
        // Calendar Year
        else{
            LocalDate ld = start.toLocalDate();
            LocalDateTime ldtFirstOfYear = LocalDateTime.of(ld.withDayOfYear(1), LocalTime.MIN);
            LocalDateTime ldtLastOfYear = LocalDateTime.of(ld.withDayOfYear(ld.lengthOfYear()), LocalTime.MAX);
            startSearch = localDateTimeToDBStr(ldtFirstOfYear);
            endSearch = localDateTimeToDBStr(ldtLastOfYear);
        }  
        
        searchTimes.addAll(startSearch, endSearch);
        
        return searchTimes;
    }
    
}
