package com.jaisel.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.jaisel.tictactoe.Utils.Job;
import com.jaisel.tictactoe.Utils.OnJobDoneListener;
import com.jaisel.tictactoe.Utils.UserAccount;

/**
 * Created by jaisel on 16/7/17.
 */

public class LoginActivity extends AppCompatActivity {
    private String TAG = LoginActivity.class.getSimpleName();
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    auth = FirebaseAuth.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new Login())
                .commit();
    }

    public static class Login extends Fragment {
        private static final String TAG = Login.class.getSimpleName();

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.activity_login1, container, false);
            final EditText inputEmail = v.findViewById(R.id.login_phone);

            Button next = v.findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNumber = inputEmail.getText().toString();
                    if(TextUtils.isEmpty(phoneNumber)) {
                        inputEmail.setError(getString(R.string.email_should_not_be_empty));
                        return;
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("PHONE_NUMBER", phoneNumber);
                    LoginConfirmation loginConfirmation = new LoginConfirmation();
                    loginConfirmation.setArguments(bundle);
                    getFragmentManager()
                            .beginTransaction()
                            .replace(android.R.id.content, loginConfirmation)
                            .commit();
                }
            });
            return v;
        }
    }

    public static class LoginConfirmation extends Fragment {
        private static final String TAG = LoginConfirmation.class.getSimpleName();
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Bundle bundle = getArguments();
            if(bundle == null) {
                Log.i(TAG, "onCreateView: getArguments returned null");
                getFragmentManager().popBackStackImmediate();
                return null;
            }
            View v = inflater.inflate(R.layout.activity_login2, container, false);
            final String phoneNumber = bundle.getString("PHONE_NUMBER");
            Button confirm = v.findViewById(R.id.confirm);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserAccount.getInstance()
                            .signIn(getActivity(), phoneNumber, new OnJobDoneListener<AuthResult>() {
                                @Override
                                public void onComplete(Job<AuthResult> job) {
                                    if (job.isSuccessful()) {
                                        startActivity(new Intent(getActivity(), AccountActivity.class));

                                    } else {
                                        Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "Login Failed");
                                    }
                                }
                            });
                }
            });
            return v;
        }
    }

}