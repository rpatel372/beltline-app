package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ManagerOnlyMenu {
    public String previousPage;
    public Label errorMessage;

    public void initialize() {
        previousPage = Context.getInstance().currentPreviousPage();
    }

    public void navigate(ActionEvent actionEvent, String page) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(page));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/managerOnlyMenu.fxml";
            root = (Parent) fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void takeTransit(ActionEvent actionEvent) {
        navigate(actionEvent, "../view/userTakeTransit.fxml");
    }

    public void manageProfile(ActionEvent actionEvent) {
        navigate(actionEvent, "../view/employeeManageProfile.fxml");
    }

    public void manageEvent(ActionEvent actionEvent) throws SQLException {

        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = null;
        stmt = connection.createStatement();

        String sql = "CALL getSiteManaged('" + Context.getInstance().currentUser().username + "')";
        System.out.println(sql);


        ResultSet rs = stmt.executeQuery(sql);

        if (rs.next()) {

            String page = "../view/managerManageEvent.fxml";
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(page));
            Parent root = null;
            try {
                Context.getInstance().previousPage = "../view/managerOnlyMenu.fxml";
                root = (Parent) fxmlLoader.load();
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                ManagerManageEvent controller = fxmlLoader.<ManagerManageEvent>getController();
                controller.setSiteName(rs.getString(1));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorMessage.setText("You do not manage a site!");
        }
    }

    public void transitHistory(ActionEvent actionEvent) {
        navigate(actionEvent, "../view/adminManageUser.fxml");
    }

    public void viewStaff(ActionEvent actionEvent) {
        navigate(actionEvent, "../view/managerManageStaff.fxml");
    }

    public void viewSiteReport(ActionEvent actionEvent) {
        navigate(actionEvent, "../view/adminManageSite.fxml");
    }

    public void goBack(ActionEvent actionEvent) {
    }
}
