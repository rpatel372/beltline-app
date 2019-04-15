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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.Event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class StaffEventDetail {
    public String previousPage;

    public Label event;
    public Label site;
    public Label startDate;
    public Label endDate;
    public Label duration;
    public Label capacity;
    public Label price;
    public Label description;
    public ListView<String> staff;

    Event globalEvent;

    public void initialize() {

        previousPage = Context.getInstance().currentPreviousPage();

    }
    public void setEvent(Event e) throws SQLException {
        globalEvent = e;
//        System.out.println(globalEvent.getName());
//        System.out.println(globalEvent.getSiteName());
//        System.out.println(globalEvent.getStartDate());

        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = null;
        stmt = connection.createStatement();

        String sql = "CALL getEventInfo('" + globalEvent.getName() + "', '" + globalEvent.getStartDate()
                + "', '" + globalEvent.getSiteName() + "')";

        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            event.setText(rs.getString(1));
            site.setText(rs.getString(9));
            startDate.setText(rs.getString(3));
            endDate.setText(rs.getString(4));
            duration.setText(Integer.toString(rs.getInt(8)));
            capacity.setText(Integer.toString(rs.getInt(6)));
            price.setText(Integer.toString(rs.getInt(2)));
            description.setText(rs.getString(7));
        }

        //TODO: add staff to lsitview
        String sql2 = "CALL getAssignedStaff('" + globalEvent.getName() + "', '" + globalEvent.getStartDate()
                + "', '" + globalEvent.getSiteName() + "')";
        ObservableList<String> allStaff = FXCollections.observableArrayList();
        ResultSet rs2 = stmt.executeQuery(sql2);
        while (rs2.next()) {
            allStaff.add(rs2.getString(1) + " " + rs2.getString(2));
        }
        staff.setItems(allStaff);
    }

    public void goBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/staffViewSchedule.fxml"));
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