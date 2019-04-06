package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.model.User;

import java.io.IOException;

public class ManagerVisitorMenu {

    public User globalUser;

    public void setUser(User loggedUser) {
        globalUser = loggedUser;
    }

    public void navigate(ActionEvent actionEvent, String page) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(page));
        Parent root = null;
        try {
            root = (Parent)fxmlLoader.load();
            UserTakeTransit controller = fxmlLoader.<UserTakeTransit>getController();
            controller.setPreviousPage("../view/managerVisitorMenu.fxml");
            controller.setUser(globalUser);
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
}
