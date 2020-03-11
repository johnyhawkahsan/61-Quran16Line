package com.johnyhawkdesigns.a61_quran16line.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
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
    Button gotoButton;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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

        

        gotoButton = view.findViewById(R.id.gotoButton);
        gotoButton.setOnClickListener(this);
        gotoButton.setVisibility(View.INVISIBLE); // at start, we want the button to be invisible

        return view;
    }


    @Override
    public boolean onTap(MotionEvent e) {
        Log.d(TAG, "onTap: pdfView.getCurrentPage() = " + pdfView.getCurrentPage());

        // Here, we are accessing method available inside "NavigationDrawer" activity. We could also use Interface method here.
        ((NavigationDrawer)getActivity()).toggleFullscreen();
        toggleGotoButton();

        return true;
    }

    private void toggleGotoButton() {
        if (gotoButton.getVisibility() == View.INVISIBLE){
            gotoButton.setVisibility(View.VISIBLE);
        } else {
            gotoButton.setVisibility(View.INVISIBLE);
        }
    }


    // NOTE: pdfView.getPageCount() returned 0 inside onCreate and onCreateView.
    @Override
    public void loadComplete(int nbPages) {
        totalNoOfPages = pdfView.getPageCount();
        // totalNoOfPages = nbPages; // same as pdfView.getPageCount()
        // gotoButton.setText(String.valueOf( pdfView.getCurrentPage()  + "/" + totalNoOfPages)); // not used here because now I'm setting text in onPageChanged
        Log.d(TAG, "loadComplete: totalNoOfPages = " + totalNoOfPages);

        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.d(TAG, "title = " + meta.getTitle());

        printBookmarksTree(pdfView.getTableOfContents());
    }

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
    public void onPageChanged(int page, int pageCount) { // pageCount is same as totalNoOfPages
        int correctedPageNo = (page + 1);
        gotoButton.setText(String.valueOf( correctedPageNo + " / " + totalNoOfPages));
    }

    @Override
    public void onClick(View v) {

        GotoDialogFragment dialogFragment = GotoDialogFragment.newInstance(totalNoOfPages); // no of pages are used as bundle arguments
//        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.nav_host_fragment, dialogFragment);
//        ft.addToBackStack(null);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "dialog");

        dialogFragment.setDialogListener(new GotoDialogFragment.DialogListener() {
            @Override
            public void onEnterPageNo(int pageNo) {
                Log.d(TAG, "onEnterPageNo: pageNo = " + pageNo);
                pdfView.jumpTo(pageNo); // go to specific page
            }
        });

    }

}