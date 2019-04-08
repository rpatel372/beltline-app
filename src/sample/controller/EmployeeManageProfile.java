package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    int i = 0;
    int x = 1;
    int y = 0;

    public List<String> emailsReturned = new ArrayList<String>();

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
            emailsReturned.add(rs3.getString("Email"));
            i = i + 1;
        }
        initialEmail.setText(emailsReturned.get(0));
        emails[0] = initialEmail;
        for (int j = 1;  j < i; j++) {
            emails[j] = new TextField();
            emails[j].setText(emailsReturned.get(j));
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
            if (rs.getString(6) != null) {
                address.setText(rs.getString(6) + ", "
                        + rs.getString(7) + ", " + rs.getString(8)
                        + " " + rs.getString(9));
            }
        }
        if (globalUser.userType == "Visitor" || globalUser.userType == "Manager Visitor" || globalUser.userType == "Staff Visitor" || globalUser.userType == "Admin Visitor") {
            visitorAccount.setSelected(true);
        }

        if (globalUser.userType == "Manager" || globalUser.userType == "Manager Visitor") {
            String sql3 = "CALL getSiteManaged('" + globalUser.username + "')";
            ResultSet rs11 = stmt.executeQuery(sql3);
            if (rs11.next()) {
                siteName.setText(rs11.getString(1));
            }
        }

    }

    public void addEmail(ActionEvent actionEvent) {
        if (i == 1 && initialEmail.getText().equals("")) {
            errorMessage.setText("Email cannot be blank when you hit Add!");
        } else {
            emails[0] = initialEmail;
        }
        if (i != 1 && emails[i - 1].getText().equals("")) {
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
        if (i == 1) {
            errorMessage.setText("You must have at least one email!");
        } else {

            addEmailPane.getChildren().remove(emails[i-1]);
            emails[i] = null;
            i = i - 1;
            y = y - 48;
        }
    }

    public void updateAccount(ActionEvent actionEvent) throws SQLException {
        boolean canYouUpdate = true;
        if (phone.getText().trim().equals("") || firstName.getText().trim().equals("") || lastName.getText().trim().equals("")) {
            canYouUpdate = false;
            errorMessage.setText("All fields are required.");
        }
        if (!phone.getText().matches("[0-9]{10}")) {
            errorMessage.setText("Phone number must be 10 digits.");
            canYouUpdate = false;
        }


        for (int g = 0; g < i; g++) {
            Pattern p = Pattern.compile( "^[a-zA-Z0-9_+&*-]+(?:\\."+
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$");
            Matcher m = p.matcher(emails[g].getText());
            if (!m.find()) {
                canYouUpdate = false;
                errorMessage.setText("Email does not have correct format.");
            }
        }

        List<String> emailsAdded = new ArrayList<String>();
        List<String> deletedEmails = new ArrayList<String>();

        for (int p = 0; p < i; p++) {
            if (!emailsReturned.contains(emails[p].getText())) {
                emailsAdded.add(emails[p].getText());
//                System.out.println(emails[p].getText());
            }
        }
        //TODO: check emails are unique
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement statement = connection.createStatement();

        for (int c = 0; c < emailsAdded.size(); c++) {
            String sql4 = "CALL checkIfEmailUnique('" + emailsAdded.get(c) + "')";
            ResultSet rs2 = statement.executeQuery(sql4);
            if (rs2.next() == true) {
                errorMessage.setText("Email already in use.");
                canYouUpdate = false;
                break;
            }
        }

        if (canYouUpdate) {
            //add emails from emailsAdded to database
            for (int k = 0; k < emailsAdded.size(); k++) {
                String sql2 = "CALL addEmail('" + emailsAdded.get(k) + "', '" + globalUser.username + "')";
                statement.executeUpdate(sql2);
            }

            List<String> emailsAsText = new ArrayList<String>();
            for (int g = 0; g < i; g++) {
                emailsAsText.add(emails[g].getText());
            }
            for (int g = 0; g < emailsReturned.size(); g++) {
                if (!emailsAsText.contains(emailsReturned.get(g))) {
                    deletedEmails.add(emailsReturned.get(g));
                }
            }

            //remove emails from deletedEmails from database
            for (int k = 0; k < deletedEmails.size(); k++) {
                String sql2 = "CALL deleteEmail('" + deletedEmails.get(k) + "')";
                statement.executeUpdate(sql2);
            }
            String uType = globalUser.userType;
            String uTypeQuery = "";
            String[] parts = uType.split(" ");
            if (visitorAccount.isSelected()) {
                uTypeQuery = "Employee-Visitor";
                if (parts.length == 1) {
                    uType = uType + " Visitor";
                }
            } else {
                uTypeQuery = "Employee";
                if (parts.length == 2) {//i.e. Manager Visitor
                    uType = parts[0];
                }
            }

            String sql24 = "CALL manageProfile('" + globalUser.username + "', '" + firstName.getText() + "', '" + lastName.getText()
                    + "', '" + phone.getText() + "', '" + uTypeQuery + "')";
            statement.executeUpdate(sql24);
            Context.getInstance().globalUser.userType = uType;


            //TODO: delete all visit history if visitor is deselected
            if (!visitorAccount.isSelected()) {
                Context.getInstance().previousPage = "../view/" + uType + "OnlyMenu.fxml";
                String sql4 = "CALL removeVisitHistory('" + globalUser.username + "')";
                statement.executeUpdate(sql4);
                Context.getInstance().globalUser.userType = uType;
            } else {
                Context.getInstance().previousPage = "../view/" + parts[0] + "VisitorMenu.fxml";
            }

            for (String x : emailsAdded){
                if (!emailsReturned.contains(x))
                    emailsReturned.add(x);
            }
            for (String x : deletedEmails){
                if (emailsReturned.contains(x))
                    emailsReturned.remove(x);
            }

            for (String x : emailsReturned) {
                System.out.println(x);
            }


            errorMessage.setText("Updated profile successfully.");
            errorMessage.setTextFill(Color.web("#75c24e"));
        }
    }

    public void goBack(ActionEvent actionEvent) {
        Parent blah = null;
        try {
            blah = FXMLLoader.load(getClass().getResource(Context.getInstance().currentPreviousPage()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(blah);
        Context.getInstance().previousPage = "../view/employeeManageProfile.fxml";
        Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }
}
