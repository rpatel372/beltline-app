package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import sample.connectivity.ConnectionClass;
import sample.model.Transit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserTakeTransit {

    public TableView<Transit> transitTable;

    public TableColumn<Transit, String> typeCol;
    public TableColumn<Transit, String> routeCol;
    public TableColumn<Transit, Integer> priceCol;
    public TableColumn<Transit, String> connectedCol;

    public ChoiceBox transportOptions;
    public ChoiceBox siteOptions;
    public ChoiceBox sortBySelection;

    public TextField lowPrice;
    public TextField highPrice;
    public TextField transitDateInput;

    public Label errorMessage;


    public static Transit chosenTransit = null;

    // HAVE THESE AS GLOBAL VARIABLES SO WHEN USER SELECTS TO SORT BY, YOU CAN USE THESE INSTEAD OF THE CURRENT VALUES OF THE
    // TEXTFIELD IN CASE THE USER DID NOT CLICK THE FILTER BUTTON YET
    Object tOption = "";
    String lPrice = "0";
    String hPrice = "9999999";
    Object sOption = "";
    String filterBy = "TransitRoute";
    String ascOrDesc = "ASC";

    @FXML
    protected void initialize() {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = "CALL getSitesForDropdown()";
        ObservableList<String> list = FXCollections.observableArrayList();

        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getString("SiteName"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        list.add("All");
        list.add("Other");
        siteOptions.getItems().addAll(list);

    }
    public void filter(ActionEvent actionEvent) {

        if (transportOptions.getValue() != null && !transportOptions.getValue().equals("All")) {
            tOption = transportOptions.getValue();
        } else {
            tOption = "";
        }
        if (!lowPrice.getText().trim().equals("")) {
            lPrice = lowPrice.getText();
        } else {
            lPrice = "0";
        }
        if (!highPrice.getText().trim().equals("")) {
            hPrice = highPrice.getText();
        } else {
            hPrice = "999999";
        }
        if (siteOptions.getValue() != null && !siteOptions.getValue().equals("All")) {
            sOption = siteOptions.getValue();
        } else {
            sOption = "";
        }

        filter();



    }

    public void logTransit(ActionEvent actionEvent) {
        if (transitTable.getSelectionModel().getSelectedItem() != null) {
            Transit selectedTransit = transitTable.getSelectionModel().getSelectedItem();
            System.out.println(selectedTransit.getRoute());

            //TODO: check to ese that date is in the correct format

            //TODO: log the transit (need to have user passed in)

            errorMessage.setText("Transit successfully logged.");
            errorMessage.setTextFill(Color.web("#75c24e"));

        } else {
            errorMessage.setText("You must select a transit!");
        }
    }

    public void filter() {
        transitTable.getItems().clear();
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        routeCol.setCellValueFactory(new PropertyValueFactory<>("route"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        connectedCol.setCellValueFactory(new PropertyValueFactory<>("connected"));

        List<Transit> transitsToAdd = new ArrayList<Transit>();
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = null;
        Statement stmt2 = null;
        try {
            stmt = connection.createStatement();
            stmt2 = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "CALL getFilteredTransits('" + tOption + "', '" + lPrice + "', '" + hPrice + "', '" + sOption + "', '" + filterBy + "', '" + ascOrDesc + "')";

        try {

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String sql2 = "CALL getNumConnectedSitesForTakeTransit('" + rs.getString("TransitRoute") + "', '" + rs.getString("TransitType") + "')";
                ResultSet rs2 = stmt2.executeQuery(sql2);
                System.out.println(sql2);
                while (rs2.next()) {
                    int count = rs2.getInt("count(*)");
                    Transit newTransit = new Transit(rs.getString("TransitRoute"), rs.getString("TransitType"), rs.getDouble("TransitPrice"), count);
                    transitsToAdd.add(newTransit);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        transitTable.getItems().addAll(transitsToAdd);

    }

    public void goBack(ActionEvent actionEvent) {

    }

    public void sortByParam(ActionEvent actionEvent) {
        //1. Take current values in the filter fi;ter by these
        if (sortBySelection.getValue() != null) {
            filterBy = sortBySelection.getValue().toString().split(" ")[0];
            ascOrDesc = sortBySelection.getValue().toString().split(" ")[1];
        }
        //2. Sort by the parameter type
        filter();
        //NOTE TO SELF: involves changing the values of the TableView (maybe make this a separate method to avoid code reuse?)
    }

}
