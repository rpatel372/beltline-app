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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.Site;
import sample.model.Transit;

import java.io.IOException;
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(previousPage));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/adminManageTransit.fxml";
            root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void create(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/adminCreateTransit.fxml"));
        Parent root = null;
        try {
            root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void edit(ActionEvent actionEvent) throws IOException, SQLException {
        if (transitTable.getSelectionModel().getSelectedItem() != null) {
            //NAVIGATE TO edit site page & pass info to fxml file of site selected

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/adminEditTransit.fxml"));
            Parent root = null;
            Transit transit =  transitTable.getSelectionModel().getSelectedItem();
            root = (Parent)fxmlLoader.load();
            AdminEditTransit controller = fxmlLoader.<AdminEditTransit>getController();
            controller.initializeInfo(transit);


            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();



        } else {
            errorMessage.setText("Must select a site to edit!");
        }
    }

    public void delete(ActionEvent actionEvent) throws SQLException {
        if (transitTable.getSelectionModel().getSelectedItem() != null) {
            Transit deletingTransit = transitTable.getSelectionModel().getSelectedItem();
            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.getConnection();
            Statement stmt = null;
            stmt = connection.createStatement();
            // key is a combo of (Type, Route)
            String sql = "CALL deleteTransit('" + deletingTransit.getType() + "', '" + deletingTransit.getRoute() + "')";
            stmt.execute(sql);
            errorMessage.setText("Tranist successfully deleted.");
            errorMessage.setTextFill(Color.web("#75c24e"));
            addToTable();
        } else {
            errorMessage.setText("You must select a transit to delete!");
        }
    }

    public void sort(ActionEvent actionEvent) {
        if (sortBySelection.getValue() != null) {
            sorting = sortBySelection.getValue().toString();
            try {
                addToTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
