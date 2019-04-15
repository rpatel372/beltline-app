package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.Event;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class VisitorEventDetail {
    public String previousPage;
    Event globalEvent;

    public Label event;
    public Label site;
    public Label startDate;
    public Label endDate;
    public Label price;
    public Label description;
    public Label ticketsRemaining;

    public Label errorMessage;

    public TextField visitDate;

    public void initialize() {

        previousPage = Context.getInstance().currentPreviousPage();

    }

    public void setEvent(Event e) throws SQLException {
        globalEvent = e;
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
            price.setText(Integer.toString(rs.getInt(2)));
            description.setText(rs.getString(7));
            ticketsRemaining.setText(Integer.toString(rs.getInt(10)));
        }

    }

    public void logVisit(ActionEvent actionEvent) throws SQLException, ParseException {
        //TODO: make sure there are tickets remaining
        if (Integer.parseInt(ticketsRemaining.getText()) == 0) {
            errorMessage.setText("There are no more tickets for this event!");
        } else if (visitDate.getText().trim().equals("")) {
            errorMessage.setText("You must fill out a visit date!");
        } else if ((!visitDate.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})"))) {
            errorMessage.setText("Visit date is not in correct format! Please follow format listed.");
        } else {
            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.getConnection();
            Statement statement = null;
            statement = connection.createStatement();
            //TODO: check if event date is within start and end date

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date sdate = formatter.parse(startDate.getText());
            Date edate = formatter.parse(endDate.getText());
            Date vdate = formatter.parse(visitDate.getText());
            boolean isItValidDate = (vdate.after(sdate) && vdate.before(edate))|| vdate.equals(sdate) || vdate.equals(edate) ;
            if (!isItValidDate) {
                errorMessage.setText("Visit date is not between the start and end date!");
            } else {


                //TODO: check event was not already logged today
                String sql2 = "CALL checkIfEventAlreadyLoggedToday('" + Context.getInstance().currentUser().username
                        + "', '" + globalEvent.getName() + "', '" + globalEvent.getStartDate() + "', '" + globalEvent.getSiteName()
                        + "', '" + visitDate.getText().trim() + "')";
                ResultSet rs = statement.executeQuery(sql2);
                if (rs.next()) {
                    if (rs.getInt(1) != 0) {
                        errorMessage.setText("You can only log a visit for the same event once a day!");
                    } else {
                        //add entry to visit event
                        String sql = "CALL logVisitToEvent('" + Context.getInstance().currentUser().username
                                + "', '" + globalEvent.getName() + "', '" + globalEvent.getStartDate() + "', '" + globalEvent.getSiteName()
                                + "', '" + visitDate.getText().trim() + "')";
                        statement.executeUpdate(sql);

                        //update value of tickets remaining
                        ticketsRemaining.setText(Integer.toString(Integer.parseInt(ticketsRemaining.getText()) - 1));
                        errorMessage.setText("Event successfully logged!");
                        errorMessage.setTextFill(Color.web("#75c24e"));
                    }
                }
            }
        }
    }


    public void goBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/visitorExploreEvent.fxml"));
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
