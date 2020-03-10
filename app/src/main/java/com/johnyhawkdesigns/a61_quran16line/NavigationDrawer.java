package com.johnyhawkdesigns.a61_quran16line;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.google.android.material.navigation.NavigationView;
import com.johnyhawkdesigns.a61_quran16line.ui.home.HomeFragment;
import com.johnyhawkdesigns.a61_quran16line.ui.utils.ExpandableListAdapter;
import com.johnyhawkdesigns.a61_quran16line.ui.utils.MenuModel;
import com.johnyhawkdesigns.a61_quran16line.ui.utils.Utils;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NavigationDrawer
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = NavigationDrawer.class.getSimpleName();

    private AppBarConfiguration mAppBarConfiguration;
    Toolbar toolbar;
    View decorView;
    FloatingActionButton fab;
    boolean fullscreen;
    boolean isStatusBarVisible;

    NavController navController;

    DrawerLayout drawer;


    // TODO: Related to ExpandableListView
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>(); // Holds headers of the Navigation Drawers
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>(); // Holds children of the Navigation Drawers


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        decorView = getWindow().getDecorView(); // this view can be used to show or hide status bar

        setupFab();

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder( // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();

        setupNavController(navigationView);

        //fullscreen = true;
        hideStatusBar(); // at start, we need to hide


        // TODO: Related to ExpandableListView
        expandableListView = findViewById(R.id.expandableListView);
        navigationView.setNavigationItemSelectedListener(this);
        prepareMenuData();
        populateExpandableList();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }


    public void setupFab() {
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
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    // To ensure the Back button works properly, you also need to override the onSupportNavigateUp() method
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

    private void toggleStatusBar() {
        Log.d(TAG, "toggleStatusBar: isStatusBarVisible = " + isStatusBarVisible);
        if (!isStatusBarVisible) { // if status bar is not visible, show status bar
            showStatusBar();
        } else { // if status bar is visible, hide status bar
            hideStatusBar();
        }
    }

    public void hideToolBar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.GONE);
        }
    }

    public void showToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    // inside activity_navigation_drawer.xml, we have to use property of "NavigationView" as app:menu="@menu/activity_navigation_drawer_drawer"
    // Right now, this doesn't serve any purpose.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.d(TAG, "onNavigationItemSelected: id = " + id);

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void prepareMenuData() {

        MenuModel menuModel = new MenuModel(Utils.MenuName_Home, true, false, 1); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null); // null means it doesn't have children
        }


        menuModel = new MenuModel(Utils.MenuName_Index, true, true, 1);
        headerList.add(menuModel);

        List<MenuModel> childModelsList = new ArrayList<>();
        MenuModel childModel = new MenuModel("Parah-1", false, false, 1);
        childModelsList.add(childModel);

        childModel = new MenuModel("Parah-2", false, false, 2);
        childModelsList.add(childModel);


        if (menuModel.hasChildren) {
            Log.d(TAG, "prepareMenuData: ");
            childList.put(menuModel, childModelsList);
        }

        menuModel = new MenuModel(Utils.MenuName_Bookmarks, true, true, 0);
        headerList.add(menuModel);

        menuModel = new MenuModel(Utils.MenuName_About, true, false, 0);
        headerList.add(menuModel);

    }

    private void populateExpandableList() {

        expandableListAdapter = new ExpandableListAdapter(this, headerList, childList);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (headerList.get(groupPosition).isGroup) { // checking for isGroup excludes all children

                    switch (groupPosition) {
                        case 0: // Home
                            // if current fragment is not nav_home, we need to launch navigate method, otherwise, we only need to close drawer
                            if (navController.getCurrentDestination().getId() != R.id.nav_home) {
                                navController.navigate(R.id.nav_home); // navigate to Home fragment
                            }
                            drawer.closeDrawer(Gravity.LEFT);
                            break;

                        case 1: // Index
                            Log.d(TAG, "onGroupClick: Index");
                            break;

                        case 2: // Bookmarks
                            Log.d(TAG, "onGroupClick: Bookmarks");
                            break;

                        case 3: // About
                            // if we are currently on some other fragment, we need to launch navigate method, otherwise, we only need to close drawer
                            navController.navigate(R.id.nav_about); // navigate to this fragment
                            drawer.closeDrawer(Gravity.LEFT);
                            break;

                        default:
                            break;
                    }
                }
                return false;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (childList.get(headerList.get(groupPosition)) != null) {
                    MenuModel model = childList.get(headerList.get(groupPosition)).get(childPosition);

                    Log.d(TAG, "onChildClick: model.parahNo = " + model.parahNo);
                    switch (model.parahNo) {
                        case 1:
                            // need to redirect to parah -1
                            Toast.makeText(NavigationDrawer.this, "Redirecting to Parah - " + model.parahNo, Toast.LENGTH_SHORT).show();
                            break;

                        case 2:
                            // need to redirect to parah -1
                            Toast.makeText(NavigationDrawer.this, "Redirecting to Parah - " + model.parahNo, Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }
}
