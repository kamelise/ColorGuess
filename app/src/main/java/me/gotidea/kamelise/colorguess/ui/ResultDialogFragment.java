package me.gotidea.kamelise.colorguess.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

;import me.gotidea.kamelise.colorguess.R;

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
        //removing title - for android version < 6.0
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

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

        ImageView resPicture = (ImageView) dialog.findViewById(R.id.result_picture);
        int resId = getArguments().getInt(getString(R.string.picture_resource_id_key));
        resPicture.setImageDrawable(getResources().getDrawable(resId));
        resPicture.setAlpha(0.8f);

        TextView titleTV = (TextView) dialog.findViewById(R.id.result_title_txt);
        titleTV.setText(getArguments().getString(getString(R.string.result_title_txt_key)));

        LinearLayout iconsLL = (LinearLayout) dialog.findViewById(R.id.result_stars_icns);
        int starsNum = getArguments().getInt(getString(R.string.stars_num_key));
        for (int i = 0; i < iconsLL.getChildCount(); i++) {
            ImageView star = (ImageView) iconsLL.getChildAt(i);
            if (starsNum >= i + 1)
                star.setColorFilter(dialog.getResources().getColor(R.color.yellow));
            else
                star.setColorFilter(dialog.getResources().getColor(R.color.light_grey));
        }

        TextView timePlayedTV = (TextView) dialog.findViewById(R.id.time_played);
        String tVText = "Time Played: " + getArguments().getString(getString(R.string.time_played_key));
        timePlayedTV.setText(tVText);
        TextView consequentWinsTV = (TextView) dialog.findViewById(R.id.wins_sequence);
        String cWText = "Wins in a row: " + getArguments().getInt(getString(R.string.consequent_wins_key));
        consequentWinsTV.setText(cWText);
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
