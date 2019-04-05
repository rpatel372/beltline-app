package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.model.User;

import java.io.IOException;

public class UserMenu {

    public User globalUser;

    public void takeTransit(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/UsertakeTransit.fxml"));
        Parent root = null;
        try {
            root = (Parent)fxmlLoader.load();
            UserTakeTransit controller = fxmlLoader.<UserTakeTransit>getController();
            controller.setUser(globalUser);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void goBack(ActionEvent actionEvent) {

    }


    public void viewTransitHistory(ActionEvent actionEvent) {

    }

    public void setUser(User loggedUser) {
        globalUser = loggedUser;
    }
}

