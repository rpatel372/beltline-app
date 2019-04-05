package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sample.connectivity.ConnectionClass;
import sample.model.Main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("MagicConstant")
public class RegisterEmployeeOnly {
    public TextField firstName;
    public TextField lastName;
    public TextField username;
    public PasswordField password;
    public PasswordField confirmPassword;
    public Label errorMessage;
    public Pane addEmailPane;
    public TextField initialEmail;
    public static TextField[] emails = new TextField[100];
    public static Button[] removeButtons = new Button[100];

    public ComboBox userType;
    public TextField phone;
    public TextField address;
    public TextField city;
    public ComboBox state;
    public TextField zipcode;

    public Button initialAddButton;
    public int i;
    public static int x = 1;
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
        } else if (usernameText.length() > 30) {
            errorMessage.setText("Username cannot be more than 30 characters.");
        } else {
            emails[0] = initialEmail;
            System.out.println("Create account");
            ConnectionClass connectionClass = new ConnectionClass();
            Connection connection = connectionClass.getConnection();

            //Validate emails are in correct format
            int incorrectEmailIndex = -1;

            for (int index1 = 0; index1 <= i; index1++) {
                if (emails[index1] != null) {
                    System.out.println(index1);
                    Pattern p = Pattern.compile( "^[a-zA-Z0-9_+&*-]+(?:\\."+
                            "[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                            "A-Z]{2,7}$");
                    Matcher m = p.matcher(emails[index1].getText());
                    if (m.find()) {
                        index1 += 1;
                    } else {
                        incorrectEmailIndex = index1;
                        break;
                    }
                }
            }
            if (incorrectEmailIndex != -1) {
                errorMessage.setText("Email #" + incorrectEmailIndex + " is not a valid email.");
                System.out.println("howdy");
            } else {
                Statement statement = connection.createStatement();

                //Validate username is unique
                String sql3 = "CALL checkIfUsernameUnique('" + usernameText + "')";
                ResultSet rs = statement.executeQuery(sql3);
                if (rs.next()) {

                    errorMessage.setText("Username already exists. Please choose another.");
                } else {
                    //Validate emails are unique
                    boolean invalidEmail = false;
                    for (int c = 0; c <= i; c++) {
                        String sql4 = "CALL checkIfEmailUnique('" + emails[c].getText() + "')";
                        ResultSet rs2 = statement.executeQuery(sql4);
                        if (rs2.next() == true) {
                            errorMessage.setText("Email #" + c + " already in use.");
                            invalidEmail = true;
                            break;
                        }
                    }

                    if (invalidEmail == false) {
                        //TODO: Hash password


                        String sql = "CALL registerUser('" + firstNameText + "', '" + lastNameText + "', '" + usernameText + "', '" + pwText + "', '" + "Pending" + "', '" + "User" + "')";
                        String sql2 = "CALL addEmail('" + initialEmailText + "', '" + usernameText + "')";
                        statement.executeUpdate(sql);
                        statement.executeUpdate(sql2);

                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/sample.fxml"));
                        Parent root = null;
                        try {
                            root = (Parent)fxmlLoader.load();
                            Controller controller = fxmlLoader.<Controller>getController();
                            controller.displaySuccessfulReg();
                            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                            Scene scene = new Scene(root);
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
    }

    public void addEmail(ActionEvent actionEvent) {


        if (i == 0 && initialEmail.getText().equals("")) {
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
            System.out.println(y);

        }
    }

    public void removeEmail(ActionEvent actionEvent) {
        if (i == 0) {
            errorMessage.setText("You must have at least one email!");
        } else {
//            System.out.println(i);
            addEmailPane.getChildren().remove(emails[i-1]);
            emails[i] = null;
            i = i - 1;
        }
    }

}
