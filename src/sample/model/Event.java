package sample.model;

public class Event {
    public String name;
    public int staffCount;
    public int duration;
    public int visits;
    public int revenue;

    public Event(String n, int s, int d, int v, int r) {
        name = n;
        staffCount = s;
        duration = d;
        visits = v;
        revenue = r;
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
}
