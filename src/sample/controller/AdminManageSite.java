package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.Site;
import sample.model.Transit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AdminManageSite {

    public ChoiceBox siteOptions;
    public ChoiceBox openEveryday;
    public ChoiceBox manager;

    public ChoiceBox sortBy;

    public TableView<Site> siteTable;

    public TableColumn<Site, String> nameCol;
    public TableColumn<Site, String> managerCol;
    public TableColumn<Site, Integer> openCol;


    String nameO = "";
    String openO = "";
    String managerO = "";
    String sortOption = "";

    String previousPage = "";

    public void initialize() throws SQLException {
        previousPage = Context.getInstance().currentPreviousPage();
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = null;
        stmt = connection.createStatement();

        String sql = "CALL getSitesForDropdown()";
        ObservableList<String> list = FXCollections.observableArrayList();

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            list.add(rs.getString("SiteName"));
        }
        list.add("All");
        siteOptions.getItems().addAll(list);

        list.clear();
        String sql2 = "CALL getManagersForDropdown()";
        ResultSet rs2 = stmt.executeQuery(sql2);
        while (rs2.next()) {
            list.add(rs2.getString(1));
        }
        list.add("All");
        manager.getItems().addAll(list);

    }
    public void addToTable() throws SQLException {
        siteTable.getItems().clear();
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        managerCol.setCellValueFactory(new PropertyValueFactory<>("manager"));
        openCol.setCellValueFactory(new PropertyValueFactory<>("openEveryday"));

        List<Site> sitesToAdd = new ArrayList<Site>();
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = null;
        stmt = connection.createStatement();

        String sql = "CALL manageSites('" + nameO + "', '" + managerO + "', '" + openO + "', '" + sortOption + "')";
        System.out.println(sql);


        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Site newSite = new Site(rs.getString(1), rs.getString(2),
                    rs.getString(3));
            sitesToAdd.add(newSite);

        }

        siteTable.getItems().addAll(sitesToAdd);
    }

    public void filter(ActionEvent actionEvent) throws SQLException {
        if (siteOptions.getValue() == null || siteOptions.getValue().toString().equals("All")) {
            nameO = "";
        } else {
            nameO = siteOptions.getValue().toString();
        }
        if (manager.getValue() == null || manager.getValue().toString().equals("All")) {
            managerO = "";
        } else {
            managerO = manager.getValue().toString();
        }
        if (openEveryday.getValue() == null || openEveryday.getValue().toString().equals("All")) {
            openO = "";
        } else {
            openO = openEveryday.getValue().toString();
        }

        addToTable();
    }

    public void goBack(ActionEvent actionEvent) {
    }

    public void sort(ActionEvent actionEvent) throws SQLException {
        if (sortBy.getValue() != null) {
            sortOption = sortBy.getValue().toString();
        }
        addToTable();
    }

    public void create(ActionEvent actionEvent) {
    }

    public void edit(ActionEvent actionEvent) {
    }

    public void delete(ActionEvent actionEvent) {
    }
}
