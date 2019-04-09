package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.Transit;
import sample.model.User;

import java.io.IOException;
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
    public TableColumn<Transit, Integer> connectedCol;

    public ChoiceBox transportOptions;
    public ChoiceBox siteOptions;
    public ChoiceBox sortBySelection;

    public TextField lowPrice;
    public TextField highPrice;
    public TextField transitDateInput;

    public Label errorMessage;

    public String previousPage = "";



    // HAVE THESE AS GLOBAL VARIABLES SO WHEN USER SELECTS TO SORT BY, YOU CAN USE THESE INSTEAD OF THE CURRENT VALUES OF THE
    // TEXTFIELD IN CASE THE USER DID NOT CLICK THE FILTER BUTTON YET
    Object tOption = "";
    String lPrice = "0";
    String hPrice = "9999999";
    Object sOption = "";
    String filterBy = "TransitRoute";
    String ascOrDesc = "ASC";

    User globalUser;


    @FXML
    protected void initialize() {
        globalUser = Context.getInstance().currentUser();
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

        boolean numeric = true;
        Double num1 = 0.0;
        Double num2 = 0.0;
        try {
            num1= Double.parseDouble(lPrice);
        } catch (NumberFormatException e) {
            numeric = false;
        }
        try {
            num2 = Double.parseDouble(hPrice);
        } catch (NumberFormatException e) {
            numeric = false;
        }

        if (numeric == false) {
            errorMessage.setText("Price filters must be numbers!");
        } else if (num2 < num1) {
            errorMessage.setText("Higher price filter must be higher than lower price filter.");
        }else {
            errorMessage.setText("");
            filter();
        }


    }

    public void logTransit(ActionEvent actionEvent) throws SQLException {
        if (transitTable.getSelectionModel().getSelectedItem() != null) {
            Transit selectedTransit = transitTable.getSelectionModel().getSelectedItem();
            System.out.println(selectedTransit.getRoute());

            //TODO: check to ese that date is in the correct format
//            for (int i = 0; i < transitDateInput.getText().length(); i++) {
//
//            }
            if (!transitDateInput.getText().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
                errorMessage.setText("Date is not in correct format! Follow format indicated.");
            } else {

                //TODO: log the transit (need to have user passed in)
                List<Transit> transitsToAdd = new ArrayList<Transit>();
                ConnectionClass connectionClass = new ConnectionClass();
                Connection connection = connectionClass.getConnection();
                Statement stmt = null;
                try {
                    stmt = connection.createStatement();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                String sql2 = "CALL checkIfTransitAlreadyTaken('" + globalUser.username + "', '" + selectedTransit.getType() + "', '" + selectedTransit.getRoute() + "', '" + transitDateInput.getText() + "')";
                ResultSet rs = null;
                try {
                    rs = stmt.executeQuery(sql2);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                int count = 0;
                if (rs.next()) {
                    count = rs.getInt("COUNT(*)");
                }

                if (count != 0) {
                    errorMessage.setText("You have already taken this transit today. You can only take the same transit once a day.");
                } else {
                    String sql = "CALL logTransit('" + globalUser.username + "', '" + selectedTransit.getType() + "', '" + selectedTransit.getRoute() + "', '" + transitDateInput.getText() + "')";
                    System.out.println(sql);
                    try {
                        stmt.execute(sql);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                    errorMessage.setText("Transit successfully logged.");
                    errorMessage.setTextFill(Color.web("#75c24e"));
                }
            }

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "CALL getFilteredTransits('" + tOption + "', '" + lPrice + "', '" + hPrice + "', '" + sOption + "', '" + filterBy + "', '" + ascOrDesc + "')";

        try {

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                Transit newTransit = new Transit(rs.getString("TransitRoute"), rs.getString("TransitType"), rs.getDouble("TransitPrice"), rs.getInt("memberCount"), 0);
                transitsToAdd.add(newTransit);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        transitTable.getItems().addAll(transitsToAdd);

    }

    public void goBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(previousPage));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/userTakeTransit.fxml";
            root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sortByParam(ActionEvent actionEvent) {
        //1. Take current values in the filter fi;ter by these
        if (sortBySelection.getValue() != null) {
            filterBy = sortBySelection.getValue().toString().split(" ")[0];
            ascOrDesc = sortBySelection.getValue().toString().split(" ")[1];
        }
        System.out.println(filterBy);
        //2. Sort by the parameter type
        filter();
        //NOTE TO SELF: involves changing the values of the TableView (maybe make this a separate method to avoid code reuse?)
    }

}
