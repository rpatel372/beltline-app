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
import sample.model.TranHist;
import sample.model.Transit;
import sample.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransitHistory {

    public ChoiceBox containsSite;
    public ChoiceBox transportType;
    public TextField route;
    public TextField startDate;
    public TextField endDate;

    public TableView<TranHist> transitHistory;
    public TableColumn dateCol;
    public TableColumn routeCol;
    public TableColumn transportTypeCol;
    public TableColumn priceCol;

    public Label errorMessage;
    User globalUser;
    String previousPage;

    public ChoiceBox sortBy;
    Object ttype;
    Object sname;
    String rname;
    String sdate;
    String edate;
    Object sorting;

    public void initialize() {
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
        list.add("Other");
        containsSite.getItems().addAll(list);
    }

    public void filter(ActionEvent actionEvent) throws SQLException {

        ttype = transportType.getValue();
        sname = containsSite.getValue();
        rname = route.getText();
        sdate = startDate.getText();
        edate = endDate.getText();


        if (ttype == null) {
            ttype = "";
        }
        if (sname == null) {
            sname = "";
        }
        if (sdate.equals("")) {
            sdate = "1000-01-01";
        }
        if (edate.equals("")) {
            edate = "9999-12-31";
        }

        if (!edate.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})") || !sdate.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
            errorMessage.setText("Date is not in correct format! Please follow format listed.");
        } else {
            filterByParam();
        }

    }

    public void sort(ActionEvent actionEvent) throws SQLException {
        sorting = sortBy.getValue();
        filterByParam();
    }

    public void filterByParam() throws SQLException {

        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        routeCol.setCellValueFactory(new PropertyValueFactory<>("route"));
        transportTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        transitHistory.getItems().clear();
        List<TranHist> transitsToAdd = new ArrayList<TranHist>();
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();

        Statement stmt = connection.createStatement();
        String sql = "CALL getTransitHistory('" + globalUser.username + "', '" + ttype
                + "', '" + sname + "', '" + rname + "', '" + sdate + "', '" + edate + "', '" + sorting + "')";

        System.out.println(sql);

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            System.out.println("blah");
            TranHist newTransit =
                    new TranHist(rs.getString("TransitDate"), rs.getString("TransitRoute"),
                            rs.getString("TransitType"), rs.getDouble("TransitPrice"));
            transitsToAdd.add(newTransit);
        }
        System.out.println(transitsToAdd.toString());
        transitHistory.getItems().addAll(transitsToAdd);

    }

    public void goBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(previousPage));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/transitHistory.fxml";
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
