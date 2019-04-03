package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sample.connectivity.ConnectionClass;
import sample.model.Main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("MagicConstant")
public class RegisterUserOnly {
    public TextField firstName;
    public TextField lastName;
    public TextField username;
    public PasswordField password;
    public PasswordField confirmPassword;
    public Label errorMessage;
    public Pane addEmailPane;
    public TextField initialEmail;
    public static TextField[] emails = new TextField[100];
    public int i;
    public static int x = 0;
    public static int y = 0;

    public void goBackToRegNav(ActionEvent actionEvent) {
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

    public void register(ActionEvent actionEvent) throws SQLException {
        String firstNameText = firstName.getText();
        String lastNameText = lastName.getText();
        String usernameText = username.getText();
        String pwText = password.getText();
        String confirmPwText = confirmPassword.getText();
        String initialEmailText = initialEmail.getText();

        // NEED TO CHECK IF USERNAME IS IN DATABASE OR EMAIL IS IN DATABASE
        if (firstNameText.equals("") || lastNameText.equals("") || usernameText.equals("") || pwText.equals("") || confirmPwText.equals("") || initialEmailText.equals("")) {
            errorMessage.setText("Must fill out all fields!");
        } else if (pwText.length() < 8) {
            errorMessage.setText("Password must be 8 characters!");
        } else if (!pwText.equals(confirmPwText)) {
            errorMessage.setText("The passwords do not match!");
        } else {
            System.out.println("Create account");
            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.getConnection();

            String sql = "CALL registerUserOnly('" + firstNameText + "', '" + lastNameText + "', '" + usernameText + "', '" + pwText + "')";
            String sql2 = "CALL addEmail('" + initialEmailText + "', '" + usernameText + "')";
            System.out.println(sql);
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.executeUpdate(sql2);
//
        }
    }

    public void addEmail(ActionEvent actionEvent) {
        emails[i] = new TextField();
        emails[i].setPrefWidth(206.0);
        emails[i].setPrefHeight(39.0);
        emails[i].setLayoutX(x);
        emails[i].setLayoutY(y);
        addEmailPane.getChildren().add(emails[i]);
        i = i + 1;
        y = y + 48;
        System.out.println(y);
    }

}
