package sample.model;

public class Transit {
    public String route;
    public String type;
    public double price;
    public int connectedSites;

    public Transit(String r, String t, double p, int c) {
        route = r;
        type = t;
        price = p;
        connectedSites = c;

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
    public int getConnected() {
        return connectedSites;
    }
}
