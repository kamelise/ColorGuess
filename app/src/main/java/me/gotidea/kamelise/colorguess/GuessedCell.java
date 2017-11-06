package me.gotidea.kamelise.colorguess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;

/**
 * Created by kamelise on 5/11/17.
 */
public class GuessedCell extends LinearLayout {


    private final int GRAY_COLOR;
    private final int GREEN_COLOR;
    private final int RED_COLOR;

    private final int GUESSED_NUM = 5;
    private Paint[] paintArr;

    private final float PADDING_LEFT_1 = 17.8f;
    private final float PADDING_LEFT_2 = 38.7f;
    private final float PADDING_LEFT_3 = 28.4f;

    private final float PADDING_TOP_1 = 19.2f;
    private final float PADDING_TOP_2 = 29.7f;
    private final float PADDING_TOP_3 = 40.2f;

    private final float RADIUS = 4.5f;

    private float density;

    public GuessedCell(Context context, int placesGuessed, int colorsGuessed) {
        super(context);
        GRAY_COLOR = ContextCompat.getColor(context, R.color.guessedNotDot);
        GREEN_COLOR = ContextCompat.getColor(context, R.color.guessedPlacesDot);
        RED_COLOR = ContextCompat.getColor(context, R.color.guessedColorsDot);

        density = getResources().getDisplayMetrics().density;

//        init(new int[] {Color.GRAY, Color.GRAY, Color.GRAY, Color.RED, Color.GREEN});
        init(placesGuessed, colorsGuessed);
    }

    private void init(int placesGuessed, int colorsGuessed) {

        int[] guessedColors = new int[GUESSED_NUM];

        for (int i = 0; i < GUESSED_NUM; i++) {
            guessedColors[i] = GRAY_COLOR;       //by default every guessed dot should be grey
        }
        for (int i = 0; i < placesGuessed; i++) {
            guessedColors[i] = GREEN_COLOR;     //setting placesGuessed dots as Green
        }
        for (int i = 4; i > 4 - colorsGuessed; i--) {
            guessedColors[i] = RED_COLOR;       //setting colorsGuessed dots as Red
        }

        paintArr = new Paint[GUESSED_NUM];
        for (int i = 0; i < paintArr.length; i++) {
            paintArr[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintArr[i].setColor(guessedColors[i]);
            paintArr[i].setStyle(Paint.Style.FILL);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawDot(canvas, PADDING_LEFT_1, PADDING_TOP_1, RADIUS, paintArr[0]);
        drawDot(canvas, PADDING_LEFT_2, PADDING_TOP_1, RADIUS, paintArr[1]);
        drawDot(canvas, PADDING_LEFT_3, PADDING_TOP_2, RADIUS, paintArr[2]);
        drawDot(canvas, PADDING_LEFT_1, PADDING_TOP_3, RADIUS, paintArr[3]);
        drawDot(canvas, PADDING_LEFT_2, PADDING_TOP_3, RADIUS, paintArr[4]);
    }

    private void drawDot(Canvas canvas, float left, float top, float radius, Paint paint) {
        canvas.drawCircle(left * density, top * density, radius * density, paint);
    }
}
