package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.Event;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VisitorExploreEvent {
    public String previousPage;

    public TextField name;
    public TextField description;
    public ChoiceBox site;
    public TextField startDate;
    public TextField endDate;
    public TextField lowVisit;
    public TextField highVisit;
    public TextField lowPrice;
    public TextField highPrice;
    public CheckBox visitedCheck;
    public CheckBox soldOutCheck;
    public ChoiceBox sortBy;

    String nameValue = "";
    String descriptionValue = "";
    String siteValue = "";
    String startDateValue = "";
    String endDateValue = "";
    String lowVisitValue = "0";
    String highVisitValue = "999999";
    String lowPriceValue = "0";
    String highPriceValue = "999999";
    boolean visitedValue = false;
    boolean soldOutValue = false;
    String sorting = "";

    public TableView<ExploreEvent> eventTable;
    public TableColumn eventCol;
    public TableColumn siteCol;
    public TableColumn priceCol;
    public TableColumn remainingCol;
    public TableColumn totalVisitsCol;
    public TableColumn myVisitsCol;

    public Label errorMessage;

    public class ExploreEvent {
        String event;
        String site;
        double price;
        int tickets;
        int totalVisits;
        int myVisits;
        public ExploreEvent(String e, String s, double p, int t, int tv, int mv) {
            event = e;
            site = s;
            price = p;
            tickets = t;
            totalVisits = tv;
            myVisits = mv;
        }
        public String getEvent() { return event; }
        public String getSite() { return site; }
        public double getPrice() { return price; }
        public int getTickets() { return tickets; }
        public int getTotalVisits() { return totalVisits; }
        public int getMyVisits() { return myVisits; }
    }

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
            list.add(rs.getString(1));
        }
        list.add("All");
        site.setItems(list);
    }

    public void addToTable() throws SQLException {
        errorMessage.setText("");
        eventCol.setCellValueFactory(new PropertyValueFactory<>("event"));
        siteCol.setCellValueFactory(new PropertyValueFactory<>("site"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        remainingCol.setCellValueFactory(new PropertyValueFactory<>("tickets"));
        totalVisitsCol.setCellValueFactory(new PropertyValueFactory<>("totalVisits"));
        myVisitsCol.setCellValueFactory(new PropertyValueFactory<>("myVisits"));

        eventTable.getItems().clear();
        List<ExploreEvent> eventsToAdd = new ArrayList<ExploreEvent>();

        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();

        Statement stmt = connection.createStatement();
        String sql = "CALL exploreEvent('" + Context.getInstance().currentUser().username + "', '" + nameValue
                + "', '" + descriptionValue + "', '" + siteValue + "', '"
                + startDateValue + "', '" + endDateValue
                + "', '" + lowVisitValue + "', '" + highVisitValue
                + "', '" + lowPriceValue + "', '" + highPriceValue
                + "', " + visitedValue + ", " + soldOutValue + ", '" + sorting + "')";

        System.out.println(sql);

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            ExploreEvent newEvent =
                    new ExploreEvent(rs.getString(1),
                            rs.getString(2),
                            rs.getDouble(3),
                            rs.getInt(4),
                            rs.getInt(5),
                            rs.getInt(6));
            eventsToAdd.add(newEvent);
        }
        eventTable.getItems().addAll(eventsToAdd);

    }

    public void filter(ActionEvent actionEvent) throws SQLException {
        boolean canYouFilter = true;

        if (!startDate.getText().trim().equals("")) {
            if (!startDate.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
                errorMessage.setText("Start Date is not in correct format! Please follow format listed.");
                canYouFilter = false;
            }
        }

        if (!endDate.getText().trim().equals("")) {
            if (!endDate.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
                errorMessage.setText("End Date is not in correct format! Please follow format listed.");
                canYouFilter = false;
            }
        }

        if (!lowVisit.getText().trim().equals("")) {
            if (!lowVisit.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Lower visits must be integer!");
                canYouFilter = false;
            }
        }

        if (!highVisit.getText().trim().equals("")) {
            if (!highVisit.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Higher visits must be integer!");
                canYouFilter = false;
            }
        }

        if (!lowPrice.getText().trim().equals("")) {
            try {
                Double num = Double.parseDouble(lowPrice.getText().trim());
            } catch (NumberFormatException e) {
                errorMessage.setText("Lower price must be a number!");
                canYouFilter = false;
            }
        }

        if (!highPrice.getText().trim().equals("")) {
            try {
                Double num = Double.parseDouble(highPrice.getText().trim());
            } catch (NumberFormatException e) {
                errorMessage.setText("Higher price must be a number!");
                canYouFilter = false;
            }
        }

        if (canYouFilter) {
            if (name.getText().trim().equals((""))) {
                nameValue = "";
            } else {
                nameValue = name.getText().trim();
            }

            if (description.getText().trim().equals("")) {
                descriptionValue = "";
            } else {
                descriptionValue = description.getText().trim();
            }

            if (site.getValue() == null || site.getValue().toString().equals("All")) {
                siteValue = "";
            } else {
                siteValue = site.getValue().toString();
            }

            if (startDate.getText().trim().equals("")) {
                startDateValue = "1000-01-01";
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
                //TODO: verify input is an integer
                lowVisitValue = lowVisit.getText().trim();
            }

            if (highVisit.getText().trim().equals("")) {
                highVisitValue = "99999999";
            } else {
                //TODO: verify input is an integer
                highVisitValue = highVisit.getText().trim();
            }

            if (lowPrice.getText().trim().equals("")) {
                lowPriceValue = "0";
            } else {
                //TODO: verify input is an integer
                lowPriceValue = lowPrice.getText().trim();
            }

            if (highPrice.getText().trim().equals("")) {
                highPriceValue = "99999999";
            } else {
                //TODO: verify input is an integer
                highPriceValue = highPrice.getText().trim();
            }

            if (visitedCheck.isSelected()) {
                visitedValue = false;
            } else {
                visitedValue = true;
            }
            if (soldOutCheck.isSelected()) {
                soldOutValue = false;
            } else {
                soldOutValue = true;
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

    public void eventDetail(ActionEvent actionEvent) {
    }

    public void goBack(ActionEvent actionEvent) {
    }
}
