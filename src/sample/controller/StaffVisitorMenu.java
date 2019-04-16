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

public class StaffVisitorMenu {
    public String previousPage;

    public void initialize() {
        previousPage = Context.getInstance().currentPreviousPage();
    }

    public void navigate(ActionEvent actionEvent, String page) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(page));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/staffVisitorMenu.fxml";
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

    public void exploreEvent(ActionEvent actionEvent) {
        navigate(actionEvent, "../view/visitorExploreEvent.fxml");
    }

    public void viewTransitHistory(ActionEvent actionEvent) {
        navigate(actionEvent, "../view/transitHistory.fxml");
    }

    public void goBack(ActionEvent actionEvent) {
        navigate(actionEvent, previousPage);
    }

    public void viewSchedule(ActionEvent actionEvent) {
        navigate(actionEvent, "../view/staffViewSchedule.fxml");
    }

    public void viewVisitHistory(ActionEvent actionEvent) {
        navigate(actionEvent, "../view/visitorVisitHistory.fxml");
    }

    public void exploreSite(ActionEvent actionEvent) {
        navigate(actionEvent, "../view/visitorExploreSite.fxml");
    }
}
