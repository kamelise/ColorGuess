package me.gotidea.kamelise.colorguess.ui.elements;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;

import me.gotidea.kamelise.colorguess.R;
import me.gotidea.kamelise.colorguess.ui.activity.GameActivity;

/**
 * Created by kamelise on 5/11/17.
 */
public class GuessedCell extends LinearLayout {

    private final int grayColor;
    private final int greenColor;
    private final int redColor;

    private final int guessedNum;
    private Paint[] paintArr;

    private final float paddingLeft1;
    private final float paddingLeft2;
    private final float paddingLeft3;

    private final float paddingTop1;
    private final float paddingTop2;
    private final float paddingTop3;

    private final float guessedDotRadius;

    private final float coefficient;

    public GuessedCell(Context context, int placesGuessed, int colorsGuessed, int guessedNum) {
        super(context);

        this.guessedNum = guessedNum;

        grayColor = ContextCompat.getColor(context, R.color.guessedNotDot);
        greenColor = ContextCompat.getColor(context, R.color.guessedPlacesDot);
        redColor = ContextCompat.getColor(context, R.color.guessedColorsDot);

        coefficient = ((GameActivity)context).getCoefficient();
        guessedDotRadius = coefficient * getResources().getDimension(R.dimen.guessed_dot_radius);

        float fieldCellWidth = coefficient * getResources().getDimension(R.dimen.field_cell_width);
        paddingLeft1 = fieldCellWidth/4;
        paddingLeft2 = fieldCellWidth*3/4;
        paddingLeft3 = fieldCellWidth/2;
        paddingTop1 = paddingLeft1;
        paddingTop2 = paddingLeft3;
        paddingTop3 = paddingLeft2;

//        init(new int[] {Color.GRAY, Color.GRAY, Color.GRAY, Color.RED, Color.GREEN});
        init(placesGuessed, colorsGuessed);
    }

    private void init(int placesGuessed, int colorsGuessed) {

        int[] guessedColors = new int[guessedNum];

        for (int i = 0; i < guessedNum; i++) {
            guessedColors[i] = grayColor;       //by default every guessed dot should be grey
        }
        for (int i = 0; i < placesGuessed; i++) {
            guessedColors[i] = greenColor;     //setting placesGuessed dots as Green
        }
        for (int i = 4; i > 4 - colorsGuessed; i--) {
            guessedColors[i] = redColor;       //setting colorsGuessed dots as Red
        }

        paintArr = new Paint[guessedNum];
        for (int i = 0; i < paintArr.length; i++) {
            paintArr[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintArr[i].setColor(guessedColors[i]);
            paintArr[i].setStyle(Paint.Style.FILL);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(paddingLeft1, paddingTop1, guessedDotRadius, paintArr[0]);
        canvas.drawCircle(paddingLeft2, paddingTop1, guessedDotRadius, paintArr[1]);
        canvas.drawCircle(paddingLeft3, paddingTop2, guessedDotRadius, paintArr[2]);
        canvas.drawCircle(paddingLeft1, paddingTop3, guessedDotRadius, paintArr[3]);
        canvas.drawCircle(paddingLeft2, paddingTop3, guessedDotRadius, paintArr[4]);
    }
}
