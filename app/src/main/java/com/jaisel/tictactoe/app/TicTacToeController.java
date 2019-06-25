package com.jaisel.tictactoe.app;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jaisel.tictactoe.Utils.Job;
import com.jaisel.tictactoe.Utils.OnJobDoneListener;
import com.jaisel.tictactoe.Utils.UserAccount;

import java.util.Date;

public class TicTacToeController {
    public static final String RESET_GAME = "RESET_GAME";
    public static final String START_GAME = "START_GAME";

    public void setMove(int move, @Nullable final OnJobDoneListener<Void> listener) {
        UserAccount.getInstance().getCurrentUserRef().collection("data").document("lastmove")
                .update("value", move, "time", new Date().getTime()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(listener != null) listener.onComplete(new Job<Void>(null, true));
            }
        });
    }

    public LiveData<Integer> getOpponentMove(Activity activity, String uid) {
        final MutableLiveData<Integer> move = new MutableLiveData<>();
        UserAccount.getUserDocRef(uid).collection("data").document("lastmove")
                .addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                    private boolean isOldData = true;

                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (isOldData) {
                            isOldData = false;
                            return;
                        }
                        if (documentSnapshot != null) {
                            move.setValue(documentSnapshot.getLong("value").intValue());
                        }
                    }
                });
        return move;
    }

    public void setStatus(String status, @Nullable final OnJobDoneListener<Void> listener) {
        UserAccount.getInstance().getCurrentUserRef().collection("data").document("status")
                .update("value", status, "time", new Date().getTime()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(listener != null) listener.onComplete(new Job<Void>(null, true));
            }
        });
    }

    public LiveData<String> getOpponentStatus(Activity activity, String uid) {
        final MutableLiveData<String> status = new MutableLiveData<>();
        UserAccount.getUserDocRef(uid).collection("data").document("status")
                .addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
                    private boolean isOldData = true;

                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (isOldData) {
                            isOldData = false;
                            return;
                        }
                        if (documentSnapshot != null) {
                            status.setValue(documentSnapshot.getString("value"));
                        }
                    }
                });
        return status;
    }
}
