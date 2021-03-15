/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author Austin Wong
 */
public class Report {
    
    // Variables
    private String reportName;
    private int reportId;
    
    // Constructor
    public Report(String reportName, int reportId){
        this.reportName = reportName;
        this.reportId = reportId;
    }
    
    //<editor-fold defaultstate="collapsed" desc="setters and getters">
    public String getReportName(){
        return reportName;
    }
    
    public int getReportId(){
        return reportId;
    }
    
    public void setReportName(String reportName){
        this.reportName = reportName;
    }
    
    public void setReportId(int reportId){
        this.reportId = reportId;
    }
    
    //</editor-fold>
    
    // Override for ComboBox display
    @Override
    public String toString(){
        String displayName = reportName + " [" + reportId + "]";
        return reportName;
    }
}
