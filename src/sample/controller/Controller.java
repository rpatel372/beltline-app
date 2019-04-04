
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
            String sql2 = "CALL userLogin('" + username + "')";
            ResultSet rs2 = stmt.executeQuery(sql2);

            //TODO: FIX LOGIN (in relation to the piazza question)

            if (rs2.next()) {

                System.out.println("Successful login");

                //TODO: need to figure out user type and navigate them to the correct menu page


            } else {
                errorMessage.setText("User credentials are incorrect!");
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

    public void displaySuccessfulReg() {
        errorMessage.setText("Registration successful! Please login.");
        errorMessage.setTextFill(Color.web("#75c24e"));
    }




}