package hu.matusz.travelapp.util.database;

/**
 * @author matusz
 * @param <T>
 */
public interface Callback<T> {
    void onAnswerReceived(T result);
    void onError(Exception e);
}
