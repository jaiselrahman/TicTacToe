package com.jaisel.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    protected static final int DRAWER_CLOSE_DELAY = 150;
    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final String CURRENT_NAV_ID = "current_nav_id";
    private static Intent mainIntent, accountIntent, aboutIntent;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private void prepareIntents() {
        if (mainIntent == null) {
            mainIntent = new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(CURRENT_NAV_ID, R.id.nav_home);
        }
        if (accountIntent == null) {
            accountIntent = new Intent(this, AccountActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(CURRENT_NAV_ID, R.id.nav_account);
        }
        if (aboutIntent == null) {
            aboutIntent = new Intent(this, AboutActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(CURRENT_NAV_ID, R.id.nav_about);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getLayoutInflater().inflate(layoutResID, (FrameLayout) findViewById(R.id.content_main));
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareIntents();
        super.setContentView(R.layout.activity_base);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            navigationView.setCheckedItem(getIntent().getExtras().getInt(CURRENT_NAV_ID, R.id.nav_home));
        } else {
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                navigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(mainIntent);
                    }
                }, DRAWER_CLOSE_DELAY);
                break;
            case R.id.nav_account:
                navigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(accountIntent);
                    }
                }, DRAWER_CLOSE_DELAY);
                break;
            case R.id.nav_friends:
                navigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        startActivity();
                    }
                }, DRAWER_CLOSE_DELAY);
                break;
            case R.id.nav_share:

                break;
            case R.id.nav_about:
                navigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(aboutIntent);
                    }
                }, DRAWER_CLOSE_DELAY);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startAccountActivity() {
        startActivity(BaseActivity.accountIntent);
    }

    public void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

}
