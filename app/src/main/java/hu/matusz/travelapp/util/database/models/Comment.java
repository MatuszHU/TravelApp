package hu.matusz.travelapp.util.database.models;

public class Comment {
    private String userId;  // String típusúra változtattuk
    private String commentId;  // String típusúra változtattuk
    private String title;
    private String comment;
    private int rate;
    private String geoId;  // Új mező a helyszínhez kapcsolódáshoz

    // Üres konstruktor Firebase-hez
    public Comment() {
        // Szükséges a Firebase-hez
    }

    public Comment(String userId, String commentId, String title, String comment, int rate, String geoId) {
        this.userId = userId;
        this.commentId = commentId;
        this.title = title;
        this.comment = comment;
        this.rate = rate;
        this.geoId = geoId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getGeoId() {
        return geoId;
    }

    public void setGeoId(String geoId) {
        this.geoId = geoId;
    }
}