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
import sample.model.Event;
import sample.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class StaffViewSchedule {
    public String previousPage;
    public User globalUser;

    public TextField name;
    public TextField description;
    public TextField startDate;
    public TextField endDate;
    public ChoiceBox sortBy;

    String nameValue = "";
    String descriptionValue = "";
    String startDateValue = "1000-01-01";
    String endDateValue = "9999-12-31";
    String sorting = "";

    public TableView<ScheduleEvent> schedule;
    public TableColumn nameCol;
    public TableColumn siteCol;
    public TableColumn startDateCol;
    public TableColumn endDateCol;
    public TableColumn staffCountCol;

    public Label errorMessage;

    public class ScheduleEvent {
        String name;
        String site;
        String startDate;
        String endDate;
        int staffCount;

        public ScheduleEvent(String n, String s, String sd, String ed, int sc) {
            name = n;
            site = s;
            startDate = sd;
            endDate = ed;
            staffCount = sc;
        }

        public String getName() {
            return name;
        }

        public String getSite() {
            return site;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public int getStaffCount() {
            return staffCount;
        }
    }

    public void initialize() {
        previousPage = Context.getInstance().currentPreviousPage();
        globalUser = Context.getInstance().currentUser();
    }

    public void addToTable() throws SQLException {
        errorMessage.setText("");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        siteCol.setCellValueFactory(new PropertyValueFactory<>("site"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        staffCountCol.setCellValueFactory(new PropertyValueFactory<>("staffCount"));

        schedule.getItems().clear();
        List<ScheduleEvent> eventsToAdd = new ArrayList<ScheduleEvent>();

        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();

        Statement stmt = connection.createStatement();
        String sql = "CALL viewSchedule('" + nameValue + "', '" + descriptionValue
                + "', '" + startDateValue + "', '" + endDateValue + "', '"
                + globalUser.username + "', '" + sorting + "')";

        System.out.println(sql);

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            ScheduleEvent newEvent =
                    new ScheduleEvent(rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getInt(5));
            eventsToAdd.add(newEvent);
        }
        schedule.getItems().addAll(eventsToAdd);


    }

    public void filter(ActionEvent actionEvent) throws ParseException, SQLException {
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
        if (!startDate.getText().trim().equals("") && !endDate.getText().trim().equals("")) {
            Date sdate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate.getText().trim());
            Date edate = new SimpleDateFormat("yyyy-MM-dd").parse(endDate.getText().trim());
            if (sdate.compareTo(edate) > 0) {
                errorMessage.setText("End date must come after start date!");
                canYouFilter = false;
            }
        }

        if (canYouFilter) {

            if (name.getText().trim().equals("")) {
                nameValue = "";
            } else {
                nameValue = name.getText().trim();
            }
            if (description.getText().trim().equals("")) {
                descriptionValue = "";
            } else {
                descriptionValue = description.getText().trim();
            }
            if (startDate.getText().trim().equals("")) {
                startDateValue = "1000-01-01";
            } else {
                //TODO: verify date has correct format
                startDateValue = startDate.getText().trim();
            }
            if (endDate.getText().trim().equals("")) {
                endDateValue = "9999-12-31";
            } else {
                //TODO: verify date has correct format
                endDateValue = endDate.getText().trim();
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

    public void viewEvent(ActionEvent actionEvent) {
    }

    public void goBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(previousPage));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/staffViewSchedule.fxml";
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
