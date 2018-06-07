package com.jaisel.tictactoe;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.jaisel.tictactoe.Utils.UserAccount;
import com.jaisel.tictactoe.volley.FriendsAdapter;

import java.util.ArrayList;

public class AccountActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    private UserAccount mUserAccount = UserAccount.getInstance();
    private ArrayList<UserItem> mUserFriends;
    private FriendsAdapter mListViewAdapter;
    private FirebaseUser user;

    private ListView mFriendsListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (false && user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user = mUserAccount.getUser();
        TextView name = findViewById(R.id.account_name);
        TextView email = findViewById(R.id.account_phone);
        mFriendsListView = findViewById(R.id.friends_list);
        findViewById(R.id.account_name_edit).setOnClickListener(this);

        if (user != null) {
            String n = user.getDisplayName();
            if (!TextUtils.isEmpty(n)) {
                name.setText(n);
            }
            String e = user.getEmail();
            if (!TextUtils.isEmpty(e)) {
                email.setText(user.getEmail());
            }
        }
        if (!hasPhoneContactsPermission(android.Manifest.permission.READ_CONTACTS)) {
            requestPermission(Manifest.permission.READ_CONTACTS);
        } else {
            mUserFriends = getContacts();
        }

        mListViewAdapter  = new FriendsAdapter(this, mUserFriends);
        mFriendsListView.setAdapter(mListViewAdapter);

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
                Toast.makeText(this, "You allowed permission, please click the button again.", Toast.LENGTH_LONG).show();
                mUserFriends = getContacts();
                mListViewAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "You denied permission.", Toast.LENGTH_LONG).show();
            }
        }
    }

    ArrayList<UserItem> getContacts() {
        ArrayList<UserItem> userItems = new ArrayList<>();

        final String[] projections = {
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
        };

        ContentResolver contentResolver = this.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                projections,
                null,
                null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                if (mimeType.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    UserItem userItem = new UserItem();
                    userItem.setName(name);
                    userItem.setPhoneNumber(phoneNumber);
                    userItems.add(userItem);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return userItems;
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