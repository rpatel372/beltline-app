package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.Event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManagerManageStaff {
    public String previousPage;

    public ChoiceBox siteDropdown;
    public TextField firstField;
    public TextField lastField;
    public TextField startField;
    public TextField endField;

    public ChoiceBox sortBy;

    public TableView<Staff> staffTable;
    public TableColumn nameCol;
    public TableColumn shiftsCol;

    String site = "";
    String firstName = "";
    String lastName = "";
    String startDate =  "1000-01-01";
    String endDate = "9999-12-31";
    String sorting  = "";

    public Label errorMessage;

    public class Staff {
        String staffName;
        int numShifts;
        public Staff(String n, int s) {
            staffName = n;
            numShifts = s;
        }
        public String getStaffName() { return staffName; }
        public int getNumShifts() { return numShifts; }
    }

    public void initialize() throws SQLException {
        previousPage = Context.getInstance().currentPreviousPage();

        //TODO: populate site dropdown
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = null;
        stmt = connection.createStatement();

        String sql = "CALL getSitesForDropdown()";
        ObservableList<String> list = FXCollections.observableArrayList();

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            list.add(rs.getString("SiteName"));
        }
        list.add("All");
        siteDropdown.getItems().addAll(list);
    }

    public void addToTable() throws SQLException {
        errorMessage.setText("");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("staffName"));
        shiftsCol.setCellValueFactory(new PropertyValueFactory<>("numShifts"));

        staffTable.getItems().clear();
        List<Staff> staffToAdd = new ArrayList<Staff>();

        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();

        Statement stmt = connection.createStatement();
        String sql = "CALL filterStaff('" + site + "', '" + firstName
                + "', '" + lastName + "', '" + startDate + "', '"
                + endDate + "', '" + sorting + "')";

        System.out.println(sql);

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Staff newStaff =
                    new Staff(rs.getString(1) + " " + rs.getString(2), rs.getInt(3));
            staffToAdd.add(newStaff);
        }
        staffTable.getItems().addAll(staffToAdd);

    }
    public void filter(ActionEvent actionEvent) throws SQLException, ParseException {
        boolean canYouAdd = true;

        if  (!startField.getText().trim().equals("") && !startField.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")){
            errorMessage.setText("Start Date is not in correct format! Please follow format listed.");
            canYouAdd = false;
        } else if (startField.getText().trim().equals("")) {
            startDate = "1000-01-01";
        } else {
            startDate = startField.getText().trim();
        }

        if (!endField.getText().trim().equals("") && !endField.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
            errorMessage.setText("End Date is not in correct format! Please follow format listed.");
            canYouAdd = false;
        } else if (endField.getText().trim().equals(""))  {
            endDate = "9999-12-31";
        } else {
            endDate = endField.getText().trim();
        }


        if (canYouAdd) {
            //TODO: make sure start date comes before end date
            Date sdate = new SimpleDateFormat("yyyy-MM-dd").parse(startField.getText().trim());
            Date edate = new SimpleDateFormat("yyyy-MM-dd").parse(endField.getText().trim());
            if (sdate.compareTo(edate) > 0) {
                errorMessage.setText("End date must come after start date!");
            } else {
                if (firstField.getText().trim().equals("")) {
                    firstName = "";
                } else {
                    firstName = firstField.getText().trim();
                }
                if (lastField.getText().trim().equals("")) {
                    lastName = "";
                } else {
                    lastName = lastField.getText().trim();
                }
                if (siteDropdown.getValue() == null || siteDropdown.getValue().toString().equals("All")) {
                    site = "";
                } else {
                    site = siteDropdown.getValue().toString().trim();
                }
                addToTable();
            }
        }
    }

    public void sort(ActionEvent actionEvent) throws SQLException {
        if (sortBy.getValue() != null) {
            sorting = sortBy.getValue().toString();
            addToTable();
        }
    }

    public void goBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(previousPage));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/managerManageStaff.fxml";
            root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
