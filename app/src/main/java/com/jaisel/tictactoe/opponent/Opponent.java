package com.jaisel.tictactoe.opponent;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.jaisel.tictactoe.Utils.OnJobDoneListener;

public abstract class Opponent {
    public final static int PLAYER = 1;
    public final static int COMPUTER = 0;

    public abstract String getName();

    public abstract int getType();

    public abstract String getId();

    public abstract LiveData<Integer> getMove();

    public abstract LiveData<String> getStatus();

    public abstract void setOpponentMove(int position, @Nullable OnJobDoneListener<Void> onJobDoneListener);

    public abstract void setOpponentStatus(String status, @Nullable OnJobDoneListener<Void> onJobDoneListener);
}
