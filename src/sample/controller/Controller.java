
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

            //TODO: MAKE SURE UESR IS NOT LOGGING IN WITH A DECLINED ACCOUNT

            if (rs2.next()) {

                System.out.println("Successful login");

                //TODO: need to figure out user type and navigate them to the correct menu page
                
                navigateToCorrectMenu(username, "User", "../view/userMenu.fxml", actionEvent);


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

    public void navigateToCorrectMenu(String username, String userType, String userMenuOption, ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/userMenu.fxml"));
        Parent root = null;
        try {
            root = (Parent)fxmlLoader.load();
            UserMenu controller = fxmlLoader.<UserMenu>getController();
            User newUser = new User(username, userType);
            controller.setUser(newUser);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displaySuccessfulReg() {
        errorMessage.setText("Registration successful! Please login.");
        errorMessage.setTextFill(Color.web("#75c24e"));
    }




}