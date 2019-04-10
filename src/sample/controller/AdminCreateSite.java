package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.Site;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminCreateSite {
    public TextField name;
    public TextField zipcode;
    public TextField address;
    public ComboBox manager;
    public CheckBox openEveryday;

    String previousPage;

    public Label errorMessage;

    public void initialize() throws SQLException {
        previousPage = Context.getInstance().currentPreviousPage();

        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = null;
        stmt = connection.createStatement();
        String sql = "CALL getManagersNotAssignedToSite('" + "')";
        ObservableList<String> list = FXCollections.observableArrayList();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            list.add(rs.getString(1));
        }
        manager.getItems().addAll(list);
    }

    public void createSite(ActionEvent actionEvent) throws SQLException {

        if (name.getText().trim().equals("") || zipcode.getText().trim().equals("")  || manager.getValue() == null) {
            errorMessage.setText("You must fill out all fields!");
        } else if (!zipcode.getText().matches("[0-9]{5}")) { //make sure zipcode is five digits
            errorMessage.setText("Zip code must be 5 digits.");
        } else {
            //make sure Site Name is unique
            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.getConnection();
            Statement stmt = connection.createStatement();
            String sql = "CALL checkIfSiteNameUnique('" + name.getText().trim() + "')";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                if (rs.getInt(1) > 0) {
                    errorMessage.setText("This site name is already in use!");
                } else {
                    //CALL EDIT SITE
                    String open = "";
                    String add = "";
                    if (openEveryday.isSelected()) {
                        open = "Yes";
                    } else {
                        open = "No";
                    }
                    if (address.getText().trim().equals("")) {
                        add = "NULL";
                    } else {
                        add = "'" + address.getText().trim() + "'";
                    }
                    String sql10 = "CALL addSite('" + name.getText().trim() + "', '" + zipcode.getText().trim()
                            + "', " + add + ", '" + manager.getValue().toString() + "', '"
                            + open + "')";
                    stmt.execute(sql10);
                    //pass in new site name into REFRESH PAGE (i.e. call setSite again) so new info can be displayed
                    errorMessage.setText("Site successfully created.");
                    errorMessage.setTextFill(Color.web("#75c24e"));
                }
            }
        }
    }

    public void goBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(previousPage));
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
