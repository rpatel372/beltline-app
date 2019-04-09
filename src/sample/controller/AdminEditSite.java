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

public class AdminEditSite {

    public TextField name;
    public TextField zipcode;
    public TextField address;
    public ComboBox manager;
    public CheckBox openEveryday;

    String previousPage;

    Site globalSite;

    public Label errorMessage;


    public void setSite(Site site) throws SQLException {
        globalSite = site;
        System.out.println(globalSite.getName());
        //add managers to dropdown that are not assigned to a site yet
        previousPage = Context.getInstance().currentPreviousPage();

        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = null;
        stmt = connection.createStatement();
        String sql = "CALL getManagersNotAssignedToSite('" + globalSite.getName() + "')";
        ObservableList<String> list = FXCollections.observableArrayList();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            list.add(rs.getString(1));
        }

        manager.getItems().addAll(list);


        //POPULATE DATA INTO FIELDS
        String sql3 = "CALL getCurrentSiteInfo('" + globalSite.getName() + "')";
        System.out.println(sql3);
        ResultSet rs2 = stmt.executeQuery(sql3);
        if (rs2.next()) {
            name.setText(rs2.getString("SiteName"));
            zipcode.setText(rs2.getString("SiteZipcode"));
            address.setText(rs2.getString("SiteAddress"));
            if (rs2.getString("OpenEveryday").equals("Yes")) {
                openEveryday.setSelected(true);
            }
            manager.setValue(rs2.getString("ManagerUsername"));
        }
    }

    public void editSite(ActionEvent actionEvent) throws SQLException {
        //make sure all fields are filled out
        if (name.getText().trim().equals("") || zipcode.getText().trim().equals("") || address.getText().trim().equals("")) {
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
                if (rs.getInt(0) > 0) {
                    errorMessage.setText("This site name is already in use!");
                } else {
                    //CALL EDIT SITE
                    String open = "";
                    if (openEveryday.isSelected()) {
                        open = "Yes";
                    } else {
                        open = "No";
                    }
                    String sql10 = "CALL editSite('" + globalSite.getName() + "', '" + name.getText().trim() + "', '" + zipcode.getText().trim()
                            + "', '" + address.getText().trim() + "', '" + manager.getValue().toString() + "', '"
                            + open + "')";
                    stmt.execute(sql10);
                    //pass in new site name into REFRESH PAGE (i.e. call setSite again) so new info can be displayed
                    errorMessage.setText("Site successfully edited.");
                    errorMessage.setTextFill(Color.web("#75c24e"));
//                Site newSite = new Site(name.getText().trim(), manager.getValue().toString(), open);
//                setSite(newSite);
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
