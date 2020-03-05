package com.johnyhawkdesigns.a61_quran16line.ui.index;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IndexViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public IndexViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}