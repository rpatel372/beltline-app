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
import sample.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VisitorVisitHistory{
    public String previousPage;
    public User globalUser;

    public TextField event;
    public ChoiceBox site;
    public TextField startDate;
    public TextField endDate;
    public ChoiceBox sortBy;

    String eventValue = "";
    String siteValue = "";
    String startDateValue = "1000-01-01";
    String endDateValue = "9999-12-31";
    String sorting = "";

    public TableView<VisitHistoryEntry> visitHistory;
    public TableColumn dateCol;
    public TableColumn eventCol;
    public TableColumn siteCol;
    public TableColumn priceCol;

    public Label errorMessage;

    public class VisitHistoryEntry {
        public String date;
        public String event;
        public String site;
        public double price;
        public VisitHistoryEntry(String d, String e, String s, double p) {
            date = d;
            event = e;
            site = s;
            price = p;
        }
        public String getDate() {
            return date;
        }
        public String getEvent() {
            return event;
        }
        public String getSite() {
            return site;
        }
        public double getPrice() {
            return price;
        }
    }

    public void initialize() throws SQLException {
        previousPage = Context.getInstance().currentPreviousPage();
        globalUser = Context.getInstance().currentUser();

        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = null;
        stmt = connection.createStatement();
        String sql = "CALL getSitesForDropdown()";
        ObservableList<String> list = FXCollections.observableArrayList();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            list.add(rs.getString(1));
        }
        list.add("All");
        site.setItems(list);
    }

    public void addToTable() throws SQLException {
        errorMessage.setText("");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        eventCol.setCellValueFactory(new PropertyValueFactory<>("event"));
        siteCol.setCellValueFactory(new PropertyValueFactory<>("site"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        visitHistory.getItems().clear();
        List<VisitHistoryEntry> entries = new ArrayList<VisitHistoryEntry>();
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();

        Statement stmt = connection.createStatement();
        String sql = "CALL filterVisitHistory('" + globalUser.username + "', '" + startDateValue
                + "', '" + endDateValue + "', '" + eventValue + "', '"
                + siteValue + "', '" + sorting + "')";

        System.out.println(sql);

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            VisitHistoryEntry entry = new VisitHistoryEntry(rs.getString(1), rs.getString(2),
                    rs.getString(3), rs.getDouble(4));
            entries.add(entry);
        }
        visitHistory.getItems().addAll(entries);
    }

    public void filter(ActionEvent actionEvent) throws SQLException {
        if (!startDate.getText().equals("") &&
                !startDate.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
            errorMessage.setText("Start date is not in the correct format.");
        }
        else if (!endDate.getText().equals("") &&
                !endDate.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
            errorMessage.setText("Start date is not in the correct format.");
        } else {
            if (startDate.getText().trim().equals("")) {
                startDateValue = "1000-01-01";
            } else {
                startDateValue = startDate.getText().trim();
            }
            if (endDate.getText().trim().equals("")) {
                endDateValue = "9999-12-31";
            } else {
                endDateValue = endDate.getText().trim();
            }
            if (event.getText().trim().equals("")) {
                eventValue = "";
            } else {
                eventValue = event.getText().trim();
            }
            if (site.getValue() == null || site.getValue().toString().equals("All")) {
                siteValue = "";
            } else {
                siteValue = site.getValue().toString().trim();
            }
            addToTable();
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
            Context.getInstance().previousPage = "../view/visitorVisitHistory.fxml";
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
