package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.Transit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AdminManageTransit {

    public TableView<Transit> transitTable;

    public TableColumn<Transit, String> typeCol;
    public TableColumn<Transit, String> routeCol;
    public TableColumn<Transit, Integer> priceCol;
    public TableColumn<Transit, Integer> connectedCol;
    public TableColumn<Transit, Integer> loggedCol;

    public ChoiceBox transportOptions;
    public ChoiceBox siteOptions;
    public ChoiceBox sortBySelection;

    public TextField lowPrice;
    public TextField highPrice;
    public TextField route;

    public Label errorMessage;

    public String previousPage = "";

    String tOption = "";
    String lPrice = "0";
    String hPrice = "9999999";
    String sOption = "";
    String sorting = "Route ASC";
    String rOption = "";


    public void initialize() {

        previousPage = Context.getInstance().currentPreviousPage();
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
        siteOptions.getItems().addAll(list);

    }

    public void addToTable() throws SQLException {
        transitTable.getItems().clear();
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        routeCol.setCellValueFactory(new PropertyValueFactory<>("route"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        connectedCol.setCellValueFactory(new PropertyValueFactory<>("connected"));
        loggedCol.setCellValueFactory(new PropertyValueFactory<>("transitsLogged"));

        List<Transit> transitsToAdd = new ArrayList<Transit>();
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = null;
        stmt = connection.createStatement();

        String sql = "CALL manageTransits('" + tOption + "', '" + lPrice + "', '" + hPrice + "', '" + rOption + "', '" + sOption + "', '" + sorting + "')";
        System.out.println(sql);


        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Transit newTransit = new Transit(rs.getString(1), rs.getString(2),
                    rs.getDouble(3), rs.getInt(4), rs.getInt(5));
            transitsToAdd.add(newTransit);

        }

        transitTable.getItems().addAll(transitsToAdd);

    }

    public void filter(ActionEvent actionEvent) throws SQLException {

        if (transportOptions.getValue() != null && !transportOptions.getValue().toString().equals("All")) {
            tOption = transportOptions.getValue().toString();
        } else {
            tOption="";
        }
        if (siteOptions.getValue() != null && !siteOptions.getValue().toString().equals("All")) {
            sOption = siteOptions.getValue().toString();
        } else {
            sOption = "";
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
        if (!route.getText().trim().equals("")) {
            rOption = route.getText();
        } else {
            rOption = "";
        }
        addToTable();

    }

    public void goBack(ActionEvent actionEvent) {
    }

    public void create(ActionEvent actionEvent) {
    }

    public void edit(ActionEvent actionEvent) {
    }

    public void delete(ActionEvent actionEvent) {
    }

    public void sort(ActionEvent actionEvent) {
        if (sortBySelection.getValue() != null) {
            sorting = sortBySelection.getValue().toString();
        }
    }
}
