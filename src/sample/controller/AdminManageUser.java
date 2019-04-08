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
import sample.model.TranHist;
import sample.model.Transit;
import sample.model.User2;

import java.io.IOException;
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

    public void decline(ActionEvent actionEvent) throws SQLException {
        //Administrator can approve a pending (or declined) account, can decline a
        //pending account, but cannot decline an approved account

        if (users.getSelectionModel().getSelectedItem() != null) {
            User2 selectedUser = users.getSelectionModel().getSelectedItem();
            if (selectedUser.getStatus().equals("Approved")) {
                errorMessage.setText("You cannot decline an approved account.");
            } else {
                ConnectionClass connectionClass = new ConnectionClass();
                Connection connection = connectionClass.getConnection();
                Statement stmt = connection.createStatement();
                String sql = "CALL changeUserStatus('" + selectedUser.getUsername() + "', '" + "Declined" + "')";
                stmt.execute(sql);
            }
        } else {
            errorMessage.setText("Must select a user first!");
        }
        addToTable();
    }

    public void approve(ActionEvent actionEvent) throws SQLException {
        //Administrator can approve a pending (or declined) account, can decline a
        //pending account, but cannot decline an approved account

        if (users.getSelectionModel().getSelectedItem() != null) {
            User2 selectedUser = users.getSelectionModel().getSelectedItem();

            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.getConnection();
            Statement stmt = connection.createStatement();
            String sql = "CALL changeUserStatus('" + selectedUser.getUsername() + "', '" + "Approved" + "')";
            stmt.execute(sql);

        } else {
            errorMessage.setText("Must select a user first!");
        }
        addToTable();

    }

    public void sort(ActionEvent actionEvent) throws SQLException {
        if (sortBy.getValue() != null) {
            sortParam = sortBy.getValue().toString();
        }
        addToTable();
    }

    public void goBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(previousPage));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/adminManageUser.fxml";
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
