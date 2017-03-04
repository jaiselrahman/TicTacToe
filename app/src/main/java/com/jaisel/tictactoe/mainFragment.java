package com.jaisel.tictactoe;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by jaisel on 26/2/17.
 */
public class mainFragment extends Fragment {
    private userDBHelper userDB;
    private FragmentManager FM;
    private ListView drawerList;

    public mainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userDB = new userDBHelper(getActivity());
        FM = getFragmentManager();
        View v = inflater.inflate(R.layout.main, container, false);
        Button Computer = (Button) v.findViewById(R.id.computer);
        Computer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                xo.player2 = 'C';
                Intent i = new Intent(getActivity(), xo.class);
                startActivityForResult(i, 0);
            }
        });
        final LayoutInflater inflater1 = inflater;
        final ViewGroup container1 = container;
        Button Player = (Button) v.findViewById(R.id.player);
        Player.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int n = userDB.getReadableDatabase().rawQuery("SELECT name FROM " + userDB.TABLE + " where id = 0 ;", null)
                        .getCount();
                if (n <= 0) {
                    View v2 = inflater1.inflate(R.layout.activity_main, container1, false);
                    FM.beginTransaction()
                            .replace(R.id.content_frame, new accountFragment())
                            .commit();
                    drawerList = (ListView) v2.findViewById(R.id.left_drawer);
                    drawerList.setItemChecked(1, true);
                    MainActivity.isNoAccountName = true;
                    MainActivity.inAccount = true;
                    Toast.makeText(getActivity(), "Set User Name First", Toast.LENGTH_SHORT).show();
                    return;
                }
                xo.player2 = 'O';
                Intent i = new Intent(getActivity(), xo.class);
                startActivityForResult(i, 0);
            }
        });
        return v;
    }
}