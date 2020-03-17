package com.johnyhawkdesigns.a61_quran16line.ui.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.johnyhawkdesigns.a61_quran16line.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class BookmarkDialogFragment extends DialogFragment {


    // This interface should be implemented by HomeFragment so the page no will be transferred there.
    public interface BookmarkDialogListener {
        void onEnterTitle(String bookmarkTitle);
    }
    private BookmarkDialogListener bookmarkDialogListener;

    private static final String TAG = GotoDialogFragment.class.getSimpleName();

    public static BookmarkDialogFragment newInstance() {
        BookmarkDialogFragment bookmarkDialogFragment = new BookmarkDialogFragment();
        return bookmarkDialogFragment;
    }

    public void setBookmarkDialogListener(BookmarkDialogListener bookmarkDialogListener){
        this.bookmarkDialogListener = bookmarkDialogListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialogfragment_bookmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText editText = view.findViewById(R.id.editTextBookmarkTitle);

        Button btnDone = view.findViewById(R.id.btnGoBkmrk);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bookmarkTitle = editText.getText().toString();

                if (!TextUtils.isEmpty(bookmarkTitle)){
                    bookmarkDialogListener.onEnterTitle(bookmarkTitle);
                    dismiss(); // close the dialog
                } else {
                    Snackbar.make(view, "Please enter title", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }


            }
        });

        Button btnCancel = view.findViewById(R.id.btnCancelBkmrk);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
