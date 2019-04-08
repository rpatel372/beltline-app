
package sample.controller;

import javafx.scene.paint.Color;
import sample.connectivity.ConnectionClass;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;
import sample.model.Context;
import sample.model.Main;

import java.io.IOException;
import java.sql.*;
import sample.model.Main;
import sample.controller.Register;
import sample.controller.UserMenu;
import sample.model.User;

import javax.xml.transform.Result;

public class Controller {
    public TextField emailField;
    public TextField passwordField;
    public Label errorMessage;
    private Main mainApp;

    public User globalUser;

    public void login(ActionEvent actionEvent) throws SQLException {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = connection.createStatement();

        String sql = "CALL checkIfUsernameExistsForLogin('" + emailField.getText() + "')";
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next() == false) {
            errorMessage.setText("There is no account with this email!");
        } else {

            String username = rs.getString("Username");
            String sql2 = "CALL userLogin('" + username + "', '" + passwordField.getText() + "')";
            ResultSet rs2 = stmt.executeQuery(sql2);

            if (rs2.next()) {
                String userType = rs2.getString("UserType");
//                System.out.println(userType);
                System.out.println("Successful login");
                globalUser = new User(username, userType);
                Context.getInstance().globalUser.username = username;

                Context.getInstance().previousPage = "../view/sample.fxml";
                //TODO: need to figure out user type and navigate them to the correct menu page
                if (userType.equals("User")) {
                    Context.getInstance().globalUser.userType = "User";
                    navigateToPage("../view/userMenu.fxml", actionEvent);

                } else if (userType.equals("Visitor")) {
                    Context.getInstance().globalUser.userType = "Visitor";
                    navigateToPage("../view/visitorMenu.fxml", actionEvent);
                } else if(userType.equals("Employee")) {
                    String sql9 = "CALL getEmployeeType('" + username + "')";
                    ResultSet rs3 = stmt.executeQuery(sql9);
                    if (rs3.next()) {
                        String empType = rs3.getString("EmployeeType");
                        if (empType.equals("Admin")) {
                            Context.getInstance().globalUser.userType = "Admin";
                            navigateToPage("../view/adminOnlyMenu.fxml", actionEvent);
                        } else if (empType.equals("Staff")) {
                            Context.getInstance().globalUser.userType = "Staff";
                            navigateToPage("../view/staffOnlyMenu.fxml", actionEvent);
                        } else {
                            Context.getInstance().globalUser.userType = "Manager";
                            navigateToPage("../view/managerOnlyMenu.fxml", actionEvent);
                        }
                    }
                } else {
                    // EMPLOYEE-VISITOR
                    Statement stmt2 = connection.createStatement();
                    String sql9 = "CALL getEmployeeType('" + username + "')";
                    ResultSet rs3 = stmt2.executeQuery(sql9);
                    if (rs3.next()) {
                        String empType = rs3.getString("EmployeeType");
                        if (empType.equals("Admin")) {
                            Context.getInstance().globalUser.userType = "Admin Visitor";
                            navigateToPage("../view/adminVisitorMenu.fxml", actionEvent);
                        } else if (empType.equals("Staff")) {
                            Context.getInstance().globalUser.userType = "Staff Visitor";
                            navigateToPage("../view/staffVisitorMenu.fxml", actionEvent);
                        } else {
                            Context.getInstance().globalUser.userType = "Manager Visitor";
                            navigateToPage("../view/managerVisitorMenu.fxml", actionEvent);
                        }
                    }
                }


            } else {
                errorMessage.setText("User credentials are incorrect or your account has not been accepted by admin!");
            }
        }
    }

    public void navigateToRegister(ActionEvent actionEvent) {
        Parent blah = null;
        try {
            blah = FXMLLoader.load(getClass().getResource("../view/register.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(blah);
        Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();


    }

    public void navigateToPage(String page, ActionEvent actionEvent) {
        Parent blah = null;
        try {
            blah = FXMLLoader.load(getClass().getResource(page));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Context.getInstance().previousPage = "../view/sample.fxml";
        Scene scene = new Scene(blah);
        Stage appStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }



    public void displaySuccessfulReg() {
        errorMessage.setText("Registration successful! Please login.");
        errorMessage.setTextFill(Color.web("#75c24e"));
    }

    public User getGlobalUser() {
        return globalUser;
    }



}