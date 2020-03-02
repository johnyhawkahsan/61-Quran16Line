package com.johnyhawkdesigns.a61_quran16line;

import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity
        extends AppCompatActivity
        implements OnTapListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String PDF_FILE = "quran.pdf";
    boolean isStatusBarVisible;

    int totalNoOfPages;

    PDFView pdfView;
    View decorView;
    Button gotoButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        decorView = getWindow().getDecorView(); // this view can be used to show or hide status bar
        gotoButton = findViewById(R.id.gotoButton);

        // At app launch, we want to hide status bar
        //hideStatusBar();
        hideGotoButton();

        // Showing pdf file
        pdfView = findViewById(R.id.pdfView);
        pdfView.fromAsset(PDF_FILE)
                .scrollHandle(new DefaultScrollHandle(this)) // This shows page number while scrolling
                .onTap(this)
                .spacing(2) // in dp
                .pageFitPolicy(FitPolicy.BOTH)
                .load();

        //pdfView.jumpTo(50); // go to specific page

        totalNoOfPages = pdfView.getPageCount(); // get total no of pages === WHY IS IT SHOWING 0 PAGES here and when initialized elsewhere, works correctly??===
        Log.d(TAG, "onCreate: pdfView.getPageCount() = " + totalNoOfPages);


        gotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pdfView.jumpTo(pdfView.getPageCount() / 2); // go to specific page
                Intent intent = new Intent(MainActivity.this, NavigationDrawer.class);
                startActivity(intent);
            }
        });

    }





    private void toggleGotoButton(){
        if (gotoButton.getVisibility() == View.INVISIBLE){
            showGotoButton();
        } else {
            hideGotoButton();
        }
    }


    private void hideGotoButton(){
        gotoButton.setVisibility(View.INVISIBLE);
    }

    private void showGotoButton(){
        gotoButton.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTap(MotionEvent e) {
        Log.d(TAG, "onTap: pdfView.getCurrentPage() = " + pdfView.getCurrentPage());
        Log.d(TAG, "onTap: pdfView.getPageCount() = " + pdfView.getPageCount());
        //toggleFullscreen();
        toggleGotoButton();
        return true;
    }

}
