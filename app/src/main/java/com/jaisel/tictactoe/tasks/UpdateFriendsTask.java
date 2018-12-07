package com.jaisel.tictactoe.tasks;

import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.collection.ArraySet;

import com.jaisel.tictactoe.Utils.Job;
import com.jaisel.tictactoe.Utils.OnJobDoneListener;
import com.jaisel.tictactoe.Utils.User;
import com.jaisel.tictactoe.Utils.UserAccount;
import com.jaisel.tictactoe.Utils.UserDatabase;

public class UpdateFriendsTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "UpdateFriendsTask";
    @NonNull
    private OnJobDoneListener<Void> job;
    private UserDatabase db = UserAccount.getDb();

    public UpdateFriendsTask(@NonNull OnJobDoneListener<Void> job) {
        this.job = job;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        final ArraySet<User> contacts = UserAccount.getContacts();
        if (contacts == null) return null;
        Job<ArraySet<String>> jobResult = UserAccount.getFriendsFromContacts(contacts);
        if (jobResult == null) return null;
        if (jobResult.isSuccessful()) {
            db.userDao().deleteAll();
            ArraySet<String> ids = jobResult.getResult();
            User tempUser = new User();
            for (String id : ids) {
                tempUser.setId(id);
                int i = contacts.indexOf(tempUser);
                if (i >= 0) {
                    db.userDao().insert(contacts.valueAt(i));
                }
            }
        }
        return jobResult.isSuccessful();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result == null)
            job.onError();
        else {
            job.onComplete(new Job<Void>(null, result));
        }
    }
}
