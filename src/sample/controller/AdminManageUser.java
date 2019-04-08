package sample.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.TranHist;
import sample.model.User2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AdminManageUser {

    public TextField username;
    public ChoiceBox type;
    public ChoiceBox status;
    public ChoiceBox sortBy;

    public TableView<User2> users;
    public TableColumn usernameCol;
    public TableColumn emailCountCol;
    public TableColumn userTypeCol;
    public TableColumn statusCol;

    public Label errorMessage;

    String uname;
    String utype;
    String emptype;
    String ustatus;

    String sortParam = "";

    String previousPage;

    public void initialize(ActionEvent actionEvent) {
        previousPage = Context.getInstance().currentPreviousPage();
    }

    public void addToTable() throws SQLException {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCountCol.setCellValueFactory(new PropertyValueFactory<>("emailCount"));
        userTypeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        users.getItems().clear();
        List<User2> usersToAdd = new ArrayList<User2>();
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();

        Statement stmt = connection.createStatement();
        String sql = "CALL filterUsers('" + uname + "', '" + utype
                + "', '" + emptype + "', '" + ustatus
                + "', '" + sortParam  + "')";

        System.out.println(sql);

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            if (rs.getString(5) == null) {
                User2 newTransit =
                        new User2(rs.getString(1),
                                rs.getInt(4),
                                rs.getString(3),
                                rs.getString(2));
                usersToAdd.add(newTransit);
            } else {
                User2 newTransit =
                        new User2(rs.getString(1),
                                rs.getInt(4),
                                rs.getString(5),
                                rs.getString(2));
                usersToAdd.add(newTransit);
            }
        }
        users.getItems().addAll(usersToAdd);


    }

    public void filter(ActionEvent actionEvent) throws SQLException {
        uname = username.getText().trim();
        if (type.getValue() == null || type.getValue().toString().equals("All")) {
            utype = "";
            emptype = "";
        } else if (type.getValue().toString().equals("User")
                || type.getValue().toString().equals("Visitor")) {
            utype = type.getValue().toString();
            emptype = "";
        } else {
            utype = "";
            emptype = type.getValue().toString();
        }

        if (status.getValue() == null || status.getValue().toString().equals("All")) {
            ustatus = "";
        } else {
            ustatus = status.getValue().toString();
        }
        //CHECK EMPLOYEE


        addToTable();
    }

    public void decline(ActionEvent actionEvent) {
    }

    public void approve(ActionEvent actionEvent) {
    }

    public void sort(ActionEvent actionEvent) throws SQLException {
        if (sortBy.getValue() != null) {
            sortParam = sortBy.getValue().toString();
        }
        addToTable();
    }

    public void goBack(ActionEvent actionEvent) {
    }
}
