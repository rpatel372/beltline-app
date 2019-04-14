package sample.controller;

import sample.model.Context;

public class ManagerSiteReport {
    public String previousPage;

    public void initialize() {

        previousPage = Context.getInstance().currentPreviousPage();

    }
}
