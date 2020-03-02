package com.johnyhawkdesigns.a61_quran16line.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.johnyhawkdesigns.a61_quran16line.NavigationDrawer;
import com.johnyhawkdesigns.a61_quran16line.R;

public class HomeFragment
        extends Fragment
        implements OnTapListener {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final String PDF_FILE = "quran.pdf";

    PDFView pdfView;

    int totalNoOfPages;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);



        // Showing pdf file
        pdfView = view.findViewById(R.id.pdfViewQuran);
        pdfView.fromAsset(PDF_FILE)
                .scrollHandle(new DefaultScrollHandle(getContext())) // This shows page number while scrolling
                .onTap(this)
                .spacing(2) // in dp
                .pageFitPolicy(FitPolicy.BOTH)
                .load();




        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        totalNoOfPages = pdfView.getPageCount();
        Log.d(TAG, "onViewCreated: totalNoOfPages = " + totalNoOfPages);
    }

    @Override
    public boolean onTap(MotionEvent e) {
        Log.d(TAG, "onTap: pdfView.getCurrentPage() = " + pdfView.getCurrentPage());
        Log.d(TAG, "onTap: pdfView.getPageCount() = " + pdfView.getPageCount());

        ((NavigationDrawer)getActivity()).toggleFullscreen();
        return true;
    }




}