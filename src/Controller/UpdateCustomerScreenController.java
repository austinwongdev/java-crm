/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.AddressDaoImpl;
import DAO.CityDaoImpl;
import DAO.CountryDaoImpl;
import DAO.CustomerDaoImpl;
import Model.Address;
import Model.City;
import Model.Country;
import Model.Customer;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Controls logic for updating customer
 *
 * @author Austin Wong
 */
public class UpdateCustomerScreenController extends GeneralController implements Initializable {

    		
    //<editor-fold defaultstate="collapsed" desc="ui-variables">
    
    @FXML
    private TextField customerNameTxt;
    @FXML
    private TextField addressTxt;
    @FXML
    private TextField address2Txt;
    @FXML
    private TextField postalCodeTxt;
    @FXML
    private TextField cityTxt;
    @FXML
    private TextField countryTxt;
    @FXML
    private TextField phoneTxt;
    @FXML
    private Button saveBtn;
    @FXML
    private Button backBtn;
    @FXML
    private Label errorLbl;
    
    //</editor-fold>

    private Customer customer;
    
    //<editor-fold defaultstate="collapsed" desc="actions">
    
    // Save updated customer
    @FXML
    private void onActionSave(ActionEvent event) {
        submit(event);
    }

    // Return to previous screen
    @FXML
    private void onActionReturn(ActionEvent event) {
        back(event);
    }
    
    @FXML
    private void onKeyPressedSubmit(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            submit(event);
        }
    }
    
    //</editor-fold>
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Pre-populate fields
        customer = Customer.getCurrentCustomer();
        customerNameTxt.setText(customer.getCustomerName());
        addressTxt.setText(customer.getAddress());
        address2Txt.setText(customer.getAddress2());
        postalCodeTxt.setText(customer.getPostalCode());
        cityTxt.setText(customer.getCity());
        countryTxt.setText(customer.getCountry());
        phoneTxt.setText(customer.getPhone());
        
    }    
    
    // Updates DB with updated customer data
    private void updateDB(Event event) {
        
        // Gather input text
        String countryName = countryTxt.getText();
        String cityName = cityTxt.getText();
        String addressName = addressTxt.getText();
        String address2Name = address2Txt.getText();
        String postalCode = postalCodeTxt.getText();
        String phone = phoneTxt.getText();
        String customerName = customerNameTxt.getText();
        
        // Attempt to update customer to DB
        try{

            // Look for existing country in DB, if not found create one
            // (we don't want to change the country for a given countryId b/c it may affect other records)
            Country country = CountryDaoImpl.getCountry(countryName);
            if(!(country instanceof Country))
                country = CountryDaoImpl.insertCountry(countryName);

            // Look for existing city in DB, if not found create one
            City city = CityDaoImpl.getCity(cityName, country.getCountryId());
            if(!(city instanceof City))
                city = CityDaoImpl.insertCity(cityName, country.getCountryId());

            // Look for existing address in DB, if not found create one
            // If only the phone # changes, we still create a new row because
            //    there could be multiple customers at the same physical location (office building)
            //    with different phone #s, so changing the phone # for a given addressId might have
            //    unintended effects for other customers with the same addressId
            Address address = AddressDaoImpl.getAddress(addressName, address2Name, city.getCityId(), postalCode, phone);
            if(!(address instanceof Address))
                address = AddressDaoImpl.insertAddress(addressName, address2Name, city.getCityId(), postalCode, phone);

            // Update customerName and addressId
            CustomerDaoImpl.updateCustomer(customerName, address.getAddressId(), customer.getCustomerId());
            displayNotification(event, "Customer updated");
            back(event);
        }
        catch (SQLException e){
            Logger.getLogger("errorlog.txt").log(Level.WARNING,null,e);
            displayErrorAlert("Error updating customer to database");
        }
    }  
    
    private void submit(Event event){
        
        if(checkFields(customerNameTxt,addressTxt,postalCodeTxt,cityTxt,countryTxt,phoneTxt)){
            displayMessage(errorLbl,errorStr);
            return;
        }
        
        assert (!(customerNameTxt.getText().isEmpty()));
        
        updateDB(event);
    }
    
    private void back(Event event){
        Customer.setCurrentCustomer(null);
        returnToLastScreen(event);
    }
}
