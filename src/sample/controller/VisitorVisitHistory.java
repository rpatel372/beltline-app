package sample.controller;

import sample.model.Context;

public class VisitorVisitHistory{
    public String previousPage;

        public void initialize() {

            previousPage = Context.getInstance().currentPreviousPage();

        }

}
