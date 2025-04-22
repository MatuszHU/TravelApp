package hu.matusz.travelapp.databaseUtil.dataClasses;

public class User {
    private int userId;
    private String name;
    private String email;
    private String countryOfOriginCode;

    public User(int userId, String name, String email, String countryOfOriginCode) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.countryOfOriginCode = countryOfOriginCode;
    }

    public User(int userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.countryOfOriginCode = "N/A";
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCountryOfOriginCode() {
        return countryOfOriginCode;
    }

    public void setCountryOfOriginCode(String countryOfOriginCode) {
        this.countryOfOriginCode = countryOfOriginCode;
    }
}
