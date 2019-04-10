package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
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

public class AdminEditTransit {

    public Label type;
    public TextField  route;
    public TextField price;
    public ListView<String> connectedSites;
    public Label errorMessage;

    public ArrayList<String> initiallyAdded = new ArrayList<String>();
    
    Transit globalTransit;

    public void initializeInfo(Transit selectedTransit) throws SQLException {
        globalTransit = selectedTransit;

        System.out.println(globalTransit.getType() + " " + globalTransit.getRoute());
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

        String sql3 = "CALL getCurrentTransitInfo('" + globalTransit.getType() + "', '" + globalTransit.getRoute() + "')";
        System.out.println(sql3);
        ResultSet rs2 = stmt.executeQuery(sql3);
        if (rs2.next()) {
            type.setText(rs2.getString(1));
            route.setText(rs2.getString(2));
            price.setText(rs2.getString(3));
        }
        initiallyAdded.clear();
        String sql8 = "CALL getConnectedSites('" + globalTransit.getType() + "', '" + globalTransit.getRoute() + "')";
        ResultSet rs6 = stmt.executeQuery(sql8);
        connectedSites.getSelectionModel().clearSelection();
        connectedSites.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        while (rs6.next()) {
            connectedSites.getSelectionModel().select(rs6.getString(1));
            initiallyAdded.add(rs6.getString(1));
        }
    }
    public void update(ActionEvent actionEvent) throws SQLException {
        System.out.println(connectedSites.getSelectionModel().getSelectedIndices().size());
        if (route.getText().trim().equals("") || price.getText().trim().equals("")) {
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
                    int decimalLength = splitter[1].length();  // After Decimal Count
                    if (decimalLength == 2 || decimalLength == 1) {
                        ConnectionClass connectionClass = new ConnectionClass();
                        Connection connection = connectionClass.getConnection();
                        Statement stmt = connection.createStatement();
                        if (!globalTransit.getRoute().equals(route.getText().trim())) {
                            String sql = "CALL checkIfTransitUnique('" + type.getText().trim() + "', '" + route.getText().trim() + "')";
                            ResultSet rs = stmt.executeQuery(sql);
                            if (rs.next()) {
                                if (rs.getInt(1) > 0) {
                                    errorMessage.setText("This combo of route and type is already in use!");
                                    canYouAdd = false;
                                }
                            }
                        }

                        //allow for edit site
                        if (canYouAdd) {
                            String sql10 = "CALL editTransit('" + globalTransit.getType() + "', '"
                                    + globalTransit.getRoute() + "', '"
                                    + route.getText().trim() + "', '" + price.getText().trim() + "')";
                            stmt.execute(sql10);

                            //TODO: delete all unselected connected sites
                            ArrayList<String> toRemove = new ArrayList<String>();
                            for (String t : initiallyAdded) {
                                if (!connectedSites.getSelectionModel().getSelectedItems().contains(t)) {
                                    toRemove.add(t);
                                    System.out.println(t);
                                }
                            }


                            //                        System.out.println("***********************");
                            //TODO: add all selected connected sites
                            ArrayList<String> toAdd = new ArrayList<String>();
                            for (String t : connectedSites.getSelectionModel().getSelectedItems()) {
                                if (!initiallyAdded.contains(t)) {
                                    toAdd.add(t);
                                    System.out.println(t);
                                }
                            }

                            for (String t : toAdd) { //add connected sites
                                String sql11 = "CALL addConnectedSite('" + t + "', '"
                                        + type.getText().trim() + "', '" + route.getText().trim() + "')";
                                stmt.execute(sql11);
                            }
                            for (String t : toRemove) {//remove deselected connected sites
                                String sql12 = "CALL removeConnectedSite('" + t + "', '"
                                        + type.getText().trim() + "', '" + route.getText().trim() + "')";
                                stmt.execute(sql12);
                            }
                            errorMessage.setText("Transit successfully edited.");
                            errorMessage.setTextFill(Color.web("#75c24e"));

                            Transit newTransit = new Transit(route.getText().trim(), type.getText().trim(), 0, 0, 0);
                            initializeInfo(newTransit);
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
