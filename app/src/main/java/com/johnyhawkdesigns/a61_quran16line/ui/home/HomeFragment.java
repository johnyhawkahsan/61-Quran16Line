package com.johnyhawkdesigns.a61_quran16line.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.johnyhawkdesigns.a61_quran16line.NavigationDrawer;
import com.johnyhawkdesigns.a61_quran16line.R;
import com.johnyhawkdesigns.a61_quran16line.ui.dialog.GotoDialogFragment;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;

public class HomeFragment
        extends Fragment
        implements OnTapListener, OnLoadCompleteListener, OnPageChangeListener, View.OnClickListener {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final String PDF_FILE = "quran.pdf";

    public PDFView pdfView;
    static int totalNoOfPages;
    Boolean isFullscreen;

    FloatingActionButton fab;
    Button gotoButton;

    private HomeFragmentListener homeFragmentListener;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        isFullscreen = true; // at the start of the app, we want the app to be fullscreen

        setupFab(view);
        gotoButton = view.findViewById(R.id.gotoButton);
        gotoButton.setOnClickListener(this);

        // Showing pdf file
        pdfView = view.findViewById(R.id.pdfViewQuran);
        pdfView.fromAsset(PDF_FILE)
                .scrollHandle(new DefaultScrollHandle(getContext())) // This shows page number while scrolling
                .onTap(this)
                .onLoad(this)
                .onPageChange(this)
                .spacing(2) // in dp
                .pageFitPolicy(FitPolicy.BOTH)
                .load();

        return view;
    }




    public void setupFab(View view) {
        fab = view.findViewById(R.id.fabBookmark);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Bookmark Saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onTap(MotionEvent e) {

        if (isFullscreen){
            showGotoButton();
            fab.show();
            homeFragmentListener.fullscreen(isFullscreen); // passing fullscreen boolean using interface method
            isFullscreen = false;
        } else {
            hideGotoButton();
            fab.hide();
            homeFragmentListener.fullscreen(isFullscreen); // passing fullscreen boolean using interface method
            isFullscreen = true;
        }
        //((NavigationDrawer)getActivity()).toggleFullscreen(); // Old method that I used before implementing interface method

        return true;
    }

    private void showGotoButton() {
        if (gotoButton.getVisibility() == View.INVISIBLE){
            gotoButton.setVisibility(View.VISIBLE);
        }
    }

    private void hideGotoButton() {
        if (gotoButton.getVisibility() == View.VISIBLE){
            gotoButton.setVisibility(View.INVISIBLE);
        }
    }

    // NOTE: pdfView.getPageCount() returned 0 inside onCreate and onCreateView.
    @Override
    public void loadComplete(int nbPages) {
        totalNoOfPages = pdfView.getPageCount();
        // totalNoOfPages = nbPages; // same as pdfView.getPageCount()
        Log.d(TAG, "loadComplete: totalNoOfPages = " + totalNoOfPages);

        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.d(TAG, "title = " + meta.getTitle());

        homeFragmentListener.returnBookmarks(pdfView.getTableOfContents()); // pass tableOfContents to Activity
        homeFragmentListener.passPdfView(pdfView); // pass pdfView to Activity
    }



    @Override
    public void onPageChanged(int page, int pageCount) { // pageCount is same as totalNoOfPages
        int correctedPageNo = (page + 1);
        gotoButton.setText(correctedPageNo + " / " + pageCount);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            homeFragmentListener = (HomeFragmentListener) context;
        } catch (ClassCastException castException) {
            /** The activity does not implement the listener. */
            Log.e(TAG, "onAttach: ", castException);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeFragmentListener = null;
    }

    @Override
    public void onClick(View v) {
        GotoDialogFragment dialogFragment = GotoDialogFragment.newInstance(pdfView.getPageCount()); // no of pages are used as bundle arguments
        dialogFragment.show(getActivity().getSupportFragmentManager(), "dialog");
        dialogFragment.setDialogListener(new GotoDialogFragment.DialogListener() {
            @Override
            public void onEnterPageNo(int pageNo) {
                Log.d(TAG, "onEnterPageNo: pageNo = " + pageNo);
                pdfView.jumpTo(pageNo); // go to specific page
            }
        });
    }


    public interface HomeFragmentListener{
        public void returnBookmarks (List<PdfDocument.Bookmark> tableOfContents);
        public void fullscreen(Boolean isFullscreen);
        public void passPdfView(PDFView pdfView);
        //public void pageChanged(int pageNo, int pageCount); // not using now because I moved goto button in fragment
    }

}


