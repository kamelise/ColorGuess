package me.gotidea.kamelise.colorguess;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

;

/**
 * Created by kamelise on 7/18/17.
 */
public class ResultDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ResultDialogListener {
        void onStartAgainClick(DialogFragment dialog);
        void onStatsClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    ResultDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the PauseDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ResultDialogListener so we can send events to the host
            mListener = (ResultDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ResultDialogListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.popup_result, container, false);
        TextView statsBtn = (TextView) dialog.findViewById(R.id.stats_btn);
        statsBtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
//                                             Log.d(GameActivity.TAG, "clicked stats button!");
                                             mListener.onStatsClick(ResultDialogFragment.this);
                                         }
                                     }
        );
        TextView newGameBtn = (TextView) dialog.findViewById(R.id.start_again_btn);
        newGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(GameActivity.TAG, "clicked start again button!");
                mListener.onStartAgainClick(ResultDialogFragment.this);
            }
        });
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        Window window = getDialog().getWindow();
        window.setLayout(width, height);
        window.setGravity(Gravity.CENTER);
    }
}
