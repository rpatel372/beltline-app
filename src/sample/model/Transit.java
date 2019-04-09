package sample.model;

public class Transit {
    public String route;
    public String type;
    public double price;
    public int connectedSites;
    public int transitsLogged;

    public Transit(String r, String t, double p, int c, int l) {
        route = r;
        type = t;
        price = p;
        connectedSites = c;
        transitsLogged = l;

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
    public int getTransitsLogged() { return transitsLogged; }
}
