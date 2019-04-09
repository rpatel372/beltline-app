package sample.model;

public class Site {

    String name;
    String manager;
    String openEveryday;

    public Site(String n, String m, String o) {
        name = n;
        manager = m;
        openEveryday = o;
    }
    public String getName() {
        return name;
    }
    public String getManager() {
        return manager;
    }
    public String getOpenEveryday() {
        return openEveryday;
    }
}
