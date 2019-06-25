package com.jaisel.tictactoe.opponent;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jaisel.tictactoe.TicTacToe;
import com.jaisel.tictactoe.Utils.OnJobDoneListener;

public class Computer extends Opponent {
    private MutableLiveData<Integer> move = new MutableLiveData<>();
    private TicTacToe t;

    public Computer(TicTacToe _t) {
        t = _t;
    }

    @Override
    public String getName() {
        return "Computer";
    }

    @Override
    public int getType() {
        return COMPUTER;
    }

    @Override
    public String getId() {
        return "computer";
    }

    @Override
    public LiveData<Integer> getMove() {
        return move;
    }

    public LiveData<String> getStatus() {
        return new MutableLiveData<>();
    }

    @Override
    public void setOpponentMove(int position, @Nullable OnJobDoneListener onJobDoneListener) {
        int pos = t.compMove();
        if (pos > 0)
            move.setValue(pos);
    }

    @Override
    public void setOpponentStatus(String status, @Nullable OnJobDoneListener onJobDoneListener) {
        // Do nothing
    }
}
