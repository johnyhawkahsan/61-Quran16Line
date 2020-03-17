package com.johnyhawkdesigns.a61_quran16line.ui.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.johnyhawkdesigns.a61_quran16line.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class GotoDialogFragment extends DialogFragment {

    // This interface should be implemented by HomeFragment so the page no will be transferred there.
    public interface GotoDialogListener {
        void onEnterPageNo(int pageNo);
    }
    private GotoDialogListener gotoDialogListener;

    private static final String TAG = GotoDialogFragment.class.getSimpleName();

    public static GotoDialogFragment newInstance(int totalNoOfPages) {
        GotoDialogFragment gotoDialogFragment = new GotoDialogFragment();

       // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("totalNoOfPages", totalNoOfPages);
        gotoDialogFragment.setArguments(args);

        return gotoDialogFragment;
    }

    public void setGotoDialogListener(GotoDialogListener gotoDialogListener){
        this.gotoDialogListener = gotoDialogListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.dialogfragment_goto, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int totalNoOfPages = 0;
        try {
            totalNoOfPages = getArguments().getInt("totalNoOfPages");
        } catch (Exception e){
            Log.e(TAG, "onViewCreated: ", e);
        }

        final EditText editText = view.findViewById(R.id.editTextPageNo);
        editText.setHint("Enter page from 1 to " + totalNoOfPages);



        Button btnDone = view.findViewById(R.id.btnGo);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredPageNoStr = editText.getText().toString();

                // if entered no is not empty
                if (!TextUtils.isEmpty(enteredPageNoStr)){
                    int enteredPageNo = (Integer.valueOf(enteredPageNoStr) - 1); // decrement by 1
                    Log.d(TAG, "onClick: enteredPageNo = " + enteredPageNoStr);
                    gotoDialogListener.onEnterPageNo(enteredPageNo);
                }

                dismiss(); // close the dialog
            }
        });

        Button btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }



}
