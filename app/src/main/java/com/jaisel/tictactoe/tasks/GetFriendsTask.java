package com.jaisel.tictactoe.tasks;

import android.os.AsyncTask;

import com.jaisel.tictactoe.Utils.Job;
import com.jaisel.tictactoe.Utils.OnJobDoneListener;
import com.jaisel.tictactoe.Utils.User;
import com.jaisel.tictactoe.Utils.UserAccount;

import java.util.List;

public class GetFriendsTask extends AsyncTask<Void, Void, List<User>> {
    private OnJobDoneListener<List<User>> job;

    public GetFriendsTask(OnJobDoneListener<List<User>> job) {
        this.job = job;
    }

    @Override
    protected List<User> doInBackground(Void... voids) {
        return UserAccount.getDb().userDao().getFriends();
    }

    @Override
    protected void onPostExecute(List<User> users) {
        if (job != null) {
            if (users != null)
                job.onComplete(new Job<>(users, true));
            else
                job.onComplete(new Job<>(users, false));
        }
    }
}