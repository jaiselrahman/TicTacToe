package com.jaisel.tictactoe.Utils;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.jaisel.tictactoe.R;
import com.jaisel.tictactoe.app.AppController;
import com.jaisel.tictactoe.data.Constants;
import com.jaisel.tictactoe.tasks.GetFriendsTask;
import com.jaisel.tictactoe.tasks.UpdateFriendsTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by jaisel on 16/7/17.
 */

public class UserAccount {
    private static String TAG = UserAccount.class.getSimpleName();
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseUser firebaseUser;
    private static CollectionReference users = FirebaseFirestore.getInstance().collection("users");
    private static UserAccount userAccount;
    private static UserDatabase db;
    private static String verificationCode;
    private DocumentReference currentUserRef;
    private User currentUser;

    private UserAccount(User user) {
        currentUserRef = users.document(firebaseUser.getUid());
        currentUser = user;
    }

    @Nullable
    public static UserAccount getInstance() {
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            userAccount = null;
            return null;
        }
        if (userAccount == null) {
            db = Room.databaseBuilder(AppController.getInstance().getApplicationContext(),
                    UserDatabase.class, "TicTacToe").build();
            User currentUser = getCurrentUser();
            userAccount = new UserAccount(currentUser);
        }
        return userAccount;
    }

    private static CollectionReference getUserColRef() {
        return users;
    }

    public static DocumentReference getUserDocRef(String s) {
        return users.document(s);
    }

    public static UserDatabase getDb() {
        return db;
    }

    public static void signIn(final Activity activity, final User user, @Nullable final OnJobDoneListener<AuthResult> onJobDoneListener) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                user.getId(), 90, TimeUnit.SECONDS, activity, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInWithCredential(user, phoneAuthCredential, onJobDoneListener);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        e.printStackTrace();
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(activity, R.string.invalid_code, Toast.LENGTH_SHORT).show();
                            if (onJobDoneListener != null) {
                                onJobDoneListener.onError();
                            }

                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            //TODO: inform me;
                            Log.w(TAG, "SMS quota exceeded");
                        } else {
                            Log.w(TAG, "onVerificationFailed", e);
                        }
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                        Toast.makeText(activity, R.string.time_out, Toast.LENGTH_SHORT).show();
                        if (onJobDoneListener != null) {
                            onJobDoneListener.onComplete(new Job<AuthResult>(null, false));
                        }
                    }
                });
    }

    public static void verifySignIn(User user, String code, final OnJobDoneListener<AuthResult> onJobDoneListener) {
        try {
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationCode, code);
            signInWithCredential(user, phoneAuthCredential, onJobDoneListener);
        } catch (Exception e) {
            e.printStackTrace();
            onJobDoneListener.onError();
        }
    }

    private static void signInWithCredential(final User user, PhoneAuthCredential phoneAuthCredential, final OnJobDoneListener<AuthResult> onJobDoneListener) {
        auth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            UserProfileChangeRequest.Builder userProfileChangeRequest = new UserProfileChangeRequest.Builder();
                            userProfileChangeRequest.setDisplayName(user.getName());
                            firebaseUser.updateProfile(userProfileChangeRequest.build());
                            Map<String, Object> userdetails = new HashMap<>();
                            userdetails.put("lastmove", 0);
                            userdetails.put("status", "");
                            userdetails.put("isonline", true);
                            getUserColRef()
                                    .document(user.getId())
                                    .set(userdetails);
                            onJobDoneListener.onComplete(new Job<>(task.getResult(), task.isSuccessful()));
                        } else {
                            onJobDoneListener.onComplete(new Job<AuthResult>(null, false));
                        }
                    }
                });
    }

    private static User getCurrentUser() {
        return new User(
                firebaseUser.getPhoneNumber(),
                firebaseUser.getDisplayName(),
                firebaseUser.getPhotoUrl()
        );
    }

    @Nullable
    public static ArraySet<User> getContacts() {
        ArraySet<User> contacts = null;
        final String[] projections = {
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
        };

        ContentResolver contentResolver = AppController
                .getInstance()
                .getContentResolver();

        Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                projections,
                null,
                null, null);

        if (cursor != null && cursor.getCount() > 0) {
            contacts = new ArraySet<>(cursor.getCount());
            cursor.moveToFirst();
            do {
                String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                if (mimeType.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Pair<Boolean, String> result = getProperPhoneNumber(phoneNumber);
                    if (!result.first) continue;
                    User user = new User();
                    user.setName(name);
                    user.setId(result.second);
                    contacts.add(user);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return contacts;
    }

    private static Pair<Boolean, String> getProperPhoneNumber(String phoneNumber) {
        boolean status = true;
        if (phoneNumber.charAt(0) == '+') {
            if (!phoneNumber.startsWith("+91")) status = false;
            else if (phoneNumber.length() != 13) status = false;
        } else {
            if (phoneNumber.length() != 10) status = false;
            else
                phoneNumber = "+91" + phoneNumber;
        }
        return new Pair<>(status, phoneNumber);
    }

    @Nullable
    public static Job<ArraySet<String>> getFriendsFromContacts(final ArraySet<User> contacts) {
        JSONObject jsonContacts = new JSONObject();
        Job<ArraySet<String>> jobResult = null;
        try {
            for (User user : contacts) {
                jsonContacts.accumulate("contacts", user.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest contactsReq = new JsonObjectRequest(Request.Method.POST, Constants.SERVER,
                jsonContacts, future, future);

        AppController.getInstance().addToRequestQueue(contactsReq);
        try {
            JSONObject jsonUsers = future.get();
            if (jsonUsers != null) {
                JSONArray json = jsonUsers.getJSONArray("data");
                int size = json.length();
                ArraySet<String> friends = null;
                if (size > 0) {
                    friends = new ArraySet<>();
                    for (int i = 0; i < size; i++) {
                        friends.add(json.getString(i));
                    }
                }
                jobResult = new Job<>(friends, true);
            }
        } catch (Exception e) {
            if (e instanceof ExecutionException) {
                jobResult = new Job<>(null, false);
            } else {
                Log.d(TAG, "getFriendsFromContacts: " + e.getMessage());
            }
        }
        return jobResult;
    }

    public DocumentReference getCurrentUserRef() {
        return currentUserRef;
    }

    public void deleteAccount(final OnJobDoneListener<Void> listener) {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    listener.onComplete(new Job<>(task.getResult(), true));
                } else {
                    listener.onError();
                }
            }
        });
    }

    public String getName() {
        return currentUser.getName();
    }

    public String getPhoneNumber() {
        return currentUser.getId();
    }

    public void setFCMToken(final String fcmToken) {
        final Map<String, Boolean> tokens = new HashMap<>();
        tokens.put(fcmToken, true);
        Map<String, Object> fcmtokens = new HashMap<>();
        fcmtokens.put("fcm_tokens", tokens);
        currentUserRef.set(fcmtokens, SetOptions.merge());
    }

    public void getFriends(final OnJobDoneListener<List<User>> onJobCompleteListener) {
        new GetFriendsTask(onJobCompleteListener).execute();
    }

    public void updateFriends(final OnJobDoneListener<Void> jobDoneListener) {
        new UpdateFriendsTask(jobDoneListener).execute();
    }

    public void sendPlayRequest(String friendUid, final OnJobDoneListener<Void> jobDoneListener) {
        Map<String, String> playRequest = new HashMap<>();
        playRequest.put("name", currentUser.getName());
        getUserDocRef(friendUid).
                collection("play_request_received")
                .document(currentUser.getId())
                .set(playRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (jobDoneListener != null)
                            jobDoneListener.onComplete(new Job<Void>(null, task.isSuccessful()));
                    }
                });
    }

    public void updateProfile(String name, Uri profilePic, final OnJobDoneListener<Void> onJobDoneListener) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(profilePic)
                .build();
        Task<Void> task = firebaseUser.updateProfile(request);
        if (onJobDoneListener != null) {
            task.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        onJobDoneListener.onComplete(new Job<>(task.getResult(), true));
                    } else {
                        onJobDoneListener.onError();
                    }
                }
            });
        }
    }

    public void signOut() {
        auth.signOut();
    }

    public void setOnline() {
        currentUserRef.update("isonline", true);
    }

    public void setOffline() {
        currentUserRef.update("isonline", false);
    }

}
