package sample.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EmployeeManageProfile {

    User globalUser;
    String previousPage;

    public TextField firstName;
    public TextField lastName;
    public Label username;
    public Label siteName;
    public Label employeeID;
    public TextField phone;
    public Label address;
    public CheckBox visitorAccount;

    public Label errorMessage;

    public TextField initialEmail;
    public Pane addEmailPane;

    public static TextField[] emails = new TextField[100];
    public static String[] emailsReturned = new String[100];
    int i = 0;
    int x = 1;
    int y = 0;


    public void initialize() throws SQLException {
        globalUser = Context.getInstance().currentUser();
        previousPage = Context.getInstance().currentPreviousPage();

        //TODO: GET ALL EMAILS FROM EMAIL TABLE AND PUT INTO emails
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = connection.createStatement();
        String sql9 = "CALL getEmailForUser('" + globalUser.username + "')";
        ResultSet rs3 = stmt.executeQuery(sql9);
        while (rs3.next()) {
            emailsReturned[i] = rs3.getString("Email");
            i = i + 1;
        }
        initialEmail.setText(emailsReturned[0]);
        emails[0] = initialEmail;
        for (int j = 1;  j < i; j++) {
            emails[j] = new TextField();
            emails[j].setText(emailsReturned[j]);
            emails[j].setPrefWidth(206.0);
            emails[j].setPrefHeight(39.0);
            emails[j].setLayoutX(x);
            emails[j].setLayoutY(y);
            addEmailPane.getChildren().add(emails[j]);
            y = y + 48;
        }

        String sql = "CALL getUserInfo('" + globalUser.username + "', '" + globalUser.userType + "')";
        System.out.println(sql);
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            firstName.setText(rs.getString(2));
            lastName.setText(rs.getString(3));
            username.setText(rs.getString(1));
            employeeID.setText(rs.getString(4));
            phone.setText(rs.getString(5));
            address.setText(rs.getString(6) + ", "
                    + rs.getString(7) + ", " + rs.getString(8)
                    + " " + rs.getString(9));
        }
        if (globalUser.userType == "Visitor" || globalUser.userType == "Manager Visitor" || globalUser.userType == "Staff Visitor" || globalUser.userType == "Admin Visitor") {
            visitorAccount.setSelected(true);
        }

    }

    public void addEmail(ActionEvent actionEvent) {
        if (i == 0 && initialEmail.getText().trim().equals("")) {
            errorMessage.setText("Email cannot be blank when you hit Add!");
        } else if (i != 0 && emails[i - 1].getText().equals("")) {
            errorMessage.setText("Email cannot be blank when you hit Add!");
        } else {
            errorMessage.setText("");
            emails[i] = new TextField();
            emails[i].setPrefWidth(206.0);
            emails[i].setPrefHeight(39.0);
            emails[i].setLayoutX(x);
            emails[i].setLayoutY(y);
            addEmailPane.getChildren().add(emails[i]);
            i = i + 1;
            y = y + 48;
        }

    }

    public void removeEmail(ActionEvent actionEvent) {
        if (i == 0) {
            errorMessage.setText("You must have at least one email!");
        } else {

            addEmailPane.getChildren().remove(emails[i-1]);
            emails[i] = null;
            i = i - 1;
            y = y - 48;
        }
    }

    public void updateAccount(ActionEvent actionEvent) {

        //TODO: input verification of email and phone number

        //TODO: update everything in database AND update user type in global context

        //TODO: TRY/CATCH - try to delete all emails in list + then add all emails in list
        for (int l = 0; l < i; l++) {
            System.out.println(emails[l].getText());
        }

        //TODO: delete all visit history if visitor is deselected
    }

    public void goBack(ActionEvent actionEvent) {
    }
}
