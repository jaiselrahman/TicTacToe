package com.jaisel.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.jaisel.tictactoe.Utils.Job;
import com.jaisel.tictactoe.Utils.OnJobDoneListener;
import com.jaisel.tictactoe.Utils.User;
import com.jaisel.tictactoe.Utils.UserAccount;
import com.mukesh.OtpView;

/**
 * Created by jaisel on 16/7/17.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String PHONE_NUMBER = "PHONE_NUMBER";
    private static final int CONFIRMATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final User user = new User();
        final EditText inputPhone = findViewById(R.id.login_phone);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final OtpView otpView = findViewById(R.id.otp);
        final Button confirm = findViewById(R.id.confirm);
        final Button getOtp = findViewById(R.id.get_otp);
        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = inputPhone.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {
                    inputPhone.setError(getString(R.string.phone_should_not_be_empty));
                    return;
                }
                if (phoneNumber.length() != 10) {
                    inputPhone.setError(getString(R.string.invalid_phone_number));
                    return;
                }
                getOtp.setVisibility(View.INVISIBLE);
                inputPhone.setEnabled(false);
                otpView.setVisibility(View.VISIBLE);
                confirm.setVisibility(View.VISIBLE);
                user.setId("+91" + phoneNumber);
                UserAccount.signIn(LoginActivity.this, user, new OnJobDoneListener<AuthResult>() {
                    @Override
                    public void onComplete(Job<AuthResult> job) {
                        if (job.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, AccountActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otpView.hasValidOTP()) {
                    progressBar.setVisibility(View.VISIBLE);
                    final String code = otpView.getOTP();
                    UserAccount.verifySignIn(user, code, new OnJobDoneListener<AuthResult>() {
                        @Override
                        public void onComplete(Job<AuthResult> job) {
                            if (job.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, AccountActivity.class));
                                finish();
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginActivity.this, R.string.invalid_code, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, R.string.invalid_code, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}