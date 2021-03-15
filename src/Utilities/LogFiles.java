/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import Model.User;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Sets up Error Log and User Log
 * @author Austin Wong
 */
public class LogFiles {
    
    private static final Logger log = Logger.getLogger("errorlog.txt");
    
    // Sets up error log
    public static void setupLogger() {
        
        try {
            FileHandler fh = new FileHandler("errorlog.txt", true);
            SimpleFormatter sf = new SimpleFormatter();
            fh.setFormatter(sf);
            log.addHandler(fh);
        } catch (IOException | SecurityException e) {
            Logger.getLogger(LogFiles.class.getName()).log(Level.SEVERE, null, e);
        }

        log.setLevel(Level.CONFIG);
        
    }
    
    // Logs user activity to file
    public static void logUserActivity() {
        
        String filename = "userlog.txt";
        File file = new File(filename);
        String msg = "USER "+User.getCurrentUser().getUserName()+" has logged in at " + ZonedDateTime.now();
        
        if(!file.exists()){
            try(PrintWriter outputFile = new PrintWriter(filename)){
                outputFile.println(msg);
            }
            catch(FileNotFoundException e){
                log.log(Level.INFO,"Could not write to userlog.txt: {0}", msg);
            }
        }
        else{
            try(PrintWriter outputFile = new PrintWriter(new FileWriter(filename,true))){
                outputFile.println(msg);
            }
            catch(IOException e){
                log.log(Level.INFO,"Could not write to userlog.txt: {0}", msg);
            }
        }
    }
 
// Log Hierarchy
// SEVERE (highest)
// WARNING
// INFO
// CONFIG
// FINE
// FINER
// FINEST
}
