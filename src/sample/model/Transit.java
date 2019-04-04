package sample.model;

public class Transit {
    public String route;
    public String type;
    public int price;
    public String connected;

    public Transit(String r, String t, int p, String c) {
        route = r;
        type = t;
        price = p;
        connected = c;

    }

    public String getRoute() {
        return route;
    }
    public String getType() {
        return type;
    }
    public int getPrice() {
        return price;
    }
    public String getConnected() {
        return connected;
    }
}
