package com.jaisel.tictactoe.Utils;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

/**
 * Created by jaisel on 16/7/17.
 */

public class UserAccount {
    private static String TAG = UserAccount.class.getSimpleName();
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static CollectionReference users = FirebaseFirestore.getInstance().collection("users");
    private static FirebaseUser user;
    private static DocumentReference myDocRef;
    private static CollectionReference friendsDocRef;
    private static UserAccount userAccount;

    static public UserAccount getInstance() {
        if(user == null) {
            user = auth.getCurrentUser();
//            myDocRef = users.document(user.getUid());
//            friendsDocRef = myDocRef.collection("friends");
            userAccount = new UserAccount();
        }
        return userAccount;
    }

    private UserAccount() { }

    public FirebaseUser getUser() {
        return user;
    }
    public DocumentReference getMyDocRef() {
        return myDocRef;
    }
    public CollectionReference getMyFriendsRef() {
        return friendsDocRef;
    }

    public void setOnline() {
        myDocRef.update("isonline", true);
    }

    public void setOffline() {
        myDocRef.update("isonline", false);
    }

    public void setUserName(String name, @Nullable final OnJobDoneListener<Void> jobDoneListener) {
        user.updateProfile(new UserProfileChangeRequest.Builder()
        .setDisplayName(name)
        .build()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (jobDoneListener !=null)
                    jobDoneListener.onComplete(new Job<>(task.getResult(), task.isSuccessful()));
            }
        });
    }

    public void setProfilePic(Uri profilePicUri, @Nullable final OnJobDoneListener<Void> jobDoneListener) {
        user.updateProfile(new UserProfileChangeRequest.Builder()
                .setPhotoUri(profilePicUri)
                .build()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (jobDoneListener !=null)
                    jobDoneListener.onComplete(new Job<>(task.getResult(), task.isSuccessful()));
            }
        });
    }

    public void setFCMToken(final String fcmToken) {
        final Map<String, Boolean> tokens = new HashMap<>();
        tokens.put(fcmToken, true);
        Map<String, Object> fcmtokens = new HashMap<>();
        fcmtokens.put("fcm_tokens", tokens);
//        myDocRef.set(fcmtokens, SetOptions.merge());
    }

    public void getFriends(final OnJobDoneListener<Vector<User>> onJobCompleteListener) {
        final Vector<User> usersList = new Vector<>();
        CollectionReference myFriendsRef = getMyFriendsRef();
        myFriendsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        User user = new User();
                        user.setName(doc.getString("name"));
                        user.setId(doc.getString("userid"));
                        user.setId(doc.getString("status"));
                        usersList.add(user);
                    }
                    onJobCompleteListener.onComplete(new Job<>(usersList, true));
                }
            }
        });
    }

    public void addFriend(String friendUid, @Nullable final OnJobDoneListener<Void> jobDoneListener) {
        myDocRef.collection("friends")
                .add(friendUid)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(jobDoneListener != null)
                            jobDoneListener.onComplete(new Job<Void>(null , task.isSuccessful()));
                    }
                });
    }

    public void removeFriend(String friendUid, @Nullable final OnJobDoneListener<Void> jobDoneListener) {
        myDocRef.collection("friends")
                .document(friendUid)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(jobDoneListener != null)
                            jobDoneListener.onComplete(new Job<>(task.getResult(), task.isSuccessful()));
                    }
                });
    }

    public void sendPlayRequest(String friendUid, final OnJobDoneListener<Void> jobDoneListener) {
        getUserDocRef(friendUid).
                collection("play_request_received").
                add(user.getUid())
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (jobDoneListener != null)
                            jobDoneListener.onComplete(new Job<Void>(null, task.isSuccessful()));
                    }
                });
    }

    public void signIn(Activity activity, String phoneNumber, @Nullable final OnJobDoneListener<AuthResult> onJobDoneListener) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, 60, TimeUnit.SECONDS, activity, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        auth.signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(onJobDoneListener != null) {
                                            onJobDoneListener.onComplete(new Job<>(task.getResult(), task.isSuccessful()));
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.w(TAG, "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.d(TAG, "Invalid Code");
                            if(onJobDoneListener != null) {
                                onJobDoneListener.onError();
                            }

                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            //TODO: inform me;
                            Log.d(TAG, "SMS quota exceeded");
                        }
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                    }
                });
    }
    public void signOut() {
        auth.signOut();
    }


    public static CollectionReference getUserColRef() {
        return users;
    }
    public static DocumentReference getUserDocRef(String s) {
        return users.document(s);
    }

    static public void create(final Activity activity, final User user, String password, @Nullable final OnJobDoneListener<FirebaseUser> listener) {
        auth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            UserProfileChangeRequest.Builder userProfileChangeRequest = new UserProfileChangeRequest.Builder();
                            userProfileChangeRequest.setDisplayName(user.getName());
                            firebaseUser.updateProfile(userProfileChangeRequest.build());
                            Map<String, Object> userdetails = new HashMap<>();
                            userdetails.put("lastmove",0);
                            userdetails.put("status","");
                            userdetails.put("isonline", true);
                            getUserColRef()
                                    .document(firebaseUser.getUid())
                                    .set(userdetails);
                        }
                        if (listener!=null) {
                            listener.onComplete(new Job<>(task.getResult().getUser(), false));
                        }
                    }
                });
    }

    static public void remove(final OnJobDoneListener<Void> listener) {
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    listener.onComplete(null);
                } else {
                    listener.onError();
                }
            }
        });
    }

    static public void sendPasswordResetEmail(String email, final OnJobDoneListener<Void> onJobDoneListener) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(onJobDoneListener != null)
                            onJobDoneListener.onComplete(new Job<>(task.getResult(), task.isSuccessful()));
                    }
                });
    }
}
