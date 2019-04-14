package sample.controller;

import sample.model.Context;

public class VisitorExploreEvent {
    public String previousPage;

    public void initialize() {

        previousPage = Context.getInstance().currentPreviousPage();

    }

}
