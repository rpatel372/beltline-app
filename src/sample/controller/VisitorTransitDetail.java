package sample.controller;

import javafx.event.ActionEvent;
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

public class VisitorTransitDetail {
    public String previousPage;
    public User globalUser;

    public Label siteName;
    public ChoiceBox transportType;
    public ChoiceBox sortBy;
    public TextField transitDate;

    public TableView<Transit> transitDetail;
    public TableColumn routeCol;
    public TableColumn transportCol;
    public TableColumn priceCol;
    public TableColumn connectedSitesCol;

    String transportValue = "";
    String filterBy = "TransitRoute";
    String ascOrDesc = "ASC";

    public Label errorMessage;


    public void initialize() {
        previousPage = Context.getInstance().currentPreviousPage();
        globalUser = Context.getInstance().currentUser();
    }
    public void initializeInfo(String sname) {
        siteName.setText(sname);
        transitDetail.getItems().clear();
        routeCol.setCellValueFactory(new PropertyValueFactory<>("route"));
        transportCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        connectedSitesCol.setCellValueFactory(new PropertyValueFactory<>("connected"));

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
        String sql = "CALL getFilteredTransits('" + transportValue
                + "', '" + 0 + "', '" + 9999999 + "', '" + sname + "', '" + "" +  filterBy + "', '" + ascOrDesc + "')";
        System.out.println(sql);
        try {

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                Transit newTransit = new Transit(rs.getString("TransitRoute"), rs.getString("TransitType"), rs.getDouble("TransitPrice"), rs.getInt("memberCount"), 0);
                transitsToAdd.add(newTransit);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        transitDetail.getItems().addAll(transitsToAdd);
    }
    public void filter(ActionEvent actionEvent) {
        if (transportType.getValue() == null || transportType.getValue().toString().equals("All")) {
            transportValue = "";
        } else {
            transportValue = transportType.getValue().toString();
        }
        initializeInfo(siteName.getText());
    }
    public void sort(ActionEvent actionEvent) {
        //1. Take current values in the filter fi;ter by these
        if (sortBy.getValue() != null) {
            filterBy = sortBy.getValue().toString().split(" ")[0];
            ascOrDesc = sortBy.getValue().toString().split(" ")[1];
            initializeInfo(siteName.getText());
        }
    }

    public void logTransit(ActionEvent actionEvent) throws SQLException {
        if (transitDetail.getSelectionModel().getSelectedItem() != null) {
            Transit selectedTransit = transitDetail.getSelectionModel().getSelectedItem();
            System.out.println(selectedTransit.getRoute());
            if (transitDate.getText().trim().equals("")) {
                errorMessage.setText("You must fill in transit date before logging transit.");
            } else {
                if (!transitDate.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
                    errorMessage.setText("Date is not in correct format! Follow format indicated.");
                } else {
                    //TODO: log the transit (need to have user passed in)
                    ConnectionClass connectionClass = new ConnectionClass();
                    Connection connection = connectionClass.getConnection();
                    Statement stmt = null;
                    try {
                        stmt = connection.createStatement();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    String sql2 = "CALL checkIfTransitAlreadyTaken('" + globalUser.username + "', '" + selectedTransit.getType()
                            + "', '" + selectedTransit.getRoute() + "', '" + transitDate.getText().trim() + "')";
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
                        String sql = "CALL logTransit('" + globalUser.username + "', '"
                                + selectedTransit.getType() + "', '" + selectedTransit.getRoute() + "', '"
                                + transitDate.getText().trim() + "')";
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
            }
        } else {
            errorMessage.setText("You must select a transit!");
        }
    }

    public void goBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/visitorExploreSite.fxml"));
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
}