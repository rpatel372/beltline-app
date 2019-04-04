package sample.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.model.Transit;

public class UserTakeTransit {

    public TableView<Transit> transitTable;

    public TableColumn<Transit, String> typeCol;
    public TableColumn<Transit, String> routeCol;
    public TableColumn<Transit, Integer> priceCol;
    public TableColumn<Transit, String> connectedCol;


    public void testing(ActionEvent actionEvent) {

        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        routeCol.setCellValueFactory(new PropertyValueFactory<>("route"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        connectedCol.setCellValueFactory(new PropertyValueFactory<>("connected"));


        //TODO: use the FILTER command to filter results
        
        Transit t1 = new Transit("hi","hi", 5, "hi");
        Transit t2 = new Transit("hello","hi", 5, "hi");
        transitTable.getItems().add(t1);

    }
}
