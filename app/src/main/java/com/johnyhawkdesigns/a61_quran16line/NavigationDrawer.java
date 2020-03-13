package com.johnyhawkdesigns.a61_quran16line;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.google.android.material.navigation.NavigationView;
import com.johnyhawkdesigns.a61_quran16line.ui.dialog.GotoDialogFragment;
import com.johnyhawkdesigns.a61_quran16line.ui.home.HomeFragment;
import com.johnyhawkdesigns.a61_quran16line.ui.utils.ExpandableListAdapter;
import com.johnyhawkdesigns.a61_quran16line.ui.utils.MenuModel;
import com.johnyhawkdesigns.a61_quran16line.ui.utils.Utils;
import com.shockwave.pdfium.PdfDocument;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NavigationDrawer
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.HomeFragmentListener, View.OnClickListener {

    private static final String TAG = NavigationDrawer.class.getSimpleName();

    private AppBarConfiguration mAppBarConfiguration;
    Toolbar toolbar;
    View decorView;
    FloatingActionButton fab;
    boolean isStatusBarVisible;

    NavController navController;
    DrawerLayout drawer;

    List<PdfDocument.Bookmark> tableOfContents;

    // TODO: Related to ExpandableListView
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>(); // Holds headers of the Navigation Drawers
    HashMap<MenuModel, List<PdfDocument.Bookmark>> childList = new HashMap<>(); // Holds children of the Navigation Drawers

    // I'm returning this pdfView from HomeFragment using interface method
    PDFView pdfView;
    Button gotoButton;


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



        // TODO: Related to ExpandableListView
        expandableListView = findViewById(R.id.expandableListView);
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        gotoButton = findViewById(R.id.gotoButton);
        gotoButton.setOnClickListener(this);
        toggleViews();
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



    // Interface method to return tableOfContents from HomeFragment
    @Override
    public void returnBookmarks(List<PdfDocument.Bookmark> tableOfContents) {
        this.tableOfContents = tableOfContents;

        // NOTE: Moved prepareMenuData(); populateExpandableList(); here because until @loadComplete in HomeFragment is not done, we don't have any bookmarks so tableOfContents was null.
        prepareMenuData();
        populateExpandableList();
        printBookmarksTree(tableOfContents);
    }

    private void prepareMenuData() {

        // I'm using this if statement because I was sometimes getting duplicate items in List, because when onLoadComplete is fired multiple times, we will get this method "prepareMenuData" multiple times.
        if (headerList.size() < 1){

            MenuModel menuModel = new MenuModel(Utils.MenuName_Home, true, false, 1); //Menu of Android Tutorial. No sub menus
            headerList.add(menuModel);

            if (!menuModel.hasChildren) {
                childList.put(menuModel, null); // null means it doesn't have children
            }

            menuModel = new MenuModel(Utils.MenuName_Index, true, true, 1);
            headerList.add(menuModel);



            if (menuModel.hasChildren) {
                Log.d(TAG, "prepareMenuData: ");
                if (tableOfContents.size() > 1){
                    Log.d(TAG, "prepareMenuData: size = " + tableOfContents.size());
                } else {
                    Log.d(TAG, "prepareMenuData: less than 1");
                }
                childList.put(menuModel, tableOfContents);
            }

            menuModel = new MenuModel(Utils.MenuName_Bookmarks, true, true, 0);
            headerList.add(menuModel);

            menuModel = new MenuModel(Utils.MenuName_About, true, false, 0);
            headerList.add(menuModel);
        }

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

                    PdfDocument.Bookmark bookmark = childList.get(headerList.get(groupPosition)).get(childPosition);
                    Log.d(TAG, "onChildClick: bookmark.getTitle() = " + bookmark.getTitle());
                    Toast.makeText(NavigationDrawer.this, "Redirect to " + bookmark.getTitle(), Toast.LENGTH_SHORT).show();

                    int pageNo = (int) bookmark.getPageIdx();
                    pdfView.jumpTo(pageNo);
                    drawer.closeDrawer(Gravity.LEFT);
                }
                return false;
            }
        });
    }

    // Bookmarks is a class which holds few properties like title, pageIndex etc
    public void printBookmarksTree(List<PdfDocument.Bookmark> tree) {

        for (PdfDocument.Bookmark bookmark : tree) {

            Log.d(TAG, "printBookmarksTree: bookmark.getTitle = " + bookmark.getTitle());
            Log.d(TAG, "printBookmarksTree: bookmark.getPageIdx = " + bookmark.getPageIdx());

            // if bookmark has children, then we can also put this inside method and retrieve children
            if (bookmark.hasChildren()) {
                printBookmarksTree(bookmark.getChildren());
            }
        }
    }



    @Override
    public void fullscreen(Boolean isFullscreen) {
        Log.d(TAG, "fullscreen: isFullscreen = " + isFullscreen);
        toggleViews();
    }


    private void toggleViews() {
        if (gotoButton.getVisibility() == View.INVISIBLE || !isStatusBarVisible ){
            showGotoButton();
            showStatusBar();
            showToolbar();
            fab.show();
        } else {
            hideGotoButton();
            hideStatusBar();
            hideToolBar();
            fab.hide();
        }
    }

    private void showGotoButton() {
        gotoButton.setVisibility(View.VISIBLE);
    }

    private void hideGotoButton() {
        gotoButton.setVisibility(View.INVISIBLE);
    }

    private void showStatusBar() {
        isStatusBarVisible = true;
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE; // Hide the status bar.
        decorView.setSystemUiVisibility(uiOptions);
    }

    // Remember that you should never show the action bar if the status bar is hidden, so hide that too if necessary.
    // ActionBar actionBar = getActionBar(); actionBar.hide(); // NOTE: I'm hiding action bar using theme Theme.AppCompat.Light.NoActionBar"
    private void hideStatusBar() {
        isStatusBarVisible = false;
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN; // Hide the status bar.
        decorView.setSystemUiVisibility(uiOptions);

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

    // Very bold attempt by me to transmit pdfView here Alhamdulillah. It will solve all of my problems so I may use NavigationDrawer Activity for all the tasks.
    @Override
    public void passPdfView(PDFView pdfView) {
        this.pdfView = pdfView;
    }

    @Override
    public void pageChanged(int pageNo, int pageCount) {
        gotoButton.setText(pageNo + " / " + pageCount);
    }





    @Override
    public void onClick(View v) {
        GotoDialogFragment dialogFragment = GotoDialogFragment.newInstance(pdfView.getPageCount()); // no of pages are used as bundle arguments
        dialogFragment.show(getSupportFragmentManager(), "dialog");
        dialogFragment.setDialogListener(new GotoDialogFragment.DialogListener() {
            @Override
            public void onEnterPageNo(int pageNo) {
                Log.d(TAG, "onEnterPageNo: pageNo = " + pageNo);
                pdfView.jumpTo(pageNo); // go to specific page
            }
        });
    }
}
