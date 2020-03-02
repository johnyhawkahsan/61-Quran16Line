package com.johnyhawkdesigns.a61_quran16line;

import android.app.ActionBar;
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

public class NavigationDrawer extends AppCompatActivity {

    private static final String TAG = NavigationDrawer.class.getSimpleName();

    private AppBarConfiguration mAppBarConfiguration;
    Toolbar toolbar;

    View decorView;
    boolean fullscreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        decorView = getWindow().getDecorView(); // this view can be used to show or hide status bar

        // at start, we want the app to launch in fullscreen
        hideStatusBar();
        hideToolBar();
        fullscreen = false;

        setupFab();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder( // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();

        setupNavController(navigationView);
    }


    public void setupFab(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
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


    public void toggleFullscreen(){
        Log.d(TAG, "toggleFullscreen: fullscreen = " + fullscreen);
        if (!fullscreen){ // if status bar is not visible, show status bar
            fullscreen = true;
            showStatusBar();
            showToolbar();
        } else { // if status bar is visible, hide status bar
            fullscreen = false;
            hideStatusBar();
            hideToolBar();
        }
    }

    // Remember that you should never show the action bar if the status bar is hidden, so hide that too if necessary.
    // ActionBar actionBar = getActionBar(); actionBar.hide(); // NOTE: I'm hiding action bar using theme Theme.AppCompat.Light.NoActionBar"
    private void hideStatusBar() {
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN; // Hide the status bar.
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void showStatusBar() {
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE; // Hide the status bar.
        decorView.setSystemUiVisibility(uiOptions);
    }


    public void hideToolBar() {
        if (toolbar != null){
            toolbar.setVisibility(View.GONE); // Note: View.Invisible only hides text.
        }
    }

    public void showToolbar() {
        if (toolbar != null){
            toolbar.setVisibility(View.VISIBLE);
        }
    }
}
