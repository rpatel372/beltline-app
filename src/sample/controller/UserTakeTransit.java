package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
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

    public TextField lowPrice;
    public TextField highPrice;
    public TextField transitDateInput;

    public Label errorMessage;

    public static Transit chosenTransit = null;

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
        siteOptions.setItems(list);

    }
    public void filter(ActionEvent actionEvent) {
        transitTable.getItems().clear();
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        routeCol.setCellValueFactory(new PropertyValueFactory<>("route"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        connectedCol.setCellValueFactory(new PropertyValueFactory<>("connected"));

        //TODO: use a view to get the results and populate the table
        //Reference: https://www.youtube.com/watch?v=luO2MWjUqe4

        //TODO: use the FILTER command to filter results
        //Route, Transport Type, & Price come from Transit table
        //# of Connected Sites comes from connect table, number of places that have this combo of route & transport type

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
        Object tOption = "";
        String lPrice = "0";
        String hPrice = "9999999";
        System.out.println(lowPrice.getText());
        if (transportOptions.getValue() != null && !transportOptions.getValue().equals("All")) {
            tOption = transportOptions.getValue();
        }
        if (!lowPrice.getText().trim().equals("")) {
            lPrice = lowPrice.getText();
        }
        if (!highPrice.getText().trim().equals("")) {
            hPrice = highPrice.getText();
        }

        String sql = "CALL getFilteredTransits('" + tOption + "', '" + lPrice + "', '" + hPrice + "')";
        System.out.println(sql);


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

        //TODO: put radio buttons next to each option
        transitTable.getItems().addAll(transitsToAdd);


    }

    public void logTransit(ActionEvent actionEvent) {
        if (transitTable.getSelectionModel().getSelectedItem() != null) {
            Transit selectedTransit = transitTable.getSelectionModel().getSelectedItem();
            System.out.println(selectedTransit.getRoute());
        } else {
            errorMessage.setText("You must select a transit!");
        }
    }

    public void goBack(ActionEvent actionEvent) {

    }


}
