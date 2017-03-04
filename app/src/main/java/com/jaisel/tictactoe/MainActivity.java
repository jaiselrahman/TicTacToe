package com.jaisel.tictactoe;


import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
    public static userDBHelper userDB;
    public static accountFragment accountF = new accountFragment();
    public static aboutFragment aboutF = new aboutFragment();
    public static boolean inAbout = false, inAccount = false;
    public static boolean isNoAccountName = false;
    private mainFragment mainF = new mainFragment();
    private Menu menu;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private FragmentManager FM = getFragmentManager();

    private Server server = new Server("http://0.0.0.0:8080/app.php");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userDB = new userDBHelper(this);
        if (savedInstanceState == null) {
            FM.beginTransaction()
                    .replace(R.id.content_frame, mainF)
                    .commit();
        }
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, new String[]{"Home", "Account", "About", "Exit"}));
        drawerList.setItemChecked(0, true);
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        FM.beginTransaction()
                                .replace(R.id.content_frame, mainF)
                                .commit();
                        inAccount = false;
                        inAbout = false;
                        break;
                    case 1:
                        FM.beginTransaction()
                                .replace(R.id.content_frame, new accountFragment())
                                .commit();
                        inAccount = true;
                        break;
                    case 2:
                        FM.beginTransaction()
                                .replace(R.id.content_frame, new aboutFragment())
                                .commit();
                        menu.setGroupVisible(R.id.menu_group, false);
                        inAbout = true;
                        break;
                    case 3:
                        System.exit(0);
                }
                drawerLayout.closeDrawer(drawerList);
            }
        });
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.menu2,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View view) {
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        //getActionBar().setElevation(5f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isDrawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.setGroupVisible(R.id.menu_group, !isDrawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.home_menu:
                FM.beginTransaction()
                        .replace(R.id.content_frame, new mainFragment())
                        .commit();
                drawerList.setItemChecked(0, true);
                inAccount = false;
                inAbout = false;
                return true;
            case R.id.account_menu:
                FM.beginTransaction()
                        .replace(R.id.content_frame, new accountFragment())
                        .commit();
                drawerList.setItemChecked(1, true);
                inAccount = true;
                return true;
            case R.id.about_menu:
                FM.beginTransaction()
                        .replace(R.id.content_frame, new aboutFragment())
                        .commit();
                menu.setGroupVisible(R.id.menu_group, false);
                drawerList.setItemChecked(2, true);
                inAbout = true;
                return true;
            case R.id.exit_menu:
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (inAbout || inAccount) {
            FM.beginTransaction()
                    .replace(R.id.content_frame, new mainFragment())
                    .commit();
            menu.setGroupVisible(R.id.menu_group, true);
            drawerList.setItemChecked(0, true);
            inAbout = false;
            inAccount = false;
        } else
            super.onBackPressed();
    }
}