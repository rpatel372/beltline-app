package sample.model;

public class Context {
    private final static Context instance = new Context();

    public static Context getInstance() {
        return instance;
    }

    public User globalUser = new User("", "");
    public String previousPage = "";

    public User currentUser() {
        return globalUser;
    }
    public String currentPreviousPage() {
        return previousPage;
    }
}