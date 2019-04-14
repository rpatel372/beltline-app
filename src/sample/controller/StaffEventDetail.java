package sample.controller;

import sample.model.Context;

public class StaffEventDetail {
    public String previousPage;

    public void initialize() {

        previousPage = Context.getInstance().currentPreviousPage();

    }

}