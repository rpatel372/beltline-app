package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.model.Context;
import sample.model.User;

import java.io.IOException;

public class AdminVisitorMenu {
    public String previousPage;

    public void initialize() {

        previousPage = Context.getInstance().currentPreviousPage();

    }


    public void navigate(ActionEvent actionEvent, String page) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(page));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/adminVisitorMenu.fxml";
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

    public void viewTransitHistory(ActionEvent actionEvent) {
    }

    public void goBack(ActionEvent actionEvent) {
    }

    public void manageProfile(ActionEvent actionEvent) {
    }

    public void manageUser(ActionEvent actionEvent) {
    }

    public void manageTransit(ActionEvent actionEvent) {
    }

    public void manageSite(ActionEvent actionEvent) {
    }

    public void exploreEvent(ActionEvent actionEvent) {
    }

    public void exploreSite(ActionEvent actionEvent) {
    }

    public void viewVisitHistory(ActionEvent actionEvent) {
    }
}
