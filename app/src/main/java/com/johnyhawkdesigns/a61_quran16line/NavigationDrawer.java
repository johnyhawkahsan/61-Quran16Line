package com.johnyhawkdesigns.a61_quran16line;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

public class NavigationDrawer
        extends AppCompatActivity {

    private static final String TAG = NavigationDrawer.class.getSimpleName();

    private AppBarConfiguration mAppBarConfiguration;
    Toolbar toolbar;
    View decorView;
    FloatingActionButton fab;
    boolean fullscreen;
    boolean isStatusBarVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        decorView = getWindow().getDecorView(); // this view can be used to show or hide status bar

        setupFab();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder( // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();

        setupNavController(navigationView);

        //fullscreen = true;
        hideStatusBar(); // at start, we need to hide
    }


    public void setupFab(){
        fab = findViewById(R.id.fabBookmark);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Bookmark Saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }



    private void setupNavController(NavigationView navigationView) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void toggleFullscreen() {
        toggleStatusBar();
        Log.d(TAG, "toggleFullscreen: ");
    }

    // Remember that you should never show the action bar if the status bar is hidden, so hide that too if necessary.
    // ActionBar actionBar = getActionBar(); actionBar.hide(); // NOTE: I'm hiding action bar using theme Theme.AppCompat.Light.NoActionBar"
    private void hideStatusBar() {
        isStatusBarVisible = false;
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN; // Hide the status bar.
        decorView.setSystemUiVisibility(uiOptions);
        hideToolBar();
        fab.hide();
    }

    private void showStatusBar() {
        isStatusBarVisible = true;
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE; // Hide the status bar.
        decorView.setSystemUiVisibility(uiOptions);
        showToolbar();
        fab.show();
    }

    private void toggleStatusBar(){
        Log.d(TAG, "toggleStatusBar: isStatusBarVisible = " + isStatusBarVisible);
        if (!isStatusBarVisible){ // if status bar is not visible, show status bar
            showStatusBar();
        } else { // if status bar is visible, hide status bar
            hideStatusBar();
        }
    }

    public void hideToolBar() {
        if (toolbar != null){
            toolbar.setVisibility(View.GONE);
        }
    }

    public void showToolbar() {
        if (toolbar != null){
            toolbar.setVisibility(View.VISIBLE);
        }
    }
}
