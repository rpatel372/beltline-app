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

public class UserMenu {

    public String previousPage;

    public void initialize() {

        previousPage = Context.getInstance().currentPreviousPage();
//        System.out.println(globalUser.username);
//        System.out.println(previousPage);
    }

    public void takeTransit(ActionEvent actionEvent) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/userTakeTransit.fxml"));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/userMenu.fxml";
            root = (Parent) fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(previousPage));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/userMenu.fxml";
            root = (Parent) fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void viewTransitHistory(ActionEvent actionEvent) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/transitHistory.fxml"));
        Parent root = null;
        try {
            Context.getInstance().previousPage = "../view/userMenu.fxml";
            root = (Parent) fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

