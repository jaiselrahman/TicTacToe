package com.jaisel.tictactoe;


import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;

/**
 * Created by jaisel on 26/2/17.
 */
public class accountFragment extends Fragment {
    boolean isEditable = false;
    private userDBHelper userDB;
    private Server server = new Server("http://0.0.0.0:8080/app.php");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.account, container, false);
        final EditText edit = (EditText) v.findViewById(R.id.account_name_edit);
        final TextView view = (TextView) v.findViewById(R.id.account_name_view);
        final Button change = (Button) v.findViewById(R.id.change);
        userDB = new userDBHelper(getActivity());

        Cursor result = userDB.getReadableDatabase().rawQuery("SELECT name FROM " + userDB.TABLE + " WHERE id = 0;", null);
        if (result.moveToFirst()) {
            view.setText(result.getString(0));
            edit.setText(result.getString(0));
        }

        change.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (!isEditable) {
                    edit.setVisibility(View.VISIBLE);
                    change.setText("Save");
                    edit.requestFocus();
                    edit.extendSelection(edit.getText().length());
                    mgr.showSoftInput(edit, 0);
                    isEditable = true;
                } else {
                    mgr.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                    String uname = edit.getText().toString();
                    String unameEncoded = "", data = "", sql = "", useridEncoded = "";
                    int userid = 0;
                    try {
                        unameEncoded = URLEncoder.encode(uname, "UTF-8");
                        Cursor result = userDB.getReadableDatabase().rawQuery("SELECT userid FROM " + userDB.TABLE + " WHERE id = 0 ;", null);
                        result.moveToFirst();
                        userid = result.getInt(0);
                        Log.d("User Id", String.valueOf(userid));
                        useridEncoded = URLEncoder.encode(String.valueOf(result.getInt(0)), "UTF-8");
                    } catch (Exception ex) {
                        Log.d("Encode", ex.getMessage());
                    }
                    if (!MainActivity.isNoAccountName) {
                        data = "source=app&action=updatename&name=" + unameEncoded + "&userid=" + useridEncoded;
                        sql = "UPDATE " + userDB.TABLE + " SET name='" + uname + "' WHERE id=0;";
                    } else {
                        data = "source=app&action=insertname&name=" + unameEncoded;
                        sql = "INSERT INTO " + userDB.TABLE + " VALUES (0, '" + uname + "',";
                    }
                    try {
                        userid = server.getResponse(data);
                        Log.d("User Id", "Account Fragment" + String.valueOf(userid));
                        if (userid == -1) {
                            Toast.makeText(getActivity(), "Setting User Name Failed\n Connect to internet and try again", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            useridEncoded = URLEncoder.encode(String.valueOf(userid), "UTF-8");
                            if (MainActivity.isNoAccountName)
                                sql += useridEncoded + ");";
                        }
                    } catch (Exception ex) {
                        Log.d("Update Name", ex.getMessage());
                        return;
                    }
                    Toast.makeText(getActivity(), "User name changed Successfully", Toast.LENGTH_SHORT).show();
                    MainActivity.isNoAccountName = false;
                    userDB.getWritableDatabase().execSQL(sql);
                    view.setText(uname);
                    edit.setVisibility(View.GONE);
                    change.setText("Change");
                    isEditable = false;
                }
            }
        });
        return v;
    }
}
