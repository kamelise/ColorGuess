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
import android.widget.LinearLayout;

;

/**
 * Created by kamelise on 7/18/17.
 */
public class PopupDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface PopupDialogListener {
        void onResumeClick(DialogFragment dialog);
        void onNewGameClick(DialogFragment dialog);
        void onMainScreenClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    PopupDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the PopupDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the PopupDialogListener so we can send events to the host
            mListener = (PopupDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement PopupDialogListener");
        }
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        // Get the layout inflater
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.pause_popup, null));
//        Log.d(GameActivity.TAG, "created dialog ");
//        return builder.create();
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.pause_popup, container, false);
        LinearLayout resumeBtn = (LinearLayout) dialog.findViewById(R.id.resume);
        resumeBtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
//                                             Log.d(GameActivity.TAG, "clicked resume button!");
                                             mListener.onResumeClick(PopupDialogFragment.this);
                                         }
                                     }
        );
        LinearLayout newGameBtn = (LinearLayout) dialog.findViewById(R.id.new_game);
        newGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(GameActivity.TAG, "clicked newGame button!");
                mListener.onNewGameClick(PopupDialogFragment.this);
            }
        });
        LinearLayout mainScreenBtn = (LinearLayout) dialog.findViewById(R.id.main_screen);
        mainScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(GameActivity.TAG, "clicked mainScreen button!");
                mListener.onMainScreenClick(PopupDialogFragment.this);
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
