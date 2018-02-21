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

    private final float CX = 67.3f;
    private final float CY = 59.3f;

//    private float[] xStartCoordBorders;
//    private float[] xFinalArr;
//    private float[] yFinal;

    private int activeCell = -1;

    private final int fieldSize;
    private final float radius;

    private Game game;
    private GameActivity gameActivity;

//    private final TranslateAnimation animation;

    public CircleCell(Context context, int index) {
        super(context);

        this.context = context;
        gameActivity = (GameActivity)context;
        game = gameActivity.getGame();
        this.colorIndex = index;
        this.color = game.getColorByIndex(index);
//        this.xStartCoordBorders = xCoordBorders;
//        this.xFinalArr = xFinalArr;
//        this.yFinal = yFinal;
//        this.activeFieldLine = game.getActiveFieldLine();
//        this.activeCellView = game.getActiveCellView();

        this.fieldSize = game.fieldSize;

        this.radius = getResources().getDimension(R.dimen.circle_radius);
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
        canvas.drawCircle(CX, CY, radius, cPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (parentView == null) {
                    parentView = (RelativeLayout)this.getParent().getParent().getParent();
                }

                shadowCircle = new ShadowCircle(context, color, colorIndex);
//                //TODO: fix this
                shadowCircle.setBackgroundResource(R.drawable.square_transparent);
                shadowCircle.setTag(10 * game.getCurrMove() + colorIndex);

                float x = event.getRawX();
                float y = event.getRawY();

                shadowCircle.setX(x - 2*radius);
                shadowCircle.setY(y - 3*radius);
                shadowCircle.setLayoutParams(new RelativeLayout.LayoutParams((int)radius*2,(int)radius*2));

                parentView.addView(shadowCircle);

                break;
        }
        return shadowCircle.onTouchEvent(event);
    }

}
