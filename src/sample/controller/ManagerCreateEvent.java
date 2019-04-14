package sample.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sample.connectivity.ConnectionClass;
import sample.model.Context;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManagerCreateEvent {

    public TextField name;
    public TextField price;
    public TextField capacity;
    public TextField minStaff;
    public TextField startDate;
    public TextField endDate;
    public TextArea description;
    public ListView<String> staff;

    boolean didTheyClickAvailableStaff = false;

    public Label errorMessage;

    String site;

    public void initialize() {
        startDate.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {

                didTheyClickAvailableStaff = false;
            }
        });
        endDate.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {

                didTheyClickAvailableStaff = false;
            }
        });
    }
    public void setSite(String siteName) {
        site = siteName;
        System.out.println(siteName);
    }
    public void displayAvailableStaff(ActionEvent actionEvent) throws SQLException {
        didTheyClickAvailableStaff = true;
        if (startDate.getText().trim().equals("") || endDate.getText().trim().equals("")) {
            errorMessage.setText("You must fill out start date and end date before seeing available staff!");
        } else if (!startDate.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
            errorMessage.setText("Start date is not in correct format! Please follow listed format.");
        } else if (!endDate.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
            errorMessage.setText("End date is not in correct format! Please follow listed format.");
        } else {
            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.getConnection();
            Statement stmt = null;
            stmt = connection.createStatement();
            String sql2 = "CALL getAllAvailableStaff('" + startDate.getText().trim() + "', '" + endDate.getText().trim() + "')";
            ObservableList<String> list = FXCollections.observableArrayList();
            ResultSet rs2 = stmt.executeQuery(sql2);
            staff.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            while (rs2.next()) {
                list.add(rs2.getString(1) + " " + rs2.getString(2));
            }
            staff.setItems(list);
            errorMessage.setText("");
        }
    }

    public void create(ActionEvent actionEvent) throws ParseException, SQLException {
        //TODO: check to see if start date and end date was changed & user did not click Show available staff button afterwards
        if (!didTheyClickAvailableStaff) {
            errorMessage.setText("You changed start date or end date and must click see available staff again!");
        } else if (name.getText().trim().equals("") || price.getText().trim().equals("") || capacity.getText().trim().equals("")
                || minStaff.getText().trim().equals("") || startDate.getText().trim().equals("") || endDate.getText().trim().equals("")
                || description.getText().trim().equals("")) {
            errorMessage.setText("All fields are required!");
        } else if (!startDate.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
            errorMessage.setText("Start date is not in correct format! Please follow listed format.");
        } else if (!endDate.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
            errorMessage.setText("End date is not in correct format! Please follow listed format.");
        } else {
            //TODO: make sure start date comes before end date
            Date sdate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate.getText().trim());
            Date edate = new SimpleDateFormat("yyyy-MM-dd").parse(endDate.getText().trim());
//            System.out.println(sdate.compareTo(edate));
            if (sdate.compareTo(edate) > 0) {
                errorMessage.setText("End date must come after start date!");
            } else if (!capacity.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Capacity must be a positive integer");
            } else if (!minStaff.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Min. staff required must be a positive integer");
            } else {
                boolean canYouAdd = true;
                //TODO: check if inputs are numbers
                try {
                    Double num = Double.parseDouble(price.getText().trim());
                    if (num < 0) {
                        errorMessage.setText("Price canot be negative!");
                        canYouAdd = false;
                    }
                } catch (NumberFormatException e) {
                    errorMessage.setText("Price must be a number!");
                    canYouAdd = false;
                }
                if (canYouAdd) {
                    //TODO: check if staff selected >= minStaff
                    if (staff.getSelectionModel().getSelectedIndices().size() < Integer.parseInt(minStaff.getText())) {
                        errorMessage.setText("You must assign at least the min. # of staff required!");
                    } else {
                        //TODO: check if name + start date + site is unique
                        boolean valid = true;
                        ConnectionClass connectionClass = new ConnectionClass();
                        Connection connection = connectionClass.getConnection();
                        Statement stmt = null;
                        stmt = connection.createStatement();
                        String sql2 = "CALL checkIfEventIsUnique('" + name.getText().trim() + "', '" + startDate.getText().trim() + "', '" + site + "')";
                        ResultSet rs = stmt.executeQuery(sql2);
                        if (rs.next()) {
                            if (rs.getInt(1) != 0) {
                                errorMessage.setText("Event with this name and start date already exists at this site!");
                                valid = false;
                            } else {
                                //TODO: check if event overlaps with another event
                                String sql3 = "CALL checkIfEventOverlaps('" + name.getText().trim() + "', '" + startDate.getText().trim()
                                        + "', '" + site + "', '" + endDate.getText().trim() + "')";
                                ResultSet rs2 = stmt.executeQuery(sql3);
                                if (rs2.next()) {
                                    if (rs2.getInt(1) != 0) {
                                        errorMessage.setText("This event overlaps with another with the same name at this site.");
                                        valid = false;
                                    }
                                }
                                if (valid) {
                                    //TODO: ADD EVENT
                                    String sql4 = "CALL addEvent('" + name.getText().trim() + "', '" + startDate.getText().trim()
                                            + "', '" + site + "', '" + endDate.getText().trim() + "', '" + price.getText().trim()
                                            + "', '" + minStaff.getText().trim() + "', '" + capacity.getText().trim()
                                            + "', '" + description.getText() + "')";
                                    stmt.execute(sql4);
                                    //TODO: add all assigned staff
                                    for (String t : staff.getSelectionModel().getSelectedItems()) { //add connected sites
                                        String[] splitted = t.split("\\s+");
    //                                System.out.println(splitted[0]);
    //                                System.out.println(splitted[1]);
                                        String sql11 = "CALL addStaffToEvent('" + name.getText().trim() + "', '"
                                                + startDate.getText().trim() + "', '" + site + "', '"
                                                + splitted[0].trim() + "', '" + splitted[1].trim()  + "', '" + endDate.getText().trim()
                                                + "')";
                                        stmt.execute(sql11);
                                    }
                                    errorMessage.setText("Event succesfully added!");
                                    errorMessage.setTextFill(Color.web("#75c24e"));

                                }
                            }
                        }
                    }
                }
            }
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
            controller.setSiteName(site);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
