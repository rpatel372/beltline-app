package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

public class AdminManageSite {

    public ChoiceBox siteOptions;
    public ChoiceBox openEveryday;
    public ChoiceBox manager;

    public ChoiceBox sortBy;

    public TableView<Site> siteTable;

    public TableColumn<Site, String> nameCol;
    public TableColumn<Site, String> managerCol;
    public TableColumn<Site, Integer> openCol;

    public Label errorMessage;


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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(previousPage));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/adminManageSite.fxml";
            root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sort(ActionEvent actionEvent) throws SQLException {
        if (sortBy.getValue() != null) {
            sortOption = sortBy.getValue().toString();
        }
        addToTable();
    }

    public void create(ActionEvent actionEvent) {


    }

    public void edit(ActionEvent actionEvent) throws IOException, SQLException {
        // GET SELECTED SITE
        if (siteTable.getSelectionModel().getSelectedItem() != null) {
            //NAVIGATE TO edit site page & pass info to fxml file of site selected

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/adminEditSite.fxml"));
            Parent root = null;
            Site site =  siteTable.getSelectionModel().getSelectedItem();
            root = (Parent)fxmlLoader.load();
            AdminEditSite controller = fxmlLoader.<AdminEditSite>getController();
            controller.setSite(site);
            Context.getInstance().previousPage = "../view/adminManageSite.fxml";

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();


        } else {
            errorMessage.setText("Must select a site to edit!");
        }
    }

    public void delete(ActionEvent actionEvent) throws SQLException {
        if (siteTable.getSelectionModel().getSelectedItem() != null) {
            Site deletingSite = siteTable.getSelectionModel().getSelectedItem();
            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.getConnection();
            Statement stmt = null;
            stmt = connection.createStatement();
            String sql = "CALL deleteSite('" + deletingSite.getName() + "')";
            stmt.execute(sql);
            errorMessage.setText("Site successfully deleted.");
            errorMessage.setTextFill(Color.web("#75c24e"));
            addToTable();
        } else {
            errorMessage.setText("You must select a site to delete!");
        }
    }
}
