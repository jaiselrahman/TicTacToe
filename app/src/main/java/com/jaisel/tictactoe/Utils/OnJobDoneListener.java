package com.jaisel.tictactoe.Utils;

/**
 * Created by jaisel on 17/7/17.
 */

public abstract class OnJobDoneListener<T> {
    public abstract void onComplete(Job<T> job);
    public void onError() {}
}
