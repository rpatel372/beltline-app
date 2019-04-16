package sample.controller;

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

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ManagerDailyDetail {
    public String previousPage;

    public String siteName;
    public String date;

    public TableView<DailyDetail> table;
    public TableColumn nameCol;
    public TableColumn staffCol;
    public TableColumn visitsCol;
    public TableColumn revenueCol;

    public ChoiceBox sortBy;
    public String sorting = "";

    public class DailyDetail {
        String name;
        String staff;
        int visits;
        double revenue;

        public DailyDetail(String n, String s, int v, double r) {
            name = n;
            staff = s;
            visits = v;
            revenue = r;
        }
        public String getName() { return name; }
        public String getStaff() { return staff; }
        public int getVisits() {
            return visits;
        }
        public double getRevenue() {
            return revenue;
        }
    }

    public void initialize() {
        previousPage = Context.getInstance().currentPreviousPage();

    }

    public void initializeInfo(String d, String s) throws SQLException {
        siteName = s;
        date = d;

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        staffCol.setCellValueFactory(new PropertyValueFactory<>("staff"));
        visitsCol.setCellValueFactory(new PropertyValueFactory<>("visits"));
        revenueCol.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        table.getItems().clear();
        List<DailyDetail> entries = new ArrayList<DailyDetail>();

        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = connection.createStatement();
        String sql = "CALL getDailyDetail('" + siteName + "', '" + date + "', '" + sorting + "')";

        System.out.println(sql);

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {

            // get the assigned staff
            String allStaff = "";
            String sql2 = "CALL getStaffForDailyDetail('" + rs.getString(1) + "', '" + rs.getString(5) + "', '" + rs.getString(6) + "')";
            Statement stmt2 = connection.createStatement();
            ResultSet rs2 = stmt2.executeQuery(sql2);
            while (rs2.next()) {
                allStaff += rs2.getString(1) + " " + rs2.getString(2) + "\n";
            }
            DailyDetail newEvent = new DailyDetail(rs.getString(1),
                    allStaff, rs.getInt(3), rs.getDouble(4));
            entries.add(newEvent);
        }
        table.getItems().addAll(entries);


    }
    public void sort(ActionEvent actionEvent) throws SQLException {
        if (sortBy.getValue() != null) {
            sorting = sortBy.getValue().toString();
            initializeInfo(date, siteName);
        }

    }
    public void goBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/managerSiteReport.fxml"));
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            ManagerSiteReport controller = fxmlLoader.<ManagerSiteReport>getController();
            controller.setSiteName(siteName);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}