package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.Event;
import sample.model.Site;
import sample.model.User;
import sun.reflect.generics.visitor.Visitor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VisitorExploreSite {
    public String previousPage;
    public User globalUser;

    public ChoiceBox name;
    public ChoiceBox openEveryday;
    public TextField startDate;
    public TextField endDate;
    public TextField lowVisit;
    public TextField highVisit;
    public TextField lowEvent;
    public TextField highEvent;
    public CheckBox visited;
    public ChoiceBox sortBy;

    String nameValue = "";
    String openEverydayValue = "";
    String startDateValue =  "1000-01-01";
    String endDateValue = "9999-12-31";
    String lowVisitValue = "0";
    String highVisitValue = "99999999";
    String lowEventValue = "0";
    String highEventValue = "99999999";
    boolean visitedValue = false;
    String sorting = "";

    public Label errorMessage;

    public TableView<ExploreSiteEntry> siteTable;
    public TableColumn siteCol;
    public TableColumn eventCountCol;
    public TableColumn totalVisitsCol;
    public TableColumn myVisitsCol;

    public class ExploreSiteEntry {
        public String site;
        public int eventCount;
        public int totalVisits;
        public int myVisits;
        public ExploreSiteEntry(String s, int e, int t, int m) {
            site = s;
            eventCount = e;
            totalVisits = t;
            myVisits = m;
        }

        public String getSite() {
            return site;
        }

        public int getEventCount() {
            return eventCount;
        }

        public int getTotalVisits() {
            return totalVisits;
        }

        public int getMyVisits() {
            return myVisits;
        }
    }

    public void initialize() throws SQLException {
        previousPage = Context.getInstance().currentPreviousPage();
        globalUser = Context.getInstance().currentUser();
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
        list.add("All");
        name.setItems(list);
    }

    public void addToTable() throws SQLException {
        errorMessage.setText("");
        siteCol.setCellValueFactory(new PropertyValueFactory<>("site"));
        eventCountCol.setCellValueFactory(new PropertyValueFactory<>("eventCount"));
        totalVisitsCol.setCellValueFactory(new PropertyValueFactory<>("totalVisits"));
        myVisitsCol.setCellValueFactory(new PropertyValueFactory<>("myVisits"));

        siteTable.getItems().clear();
        List<ExploreSiteEntry> entries = new ArrayList<ExploreSiteEntry>();

        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();

        Statement stmt = connection.createStatement();
        String sql = "CALL exploreSite('" + globalUser.username + "', '" + nameValue
                + "', '" + openEverydayValue + "', '" + startDateValue + "', '"
                + endDateValue + "', '" + lowVisitValue
                + "', '" + highVisitValue + "', '" + lowEventValue
                + "', '" + highEventValue
                + "', " + visitedValue + ", '" + sorting + "')";

        System.out.println(sql);

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            ExploreSiteEntry entry =
                    new ExploreSiteEntry(rs.getString(1),
                            rs.getInt(2),
                            rs.getInt(3),
                            rs.getInt(4));
            entries.add(entry);
        }
        siteTable.getItems().addAll(entries);


    }

    public void filter(ActionEvent actionEvent) throws SQLException {
        if (!startDate.getText().trim().equals("") && !startDate.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
                errorMessage.setText("Start Date is not in correct format! Please follow format listed.");
        } else if (!endDate.getText().trim().equals("") && !endDate.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
                errorMessage.setText("End Date is not in correct format! Please follow format listed.");
        } else if (!lowVisit.getText().trim().equals("") && !lowVisit.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Lower visit must be integer!");
        }  else if (!highVisit.getText().trim().equals("") && !highVisit.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Higher visit must be integer!");
        }  else if (!lowEvent.getText().trim().equals("") && !lowEvent.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Lower event must be integer!");
        } else if (!highEvent.getText().trim().equals("") && !highEvent.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("High event must be integer!");
        } else {
            if (name.getValue() == null || name.getValue().toString().equals("All")) {
                nameValue = "";
            } else {
                nameValue = name.getValue().toString().trim();
            }
            if (openEveryday.getValue() == null || openEveryday.getValue().toString().equals("All")) {
                openEverydayValue = "";
            } else {
                openEverydayValue = openEveryday.getValue().toString().trim();
            }
            if (startDate.getText().trim().equals("")) {
                startDateValue =  "1000-01-01";
            } else {
                startDateValue = startDate.getText().trim();
            }
            if (endDate.getText().trim().equals("")) {
                endDateValue = "9999-12-31";
            } else {
                endDateValue = endDate.getText().trim();
            }
            if (lowVisit.getText().trim().equals("")) {
                lowVisitValue = "0";
            } else {
                lowVisitValue = lowVisit.getText().trim();
            }
            if (highVisit.getText().trim().equals("")) {
                highVisitValue = "99999999";
            } else {
                highVisitValue = highVisit.getText().trim();
            }
            if (lowEvent.getText().trim().equals("")) {
                lowEventValue = "0";
            } else {
                lowEventValue = lowEvent.getText().trim();
            }
            if (highEvent.getText().trim().equals("")) {
                highEventValue = "99999999";
            } else {
                highEventValue = highEvent.getText().trim();
            }
            if (visited.isSelected()) {
                visitedValue = true;
            } else {
                visitedValue = false;
            }
            addToTable();
        }

    }

    public void sort(ActionEvent actionEvent) throws SQLException {
        if (sortBy.getValue() != null) {
            sorting = sortBy.getValue().toString();
            addToTable();
        }
    }

    public void siteDetail(ActionEvent actionEvent) throws IOException, SQLException {
        if (siteTable.getSelectionModel().getSelectedItem() != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/visitorSiteDetail.fxml"));
            Parent root = null;
            ExploreSiteEntry site =  siteTable.getSelectionModel().getSelectedItem();
            root = (Parent)fxmlLoader.load();
            VisitorSiteDetail controller = fxmlLoader.<VisitorSiteDetail>getController();
            controller.initializeInfo(site.getSite());
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            errorMessage.setText("Must select a site to view site detail!");
        }

    }

    public void transitDetail(ActionEvent actionEvent) throws SQLException, IOException {
        if (siteTable.getSelectionModel().getSelectedItem() != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/visitorTransitDetail.fxml"));
            Parent root = null;
            ExploreSiteEntry site =  siteTable.getSelectionModel().getSelectedItem();
            root = (Parent)fxmlLoader.load();
            VisitorTransitDetail controller = fxmlLoader.<VisitorTransitDetail>getController();
            controller.initializeInfo(site.getSite());
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            errorMessage.setText("Must select a site to view transit detail!");
        }
    }


    public void goBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(previousPage));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/visitorExploreSite.fxml";
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