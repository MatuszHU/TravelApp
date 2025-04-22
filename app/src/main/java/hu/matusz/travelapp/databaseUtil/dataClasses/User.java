package hu.matusz.travelapp.databaseUtil.dataClasses;

public class User {
    private String userId;  // String típusúra változtattuk
    private String name;
    private String email;
    private String countryOfOriginCode;

    // Üres konstruktor Firebase-hez
    public User() {
        // Szükséges a Firebase-hez
    }

    public User(String userId, String name, String email, String countryOfOriginCode) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.countryOfOriginCode = countryOfOriginCode;
    }

    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.countryOfOriginCode = "N/A";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryOfOriginCode() {
        return countryOfOriginCode;
    }

    public void setCountryOfOriginCode(String countryOfOriginCode) {
        this.countryOfOriginCode = countryOfOriginCode;
    }
}