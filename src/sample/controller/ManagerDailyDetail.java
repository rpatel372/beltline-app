package sample.controller;

import sample.model.Context;

public class ManagerDailyDetail {
    public String previousPage;

    public void initialize() {

        previousPage = Context.getInstance().currentPreviousPage();

    }

}