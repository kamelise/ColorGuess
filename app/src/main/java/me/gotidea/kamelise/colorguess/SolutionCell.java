package me.gotidea.kamelise.colorguess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.LinearLayout;

/**
 * Created by kamelise on 3/15/18.
 */

public class SolutionCell extends LinearLayout {
    private Paint cPaint;
    private final float coefficient;

    public SolutionCell(Context context, int color) {
        super(context);

        GameActivity gameActivity = (GameActivity) context;
        coefficient = gameActivity.getCoefficient();

        cPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cPaint.setColor(color);
        cPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final float radius = coefficient * getResources().getDimension(R.dimen.circle_radius);
        float cx =  coefficient * getResources().getDimension(R.dimen.field_cell_width) / 2;
        float cy = cx;

        canvas.drawCircle(cx, cy, radius, cPaint);
//        Log.d("======> onDraw - ", "x: " + cx + "; y " + cy + "; radius: " + radius);
    }
}
