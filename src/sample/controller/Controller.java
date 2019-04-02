
package sample.controller;

import sample.connectivity.ConnectionClass;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Controller {
    public TextField emailField;
    public Label errorMessage;

    public void login(ActionEvent actionEvent) throws SQLException {
        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();

        String sql= "INSERT INTO USER VALUES('" + emailField.getText() + "')";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);

//        errorMessage.setText(textfield.getText());
    }
}