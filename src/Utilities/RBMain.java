/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Resource Bundle
 * Supports English and Spanish
 * Defaults to English
 * @author Austin Wong
 */
public class RBMain {
    
    private static ResourceBundle rb;
    
    public static void setRb(){
        
        // Default to English if neither English nor Spanish is detected
        if(!(Locale.getDefault().getLanguage().equals("en") || Locale.getDefault().getLanguage().equals("es")))
            Locale.setDefault(Locale.ENGLISH);
        
        //Locale.setDefault(new Locale("es")); // ***AAW: Test Spanish locale (Not in place of but in addition to changing system settings)
        
        rb = ResourceBundle.getBundle("Utilities/Nat",Locale.getDefault());        
        
    }
    
    public static ResourceBundle getRb(){
        return rb;
    }
}
