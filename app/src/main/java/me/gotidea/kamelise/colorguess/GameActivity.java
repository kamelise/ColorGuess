package me.gotidea.kamelise.colorguess;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Set;

public class GameActivity extends AppCompatActivity implements PopupDialogFragment.PopupDialogListener {

    public static final String TAG = "======";

    private Game game;

    private float field_cell_left_margin;
    private float field_cell_right_margin;
    private float field_cell_transparent_left;
    private float field_cell_transparent_right;
    private float field_cell_filled_horizontal;

    private float field_line_negative_margin;
    private float field_line_start_end_margin;

    private float field_cell_filled_vertical;
    private float field_cell_transparent_top;
    private float field_cell_transparent_bottom;

    private float field_line_top_margin;
    private float field_line_bottom_margin;
    private float guessed_cell_top_margin;
    private float guessed_cell_right_margin;
    private float guessed_cell_width;
    private float stackCellLeftMargin;
    private float stackCellTopMargin;
    private float stackCellRightMargin;
    private float stackCellBottomMargin;

    private LinearLayout mainField;
    private LinearLayout guessedLayout;
    private LinearLayout stackLayout;

    private LinearLayout activeFieldLine;
    private LinearLayout activeCellView;

//    private float[] xStartCoordBorders;
    private float[] xCenterCoordOfFieldCell;
    private float[] xFinalArr;
    private float[] yFinalArr;

    public float radius;

    private float density;

    private HashMap<ShadowCircle, Integer> activeFieldCircles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        field_cell_left_margin = getResources().getDimension(R.dimen.field_cell_left_margin);
//        field_cell_right_margin = getResources().getDimension(R.dimen.field_cell_right_margin);
        field_cell_transparent_left = getResources().getDimension(R.dimen.field_cell_transparent_left);
        field_cell_transparent_right = getResources().getDimension(R.dimen.field_cell_transparent_right);
        field_cell_filled_horizontal = getResources().getDimension(R.dimen.field_cell_filled_horizontal);

        field_line_top_margin = getResources().getDimension(R.dimen.field_line_top_margin);
        field_line_bottom_margin = getResources().getDimension(R.dimen.field_line_bottom_margin);
        field_cell_filled_vertical = getResources().getDimension(R.dimen.field_cell_filled_vertical);
        field_cell_transparent_top = getResources().getDimension(R.dimen.field_cell_transparent_top);
        field_cell_transparent_bottom = getResources().getDimension(R.dimen.field_cell_transparent_bottom);

        guessed_cell_right_margin = getResources().getDimension(R.dimen.guessed_cell_right_margin);
        guessed_cell_top_margin = getResources().getDimension(R.dimen.guessed_cell_top_margin);
        guessed_cell_width = getResources().getDimension(R.dimen.guessed_cell_width);
        stackCellLeftMargin = getResources().getDimension(R.dimen.stack_cell_left_margin);
        stackCellTopMargin = getResources().getDimension(R.dimen.stack_cell_top_margin);
        stackCellRightMargin = getResources().getDimension(R.dimen.stack_cell_right_margin);
        stackCellBottomMargin = getResources().getDimension(R.dimen.stack_cell_bottom_margin);

        setContentView(R.layout.activity_game);

        mainField = (LinearLayout)this.findViewById(R.id.main_field);
        stackLayout = (LinearLayout)this.findViewById(R.id.stack_layout);
        guessedLayout = (LinearLayout)this.findViewById(R.id.guessed_layout);
//        guessedLayout.setBackgroundColor(Color.argb(20, 0, 0, 120));
//        mainField.setBackgroundColor(Color.argb(20, 0,120,0));
//        stackLayout.setBackgroundColor(Color.argb(20, 120,0,0));

        LinearLayout.LayoutParams mParams =
                (LinearLayout.LayoutParams) mainField.getLayoutParams();
        mParams.setMargins((int)(-field_cell_transparent_left), 0,
                (int)(-field_cell_transparent_right), 0);
        mainField.setLayoutParams(mParams);

        density = getResources().getDisplayMetrics().density;
        radius = getResources().getDimension(R.dimen.circle_radius);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        /* Hide both the navigation bar and the status bar.
         * SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
         * a general rule, you should design your app to hide the status bar whenever you
         * hide the navigation bar.
         */
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        final View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        //need negative margin to allow shadow to spread and not be cut
        field_line_negative_margin = -field_cell_transparent_left;
        //extra margin in visible part of mainField for better representation
        field_line_start_end_margin = field_cell_transparent_left;

        //calculate distance between centers of visible parts of field cells
        float delta = (mainField.getRight() - mainField.getLeft()
                + field_line_negative_margin*2f - field_line_start_end_margin*2f)/5;
        //calculate whole field cell width
        float field_cell_width = field_cell_filled_horizontal
                + field_cell_transparent_left + field_cell_transparent_right;
        //relative coord of cell field center point to the beginning of right border
        //of field cell image
        float field_cell_center = field_cell_width/2;

        //calculate field cell image margins
        field_cell_left_margin = (delta - field_cell_width)/2;
        field_cell_right_margin = field_cell_left_margin;

//==========
        //now when we know margins

        int first_cell_left_margin = (int)(-field_line_negative_margin
                + field_line_start_end_margin + field_cell_left_margin);
        int last_cell_right_margin = (int)(-field_line_negative_margin
                + field_line_start_end_margin + field_cell_right_margin);

        for (int i = 0; i < activeFieldLine.getChildCount(); i++) {
            LinearLayout fieldCell = (LinearLayout) activeFieldLine.getChildAt(i);
            LinearLayout.LayoutParams fCParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            //left margin for first cell
            fCParams.setMargins(i != 0 ? (int)(field_cell_left_margin) : first_cell_left_margin, 0,
                    i != 4 ? (int)(field_cell_right_margin) : last_cell_right_margin, 0);

            fieldCell.setLayoutParams(fCParams);
        }


//            if (i == 0)
//                fCParams.setMargins(0, 0, (int)(field_cell_right_margin), 0);
//            else if (i == 4)
//                fCParams.setMargins(0, (int)(field_cell_left_margin), 0, 0);
//            else
//                fCParams.setMargins((int)(field_cell_left_margin), 0, (int)(field_cell_right_margin), 0);
//        fCParams.setMargins((int)(field_cell_left_margin), 0, (int)(field_cell_right_margin), 0);
//            fCParams.setMargins(i != 0 ? (int)(field_cell_left_margin) : 0, 0,
//                    i != 4 ? (int)(field_cell_right_margin) : 0, 0);




//==========
        //absolute coord of first field cell center
        xCenterCoordOfFieldCell[0] = mainField.getLeft() + first_cell_left_margin
                + field_cell_center;

//        //initiate start bound of every cell in activeField
//        xStartCoordBorders[0] = temp - field_cell_filled_horizontal/2;

        for (int i = 1; i < game.fieldSize; i++) {
//            xStartCoordBorders[i] = xStartCoordBorders[i - 1] + delta;
            xCenterCoordOfFieldCell[i] = xCenterCoordOfFieldCell[i - 1] + delta;
        }

//        //the last one is rather end of mainField
//        xStartCoordBorders[game.fieldSize] = mainField.getRight() - mainField.getPaddingRight();

        RelativeLayout parentView = (RelativeLayout) this.findViewById(R.id.parent);

        yFinalArr[0] = parentView.getBottom() - radius * 2 + 25;
        float dy = (field_cell_filled_vertical + field_cell_transparent_top
                + field_cell_transparent_bottom)
                + field_line_top_margin + field_line_bottom_margin;
        for (int i = 1; i < game.maxMooves; i++) {
            yFinalArr[i] = yFinalArr[i - 1] - dy;
        }

        for (int i = 0; i < game.fieldSize; i++) {
//            xFinalArr[i] = xStartCoordBorders[i] + field_cell_center - radius;
            xFinalArr[i] = xCenterCoordOfFieldCell[i] - radius;
        }
    }

    private void init() {
        game = new Game(this, 5, 10, 10);

        //there are fieldSize+1 borders out there
//        xStartCoordBorders = new float[game.fieldSize + 1];
        xCenterCoordOfFieldCell = new float[5];
        xFinalArr = new float[game.fieldSize];
        yFinalArr = new float[game.maxMooves];

        activeFieldLine = addFieldLine();
        addStackToDrag(this, stackLayout);
        activeFieldCircles = new HashMap<>();
    }

    public void redraw() {
        game = new Game(this, 5, 10, 10);
        mainField.removeAllViews();
        guessedLayout.removeAllViews();
        stackLayout.removeAllViews();

        ViewGroup mainLayout = (ViewGroup) mainField.getParent().getParent();
        mainLayout.removeViews(2, mainLayout.getChildCount() - 2);


        activeFieldLine = addFieldLine();
        addStackToDrag(this, stackLayout);
        activeFieldCircles = new HashMap<>();
    }

    //should be run before filling out "stack to drag"
    public LinearLayout addFieldLine() {
        LinearLayout fieldLine = new LinearLayout(this);
        LinearLayout.LayoutParams fLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        fLParams.setMargins(0, (int) field_line_top_margin, 0, (int) field_line_bottom_margin);
        fieldLine.setLayoutParams(fLParams);
        fieldLine.setOrientation(LinearLayout.HORIZONTAL);
        fieldLine.setGravity(Gravity.CENTER);
        mainField.addView(fieldLine);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < game.fieldSize; i++) {
            LinearLayout fieldCell = (LinearLayout)inflater.inflate(R.layout.field_cell, null);

            fieldCell.setBackgroundResource(R.drawable.field_cell_normal);
            fieldCell.setId(i);

            LinearLayout.LayoutParams fCParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            //left margin for first cell
            int first_cell_left_margin = (int)(-field_line_negative_margin
                    + field_line_start_end_margin + field_cell_left_margin);
            int last_cell_right_margin = (int)(-field_line_negative_margin
                    + field_line_start_end_margin + field_cell_right_margin);
            fCParams.setMargins(i != 0 ? (int)(field_cell_left_margin) : first_cell_left_margin, 0,
                    i != 4 ? (int)(field_cell_right_margin) : last_cell_right_margin, 0);

            fieldCell.setLayoutParams(fCParams);

            fieldLine.addView(fieldCell);
        }
        return fieldLine;
    }

    private void addStackToDrag(Context context, final LinearLayout stackLayout) {
        for (int i = 0; i < game.numBalls; i++) {

            final CircleCell stackCell = new CircleCell(context, i);
            LinearLayout.LayoutParams sCParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            sCParams.setMargins((int)stackCellLeftMargin, (int)stackCellTopMargin,
                    (int)stackCellRightMargin, (int)stackCellBottomMargin);
            sCParams.gravity = Gravity.CENTER_HORIZONTAL;
            stackCell.setLayoutParams(sCParams);
            stackCell.setBackgroundResource(R.drawable.stack_cell);

            stackLayout.addView(stackCell);
        }
    }

    public void showGuessed(int placesGuessed, int colorsGuessed) {
        GuessedCell guessedCell = new GuessedCell(this, placesGuessed, colorsGuessed);

        LinearLayout.LayoutParams sCParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        sCParams.setMargins(0, (int)guessed_cell_top_margin, (int)guessed_cell_right_margin, 0);
        sCParams.gravity = Gravity.CENTER_HORIZONTAL;

        guessedCell.setLayoutParams(sCParams);
        guessedCell.setBackgroundResource(R.drawable.guessed_cell);
        guessedLayout.addView(guessedCell, 0);
    }

    public Game getGame() {
        return game;
    }

    public LinearLayout getActiveCellViewById(int i) {
        return (LinearLayout)activeFieldLine.getChildAt(i);
    }

    public LinearLayout getActiveCellView() {
        return activeCellView;
    }

    public void setActiveCellView(LinearLayout activeCellView) {
        this.activeCellView = activeCellView;
    }

    public void setActiveCellViewBackground(int drawableRes) {
        activeCellView.setBackgroundResource(drawableRes);
    }

    public float xStartBorder(int i) {
//        return xStartCoordBorders[i];
        if (i == 5)
            return xCenterCoordOfFieldCell[4] + field_cell_filled_horizontal/2;
        return xCenterCoordOfFieldCell[i] - field_cell_filled_horizontal/2;
    }

    public float xFinal(int i) {
        return xFinalArr[i];
    }

    public float yFinal(int i) {
        return yFinalArr[i];
    }

    public void onPauseClick(View view) {
        PopupDialogFragment pdf = new PopupDialogFragment();

        pdf.show(getSupportFragmentManager(), "PopupDialogFragment");
    }

    @Override
    public void onResumeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onNewGameClick(DialogFragment dialog)  {
        dialog.dismiss();
        redraw();
    }

    @Override
    public void onMainScreenClick(DialogFragment dialog) {
        super.finish();
        dialog.dismiss();
//        Toast.makeText(this, "Main screen is not yet ready", Toast.LENGTH_LONG).show();
    }

    public boolean isActiveLineContainCircle (ShadowCircle shadowCircle) {
        return activeFieldCircles.containsKey(shadowCircle);
    }

    public void addCircleToActiveLine(int index, ShadowCircle shadowCircle) {
        activeFieldCircles.put(shadowCircle, index);
    }

    public int removeCircleFromActiveLine(ShadowCircle shadowCircle) {
        return activeFieldCircles.remove(shadowCircle);
    }

    //"remove" touch listeners from shadowcircles in active field
    public void removeTouchListeners() {
        Set<ShadowCircle> keys = activeFieldCircles.keySet();
        for (ShadowCircle key: keys) {
            key.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }
        activeFieldCircles.clear();
    }

    public ShadowCircle findShadowCircleToRemoveFromBoard(int index) {
        Set<ShadowCircle> keys = activeFieldCircles.keySet();
        for (ShadowCircle key: keys) {
            int i = activeFieldCircles.get(key);
            if (i == index) {
                System.out.println("removing shadowcircle " + key + " from game activity");
                activeFieldCircles.remove(key);
                return key;
            }
        }
        return null;
    }

    public void removeShadowCircleFromBoard(ShadowCircle shadowCircle) {
        ViewGroup mainLayout = (ViewGroup) mainField.getParent().getParent();
        mainLayout.removeView((shadowCircle));
    }
}