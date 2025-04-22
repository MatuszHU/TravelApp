package hu.matusz.travelapp.databaseUtil.dataClasses;

public class Comment {
    private int userId;
    private int commentId;
    private String title;
    private String comment;
    private int rate;

    public Comment(int userId, int commentId, String title, String comment, int rate) {
        this.userId = userId;
        this.commentId = commentId;
        this.title = title;
        this.comment = comment;
        this.rate = rate;
    }
}
