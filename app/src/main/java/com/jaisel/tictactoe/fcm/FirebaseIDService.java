package com.jaisel.tictactoe.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.jaisel.tictactoe.Utils.UserAccount;

/**
 * Created by jaisel on 10/6/17.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {
    private final String TAG = FirebaseIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token " + refreshedToken);
        UserAccount.getInstance().setFCMToken(refreshedToken);

    }
}
