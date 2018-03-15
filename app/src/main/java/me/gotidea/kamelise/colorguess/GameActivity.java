package me.gotidea.kamelise.colorguess;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Set;

public class GameActivity extends AppCompatActivity implements PopupDialogFragment.PopupDialogListener {

    public static final String TAG = "======";

    private Game game;

    private float fieldCellLeftMargin;
    private float fieldCellRightMargin;
    private float firstCellLeftMargin;
    private float lastCellRightMargin;
    private float fieldCellWidth;
    private float fieldCellHeight;
    private int partsForExtraMargin;

    private float fieldLineTopMargin;
    private float fieldLineBottomMargin;

    //used to not cut shadows
    private float overlapMarginVertical;
    private float overlapMarginHorizontal;
    private float extraBottomMargin;

    private LinearLayout topBarLayout;

    private LinearLayout mainField;
    private LinearLayout guessedLayout;
    private LinearLayout stackLayout;

    private LinearLayout activeFieldLine;
    private LinearLayout activeCellView;

    private int[] xCenterCoordOfFieldCell;
    private int[] xFinalArr;
    private int[] yFinalArr;

    public float radius;
    float fieldCellElevation;

    private float density;

    private HashMap<ShadowCircle, Integer> activeFieldCircles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        density = getResources().getDisplayMetrics().density;


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.d(TAG, "screen width is " + metrics.widthPixels + ", height is " + metrics.heightPixels);

        overlapMarginVertical = getResources().getDimension(R.dimen.overlap_margin_vertical);
        overlapMarginHorizontal = getResources().getDimension(R.dimen.overlap_margin_horizontal);

        extraBottomMargin = getResources().getDimension(R.dimen.extra_bottom_margin);

        fieldLineTopMargin = getResources().getDimension(R.dimen.field_line_top_margin);
        fieldLineBottomMargin = getResources().getDimension(R.dimen.field_line_bottom_margin);

        fieldCellWidth = getResources().getDimension(R.dimen.field_cell_width);
        fieldCellHeight = fieldCellWidth;

        fieldCellElevation = getResources().getDimension(R.dimen.field_cell_elevation);

        setContentView(R.layout.activity_game);

//        topBarLayout = (LinearLayout)this.findViewById(R.id.top_bar);

        mainField = (LinearLayout)this.findViewById(R.id.main_field);
        stackLayout = (LinearLayout)this.findViewById(R.id.stack_layout);
        guessedLayout = (LinearLayout)this.findViewById(R.id.guessed_layout);
//        guessedLayout.setBackgroundColor(Color.argb(20, 0, 0, 120));
//        mainField.setBackgroundColor(Color.argb(20, 0,120,0));
//        stackLayout.setBackgroundColor(Color.argb(20, 120,0,0));

        LinearLayout.LayoutParams mParams =
                (LinearLayout.LayoutParams) mainField.getLayoutParams();
        mParams.setMargins(-(int)overlapMarginHorizontal, 0,0, 0);
//        mainField.setLayoutParams(mParams);

        LinearLayout.LayoutParams sParams =
                (LinearLayout.LayoutParams) stackLayout.getLayoutParams();
        sParams.setMargins(0, 0, 0, 0);
//        stackLayout.setLayoutParams(sParams);

//        Log.d(TAG, "mainField right margin is " + mParams.rightMargin + ", stackLayout left margin is " + sParams.leftMargin);

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
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
//        Log.d(TAG, "top bar z is " + topBarLayout.getZ());

//        RelativeLayout parent = (RelativeLayout) this.findViewById(R.id.parent);
//        LinearLayout bottomPart = (LinearLayout) this.findViewById(R.id.bottom_part);
//
//        Log.d(TAG, "parent width is " + parent.getWidth() + ", right: " + parent.getRight() + ", left: " + parent.getLeft());
//        Log.d(TAG, "bottom part width is " + bottomPart.getWidth() + ", right: " + bottomPart.getRight() + ", left: " + bottomPart.getLeft());
//        Log.d(TAG, "mainField width is " + mainField.getWidth() + ", right: " + mainField.getRight() + ", left: " + mainField.getLeft());
//        Log.d(TAG, "stackLayout width is " + stackLayout.getWidth() + ", right: " + stackLayout.getRight() + ", left: " + stackLayout.getLeft());
//        Log.d(TAG, "guessedLayout width is " + guessedLayout.getWidth() + ", right: " + guessedLayout.getRight() + ", left: " + guessedLayout.getLeft());

       //how much extra margin parts we want to add to first and last cells
        partsForExtraMargin = 5;
        //calculate space between field cells in field line
//        Log.d(TAG, "mainField right is " + mainField.getRight() + ", mainfield left is " + mainField.getLeft());
        float fieldCellsSpace = (mainField.getRight() - mainField.getLeft()
                - game.fieldSize * fieldCellWidth)/(game.fieldSize + partsForExtraMargin);
        //calculate distance between centers of field cells
        int delta = (int)fieldCellWidth + (int)fieldCellsSpace;
        //relative coord of cell field center point to the beginning of right border
        //of field cell image
        float fieldCellCenter = fieldCellWidth /2;

//        Log.d(TAG, "field cells space is " + fieldCellsSpace + ", delta is " + delta
//                + ", fieldCellCenter is " + fieldCellCenter);

        //calc field cell image margins
        fieldCellRightMargin = fieldCellsSpace;
        fieldCellLeftMargin = 0;

        //calc first and last cell margins
        firstCellLeftMargin = (partsForExtraMargin + 1) * fieldCellsSpace/2;
        lastCellRightMargin = firstCellLeftMargin;

//        Log.d(TAG, "fieldCell right margin is " + fieldCellLeftMargin
//                + ", first and last cell margin is " + firstCellLeftMargin);

        for (int i = 0; i < activeFieldLine.getChildCount(); i++) {
            LinearLayout fieldCell = (LinearLayout) activeFieldLine.getChildAt(i);
            LinearLayout.LayoutParams fCParams = (LinearLayout.LayoutParams) fieldCell.getLayoutParams();
            fCParams.setMargins(i != 0 ? (int)(fieldCellLeftMargin) : (int) firstCellLeftMargin, fCParams.topMargin,
                    i != game.fieldSize - 1 ? (int)(fieldCellRightMargin) : (int) lastCellRightMargin, fCParams.bottomMargin);
        }

        //in connection with rounding, activeFieldLine got some margins,
        // need to calc them and consider them for further calculations
        int fLMargins = mainField.getWidth() - 4*((int)fieldCellLeftMargin + (int)fieldCellRightMargin)
                - (int)firstCellLeftMargin - (int)lastCellRightMargin - 5*(int)fieldCellWidth;

        //absolute coord of first field cell center
        xCenterCoordOfFieldCell[0] = mainField.getLeft() + (int)(fLMargins/2) + (int)firstCellLeftMargin
                + (int)fieldCellCenter;
//        Log.d(TAG, "xCenterCoordOfFieldCell[0] = " + xCenterCoordOfFieldCell[0]);

        for (int i = 1; i < game.fieldSize; i++) {
            xCenterCoordOfFieldCell[i] = xCenterCoordOfFieldCell[i - 1] + delta;
//            Log.d(TAG, "xCenterCoordOfFieldCell[" + i + "] = xCenterCoordOfFieldCell[" + (i - 1) + "] + delta: "
//                    + xCenterCoordOfFieldCell[i]);
        }

        for (int i = 0; i < game.fieldSize; i++) {
            xFinalArr[i] = Math.round(xCenterCoordOfFieldCell[i] - (int)radius);
//            Log.d(TAG, "xFinal[" + i + "] is " + xFinalArr[i]);
        }

        int yCenterCoordBottomCell = mainField.getBottom()
                - ((int)(fieldLineBottomMargin + extraBottomMargin - overlapMarginVertical) + (int) overlapMarginVertical)
                - (int)(fieldCellHeight/2 + 1);
        yFinalArr[0] = yCenterCoordBottomCell - (int) radius;

        int dy = (int)fieldCellHeight + (int)fieldLineTopMargin + (int)fieldLineBottomMargin;
//        Log.d(TAG, "yFinal[0] is " + Math.round(yFinalArr[0]));
        for (int i = 1; i < game.maxMoves; i++) {
            yFinalArr[i] = yFinalArr[i - 1] - dy;
//            Log.d(TAG, "yFinal[" + i + "] is " + yFinalArr[i]);
        }

//        Log.d(TAG, "activeFieldLine left is " + activeFieldLine.getLeft() + ", right is " + activeFieldLine.getRight());
//
//        for (int i =0; i < activeFieldLine.getChildCount(); i++) {
//            LinearLayout fieldCell = (LinearLayout) activeFieldLine.getChildAt(i);
//
//            Log.d(TAG, "cell" + i + " left is " + fieldCell.getLeft() + ", right is " + fieldCell.getRight()
//                    + ", xCenterCoordOfFieldCell[" + i + "] = "
//                    + (mainField.getLeft() + activeFieldLine.getLeft() + fieldCell.getLeft() + fieldCellCenter - radius));
//        }
    }

    private void init() {
        game = new Game(this, 5, 10, 10);

        //there are fieldSize+1 borders out there
//        xStartCoordBorders = new float[game.fieldSize + 1];
        xCenterCoordOfFieldCell = new int[game.fieldSize];
        xFinalArr = new int[game.fieldSize];
        yFinalArr = new int[game.maxMoves];

        addFieldLine(8 * density);
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


        addFieldLine(8 * density);
        addStackToDrag(this, stackLayout);
        activeFieldCircles = new HashMap<>();
    }

    //should be run before filling out "stack to drag"
    public void addFieldLine(float extraBottomMargin) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout fieldLine = new LinearLayout(this);

        LinearLayout.LayoutParams fLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
//        Log.d(TAG, "extra_bottom_margin will be applied: " + extra_bottom_margin);
        fLParams.setMargins(0, (int) (fieldLineTopMargin - overlapMarginVertical),
                0, (int) (fieldLineBottomMargin + extraBottomMargin - overlapMarginVertical));
        fLParams.gravity = Gravity.CENTER_HORIZONTAL;
        fieldLine.setLayoutParams(fLParams);
        fieldLine.setOrientation(LinearLayout.HORIZONTAL);
        fieldLine.setGravity(Gravity.CENTER);
//        fieldLine.setBackgroundColor(Color.parseColor("#33ffff00"));
        mainField.addView(fieldLine, 0);

        for (int i = 0; i < game.fieldSize; i++) {
            LinearLayout fieldCell = (LinearLayout)inflater.inflate(R.layout.field_cell, null);

            LinearLayout.LayoutParams fCParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

//            Log.d(TAG, "here i=" + i + " overlapMarginVertical=" + (int)overlapMarginVertical
//                    + ", left is " + (i != 0 ? (int)(fieldCellLeftMargin) : (int) firstCellLeftMargin)
//                    + ", right is " + (i != 4 ? (int)(fieldCellRightMargin) : (int) lastCellRightMargin));
            fCParams.setMargins(i != 0 ? (int) fieldCellLeftMargin : (int) firstCellLeftMargin, (int) overlapMarginVertical,
                    i != game.fieldSize - 1 ? (int) fieldCellRightMargin : (int) lastCellRightMargin,
                    (int) (overlapMarginVertical));
            fieldCell.setLayoutParams(fCParams);
            fieldCell.setBackgroundResource(R.drawable.ic_field_cell);
            fieldCell.setId(i);

            fieldLine.addView(fieldCell);
        }

        activeFieldLine = fieldLine;
    }

    private void addStackToDrag(Context context, final LinearLayout stackLayout) {

        for (int i = 0; i < game.numBalls; i++) {

            final CircleCell stackCell = new CircleCell(context, i);
            LinearLayout.LayoutParams sCParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            sCParams.setMargins(0, (int) fieldLineTopMargin,0,
                     i == game.numBalls - 1 ? (int) (fieldLineBottomMargin + extraBottomMargin)
                             : (int) fieldLineBottomMargin);
            sCParams.gravity = Gravity.CENTER_HORIZONTAL;
            stackCell.setLayoutParams(sCParams);


            stackCell.setBackgroundResource(R.drawable.stack_cell_2);
//            stackCell.setBackgroundResource(R.drawable.stack_cell);
            stackCell.setElevation(fieldCellElevation);
            stackLayout.addView(stackCell);
        }
    }

    public void showGuessed(int placesGuessed, int colorsGuessed, int moveNumber) {
        Log.d(TAG, "moveNumber is " + moveNumber);
        GuessedCell guessedCell = new GuessedCell(this, placesGuessed, colorsGuessed, game.fieldSize);

        LinearLayout.LayoutParams gCParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        gCParams.setMargins(0, (int) fieldLineTopMargin, (int)overlapMarginHorizontal,
                moveNumber == 1 ? (int) (fieldLineBottomMargin + extraBottomMargin) : (int) fieldLineBottomMargin);
        gCParams.gravity = Gravity.RIGHT;
        guessedCell.setLayoutParams(gCParams);

        guessedCell.setBackgroundResource(R.drawable.stack_cell_2);
        guessedCell.setElevation(fieldCellElevation);
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
            return xCenterCoordOfFieldCell[4] + radius;
        return xCenterCoordOfFieldCell[i] - radius;
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