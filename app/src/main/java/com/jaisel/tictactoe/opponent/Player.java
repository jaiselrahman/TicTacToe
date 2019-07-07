package com.jaisel.tictactoe.opponent;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.jaisel.tictactoe.Utils.OnJobDoneListener;
import com.jaisel.tictactoe.app.TicTacToeController;

public class Player extends Opponent {
    private TicTacToeController ticTacToeController = new TicTacToeController();
    private LiveData<Integer> move;
    private LiveData<String> status;
    private String userid;
    private String name;

    public Player(Activity activity, String userid, String name) {
        this.userid = userid;
        this.name = name;
        this.move = ticTacToeController.getOpponentMove(activity, userid);
        this.status = ticTacToeController.getOpponentStatus(activity, userid);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return userid;
    }

    @Override
    public LiveData<Integer> getMove() {
        return move;
    }

    @Override
    public LiveData<String> getStatus() {
        return status;
    }

    @Override
    public void setOpponentMove(int position, @Nullable OnJobDoneListener<Void> onJobDoneListener) {
        ticTacToeController.setMove(position, onJobDoneListener);
    }

    @Override
    public void setOpponentStatus(String status, @Nullable OnJobDoneListener<Void> onJobDoneListener) {
        ticTacToeController.setStatus(status, onJobDoneListener);
    }
}
