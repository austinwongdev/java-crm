/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

/**
 *
 * @author Austin Wong
 */
public class BusinessException extends Exception {
    
    public BusinessException(){
        super();
    }
    
    public BusinessException(Exception e){
        super(e);
    }
    
    public BusinessException(String message){
        super(message);
    }
    
}
