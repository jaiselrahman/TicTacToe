package com.jaisel.tictactoe.Utils;

/**
 * Created by jaisel on 3/25/18.
 */

public class Job<T> {
    private T result;
    private boolean isSuccessful;

    Job(T result, boolean status) {
        this.result = result;
        this.isSuccessful = status;
    }

    public T getResult() {
        return result;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}

