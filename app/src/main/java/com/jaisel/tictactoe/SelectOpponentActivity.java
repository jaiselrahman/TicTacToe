package com.jaisel.tictactoe;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.jaisel.tictactoe.Utils.Job;
import com.jaisel.tictactoe.Utils.OnJobDoneListener;
import com.jaisel.tictactoe.Utils.User;
import com.jaisel.tictactoe.Utils.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class SelectOpponentActivity extends AppCompatActivity {
    private UserAccount user = UserAccount.getInstance();
    private ArrayList<User> mFriendsList;
    private ArrayList<String> friendsName = new ArrayList<>();
    private int mPosition = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_opponent);

        final ListView mListView = findViewById(R.id.select_friend_list);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        final ArrayAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, friendsName);
        mListView.setAdapter(listAdapter);

        user.getFriends(new OnJobDoneListener<List<User>>() {
            @Override
            public void onComplete(Job<List<User>> job) {
                if (job.isSuccessful()) {
                    mFriendsList = (ArrayList<User>) job.getResult();
                    for (User user : mFriendsList) {
                        friendsName.add(user.getName());
                    }
                    listAdapter.notifyDataSetChanged();
                }
            }
        });

        Button select = findViewById(R.id.select_friend_select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mListView.getCheckedItemPosition();
                if (position < 0) return;
                user.sendPlayRequest(mFriendsList.get(position).getId(), new OnJobDoneListener<Void>() {
                    @Override
                    public void onComplete(Job<Void> job) {
                        if (job.isSuccessful()) {
                            Toast.makeText(SelectOpponentActivity.this, "Play request sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SelectOpponentActivity.this, "Sending play request failed\nTry again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
