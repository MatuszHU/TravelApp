package hu.matusz.travelapp.util.database.models;

public class User {
    private String userId;
    private String name;
    private String email;
    private String countryOfOriginCode;
    private String photoURI;


    public User() {

    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", countryOfOriginCode='" + countryOfOriginCode + '\'' +
                ", photoURI='" + photoURI + '\'' +
                '}';
    }

    public User(String userId, String name, String email, String photoURI) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.photoURI = photoURI;
        this.countryOfOriginCode = "";
    }

    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.countryOfOriginCode = "";
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

    public String getPhotoURI() {
        return photoURI;
    }

    public void setPhotoURI(String photoURI) {
        this.photoURI = photoURI;
    }
}