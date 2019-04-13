package sample.model;

public class Event {
    public String name;
    public int staffCount;
    public int duration;
    public int visits;
    public int revenue;

    public String startDate;
    public String siteName;

    public Event(String n, int s, int d, int v, int r, String sd, String sn) {
        name = n;
        staffCount = s;
        duration = d;
        visits = v;
        revenue = r;
        startDate = sd;
        siteName = sn;
    }

    public String getName() {
        return name;
    }
    public int getStaffCount() {
        return staffCount;
    }
    public int getDuration() {
        return duration;
    }
    public int getVisits() {
        return visits;
    }
    public int getRevenue() {
        return revenue;
    }
    public String getStartDate() { return startDate; }
    public String getSiteName() { return siteName; }
}
