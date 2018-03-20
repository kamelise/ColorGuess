package me.gotidea.kamelise.colorguess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by kamelise on 5/10/17.
 */

public class CircleCell extends LinearLayout {

    private final Context context;
    private final int color;
    private final int colorIndex;
    private Paint cPaint;

    private ShadowCircle shadowCircle;
    private RelativeLayout parentView;

    private final float cx;
    private final float cy;

    private final float radius;
    private final float coefficient;

    private Game game;

    public CircleCell(Context context, int index) {
        super(context);

        this.context = context;
        GameActivity gameActivity = (GameActivity) context;
        coefficient = gameActivity.getCoefficient();
        radius = coefficient * getResources().getDimension(R.dimen.circle_radius);
        cx =  coefficient * getResources().getDimension(R.dimen.field_cell_width) / 2;
        cy = cx;

        game = gameActivity.getGame();
        colorIndex = index;
        color = game.getColorByIndex(index);

        init(color);

    }

    private void init(int color) {
        cPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cPaint.setColor(color);
        cPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.d("======> onDraw - ", "x: " + cx + "; y " + cy + "; radius: " + radius);
        canvas.drawCircle(cx, cy, radius, cPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (parentView == null) {
                    parentView = (RelativeLayout)this.getParent().getParent().getParent();
                }

                shadowCircle = new ShadowCircle(context, color, colorIndex);
                shadowCircle.setBackgroundResource(R.drawable.square_transparent);
                shadowCircle.setTag(10 * game.getCurrMove() + colorIndex);

                float topBarElevation = getResources().getDimension(R.dimen.top_bar_elevation);
                int z = (int) (topBarElevation + 1);
                shadowCircle.setZ(z);
//                Log.d(GameActivity.TAG, "shadow circle z is " + shadowCircle.getZ());

                float x = event.getRawX();
                float y = event.getRawY();

                shadowCircle.setX(x - 2*radius);
                shadowCircle.setY(y - 3*radius);
                RelativeLayout.LayoutParams shParams =
                        new RelativeLayout.LayoutParams((int)(radius*2 + 1),(int)(radius*2 + 1));
                shadowCircle.setLayoutParams(shParams);

                parentView.addView(shadowCircle);

                break;
        }
        return shadowCircle.onTouchEvent(event);
    }

}
