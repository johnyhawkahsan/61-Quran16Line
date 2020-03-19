package com.johnyhawkdesigns.a61_quran16line;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
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
import com.johnyhawkdesigns.a61_quran16line.ui.dialog.GotoDialogFragment;
import com.johnyhawkdesigns.a61_quran16line.ui.home.HomeFragment;
import com.johnyhawkdesigns.a61_quran16line.ui.utils.Bookmark;
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
import java.util.Map;

public class NavigationDrawer
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.HomeFragmentListener {

    private static final String TAG = NavigationDrawer.class.getSimpleName();

    private AppBarConfiguration mAppBarConfiguration;
    Toolbar toolbar;
    View decorView;

    NavController navController;
    DrawerLayout drawer;

    List<PdfDocument.Bookmark> tableOfContents;
    List<Bookmark> tableOfContentsBookmark; // I am converting above PdfDocument.Bookmark to Bookmark
    List<Bookmark> parahContents;
    List<Bookmark> soorahContents;
    List<Bookmark> bookmarkContents;


    // TODO: Related to ExpandableListView
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>(); // Holds headers of the Navigation Drawers
    HashMap<MenuModel, List<Bookmark>> childList = new HashMap<>(); // Holds children of the Navigation Drawers

    // I'm returning this pdfView from HomeFragment using interface method
    PDFView pdfView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        decorView = getWindow().getDecorView(); // this view can be used to show or hide status bar

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


        // Initialize bookmark lists
        tableOfContentsBookmark = new ArrayList<>(); // holds all Bookmark tableOfContents converted from original tableOfContents
        parahContents = new ArrayList<>();
        soorahContents = new ArrayList<>();
        bookmarkContents = new ArrayList<>(); // holds bookmarks retrieved from shared preferences
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

        this.tableOfContentsBookmark = convertPdfDocumentBookmarkToBookmark(tableOfContents); // after converting tableOfContents, we get modified tableOfContents

        printBookmarksTree(tableOfContentsBookmark);
        prepareMenuData(); // NOTE: Moved prepareMenuData(); populateExpandableList(); here because until @loadComplete in HomeFragment is not done, we don't have any bookmarks so tableOfContents was null.
        populateExpandableList();
    }

    // This method convert PdfDocument.Bookmark to Bookmark
    private List<Bookmark> convertPdfDocumentBookmarkToBookmark(List<PdfDocument.Bookmark> tableOfContents) {

        List<Bookmark> bookmarkList = new ArrayList<>();

        for (PdfDocument.Bookmark pdfBookmark : tableOfContents){

            String title = pdfBookmark.getTitle(); // PdfDocument.Bookmark title
            int page = (int) pdfBookmark.getPageIdx(); // PdfDocument.Bookmark page no
            Bookmark bookmark = new Bookmark(title, page);

            // if this pdfBookmark has children ie, parah has children parah-1, parah-2 etc, we need to convert that as well.
            if (pdfBookmark.hasChildren()){
                bookmark.setChildren(convertPdfDocumentBookmarkToBookmark(pdfBookmark.getChildren())); // convert children into Bookmark as well
            }

            bookmarkList.add(bookmark); // add each bookmark in our list item in

        }

        Log.d(TAG, "convertPdfDocumentBookmarkToBookmark: bookmarkList.size() = " + bookmarkList.size());
        return bookmarkList;
    }


    // Bookmarks is a class which holds few properties like title, pageIndex etc
    public void printBookmarksTree(List<Bookmark> tree) {

        for (Bookmark bookmark : tree) {

            // if bookmark has children, then we can also put this inside method and retrieve children
            if (bookmark.hasChildren()) {

                // differentiate between parah and soorah
                if (bookmark.getTitle().equals("parah")){
                    Log.d(TAG, "printBookmarksTree: populateParahContents");
                    populateParahContents(bookmark.getChildren());
                } else if (bookmark.getTitle().equals("soorah")) {
                    Log.d(TAG, "printBookmarksTree: populateSoorahContents");
                    populateSoorahContents(bookmark.getChildren());
                }

            } else {
                Log.d(TAG, "printBookmarksTree: no children");
            }
        }
    }

    // method to populate/extract Soorah bookmarks from tableOfContents
    private void populateSoorahContents(List<Bookmark> list) {
        this.soorahContents = list; // it is simpler than using for loop - just assign the list

/*      // we can use for loop to add items individually to the soorahContents
        for (PdfDocument.Bookmark soorah : list) {
            //soorahContents.add(soorah); // use for loop to add all items to soorahContents
            Log.d(TAG, "populateSoorahContents: adding soorah to list = " + soorah.getTitle() );
}*/

        Log.d(TAG, "populateSoorahContents: soorahContents.size() = " + soorahContents.size());

    }

    // method to populate/extract Parah bookmarks from tableOfContents
    private void populateParahContents(List<Bookmark> list) {
        this.parahContents = list; // it is simpler than using for loop - just assign the list

    }



    private void prepareMenuData() {

        // I'm using this if statement because I was sometimes getting duplicate items in List, because when onLoadComplete is fired multiple times, we will get this method "prepareMenuData" multiple times.
        if (headerList.size() < 1){

            MenuModel menuModel = new MenuModel(Utils.MenuName_Home, true, false); //Menu of Android Tutorial. No sub menus
            headerList.add(menuModel);
            if (!menuModel.hasChildren) {
                childList.put(menuModel, null); // null means it doesn't have children
            }

            menuModel = new MenuModel(Utils.MenuName_Parah, true, true);
            headerList.add(menuModel);
            if (menuModel.hasChildren) {
                Log.d(TAG, "prepareParahData: ");
                if (parahContents.size() > 1){
                    Log.d(TAG, "prepareParahData: size = " + parahContents.size());
                } else {
                    Log.d(TAG, "prepareParahData: less than 1");
                }
                childList.put(menuModel, parahContents);
            }

            menuModel = new MenuModel(Utils.MenuName_Soorah, true, true);
            headerList.add(menuModel);
            if (menuModel.hasChildren) {
                Log.d(TAG, "prepareSoorahData: ");
                if (soorahContents.size() > 1){
                    Log.d(TAG, "prepareSoorahData: size = " + soorahContents.size());
                } else {
                    Log.d(TAG, "prepareSoorahData: less than 1");
                }
                childList.put(menuModel, soorahContents);
            }

            menuModel = new MenuModel(Utils.MenuName_Bookmarks, true, true);
            headerList.add(menuModel);
            // add bookmarks
            if (menuModel.hasChildren) {
                getBookmarkedPages(this); // get shared preferences data and store inside bookmarkContents list
                Log.d(TAG, "prepareMenuData: bookmarkContents.size() = " + bookmarkContents.size());
                childList.put(menuModel, bookmarkContents);
            }
            menuModel = new MenuModel(Utils.MenuName_About, true, false);
            headerList.add(menuModel);
        }

    }

    public void getBookmarkedPages(Context context){

        SharedPreferences preferences = context.getSharedPreferences(Utils.BOOKMARKS_PREFERENCES, MODE_PRIVATE);

        Log.d(TAG, "getBookmarkedPages: size of preferences = " + preferences.getAll().size());
        Map<String, ?> allEntries = preferences.getAll();

        // loop through all preferences and get all keys and values
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String prefKey = entry.getKey();
            int pageNo = preferences.getInt(prefKey, 0); // entry.getValue().toString() also correct
            Log.d(TAG, "mapValues = " + prefKey + ": " + entry.getValue().toString());

            Bookmark bookmark = new Bookmark(prefKey, pageNo);

            //PdfDocument.Bookmark bookmarkPdf = (Bookmark) bookmark;
            bookmarkContents.add(bookmark);
        }
    }

    private void populateExpandableList() {

        expandableListAdapter = new ExpandableListAdapter(this, headerList, childList);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (headerList.get(groupPosition).isGroup) { // checking for isGroup excludes all children

                    switch (headerList.get(groupPosition).menuName) {
                        case Utils.MenuName_Home: // Home
                            // if current fragment is not nav_home, we need to launch navigate method, otherwise, we only need to close drawer
                            if (navController.getCurrentDestination().getId() != R.id.nav_home) {
                                navController.navigate(R.id.nav_home); // navigate to Home fragment
                            }
                            drawer.closeDrawer(Gravity.LEFT);
                            break;

                        case Utils.MenuName_Parah: // Parah
                            Log.d(TAG, "onGroupClick: Parah");
                            break;

                        case Utils.MenuName_Soorah: // Soorah
                            Log.d(TAG, "onGroupClick: Soorah");
                            break;

                        case Utils.MenuName_Bookmarks: // Bookmarks
                            Log.d(TAG, "onGroupClick: Bookmarks");
                            break;

                        case Utils.MenuName_About: // About
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

                    Bookmark bookmark = childList.get(headerList.get(groupPosition)).get(childPosition); // single bookmark
                    String bookmarkName = bookmark.getTitle();
                    int pageNo = (int) bookmark.getPageIdx();
                    pdfView.jumpTo(pageNo);
                    drawer.closeDrawer(Gravity.LEFT);

                    Log.d(TAG, "onChildClick: bookmarkName = " + bookmarkName + ", pageNo = " + pageNo);
                    Toast.makeText(NavigationDrawer.this, "Parah = " + bookmarkName + ", pageNo = " + pageNo, Toast.LENGTH_SHORT).show();

                }
                return false;
            }
        });
    }


    @Override
    public void fullscreen(Boolean isFullscreen) {
        Log.d(TAG, "fullscreen: isFullscreen = " + isFullscreen);
        if (isFullscreen){
            showStatusBar();
            showToolbar();
        }else {
            hideStatusBar();
            hideToolBar();
        }

    }


    private void showStatusBar() {
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE; // Hide the status bar.
        decorView.setSystemUiVisibility(uiOptions);
    }

    // Remember that you should never show the action bar if the status bar is hidden, so hide that too if necessary.
    // ActionBar actionBar = getActionBar(); actionBar.hide(); // NOTE: I'm hiding action bar using theme Theme.AppCompat.Light.NoActionBar"
    private void hideStatusBar() {
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




}
