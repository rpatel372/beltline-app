
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
                System.out.println(userType);
                System.out.println("Successful login");
                globalUser = new User(username, userType);
                //TODO: need to figure out user type and navigate them to the correct menu page
                if (userType.equals("User")) {
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

                } else if (userType.equals("Visitor")) {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/visitorMenu.fxml"));
                    Parent root = null;
                    try {
                        root = (Parent)fxmlLoader.load();
                        VisitorMenu controller = fxmlLoader.<VisitorMenu>getController();
                        User newUser = new User(username, userType);
                        controller.setUser(newUser);
                        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if(userType.equals("Employee")) {
                    String sql9 = "CALL getEmployeeType('" + username + "')";
                    ResultSet rs3 = stmt.executeQuery(sql9);
                    if (rs3.next()) {
                        String empType = rs.getString("EmployeeType");
                        if (empType.equals("Admin")) {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/adminOnlyMenu.fxml"));
                            Parent root = null;
                            try {
                                root = (Parent)fxmlLoader.load();
                                AdminOnlyMenu controller = fxmlLoader.<AdminOnlyMenu>getController();
                                User newUser = new User(username, userType);
                                controller.setUser(newUser);
                                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                                Scene scene = new Scene(root);
                                stage.setScene(scene);
                                stage.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (empType.equals("Staff")) {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/staffOnlyMenu.fxml"));
                            Parent root = null;
                            try {
                                root = (Parent)fxmlLoader.load();
                                StaffOnlyMenu controller = fxmlLoader.<StaffOnlyMenu>getController();
                                User newUser = new User(username, userType);
                                controller.setUser(newUser);
                                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                                Scene scene = new Scene(root);
                                stage.setScene(scene);
                                stage.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/managerOnlyMenu.fxml"));
                            Parent root = null;
                            try {
                                root = (Parent)fxmlLoader.load();
                                ManagerOnlyMenu controller = fxmlLoader.<ManagerOnlyMenu>getController();
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
                    }
                } else {
                    // EMPLOYEE-VISITOR
                    Statement stmt2 = connection.createStatement();
                    String sql9 = "CALL getEmployeeType('" + username + "')";
                    ResultSet rs3 = stmt2.executeQuery(sql9);
                    if (rs3.next()) {
                        String empType = rs3.getString("EmployeeType");
                        if (empType.equals("Admin")) {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/adminVisitorMenu.fxml"));
                            Parent root = null;
                            try {
                                root = (Parent)fxmlLoader.load();
                                AdminVisitorMenu controller = fxmlLoader.<AdminVisitorMenu>getController();
                                User newUser = new User(username, userType);
                                controller.setUser(newUser);
                                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                                Scene scene = new Scene(root);
                                stage.setScene(scene);
                                stage.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (empType.equals("Staff")) {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/staffVisitorMenu.fxml"));
                            Parent root = null;
                            try {
                                root = (Parent)fxmlLoader.load();
                                StaffVisitorMenu controller = fxmlLoader.<StaffVisitorMenu>getController();
                                User newUser = new User(username, userType);
                                controller.setUser(newUser);
                                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                                Scene scene = new Scene(root);
                                stage.setScene(scene);
                                stage.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/managerVisitorMenu.fxml"));
                            Parent root = null;
                            try {
                                root = (Parent)fxmlLoader.load();
                                ManagerVisitorMenu controller = fxmlLoader.<ManagerVisitorMenu>getController();
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



    public void displaySuccessfulReg() {
        errorMessage.setText("Registration successful! Please login.");
        errorMessage.setTextFill(Color.web("#75c24e"));
    }

    public User getGlobalUser() {
        return globalUser;
    }



}