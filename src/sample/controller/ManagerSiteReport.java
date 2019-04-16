package sample.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.connectivity.ConnectionClass;
import sample.model.Context;
import sample.model.Event;
import sample.model.Site;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ManagerSiteReport {
    public String previousPage;

    public TextField startField;
    public TextField endField;
    public TextField staffLow;
    public TextField staffHigh;
    public TextField eventLow;
    public TextField eventHigh;
    public TextField visitsLow;
    public TextField visitsHigh;
    public TextField revenueHigh;
    public TextField revenueLow;

    public Label errorMessage;

    public String startDate;
    public String endDate;
    public String staffLowValue;
    public String staffHighValue;
    public String eventLowValue;
    public String eventHighValue;
    public String visitsLowValue;
    public String visitsHighValue;
    public String revenueHighValue;
    public String revenueLowValue;
    public int numDays = 0;

    public String siteName;

    public ChoiceBox sortBy;
    public String sorting = "";

    public TableView<SiteReportEntry> siteReportTable;
    public TableColumn dateCol;
    public TableColumn eventCountCol;
    public TableColumn staffCountCol;
    public TableColumn totalVisitsCol;
    public TableColumn totalRevenueCol;

    public class SiteReportEntry {
        String date;
        int eventCount;
        int staffCount;
        int totalVisits;
        double totalRevenue;

        public SiteReportEntry(String d, int e, int s, int v, int r) {
            date = d;
            eventCount = e;
            staffCount = s;
            totalVisits = v;
            totalRevenue = r;
        }
        public String getDate() { return date; }
        public int getEventCount() { return eventCount; }
        public int getStaffCount() { return staffCount; }
        public int getTotalVisits() { return totalVisits; }
        public double getTotalRevenue() { return totalRevenue; }
    }


    public void initialize() {
        previousPage = Context.getInstance().currentPreviousPage();
    }
    public void setSiteName(String site) { siteName = site; }

    public void addToTable() throws SQLException {
        errorMessage.setText("");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        eventCountCol.setCellValueFactory(new PropertyValueFactory<>("eventCount"));
        staffCountCol.setCellValueFactory(new PropertyValueFactory<>("staffCount"));
        totalVisitsCol.setCellValueFactory(new PropertyValueFactory<>("totalVisits"));
        totalRevenueCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));

        siteReportTable.getItems().clear();
        List<SiteReportEntry> entries = new ArrayList<SiteReportEntry>();

        ConnectionClass connectionClass = new ConnectionClass();
        Connection connection = connectionClass.getConnection();
        Statement stmt = connection.createStatement();
        String sql = "CALL getSiteReport('" + siteName + "', '" + startDate
                + "', '" + numDays + "', '" + eventLowValue + "', '"
                + eventHighValue + "', '" + staffLowValue
                + "', '" + staffHighValue + "', '" + visitsLowValue
                + "', '" + visitsHighValue
                + "', '" + revenueLowValue + "', '" + revenueHighValue + "', '" + sorting + "')";

        System.out.println(sql);

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            SiteReportEntry newEntry =
                    new SiteReportEntry(rs.getString(1),
                            rs.getInt(2),
                            rs.getInt(3),
                            rs.getInt(4),
                            rs.getInt(5));
            entries.add(newEntry);
        }
        siteReportTable.getItems().addAll(entries);

    }

    public void filter(ActionEvent actionEvent) throws ParseException, SQLException {
        boolean canYouAdd = true;
        if (startField.getText().trim().equals("") || endField.getText().trim().equals("")) {
            errorMessage.setText("You must enter a start date and end date before filtering!");
        } else {
            if (!startField.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
                errorMessage.setText("Start Date is not in correct format! Please follow format listed.");
                canYouAdd = false;
            } else {
                startDate = startField.getText().trim();
            }

            if (!endField.getText().trim().matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
                errorMessage.setText("End Date is not in correct format! Please follow format listed.");
                canYouAdd = false;
            } else {
                endDate = endField.getText().trim();
            }

            if (!staffLow.getText().trim().equals("") && !staffLow.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Staff low value must be an integer.");
                canYouAdd = false;
            } else if (staffLow.getText().trim().equals("")) {
                staffLowValue = "0";
            } else {
                staffLowValue = staffLow.getText().trim();
            }
            if (!staffHigh.getText().trim().equals("") && !staffHigh.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Staff high value must be an integer.");
                canYouAdd = false;
            } else if (staffHigh.getText().trim().equals("")) {
                staffHighValue = "9999999";
            } else {
                staffHighValue = staffHigh.getText().trim();
            }

            if (!eventLow.getText().trim().equals("") && !eventLow.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Event low value must be an integer.");
                canYouAdd = false;
            } else if (eventLow.getText().trim().equals("")) {
                eventLowValue = "0";
            } else {
                eventLowValue = eventLow.getText().trim();
            }
            if (!eventHigh.getText().trim().equals("") && !eventHigh.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Event high value must be an integer.");
                canYouAdd = false;
            } else if (eventHigh.getText().trim().equals("")) {
                eventHighValue = "9999999";
            } else {
                eventHighValue = eventHigh.getText().trim();
            }

            if (!visitsLow.getText().trim().equals("") && !visitsLow.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Visits low value must be an integer.");
                canYouAdd = false;
            } else if (visitsLow.getText().trim().equals("")) {
                visitsLowValue = "0";
            } else {
                visitsLowValue = visitsLow.getText().trim();
            }
            if (!visitsHigh.getText().trim().equals("") && !visitsHigh.getText().trim().matches("[0-9]+")) {
                errorMessage.setText("Visits high value must be an integer.");
                canYouAdd = false;
            } else if (visitsHigh.getText().trim().equals("")) {
                visitsHighValue = "9999999";
            } else {
                visitsHighValue = visitsHigh.getText().trim();
            }

            if (!revenueLow.getText().trim().equals("")) {
                try {
                    Double num = Double.parseDouble(revenueLow.getText().trim());
                } catch (NumberFormatException e) {
                    errorMessage.setText("Low revenue must be a number!");
                    canYouAdd = false;
                }
            }
            if (!revenueHigh.getText().trim().equals("")) {
                try {
                    Double num = Double.parseDouble(revenueHigh.getText().trim());
                } catch (NumberFormatException e) {
                    errorMessage.setText("Higher revenue must be a number!");
                    canYouAdd = false;
                }
            }

            if (canYouAdd) {
                Date sdate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
                Date edate = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
                if (sdate.compareTo(edate) > 0) {
                    errorMessage.setText("End date must come after start date!");
                } else {
                    long diffInMillies = Math.abs(edate.getTime() - sdate.getTime());
                    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;
                    numDays = (int)diff;

                    if (revenueLow.getText().trim().equals("")) {
                        revenueLowValue = "0";
                    } else {
                        revenueLowValue = revenueLow.getText().trim();
                    }
                    if (revenueHigh.getText().trim().equals("")) {
                        revenueHighValue = "9999999";
                    } else {
                        revenueHighValue = revenueHigh.getText().trim();
                    }

                    addToTable();
                }
            }
        }
    }

    public void dailyDetail(ActionEvent actionEvent) {
    }

    public void sort(ActionEvent actionEvent) throws SQLException {
        if (sortBy.getValue() != null) {
            sorting = sortBy.getValue().toString();
            addToTable();
        }
    }

    public void goBack(ActionEvent actionEvent) {
    }
}
