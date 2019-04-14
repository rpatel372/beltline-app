package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.Event;
import sample.model.TranHist;
import sample.model.Transit;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ManagerManageEvent {

    public TextField name;
    public TextField descriptionKeyword;
    public TextField startDate;
    public TextField endDate;
    public TextField lowerDuration;
    public TextField higherDuration;
    public TextField lowerVisits;
    public TextField higherVisits;
    public TextField lowerRevenue;
    public TextField higherRevenue;

    public TableView<Event> eventTable;
    public TableColumn nameCol;
    public TableColumn staffCountCol;
    public TableColumn durationCol;
    public TableColumn visitsCol;
    public TableColumn revenueCol;

    public ComboBox sortBy;

    public Label errorMessage;

    //GLOBAL VARIABLES TO HOLD VALUES SELECTED
    String nameValue = "";
    String descriptionKeywordValue = "";
    String startDateValue =  "1000-01-01";
    String endDateValue = "9999-12-31";
    String lowerDurationValue = "0";
    String higherDurationValue =  "99999999";
    String lowerVisitsValue = "0";
    String higherVisitsValue = "99999999";
    String lowerRevenueValue = "0";
    String higherRevenueValue = "99999999";
    String sortBySelection = "";

    String siteName;

    public void setSiteName(String sname) {
        siteName = sname;
    }

    String previousPage = Context.getInstance().currentPreviousPage();

    public void addToTable() throws SQLException {
        errorMessage.setText("");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        staffCountCol.setCellValueFactory(new PropertyValueFactory<>("staffCount"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        visitsCol.setCellValueFactory(new PropertyValueFactory<>("visits"));
        revenueCol.setCellValueFactory(new PropertyValueFactory<>("revenue"));

        eventTable.getItems().clear();
        List<Event> eventsToAdd = new ArrayList<Event>();

        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();

        Statement stmt = connection.createStatement();
        String sql = "CALL filterEvents('" + nameValue + "', '" + descriptionKeywordValue
                + "', '" + startDateValue + "', '" + endDateValue + "', '"
                + lowerDurationValue + "', '" + higherDurationValue
                + "', '" + lowerVisitsValue + "', '" + higherVisitsValue
                + "', '" + lowerRevenueValue
                + "', '" + higherRevenueValue + "', '" + siteName + "', '" + sortBySelection + "')";

        System.out.println(sql);

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Event newEvent =
                    new Event(rs.getString(1),
                            rs.getInt(3),
                            rs.getInt(2),
                            rs.getInt(4),
                            rs.getInt(5),
                            rs.getString(6),
                            rs.getString(7));
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

        if (!lowerDuration.getText().trim().equals("")) {
            if (!lowerDuration.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Lower duration must be integer!");
                canYouFilter = false;
            }
        }

        if (!higherDuration.getText().trim().equals("")) {
            if (!higherDuration.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Higher duration must be integer!");
                canYouFilter = false;
            }
        }

        if (!lowerVisits.getText().trim().equals("")) {
            if (!lowerVisits.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Lower visits must be integer!");
                canYouFilter = false;
            }
        }

        if (!higherVisits.getText().trim().equals("")) {
            if (!higherVisits.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Higher visits must be integer!");
                canYouFilter = false;
            }
        }

        if (!lowerRevenue.getText().trim().equals("")) {
            try {
                Double num = Double.parseDouble(lowerRevenue.getText().trim());
            } catch (NumberFormatException e) {
                errorMessage.setText("Lower revenue must be a number!");
                canYouFilter = false;
            }
        }

        if (!higherRevenue.getText().trim().equals("")) {
            try {
                Double num = Double.parseDouble(higherRevenue.getText().trim());
            } catch (NumberFormatException e) {
                errorMessage.setText("Higher revenue must be a number!");
                canYouFilter = false;
            }
        }

        if (canYouFilter) {
            if (name.getText().trim().equals((""))) {
                nameValue = "";
            } else {
                nameValue = name.getText().trim();
            }

            if (descriptionKeyword.getText().trim().equals("")) {
                descriptionKeywordValue = "";
            } else {
                descriptionKeywordValue = descriptionKeyword.getText().trim();
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

            if (lowerDuration.getText().trim().equals("")) {
                lowerDurationValue = "0";
            } else {
                //TODO: verify input is an integer
                lowerDurationValue = lowerDuration.getText().trim();
            }

            if (higherDuration.getText().trim().equals("")) {
                higherDurationValue = "99999999";
            } else {
                //TODO: verify input is an integer
                higherDurationValue = higherDuration.getText().trim();
            }

            if (lowerVisits.getText().trim().equals("")) {
                lowerVisitsValue = "0";
            } else {
                //TODO: verify input is an integer
                lowerVisitsValue = lowerVisits.getText().trim();
            }

            if (higherVisits.getText().trim().equals("")) {
                higherVisitsValue = "99999999";
            } else {
                //TODO: verify input is an integer
                higherVisitsValue = higherVisits.getText().trim();
            }

            if (lowerRevenue.getText().trim().equals("")) {
                lowerRevenueValue = "0";
            } else {
                //TODO: verify input is an integer
                lowerRevenueValue = lowerRevenue.getText().trim();
            }

            if (higherRevenue.getText().trim().equals("")) {
                higherRevenueValue = "99999999";
            } else {
                //TODO: verify input is an integer
                higherRevenueValue = higherRevenue.getText().trim();
            }

            addToTable();

        }
    }

    public void sort(ActionEvent actionEvent) throws SQLException {
        if (sortBy.getValue() != null) {
            sortBySelection = sortBy.getValue().toString();
        }
        addToTable();
    }

    public void create(ActionEvent actionEvent)  {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/managerCreateEvent.fxml"));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/managerManageEvent.fxml";
            root = (Parent)fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void edit(ActionEvent actionEvent) throws SQLException {
        if (eventTable.getSelectionModel().getSelectedItem() != null) {
            String page = "../view/managerEditEvent.fxml";
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(page));
            Parent root = null;
            try {
                Context.getInstance().previousPage = "../view/managerManageEvent.fxml";
                root = (Parent) fxmlLoader.load();
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                ManagerEditEvent controller = fxmlLoader.<ManagerEditEvent>getController();
                controller.initializeInfo(eventTable.getSelectionModel().getSelectedItem());
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorMessage.setText("You must select an event to view/edit!");
        }
    }

    public void delete(ActionEvent actionEvent) throws SQLException {
        if (eventTable.getSelectionModel().getSelectedItem() != null) {
            Event deletingEvent = eventTable.getSelectionModel().getSelectedItem();
            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.getConnection();
            Statement stmt = null;
            stmt = connection.createStatement();
            // key is a combo of (Type, Route)
            String sql = "CALL deleteEvent('" + deletingEvent.getName() + "', '" + deletingEvent.getStartDate() + "', '" + deletingEvent.getSiteName()+ "')";
            stmt.execute(sql);
            errorMessage.setText("Tranist successfully deleted.");
            errorMessage.setTextFill(Color.web("#75c24e"));
            addToTable();
        } else {
            errorMessage.setText("You must select an event to delete!");
        }
    }

    public void goBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(previousPage));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/managerManageEvent.fxml";
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
