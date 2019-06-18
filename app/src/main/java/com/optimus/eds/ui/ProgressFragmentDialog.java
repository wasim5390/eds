package com.optimus.eds.ui;

import android.app.Dialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.optimus.eds.R;


/**
 * Created by sidhu on 12/04/2018.
 */

public class ProgressFragmentDialog extends DialogFragment {

    public static ProgressFragmentDialog newInstance() {
        Bundle args = new Bundle();
        ProgressFragmentDialog fragment = new ProgressFragmentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (d.getWindow() != null)
                d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_progress, container, false);
        ((ProgressBar)view.findViewById(R.id.progressBar))
                .getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        return view;
    }
}
