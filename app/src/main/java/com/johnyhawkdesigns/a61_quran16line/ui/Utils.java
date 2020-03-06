package com.johnyhawkdesigns.a61_quran16line.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import static android.content.Context.MODE_PRIVATE;

public class Utils {


    private static final String BOOKMARKS_PREFERENCES = null;

    public void saveBookmark(PDFView pdfView, Context context){
        int bookmarkedPage = pdfView.getCurrentPage();

        //bookmarkedPage is the page which has bookmark
        SharedPreferences.Editor editor = context.getSharedPreferences(BOOKMARKS_PREFERENCES, MODE_PRIVATE).edit();
        editor.putBoolean("itemID", true);
        editor.putInt("bookmarkedPageNum", bookmarkedPage);
        editor.apply();
        Toast.makeText(context, "Save bookMark", Toast.LENGTH_SHORT).show();
    }

    public void getBookmarkedPage(PDFView pdfView, Context context){
        //TODO load page saved in share preferance
        SharedPreferences preferences = context.getSharedPreferences(BOOKMARKS_PREFERENCES, MODE_PRIVATE);
        boolean isBookmark = preferences.getBoolean("itemID", false);
        int bookmarkedPage = preferences.getInt("bookmarkedPageNum", 0);
        pdfView.jumpTo(bookmarkedPage, true);
        Toast.makeText(context, " bookMark", Toast.LENGTH_SHORT).show();

        // the bookmarked icon

        if( bookmarkedPage == pdfView.getCurrentPage()) {
            // bookmarkIcon.setVisibility(View.VISIBLE);
        }
    }
}
