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
import sample.model.Transit;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ManagerEditEvent {
    public Label eventName;
    public Label price;
    public Label startDate;
    public Label endDate;
    public Label minStaff;
    public Label capacity;
    public ListView<String> staffAssigned;
    public TextArea description;


    public TextField lowerVisits;
    public TextField higherVisits;
    public TextField lowerRevenue;
    public TextField higherRevenue;
    
    public TableView<DailyEvent> eventTable;
    public TableColumn dateCol;
    public TableColumn dailyVisitsCol;
    public TableColumn dailyRevenueCol;

    public Event globalEvent;
    public ArrayList<String> initiallyAdded = new ArrayList<String>();

    String lowerVisitsValue = "0";
    String higherVisitsValue = "9999999";
    String lowerRevenueValue = "0";
    String higherRevenueValue = "999999";

    public ComboBox sortBy;
    String sorting = "";

    public Label errorMessage;

    public class DailyEvent { // class used to populate daily event table
        public String date;
        public int visits;
        public int revenue;

        public DailyEvent(String d, int v, int r) {
            date = d;
            visits = v;
            revenue = r;
        }
        public String getDate() { return date; }
        public int getVisits() { return visits; }
        public int getRevenue() { return revenue; }
    }

    public void initializeInfo(Event event) throws SQLException {
        globalEvent = event;

        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = null;
        stmt = connection.createStatement();
        //SET CURRENT SITE INFO
        String sql = "CALL getEventInfo('" + globalEvent.getName() + "', '" + globalEvent.getStartDate() + "', '" + globalEvent.getSiteName()+ "')";
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            eventName.setText(rs.getString(1));
            price.setText(Double.toString(rs.getDouble(2)));
            startDate.setText(rs.getString(3));
            endDate.setText(rs.getString(4));
            minStaff.setText(Integer.toString(rs.getInt(5)));
            capacity.setText(Integer.toString(rs.getInt(6)));
            description.setText(rs.getString(7));
        }

        //GET ALL STAFF POSSIBLE
        String sql2 = "CALL getAllAvailableStaff('" + startDate.getText() + "', '" + endDate.getText() + "')";
        ObservableList<String> list = FXCollections.observableArrayList();
        ResultSet rs2 = stmt.executeQuery(sql2);
        while (rs2.next()) {
            list.add(rs2.getString(1) + " " + rs2.getString(2));
        }


        //SELECT CURRENT STAFF SELECTED
        initiallyAdded.clear();
        String sql8 = "CALL getAssignedStaff('" + globalEvent.getName() + "', '" + globalEvent.getStartDate() + "', '" + globalEvent.getSiteName() + "')";
        ResultSet rs6 = stmt.executeQuery(sql8);
        staffAssigned.getSelectionModel().clearSelection();
        staffAssigned.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        while (rs6.next()) {
            list.add(rs6.getString(1) + " " + rs6.getString(2));
            initiallyAdded.add(rs6.getString(1) + " " + rs6.getString(2));
        }
        staffAssigned.setItems(list);
        for (String c : initiallyAdded) {
            staffAssigned.getSelectionModel().select(c);
        }
    }

    public void addToTable() throws SQLException {
        errorMessage.setText("");
        eventTable.getItems().clear();
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dailyVisitsCol.setCellValueFactory(new PropertyValueFactory<>("visits"));
        dailyRevenueCol.setCellValueFactory(new PropertyValueFactory<>("revenue"));

        List<DailyEvent> eventsToAdd = new ArrayList<DailyEvent>();
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = null;
        stmt = connection.createStatement();

        String sql = "CALL getDailyEventInfo('" + globalEvent.getName() + "', '" + globalEvent.getStartDate() + "', '" + globalEvent.getSiteName()
                + "', '" + lowerVisitsValue + "', '" + higherVisitsValue + "', '" + lowerRevenueValue
                + "', '" + higherRevenueValue + "', '" + sorting + "')";

        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            DailyEvent newDailyEvent = new DailyEvent(rs.getString(1), rs.getInt(3),
                    rs.getInt(2));
            eventsToAdd.add(newDailyEvent);

        }
        eventTable.getItems().addAll(eventsToAdd);
    }

    public void filter(ActionEvent actionEvent) throws SQLException {
        boolean canYouAdd = true;
        if (!lowerVisits.getText().trim().equals("")) {
            //TODO: make sure it is an int
            if (!lowerVisits.getText().matches("[0-9]+")) {
                errorMessage.setText("Lower visits value must be an integer.");
                canYouAdd = false;
            } else {
                lowerVisitsValue = lowerVisits.getText().trim();
            }
        } else {
            lowerVisitsValue = "0";
        }

        if (!higherVisits.getText().trim().equals("")) {
            //TODO: make sure it is an int
            if (!higherVisits.getText().matches("[0-9]+")) {
                errorMessage.setText("Higher visits value must be an integer.");
                canYouAdd = false;
            } else {
                higherVisitsValue = higherVisits.getText().trim();
            }
        } else {
            higherVisitsValue = "999999";
        }

        if (!lowerRevenue.getText().trim().equals("")) {
            try {
                Double num = Double.parseDouble(lowerRevenue.getText().trim());
            } catch (NumberFormatException e) {
                errorMessage.setText("Lower revenue must be a number!");
                canYouAdd = false;
            }
        }
        if (canYouAdd) {
            if (!lowerRevenue.getText().trim().equals("")) {
                lowerRevenueValue = lowerRevenue.getText().trim();
            } else {
                lowerRevenueValue = "0";
            }
        }

        if (!higherRevenue.getText().trim().equals("")) {
            try {
                Double num = Double.parseDouble(higherRevenue.getText().trim());
            } catch (NumberFormatException e) {
                errorMessage.setText("Higher revenue must be a number!");
                canYouAdd = false;
            }
        }
        if (canYouAdd) {
            if (!higherRevenue.getText().trim().equals("")) {
                higherRevenueValue = higherRevenue.getText().trim();
            } else {
                higherRevenueValue = "9999999";
            }
        }


        if (canYouAdd) {
            addToTable();
        }
    }

    public void sort(ActionEvent actionEvent) throws SQLException {
        if (sortBy.getValue() != null) {
            sorting = sortBy.getValue().toString();
            addToTable();
        }
    }

    public void update(ActionEvent actionEvent) throws SQLException {
        if (description.getText().trim().equals("")) {
            errorMessage.setText("All fields are required!");
        } else if (staffAssigned.getSelectionModel().getSelectedIndices().size() < Integer.parseInt(minStaff.getText())) {
            errorMessage.setText("You must assign at least the min. # of staff required!");
        } else {
            //can update
            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.getConnection();
            Statement stmt = connection.createStatement();
            String sql10 = "CALL updateEvent('" + globalEvent.getName() + "', '"
                    + globalEvent.getStartDate() + "', '"
                    + globalEvent.getSiteName() + "', '" + description.getText().trim() + "')";
            stmt.execute(sql10);

            //TODO: delete all unselected staff
            ArrayList<String> toRemove = new ArrayList<String>();
            for (String t : initiallyAdded) {
                if (!staffAssigned.getSelectionModel().getSelectedItems().contains(t)) {
                    toRemove.add(t);
                }
            }

            //TODO: add all selected staff
            ArrayList<String> toAdd = new ArrayList<String>();
            for (String t : staffAssigned.getSelectionModel().getSelectedItems()) {
                if (!initiallyAdded.contains(t)) {
                    toAdd.add(t);
                    System.out.println(t);
                }
            }

            for (String t : toAdd) { //add connected sites
                String[] splitted = t.split("\\s+");
                System.out.println(splitted[0]);
                System.out.println(splitted[1]);
                String sql11 = "CALL addStaffToEvent('" + globalEvent.getName() + "', '"
                        + globalEvent.getStartDate() + "', '" + globalEvent.getSiteName() + "', '"
                        + splitted[0].trim() + "', '" + splitted[1].trim() + "')";
                stmt.execute(sql11);
            }
            for (String t : toRemove) {//remove deselected connected sites
                String[] splitted = t.split("\\s+");
                String sql12 = "CALL removeStaffFromEvent('" + globalEvent.getName() + "', '"
                        + globalEvent.getStartDate() + "', '" + globalEvent.getSiteName() + "', '"
                        + splitted[0].trim() + "', '" + splitted[1].trim() + "')";
                stmt.execute(sql12);
            }
            errorMessage.setText("Event successfully edited.");
            errorMessage.setTextFill(Color.web("#75c24e"));

            initializeInfo(globalEvent);
        }

    }

    public void goBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/managerManageEvent.fxml"));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/managerManageEvent.fxml";
            root = (Parent) fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            ManagerManageEvent controller = fxmlLoader.<ManagerManageEvent>getController();
            controller.setSiteName(globalEvent.getSiteName());
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
