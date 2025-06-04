package hu.matusz.travelapp.util.database;

public interface Callback<T> {
    void onAnswerReceived(T result);
    void onError(Exception e);
}
