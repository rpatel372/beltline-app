package sample.model;

public class User2 {
    String username;
    int emailCount;
    String userType;
    String status;

    public User2(String u, int e, String t, String s) {
        username = u;
        emailCount = e;
        userType = t;
        status = s;
    }

    public String getUsername() {
        return username;
    }
    public int getEmailCount() {
        return emailCount;
    }
    public String getUserType() {
        return userType;
    }
    public String getStatus() {
        return status;
    }

}
