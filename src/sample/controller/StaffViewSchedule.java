package sample.controller;

import sample.model.Context;

public class StaffViewSchedule {
    public String previousPage;

    public void initialize() {

        previousPage = Context.getInstance().currentPreviousPage();

    }

}
