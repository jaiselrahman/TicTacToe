package com.jaisel.tictactoe.opponent;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jaisel.tictactoe.TicTacToe;
import com.jaisel.tictactoe.Utils.OnJobDoneListener;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Computer extends Opponent {
    private MutableLiveData<Integer> move = new MutableLiveData<>();
    private TicTacToe t;
    private Executor executor = Executors.newSingleThreadExecutor();
    private Runnable makeMove = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                int pos = t.compMove();
                if (pos > 0)
                    move.postValue(pos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public Computer(TicTacToe _t) {
        t = _t;
    }

    @Override
    public String getName() {
        return "Computer";
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
        executor.execute(makeMove);
    }

    @Override
    public void setOpponentStatus(String status, @Nullable OnJobDoneListener onJobDoneListener) {
        // Do nothing
    }

    @Override
    public void makeFirstMove() {
        setOpponentMove(0, null);
    }
}
