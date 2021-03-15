/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import DAO.CustomerDaoImpl;
import Model.Customer;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controls screen for viewing customers
 *
 * @author Austin Wong
 */
public class ViewCustomerScreenController extends GeneralController implements Initializable {

    //<editor-fold defaultstate="collapsed" desc="ui-variables">
    
    @FXML
    private TableColumn<?, ?> phoneCol;

    @FXML
    private TableColumn<?, ?> addressCol;

    @FXML
    private Button updateBtn;

    @FXML
    private TableColumn<?, ?> cityCol;

    @FXML
    private TableView<Customer> customerTableView;

    @FXML
    private TableColumn<?, ?> postalCodeCol;

    @FXML
    private Button scheduleApptBtn;
    
    @FXML
    private Button addBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private TableColumn<?, ?> countryCol;

    @FXML
    private TableColumn<?, ?> idCol;

    @FXML
    private TableColumn<?, ?> nameCol;

    @FXML
    private Button backBtn;

    @FXML
    private TableColumn<?, ?> address2Col;
    
    //</editor-fold>
    
    private final ObservableList<Customer> Customers = FXCollections.observableArrayList();
    private TableView.TableViewSelectionModel<Customer> tvSelCustomer;

    // Query the DB to repopulate table
    private void refreshTable(){
        Customers.clear();
        try{
            Customers.addAll(CustomerDaoImpl.getAllActiveCustomers());
        }
        catch(SQLException e){
            Logger.getLogger("errorlog.txt").log(Level.WARNING,null,e);
            displayErrorAlert("Error retrieving customers from database");
        }
        customerTableView.setItems(Customers);
        tvSelCustomer = customerTableView.getSelectionModel();
    }
    
    private void selectionError(){
        displayErrorAlert("Select a customer first");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Set this as return screen
        lastScreen = AppScreen.VIEWCUSTOMERSCREEN;
        
        //Populate Table
        idCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        address2Col.setCellValueFactory(new PropertyValueFactory<>("address2"));
        postalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        cityCol.setCellValueFactory(new PropertyValueFactory<>("city"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        refreshTable();
    }
    
    //<editor-fold defaultstate="collapsed" desc="actions">
    
    // Schedule appointment for selected customer
    @FXML
    void onActionScheduleAppt(ActionEvent event) {
        if(tvSelCustomer.isEmpty())
            selectionError();
        else{
            Customer.setCurrentCustomer(tvSelCustomer.getSelectedItem());
            displayScreen(event, "/View/AddAppointmentScreen.fxml");
        }
    }
    
    // Add a new customer
    @FXML
    void onActionAddCustomer(ActionEvent event) {
        displayScreen(event, "/View/AddCustomerScreen.fxml");
    }

    // Update an existing customer
    @FXML
    void onActionUpdateCustomer(ActionEvent event) {
        if(tvSelCustomer.isEmpty()){
            selectionError();
        }
        else{
            Customer.setCurrentCustomer(tvSelCustomer.getSelectedItem());
            displayScreen(event, "/View/UpdateCustomerScreen.fxml");
        }
    }

    // Delete selected customer
    @FXML
    void onActionDeleteCustomer(ActionEvent event) {
        if(tvSelCustomer.isEmpty()){
            selectionError();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Deleting customer will delete all appointments with customer. Continue?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait()
                .filter(res -> res == ButtonType.YES) // Using lambda to create specific event handling if user clicks YES. Not going to reuse this elsewhere.
                .ifPresent(res -> {
                    try {
                        CustomerDaoImpl.deleteCustomer(tvSelCustomer.getSelectedItem().getCustomerId());
                        Customer.setCurrentCustomer(null);
                        displayNotification(event, "Customer deleted");
                        refreshTable();
                    }
                    catch (SQLException e) {
                        Logger.getLogger("errorlog.txt").log(Level.WARNING, null, e);
                        displayErrorAlert("Error deleting customer from database");
                    }
                });
        }
    }

    // Return to previous screen
    @FXML
    void onActionReturn(ActionEvent event) {
        displayScreen(event, "/View/MainMenu.fxml");
    }
    
    //</editor-fold>
    
}
