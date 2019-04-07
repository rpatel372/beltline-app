package sample.model;

public class TranHist {
    public String date;
    public String route;
    public String type;
    public double price;

    public TranHist(String d, String r, String t, double p) {
        date = d;
        route = r;
        type = t;
        price = p;
    }

    public String getDate() {
        return date;
    }
    public String getRoute() {
        return route;
    }
    public String getType() {
        return type;
    }
    public double getPrice() {
        return price;
    }
}
