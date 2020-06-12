package com.johnyhawkdesigns.a61_quran16line;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.google.android.material.navigation.NavigationView;
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
import android.view.WindowManager;
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


    // Related to ExpandableListView
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>(); // Holds headers of the Navigation Drawers
    HashMap<MenuModel, List<Bookmark>> childList = new HashMap<>(); // Holds children of the Navigation Drawers

    // I'm returning this pdfView from HomeFragment using interface method
    PDFView pdfView;

    MenuModel bookmarkMenuModel; // this single menu model for bookmarks is used to update when bookmark is saved

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        decorView = getWindow().getDecorView(); // this view can be used to show or hide status bar
        showStatusBar2(); // we want the app to start in full screen

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder( // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();

        setupNavController(navigationView);

        // Related to ExpandableListView
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
                    //this.soorahContents = bookmark.getChildren(); // also correct
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
                Log.d(TAG, "prepareParahData: size = " + parahContents.size());
                childList.put(menuModel, parahContents);
            }

            menuModel = new MenuModel(Utils.MenuName_Soorah, true, true);
            headerList.add(menuModel);
            if (menuModel.hasChildren) {
                Log.d(TAG, "prepareSoorahData: size = " + soorahContents.size());
                childList.put(menuModel, soorahContents);
            }

            bookmarkMenuModel = new MenuModel(Utils.MenuName_Bookmarks, true, true);
            headerList.add(bookmarkMenuModel);
            // add bookmarks
            if (bookmarkMenuModel.hasChildren) {
                getBookmarkedPages(this); // get shared preferences data and store inside bookmarkContents list
                Log.d(TAG, "prepareMenuData: bookmarkContents.size() = " + bookmarkContents.size());
                childList.put(bookmarkMenuModel, bookmarkContents);
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
            bookmarkContents.add(bookmark); // add this bookmark to bookmarkContents list so we can populate Bookmarks in menu
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
                            navController.navigate(R.id.nav_about);// navigate to this fragment
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
                    //Toast.makeText(NavigationDrawer.this, "Parah = " + bookmarkName + ", pageNo = " + pageNo, Toast.LENGTH_SHORT).show();
                }

                // by clicking on "Bookmarks" children, we need to be able to delete bookmarks
                if (headerList.get(groupPosition).menuName.equals(Utils.MenuName_Bookmarks)){
                    Log.d(TAG, "onChildClick: Child is a part of ==BOOKMARKS Group=");
                }


                return true;
            }
        });



    }


    @Override
    public void fullscreen(Boolean isFullscreen) {
        Log.d(TAG, "fullscreen: isFullscreen = " + isFullscreen);
        if (isFullscreen){
            showStatusBar();
            showStatusBar2(); // This method is actually working and showing toolbar
            showToolbar();

        }else {
            hideStatusBar();
            hideStatusBar2();
            hideToolBar();
        }

    }

    // I'm trying/practicing to actually show status bar - showStatusBar method did not work for me
    private void showStatusBar2() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // clear full screen
        Log.d(TAG, "showStatusBar2: show");
    }

    private void hideStatusBar2() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // use full screen
        Log.d(TAG, "hideStatusBar2: hide");
    }

    private void showStatusBar() {
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE; // Show the status bar.
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


    @Override
    public void bookmarkSaved(String title) {
        Log.d(TAG, "bookmarkSaved: title = " + title);

        refreshBookmarkList();
    }

    // 1st clear bookmark content, then retrieve bookmarks again to fill the list = NOTE: Once list is updated, then we will be able to call notifyDataSetChanged()
    public void refreshBookmarkList(){
        bookmarkContents.clear();
        getBookmarkedPages(this); // get updated bookmarks list and update List<Bookmark> bookmarkContents
        expandableListAdapter.notifyDataSetChanged();
        Log.d(TAG, "prepareMenuData: bookmarkContents.size() = " + bookmarkContents.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.clear_bookmarks:
                Log.d(TAG, "onOptionsItemSelected: delete all children profiles");

                // Build alert dialog for confirmation
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Do you want to delete all bookmark data??");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(NavigationDrawer.this, "Deleting All Bookmarks", Toast.LENGTH_SHORT).show();

                        SharedPreferences preferences = getSharedPreferences(Utils.BOOKMARKS_PREFERENCES, MODE_PRIVATE);
                        //preferences.edit().remove("key").commit(); // remove single item
                        preferences.edit().clear().apply(); // clear all keys
                        refreshBookmarkList(); // to clear all items in list
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog ad = builder.create();
                ad.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
