package me.gotidea.kamelise.colorguess;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GameActivity extends AppCompatActivity implements PopupDialogFragment.PopupDialogListener {

    public static final String TAG = "======";
    private final String STRING_FORMAT = "%02d:%02d";
    private final float HEIGHT_NEXUS6 = 683.4f;
    private final float WIDTH_NEXUS6 = 411;

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

    private LinearLayout mainField;
    private LinearLayout guessedLayout;
    private LinearLayout stackLayout;
//    private RelativeLayout topBarLayout;
    private LinearLayout solutionLine;
    private Chronometer chronometer;

    private LinearLayout activeFieldLine;
    private LinearLayout activeCellView;

    private int[] xCenterCoordOfFieldCell;
    private int[] xFinalArr;
    private int[] yFinalArr;

    public float radius;
    float fieldCellElevation;

    private float density;
    float coefficient;

    public float getCoefficient() {
        return coefficient;
    }

    private HashMap<ShadowCircle, Integer> activeFieldCircles;

    long originalBase = 0L;
    long pausedTime = 0L;
    long totalPauseTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        density = getResources().getDisplayMetrics().density;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.d(TAG, "screen width is " + metrics.widthPixels / density + ", height is "
                + metrics.heightPixels / density + ", density is " + density);

        //since all the dimensions are calculated for screen height = 683.4 and width = 411.4,
        //they have to be adjusted
        coefficient = 1.0f;

        if ((metrics.heightPixels / density < 0.95 * HEIGHT_NEXUS6)
                || metrics.widthPixels / density < 0.95 * WIDTH_NEXUS6)
//            coefficient = Math.min(metrics.heightPixels / density / HEIGHT_NEXUS6, metrics.widthPixels / density / WIDTH_NEXUS6);
            coefficient = metrics.widthPixels / density / WIDTH_NEXUS6;

        Log.d(TAG, "coefficient is " + coefficient);

        overlapMarginVertical = coefficient * getResources().getDimension(R.dimen.overlap_margin_vertical);
        overlapMarginHorizontal = coefficient * getResources().getDimension(R.dimen.overlap_margin_horizontal);

        extraBottomMargin = coefficient * getResources().getDimension(R.dimen.extra_bottom_margin);

        fieldLineTopMargin = coefficient * getResources().getDimension(R.dimen.field_line_top_margin);
        fieldLineBottomMargin = coefficient * getResources().getDimension(R.dimen.field_line_bottom_margin);

        fieldCellWidth = coefficient * getResources().getDimension(R.dimen.field_cell_width);
        fieldCellHeight = fieldCellWidth;
        Log.d(TAG, "field cell width is " + fieldCellWidth);

        fieldCellElevation = getResources().getDimension(R.dimen.field_cell_elevation);

        setContentView(R.layout.activity_game);

//        topBarLayout = (RelativeLayout) this.findViewById(R.id.top_bar);
        solutionLine = (LinearLayout) this.findViewById(R.id.solution_line);
        chronometer = (Chronometer) this.findViewById(R.id.chronometer);

        mainField = (LinearLayout) this.findViewById(R.id.main_field);
        stackLayout = (LinearLayout) this.findViewById(R.id.stack_layout);
        guessedLayout = (LinearLayout) this.findViewById(R.id.guessed_layout);

        LinearLayout.LayoutParams mParams =
                (LinearLayout.LayoutParams) mainField.getLayoutParams();
        mParams.setMargins(-(int) overlapMarginHorizontal, 0, 0, 0);
//        mainField.setLayoutParams(mParams);

        LinearLayout.LayoutParams sParams =
                (LinearLayout.LayoutParams) stackLayout.getLayoutParams();
        sParams.setMargins(0, 0, 0, 0);
//        stackLayout.setLayoutParams(sParams);

//        Log.d(TAG, "mainField right margin is " + mParams.rightMargin + ", stackLayout left margin is " + sParams.leftMargin);

        radius = coefficient * getResources().getDimension(R.dimen.circle_radius);

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
//        Log.d(TAG, "onwindowsfocuschanged triggered: " + hasFocus);
        gameOnPause(!hasFocus);
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
                - game.fieldSize * fieldCellWidth) / (game.fieldSize + partsForExtraMargin);
        //calculate distance between centers of field cells
        int delta = (int) fieldCellWidth + (int) fieldCellsSpace;
        //relative coord of cell field center point to the beginning of right border
        //of field cell image
        float fieldCellCenter = fieldCellWidth / 2;

//        Log.d(TAG, "field cells space is " + fieldCellsSpace + ", delta is " + delta
//                + ", fieldCellCenter is " + fieldCellCenter);

        //calc field cell image margins
        fieldCellRightMargin = fieldCellsSpace;
        fieldCellLeftMargin = 0;

        //calc first and last cell margins
        firstCellLeftMargin = (partsForExtraMargin + 1) * fieldCellsSpace / 2;
        lastCellRightMargin = firstCellLeftMargin;

//        Log.d(TAG, "fieldCell right margin is " + fieldCellLeftMargin
//                + ", first and last cell margin is " + firstCellLeftMargin);

        for (int i = 0; i < activeFieldLine.getChildCount(); i++) {
            LinearLayout fieldCell = (LinearLayout) activeFieldLine.getChildAt(i);
            LinearLayout.LayoutParams fCParams = (LinearLayout.LayoutParams) fieldCell.getLayoutParams();
            fCParams.setMargins(i != 0 ? (int) (fieldCellLeftMargin) : (int) firstCellLeftMargin, fCParams.topMargin,
                    i != game.fieldSize - 1 ? (int) (fieldCellRightMargin) : (int) lastCellRightMargin, fCParams.bottomMargin);
        }

        //in connection with rounding, activeFieldLine got some margins,
        // need to calc them and consider them for further calculations
        int fLMargins = mainField.getWidth() - 4 * ((int) fieldCellLeftMargin + (int) fieldCellRightMargin)
                - (int) firstCellLeftMargin - (int) lastCellRightMargin - 5 * (int) fieldCellWidth;

        //absolute coord of first field cell center
        xCenterCoordOfFieldCell[0] = mainField.getLeft() + (int) (fLMargins / 2) + (int) firstCellLeftMargin
                + (int) fieldCellCenter;

        for (int i = 1; i < game.fieldSize; i++) {
            xCenterCoordOfFieldCell[i] = xCenterCoordOfFieldCell[i - 1] + delta;
        }

        for (int i = 0; i < game.fieldSize; i++) {
            xFinalArr[i] = Math.round(xCenterCoordOfFieldCell[i] - (int) radius);
        }

        int yCenterCoordBottomCell = mainField.getBottom()
                - ((int) (fieldLineBottomMargin + extraBottomMargin - overlapMarginVertical) + (int) overlapMarginVertical)
                - (int) (fieldCellHeight / 2 + 1);
        yFinalArr[0] = yCenterCoordBottomCell - (int) radius;

        int dy = (int) fieldCellHeight + (int) fieldLineTopMargin + (int) fieldLineBottomMargin;
        for (int i = 1; i < game.maxMoves; i++) {
            yFinalArr[i] = yFinalArr[i - 1] - dy;
        }
    }

    private void init() {
        game = new Game(this, 5, 10, 10);

        xCenterCoordOfFieldCell = new int[game.fieldSize];
        xFinalArr = new int[game.fieldSize];
        yFinalArr = new int[game.maxMoves];

        addFieldLine(8 * density);
        addStackToDrag();
        activeFieldCircles = new HashMap<>();
        chronometer.start();
        originalBase = chronometer.getBase();
        Log.d(TAG, "original time base is " + originalBase);
    }

    public void redraw() {
        game = new Game(this, 5, 10, 10);
        mainField.removeAllViews();
        guessedLayout.removeAllViews();
        stackLayout.removeAllViews();
        solutionLine.removeAllViews();

        ViewGroup mainLayout = (ViewGroup) mainField.getParent().getParent();
        mainLayout.removeViews(3, mainLayout.getChildCount() - 3);
        chronometer.stop();
        totalPauseTime = 0;
        chronometer.setBase(SystemClock.elapsedRealtime());
        originalBase = chronometer.getBase();
        pausedTime = 0;
        Log.d(TAG, "redrawn time base is " + originalBase);

        addFieldLine(extraBottomMargin);
        addStackToDrag();
        activeFieldCircles = new HashMap<>();
    }

    //should be run before filling out "stack to drag"
    public void addFieldLine(float extraBottomMargin) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout fieldLine = new LinearLayout(this);

        LinearLayout.LayoutParams fLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        fLParams.setMargins(0, (int) (fieldLineTopMargin - overlapMarginVertical),
                0, (int) (fieldLineBottomMargin + extraBottomMargin - overlapMarginVertical));
        fLParams.gravity = Gravity.CENTER_HORIZONTAL;
        fieldLine.setLayoutParams(fLParams);
        fieldLine.setOrientation(LinearLayout.HORIZONTAL);
        fieldLine.setGravity(Gravity.CENTER);
        mainField.addView(fieldLine, 0);

        for (int i = 0; i < game.fieldSize; i++) {
            LinearLayout fieldCell = (LinearLayout) inflater.inflate(R.layout.field_cell, null);

            LinearLayout.LayoutParams fCParams = new LinearLayout.LayoutParams((int) fieldCellWidth, (int) fieldCellHeight);

            fCParams.setMargins(i != 0 ? (int) fieldCellLeftMargin : (int) firstCellLeftMargin, (int) overlapMarginVertical,
                    i != game.fieldSize - 1 ? (int) fieldCellRightMargin : (int) lastCellRightMargin,
                    (int) (overlapMarginVertical));
            fieldCell.setLayoutParams(fCParams);
            fieldCell.setBackgroundResource(R.drawable.field_cell);
            fieldCell.setId(i);

            fieldLine.addView(fieldCell);
        }

        activeFieldLine = fieldLine;
    }

    private void addStackToDrag() {

        for (int i = 0; i < game.numBalls; i++) {
            final CircleCell stackCell = new CircleCell(this, i);
            LinearLayout.LayoutParams sCParams =
                    new LinearLayout.LayoutParams((int) fieldCellWidth, (int) fieldCellHeight);
            sCParams.setMargins(0, (int) fieldLineTopMargin, 0,
                    i == game.numBalls - 1 ? (int) (fieldLineBottomMargin + extraBottomMargin)
                            : (int) fieldLineBottomMargin);
            sCParams.gravity = Gravity.CENTER_HORIZONTAL;
            stackCell.setLayoutParams(sCParams);
            stackCell.setBackgroundResource(R.drawable.stack_cell);
            stackCell.setElevation(fieldCellElevation);
            stackLayout.addView(stackCell);
        }
    }

    public void showGuessed(int placesGuessed, int colorsGuessed, int moveNumber) {
//        Log.d(TAG, "moveNumber is " + moveNumber);
        GuessedCell guessedCell = new GuessedCell(this, placesGuessed, colorsGuessed, game.fieldSize);

        LinearLayout.LayoutParams gCParams = new LinearLayout.LayoutParams((int) fieldCellWidth, (int) fieldCellHeight);
        gCParams.setMargins(0, (int) fieldLineTopMargin, (int) overlapMarginHorizontal,
                moveNumber == 1 ? (int) (fieldLineBottomMargin + extraBottomMargin) : (int) fieldLineBottomMargin);
        gCParams.gravity = Gravity.RIGHT;
        guessedCell.setLayoutParams(gCParams);

        guessedCell.setBackgroundResource(R.drawable.stack_cell);
        guessedCell.setElevation(fieldCellElevation);
        guessedLayout.addView(guessedCell, 0);
    }

    private void addSolutionCells() {
        for (int i = 0; i < game.fieldSize; i++) {
            int color = game.getCodedColor(i);
            final SolutionCell solutionCell = new SolutionCell(this, color);

            LinearLayout.LayoutParams sCParams =
                    new LinearLayout.LayoutParams((int) fieldCellWidth, (int) fieldCellHeight);

            sCParams.setMargins(i != 0 ? (int) fieldCellLeftMargin : (int) firstCellLeftMargin,
                    (int) overlapMarginVertical,
                    i != game.fieldSize - 1 ? (int) fieldCellRightMargin : (int) lastCellRightMargin,
                    (int) overlapMarginVertical);

            float elevation = getResources().getDimension(R.dimen.solution_cell_elevation);
            solutionCell.setElevation(elevation);
            solutionCell.setBackgroundResource(R.drawable.field_cell);
            solutionLine.addView(solutionCell, sCParams);
        }
    }

    public void gameEnded(boolean won) {//}, long time1) {
        long time = SystemClock.elapsedRealtime() - chronometer.getBase();
        chronometer.stop();
        Log.d(TAG, "game time is " + time);
        long min = TimeUnit.MILLISECONDS.toMinutes(time);
        long sec = TimeUnit.MILLISECONDS.toSeconds(time) % 60;
        String wonOrLost = won ? "You won!" : "You Lost!";
        String timeWon = String.format(STRING_FORMAT, min, sec);
        Toast.makeText(getApplicationContext(),wonOrLost + " " + timeWon,
                Toast.LENGTH_LONG).show();
        chronometer.setText(timeWon);
//        Log.d(TAG, "time is " + time + ", time1 is " + time1);
        addSolutionCells();
    }

    public Game getGame() {
        return game;
    }

    public LinearLayout getActiveCellViewById(int i) {
        return (LinearLayout) activeFieldLine.getChildAt(i);
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
        if (i == 5)
            return xCenterCoordOfFieldCell[4] + 2 * radius;
        return xCenterCoordOfFieldCell[i];
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
    public void onNewGameClick(DialogFragment dialog) {
        dialog.dismiss();
        redraw();
    }

    @Override
    public void onMainScreenClick(DialogFragment dialog) {
        super.finish();
        dialog.dismiss();
//        Toast.makeText(this, "Main screen is not yet ready", Toast.LENGTH_LONG).show();
    }

//    @Override
//    public void onDialogDismiss() {
//        gameOnPause(false);
//    }

    private void gameOnPause(boolean paused) {
        if (!game.isEnded) {
            if (paused) {
                pausedTime = SystemClock.elapsedRealtime();
                chronometer.stop();
            } else {
                if (pausedTime != 0) {
                    totalPauseTime += SystemClock.elapsedRealtime() - pausedTime;
                    Log.d(TAG, "updating chronometer base after resume:");
                    chronometer.setBase(originalBase + totalPauseTime);
                    Log.d(TAG, "totalPausedTime is " + totalPauseTime + ", new base time is " + chronometer.getBase());
                }
                chronometer.start();
                pausedTime = 0;
            }
//        game.gameOnPause(paused);
        }
    }

    public boolean isActiveLineContainCircle(ShadowCircle shadowCircle) {
        return activeFieldCircles.containsKey(shadowCircle);
    }

    public void addCircleToActiveLine(int index, ShadowCircle shadowCircle) {
        activeFieldCircles.put(shadowCircle, index);
    }

    public int removeCircleFromActiveLine(ShadowCircle shadowCircle) {
        return activeFieldCircles.remove(shadowCircle);
    }

    //"remove" touch listeners from shadowcircles in active field
    public boolean removeTouchListeners() {
        Set<ShadowCircle> keys = activeFieldCircles.keySet();
        if (keys.size() < 5)
            return false;
        for (ShadowCircle key : keys) {
            key.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }
        activeFieldCircles.clear();
        return true;
    }

    public ShadowCircle findShadowCircleToRemoveFromBoard(int index) {
        Set<ShadowCircle> keys = activeFieldCircles.keySet();
        for (ShadowCircle key : keys) {
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