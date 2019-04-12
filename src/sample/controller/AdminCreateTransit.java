package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sample.connectivity.ConnectionClass;
import sample.model.Transit;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AdminCreateTransit {

    public ChoiceBox type;
    public TextField route;
    public TextField price;
    public ListView<String> connectedSites;
    public Label errorMessage;


    public void initialize() throws SQLException {

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
        connectedSites.setItems(list);
        connectedSites.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    public void create(ActionEvent actionEvent) throws SQLException {
        System.out.println(connectedSites.getSelectionModel().getSelectedIndices().size());
        if (route.getText().trim().equals("") || price.getText().trim().equals("") || type.getValue() == null) {
            errorMessage.setText("All fields are required!");
        } else if (connectedSites.getSelectionModel().getSelectedIndices().size() < 2) {
            errorMessage.setText("You must select at least 2 connected sites!");
        } else {
            boolean canYouAdd = true;
            try {
                double d = Double.parseDouble(price.getText().trim());
            } catch (NumberFormatException | NullPointerException nfe) {
                canYouAdd = false;
                errorMessage.setText("Your number does not have the correct format.");
            }
            if (canYouAdd) {

                if (Double.parseDouble(price.getText().trim()) < 0) {
                    errorMessage.setText("You cannot have a negative price.");
                } else {
                    String[] splitter = price.getText().trim().toString().split("\\.");
                    splitter[0].length();   // Before Decimal Count
                    int decimalLength = 0;
                    if (splitter.length > 1) {
                        decimalLength = splitter[1].length();  // After Decimal Count
                    }
                    if (decimalLength == 2 || decimalLength == 1 || decimalLength == 0) {
                        ConnectionClass connectionClass = new ConnectionClass();
                        Connection connection = connectionClass.getConnection();
                        Statement stmt = connection.createStatement();

                        String sql = "CALL checkIfTransitUnique('" + type.getValue().toString() + "', '" + route.getText().trim() + "')";
                        ResultSet rs = stmt.executeQuery(sql);
                        if (rs.next()) {
                            if (rs.getInt(1) > 0) {
                                errorMessage.setText("This combo of route and type is already in use!");
                                canYouAdd = false;
                            }
                        }


                        //allow for edit site
                        if (canYouAdd) {
                            if (!route.getText().trim().matches("[a-zA-Z0-9]+")) {
                                String sql10 = "CALL addTransit('" + type.getValue().toString() + "', '" + route.getText().trim() + "', '" + price.getText().trim() + "')";
                                stmt.execute(sql10);


                                for (String t : connectedSites.getSelectionModel().getSelectedItems()) { //add connected sites
                                    String sql11 = "CALL addConnectedSite('" + t + "', '"
                                            + type.getValue().toString() + "', '" + route.getText().trim() + "')";
                                    stmt.execute(sql11);
                                }
                                errorMessage.setText("Transit successfully added.");
                                errorMessage.setTextFill(Color.web("#75c24e"));
                            } else {
                                errorMessage.setText("Route must be numeric or alphanumeric only");
                            }
                        }


                    } else {
                        errorMessage.setText("Money is not in correct format");
                    }
                }
            }
        }
    }

    public void goBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/adminManageTransit.fxml"));
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
