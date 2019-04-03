
package sample.controller;

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

public class Controller {
    public TextField emailField;
    public Label errorMessage;
    private Main mainApp;

    public void login(ActionEvent actionEvent) throws SQLException {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = connection.createStatement();

        String sql = "CALL getEmailForLogin('" + emailField.getText() + "')";
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next() == false) {
            errorMessage.setText("There is no account with this email!");
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




}