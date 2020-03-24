package com.johnyhawkdesigns.a61_quran16line.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.johnyhawkdesigns.a61_quran16line.R;

public class AboutFragment extends Fragment {

    TextView aboutTextView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        aboutTextView = view.findViewById(R.id.text_about);
        aboutTextView.setText("Developed by Ahsan");


        return view;
    }
}