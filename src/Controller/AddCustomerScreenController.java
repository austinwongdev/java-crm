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
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Controls screen for adding customers
 *
 * @author Austin Wong
 */
public class AddCustomerScreenController extends GeneralController {

    //<editor-fold defaultstate="collapsed" desc="ui-variables">
    @FXML
    private TextField countryTxt;

    @FXML
    private TextField phoneTxt;

    @FXML
    private TextField addressTxt;

    @FXML
    private TextField cityTxt;

    @FXML
    private TextField customerNameTxt;

    @FXML
    private TextField address2Txt;

    @FXML
    private TextField postalCodeTxt;

    @FXML
    private Button saveBtn;

    @FXML
    private Button backBtn;
        
    @FXML
    private Label errorLbl;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="actions">
    
    // Save new customer to DB
    @FXML
    void onActionSave(ActionEvent event) {
        submit(event);
    }

    // Return to previous screen
    @FXML
    void onActionReturn(ActionEvent event) {
        back(event);
    }
    
    @FXML
    void onKeyPressedSubmit(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            submit(event);
        }
    }
    //</editor-fold>
    
    private void submit(Event event){
        // Address2 not required - many people do not have an address that requires filling that line out
        if(checkFields(customerNameTxt,addressTxt,postalCodeTxt,cityTxt,countryTxt,phoneTxt)){
            displayMessage(errorLbl,errorStr);
            return;
        }
        
        assert (!customerNameTxt.getText().isEmpty());

        // Gather input text
        String countryName = countryTxt.getText();
        String cityName = cityTxt.getText();
        String addressName = addressTxt.getText();
        String address2Name = address2Txt.getText();
        String postalCode = postalCodeTxt.getText();
        String phone = phoneTxt.getText();
        String customerName = customerNameTxt.getText();
        
        // Attempt to save customer to DB
        try{
            
            // Look for existing country in DB, if not found create one
            Country country = CountryDaoImpl.getCountry(countryName);
            if(!(country instanceof Country))
                country = CountryDaoImpl.insertCountry(countryName);

            // Look for existing city in DB, if not found create one
            City city = CityDaoImpl.getCity(cityName, country.getCountryId());
            if(!(city instanceof City))
                city = CityDaoImpl.insertCity(cityName, country.getCountryId());

            // Look for existing address in DB, if not found create one
            Address address = AddressDaoImpl.getAddress(addressName, address2Name, city.getCityId(), postalCode, phone);
            if(!(address instanceof Address))
                address = AddressDaoImpl.insertAddress(addressName, address2Name, city.getCityId(), postalCode, phone);

            // Look for existing customer in DB, if not found create one
            Customer customer = CustomerDaoImpl.getCustomer(customerName, address.getAddressId());
            if(!(customer instanceof Customer)){
                CustomerDaoImpl.insertCustomer(customerName, address.getAddressId());
                displayNotification(event, "Customer successfully added");
                back(event);
            }
            else
                displayMessage(errorLbl,"Customer already exists");
        }
        catch (SQLException e){
            displayErrorAlert(e.getMessage());
        }
    }    
    
    private void back(Event event){
        Customer.setCurrentCustomer(null);
        returnToLastScreen(event);
    }
}
