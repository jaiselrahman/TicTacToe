package com.jaisel.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

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
            adBuilder.addTestDevice("6800C57467B8570202EBD32F5B0EC0BA")
                    .addTestDevice("B0A5AA8099D1569630280C10FA42CCD8")
                    .addTestDevice("B8CC45B4F88260D23A22D7517FE4216F");
        }
        AdRequest adRequest = adBuilder.build();
        mAdView.loadAd(adRequest);

        Button Computer = findViewById(R.id.computer);
        Computer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, XoActivity.class);
                i.putExtra(XoActivity.PLAYER_TYPE, XoActivity.TYPE_COMPUTER);
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
                case "play_request": {
                    if (XoActivity.isPlaying()) {
                        Toast.makeText(this, "Already Playing", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    userAccount.acceptPlayRequest(bundle.getString("userId"), null);
                    Intent i = new Intent(this, XoActivity.class);
                    i.putExtra(XoActivity.PLAYER_TYPE, XoActivity.TYPE_PLAYER);
                    i.putExtra(XoActivity.PLAYER_TURN, 2);
                    i.putExtra(XoActivity.PLAYER_ID, bundle.getString("userId"));
                    i.putExtra(XoActivity.PLAYER_NAME, bundle.getString("name"));
                    startActivity(i);
                    break;
                }
                case "play_request_accepted": {
                    Intent i = new Intent(this, XoActivity.class);
                    i.putExtra(XoActivity.PLAYER_TYPE, XoActivity.TYPE_PLAYER);
                    i.putExtra(XoActivity.PLAYER_TURN, 1);
                    i.putExtra(XoActivity.PLAYER_ID, bundle.getString("userId"));
                    i.putExtra(XoActivity.PLAYER_NAME, bundle.getString("name"));
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