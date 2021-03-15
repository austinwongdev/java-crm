/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.AddressDaoImpl;
import DAO.AppointmentDaoImpl;
import DAO.CityDaoImpl;
import DAO.CountryDaoImpl;
import DAO.CustomerDaoImpl;
import DAO.UserDaoImpl;
import Model.Address;
import Model.Appointment;
import Model.City;
import Model.Country;
import Model.Customer;
import static Utilities.TimeFiles.localDateTimeToUITime;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Austin Wong
 */
public class TestConditions {
    
    public static ObservableList<Customer> testCustomers = FXCollections.observableArrayList();
    public static ObservableList<Appointment> testAppointments = FXCollections.observableArrayList();
    
    public static void deleteTestCustomers() throws SQLException {
        
        for(Customer c : testCustomers){
            CustomerDaoImpl.deleteCustomer(c.getCustomerId());
        }
        
    }
    
    public static void deleteAppointments() throws SQLException {
        
        for (Appointment a : testAppointments){
            AppointmentDaoImpl.deleteAppointment(a.getAppointmentId());
        }
        
    }
    
    public static void cleanUp() throws SQLException {
        
        deleteAppointments();
        deleteTestCustomers();
        
    }
    
    public static void createTestCustomer() throws SQLException, FileNotFoundException, IOException {
        
        // SET USER
        UserDaoImpl.logIn("test", "test");
        
        // CUSTOMER PROFILE
        
        //Randomize suffix
        int rand = ThreadLocalRandom.current().nextInt(100);
        String customerName = "John Wick " + rand;
        String countryName = "USA";
        String cityName = "Houston";
        String addressName = "956 Main St";
        String address2Name = "Apt No. 3";
        String postalCode = "80210";
        String phone = "854-908-7889";
        
        
        // ADD CUSTOMER
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
            customer = CustomerDaoImpl.insertCustomer(customerName, address.getAddressId());
        }
        
        
        // SET CUSTOMER
        Customer.setCurrentCustomer(customer);
        
        
        // APPOINTMENT SETTINGS
        
        //Randomize Time and Date
        int randMonth = ThreadLocalRandom.current().nextInt(1,13);
        int randDay = ThreadLocalRandom.current().nextInt(1,29);
        int year = 2020;
        int randMin = ThreadLocalRandom.current().nextInt(2)*30;
        int randHour = ThreadLocalRandom.current().nextInt(8,17);
        
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.of(year, randMonth, randDay), LocalTime.of(randHour, randMin));
        LocalDateTime endDateTime = startDateTime.plusMinutes(30);
        String title = "Meet and Greet";
        String description = "Meet others, greet others";
        String location = "Office";
        String contact = "No";
        String type = "Casual";
        String url = "Not Applicable";
        
        // ADD APPOINTMENT
        Appointment appointment = AppointmentDaoImpl.getAppointment(startDateTime, endDateTime);
        if(!(appointment instanceof Appointment)){
            appointment = AppointmentDaoImpl.insertAppointment(title, description, location, contact, type, url, startDateTime, endDateTime);
        }
        
        // SET APPOINTMENT
        Appointment.setCurrentAppointment(appointment);
        
        // ADD TO LIST
        testCustomers.add(customer);
        testAppointments.add(appointment);
        
        System.out.println(customer.getCustomerName() + " : " + localDateTimeToUITime(appointment.getStart()));
    }  
}
