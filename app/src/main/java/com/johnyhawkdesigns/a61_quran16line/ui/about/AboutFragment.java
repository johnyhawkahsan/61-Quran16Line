package com.johnyhawkdesigns.a61_quran16line.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.johnyhawkdesigns.a61_quran16line.R;

public class AboutFragment extends Fragment {

    Toolbar toolbar;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        toolbar = view.findViewById(R.id.toolbar);

        ImageView imageView = view.findViewById(R.id.appIconImage);
        Glide
                .with(this)
                .load(R.drawable.quran_app_icon)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);

        return view;
    }



}