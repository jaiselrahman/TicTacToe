package com.jaisel.tictactoe;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by jaisel on 31/7/17.
 */

public class AccountEditFragment extends Fragment {
    private EditText input1, input2, inputPassword;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        TextInputLayout TIL1, TIL2, TIL3;
        View v = inflater.inflate(R.layout.account_detail_edit, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return v;
        }

        TIL1 = v.findViewById(R.id.edit_1);
        input1 = TIL1.getEditText();
        TIL2 = v.findViewById(R.id.edit_2);
        input2 = TIL2.getEditText();
        TIL3 = v.findViewById(R.id.password);
        inputPassword = TIL3.getEditText();

        Bundle bundle = getArguments();
        final String edit = bundle.getString("edit", "none");
        switch (edit) {
            case "name":
                TIL1.setHint(getString(R.string.hint_new_user_name));
                break;
            case "email":
                TIL1.setHint(getString(R.string.hint_new_email));
                break;
            case "password":
                TIL1.setHint(getString(R.string.hint_new_password));
                TIL2.setVisibility(View.VISIBLE);
                TIL2.setHint(getString(R.string.hint_re_enter_password));
                break;
            case "none":
                getFragmentManager()
                        .popBackStack();
                break;
        }

        Button save = (Button) v.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (edit) {
                    case "name":
                        final String name = input1.getText().toString();
                        UserProfileChangeRequest.Builder userProfileChangeRequest = new UserProfileChangeRequest.Builder();
                        userProfileChangeRequest.setDisplayName(name);
                        if (isAuthenticated()) {
                            user.updateProfile(userProfileChangeRequest.build())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user.sendEmailVerification();
                                            if(isAdded()) Toast.makeText(getActivity(), R.string.user_name_changed_successfully, Toast.LENGTH_SHORT).show();
                                        } else {
                                            if(isAdded()) Toast.makeText(getActivity(), R.string.failed_to_change_user_name, Toast.LENGTH_SHORT).show();
                                        }
                                        }
                                    });
                        }
                        break;
                    case "email":
                        final String email = input1.getText().toString();
                        if (TextUtils.isEmpty(email)) {
                            inputPassword.setError(getString(R.string.email_should_not_be_empty));
                            return;
                        }
                        if(isAuthenticated()) {
                            user.updateEmail(email)
                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user.sendEmailVerification();
                                            if(isAdded()) Toast.makeText(getActivity(), R.string.email_changed_successfully, Toast.LENGTH_SHORT).show();
                                        } else {
                                            if(isAdded()) Toast.makeText(getActivity(), R.string.failed_to_change_email, Toast.LENGTH_SHORT).show();
                                        }
                                        }
                                    });
                        }
                        break;
                    case "password":
                        final String password1 = input1.getText().toString();
                        final String password2 = input2.getText().toString();

                        if (TextUtils.isEmpty(password1)) {
                            input1.setError(getString(R.string.password_should_not_be_empty));
                            return;
                        } else if (password1.length() < 6) {
                            input1.setError(getString(R.string.password_alteast_6_chars));
                            return;
                        } else if (!password1.equals(password2)) {
                            input2.setError(getString(R.string.password_does_not_matches));
                            return;
                        }

                        if(isAuthenticated()) {
                            user.updatePassword(password1)
                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user.sendEmailVerification();
                                            if(isAdded()) Toast.makeText(getActivity(), R.string.password_changed_successfully, Toast.LENGTH_SHORT).show();
                                        } else {
                                            if(isAdded()) Toast.makeText(getActivity(), R.string.failed_to_change_password, Toast.LENGTH_SHORT).show();
                                        }
                                        }
                                    });
                        }

                        break;
                    case "none":
                        getFragmentManager()
                                .popBackStack();
                        break;
                }
            }

        });
        return v;
    }

    boolean isAuthenticated() {
        String password = inputPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError(getString(R.string.enter_current_password));
            return false;
        } else if (user.getEmail() == null) {
            Toast.makeText(getActivity(), R.string.invalid_password, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            return user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()) {
                                if(isAdded()) Toast.makeText(getActivity(), R.string.invalid_password, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .isSuccessful();
        }
    }
}