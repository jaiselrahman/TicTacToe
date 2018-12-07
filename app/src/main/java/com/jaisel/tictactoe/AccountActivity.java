package com.jaisel.tictactoe;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jaisel.tictactoe.Utils.Job;
import com.jaisel.tictactoe.Utils.OnJobDoneListener;
import com.jaisel.tictactoe.Utils.User;
import com.jaisel.tictactoe.Utils.UserAccount;
import com.jaisel.tictactoe.volley.FriendsAdapter;

import java.util.ArrayList;
import java.util.List;

public class AccountActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AccountActivity";
    private UserAccount user = UserAccount.getInstance();
    private FriendsAdapter friendsAdapter;
    private ImageView refresh;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView name = findViewById(R.id.account_name);
        name.setText(user.getName());
        TextView email = findViewById(R.id.account_phone);
        email.setText(user.getPhoneNumber());

        findViewById(R.id.account_name_edit).setOnClickListener(this);

        ListView friendsListView = findViewById(R.id.friends_list);
        friendsAdapter = new FriendsAdapter(this);
        friendsListView.setAdapter(friendsAdapter);

        if (!hasPhoneContactsPermission(android.Manifest.permission.READ_CONTACTS)) {
            requestPermission(Manifest.permission.READ_CONTACTS);
        } else {
            loadFriends();
        }

        progressBar = findViewById(R.id.refresh_progress);
        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFriends();
            }
        });
    }

    private void updateFriends() {
        refresh.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        user.updateFriends(new OnJobDoneListener<Void>() {
            @Override
            public void onComplete(Job<Void> job) {
                if (!job.isSuccessful())
                    Toast.makeText(AccountActivity.this, "Check your Network", Toast.LENGTH_SHORT).show();
                else loadFriends();
                refresh.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void loadFriends() {
        user.getFriends(new OnJobDoneListener<List<User>>() {
            @Override
            public void onComplete(Job<List<User>> job) {
                if (job.isSuccessful())
                    friendsAdapter.setFriends((ArrayList<User>) job.getResult());
            }
        });
    }

    private boolean hasPhoneContactsPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermission = ContextCompat.checkSelfPermission(this, permission);
            return hasPermission == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission(String permission) {
        String requestPermissionArray[] = {permission};
        ActivityCompat.requestPermissions(this, requestPermissionArray, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int length = grantResults.length;
        if (length > 0) {
            int grantResult = grantResults[0];
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                updateFriends();
            } else {
                Toast.makeText(this, "Please grant permission to find your friends.", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onClick(View v) {
        AccountEditFragment accountEditFragment;
        Bundle bundle;
        switch (v.getId()) {
//            case R.id.delete_account:
//                new AlertDialog.Builder(this)
//                        .setTitle(R.string.app_name)
//                        .setMessage(R.string.account_will_deleted_permanently)
//                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            Toast.makeText(AccountActivity.this, R.string.account_deleted_successfully, Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            Toast.makeText(AccountActivity.this, R.string.failed_to_delete_account, Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .setNegativeButton(R.string.no, null)
//                        .show();
//                break;
            case R.id.account_name_edit:
                bundle = new Bundle();
                bundle.putString("edit", "name");
                accountEditFragment = new AccountEditFragment();
                accountEditFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_main, accountEditFragment)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }
}