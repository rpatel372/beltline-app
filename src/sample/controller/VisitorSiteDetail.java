package sample.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VisitorSiteDetail {
    public String previousPage;
    public User globalUser;

    public Label site;
    public Label openEveryday;
    public Label address;
    public TextField visitDate;
    public Label errorMessage;

    public void initialize() {
        previousPage = Context.getInstance().currentPreviousPage();
        globalUser = Context.getInstance().currentUser();
    }
    public void initializeInfo(String sname) throws SQLException {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = null;
        stmt = connection.createStatement();
        String sql3 = "CALL getCurrentSiteInfo('" + sname + "')";
        System.out.println(sql3);
        ResultSet rs2 = stmt.executeQuery(sql3);
        if (rs2.next()) {
            String addressValue = "";
            if (rs2.getString("SiteAddress") == null) {
                addressValue = "";
            } else {
                addressValue = rs2.getString("SiteAddress");
            }
            site.setText(rs2.getString("SiteName"));
            address.setText(addressValue + " " + rs2.getString("SiteZipcode"));
            openEveryday.setText(rs2.getString("OpenEveryday"));
        }
    }

    public void logVisit(ActionEvent actionEvent) throws SQLException {
        if (visitDate.getText().trim().equals("")) {
            errorMessage.setText("You must fill in a visit date to log visit.");
        } else if (!visitDate.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
            errorMessage.setText("Date has incorrect format. Please follow format listed.");
        } else {
            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.getConnection();
            Statement stmt = connection.createStatement();

            String sql2 = "CALL checkIfSiteAlreadyLoggedToday('" + site.getText() + "', '" + globalUser.username
                    + "', '" + visitDate.getText().trim() + "')";
            ResultSet rs = stmt.executeQuery(sql2);
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            if (count != 0) {
                errorMessage.setText("You can only visit the same site once a day!");
            } else {
                //log visit
                String sql = "CALL logVisitToSite('" + site.getText() + "', '"
                        + globalUser.username + "', '" + visitDate.getText().trim() + "')";
                System.out.println(sql);
                stmt.execute(sql);
                errorMessage.setText("Site visit successfully logged.");
                errorMessage.setTextFill(Color.web("#75c24e"));

            }
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