package com.jaisel.tictactoe.opponent;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.jaisel.tictactoe.Utils.OnJobDoneListener;

public abstract class Opponent {

    public abstract String getName();

    public abstract String getId();

    public abstract LiveData<Integer> getMove();

    public abstract LiveData<String> getStatus();

    public abstract void setOpponentMove(int position, @Nullable OnJobDoneListener<Void> onJobDoneListener);

    public abstract void setOpponentStatus(String status, @Nullable OnJobDoneListener<Void> onJobDoneListener);

    public void makeFirstMove() {
    }
}
