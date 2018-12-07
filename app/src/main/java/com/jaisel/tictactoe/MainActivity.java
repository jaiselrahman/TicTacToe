package com.jaisel.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jaisel.tictactoe.Utils.UserAccount;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static UserAccount userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userAccount = UserAccount.getInstance();
        AdView mAdView = findViewById(R.id.ad_banner);
        AdRequest.Builder adBuilder = new AdRequest.Builder();
        if (BuildConfig.DEBUG) {
            adBuilder.addTestDevice("8BE1E7368A43733B68CD8EB8C618A917");
        }
        AdRequest adRequest = adBuilder.build();
        mAdView.loadAd(adRequest);

        Button Computer = findViewById(R.id.computer);
        Computer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, XoActivity.class);
                i.putExtra("PLAYERTYPE", "COMPUTER");
                startActivity(i);
            }
        });

        Button Player = findViewById(R.id.player);
        Player.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (userAccount == null) {
                    startActivity(new Intent(MainActivity.this, AccountActivity.class));
                    Toast.makeText(MainActivity.this, "Set User Name First", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(MainActivity.this, SelectOpponentActivity.class));
                }
            }
        });

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String a : bundle.keySet()) {
                Log.d(TAG, a + ":" + bundle.get(a));
            }

            String action = bundle.getString("action", "");
            switch (action) {
                case "friend_request": {
                    Intent i = new Intent(this, AccountActivity.class);
                    i.putExtra("FRIEND_REQUEST", true);
                    startActivity(i);
                    break;
                }
                case "play_request": {
                    if (XoActivity.isPlaying) {
                        Toast.makeText(this, "Already Playing", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    Intent i = new Intent(this, XoActivity.class);
                    i.putExtra("PLAY", true);
                    i.putExtra("PLAYER_TYPE", "PLAYER");
                    i.putExtra("PLAYER_TURN", 2);
                    i.putExtra("PLAYER_ID", bundle.getString("userid"));
                    i.putExtra("PLAYER_NAME", bundle.getString("name"));
                    startActivity(i);
                    break;
                }
                case "accepted_play_request": {
                    Intent i = new Intent(this, XoActivity.class);
                    i.putExtra("PLAY", true);
                    i.putExtra("PLAYER_TYPE", "PLAYER");
                    i.putExtra("PLAYER_TURN", 1);
                    i.putExtra("PLAYER_ID", bundle.getString("userid"));
                    i.putExtra("PLAYER_NAME", bundle.getString("name"));
                    startActivity(i);
                    break;
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
}