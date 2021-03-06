package me.gotidea.kamelise.colorguess.ui.elements;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import me.gotidea.kamelise.colorguess.model.Game;
import me.gotidea.kamelise.colorguess.R;
import me.gotidea.kamelise.colorguess.ui.activity.GameActivity;

/**
 * Created by kamelise on 5/12/17.
 */
public class ShadowCircle extends RelativeLayout {

    private Paint cPaint;
    private int colorId;

    private RelativeLayout parentView;

    private Game game;
    private GameActivity gameActivity;

    private int activeCell = -1;

    private final int fieldSize;
    private final float radius;
    private final float coefficient;

    public int getColorId() {
        return colorId;
    }

    public ShadowCircle(Context context, int color, int colorIndex) {
        super(context);

        colorId = colorIndex;
        init(color);

        gameActivity = (GameActivity)context;
        game = gameActivity.getGame();

        fieldSize = game.fieldSize;

        coefficient = gameActivity.getCoefficient();
        radius = coefficient * getResources().getDimension(R.dimen.circle_radius);

        dX = - 2*radius;
        dY = dX;
    }

    private void init(int color) {
        cPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cPaint.setColor(color);
        cPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(radius, radius, radius, cPaint);
    }

    float dX, dY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

//                Log.d(GameActivity.TAG, game.getColorByIndex(colorId) + ": ACTION_DOWN1");
                if (gameActivity.isActiveLineContainCircle(this)) {
                    int index = gameActivity.removeCircleFromActiveLine(this);
                    game.removeFieldCellState(index);
                }
//                Log.d(GameActivity.TAG, game.getColorByIndex(colorId) + ": ACTION_DOWN2");
                break;

            case MotionEvent.ACTION_MOVE:
                float eventRawX = event.getRawX();
                this.animate()
                        .x(eventRawX + dX)
                        .y(event.getRawY() + dY)
                        .setDuration(0)
                        .start();

                //check if in bounds of borders, find under which cell it is
                if (activeCell == -1 || eventRawX < gameActivity.xStartBorder(activeCell)
                        || eventRawX > gameActivity.xStartBorder(activeCell + 1)) {
                    for (int i = fieldSize; i >= 0; i--) {
                        if (eventRawX < gameActivity.xStartBorder(i)) {
                            //if i==0 and activeCell become -1, we'll know it's out of bounds
                            activeCell = i - 1;
                        } else if (i == fieldSize) {
                            activeCell = -1;
                        }
                    }

                    if (activeCell >= 0) {
                        if (gameActivity.getActiveCellView() != null)
//                            gameActivity.setActiveCellViewBackground(R.drawable.field_cell_normal);
                            gameActivity.setActiveCellViewBackground(R.drawable.field_cell);
                        gameActivity.setActiveCellView(gameActivity.getActiveCellViewById(activeCell));
                    } else {
                        if (gameActivity.getActiveCellView() != null)
//                            gameActivity.setActiveCellViewBackground(R.drawable.field_cell_normal);
                            gameActivity.setActiveCellViewBackground(R.drawable.field_cell);
                        gameActivity.setActiveCellView(null);
                    }
                    if (gameActivity.getActiveCellView() != null)
//                        gameActivity.setActiveCellViewBackground(R.drawable.field_cell_pressed);
                        gameActivity.setActiveCellViewBackground(R.drawable.field_cell_highlighted);
                }

                break;
            case MotionEvent.ACTION_UP:
                if (parentView == null) {
                    parentView = (RelativeLayout)this.getParent();
                }

                eventRawX = event.getRawX();
                float eventRawY = event.getRawY();

                if (activeCell != -1) {

                    //needed for onAnimationEnd: if another ShadowCircle is in this cell already - remove it from layout
                    final int activeCellIndexRecorded = activeCell;
                    final boolean isCircleInLine = game.isFieldCellStateFilled(activeCellIndexRecorded);

                    ShadowCircle shadowCircleToRemove = null;
                    if (isCircleInLine)
                        shadowCircleToRemove = gameActivity.findShadowCircleToRemoveFromBoard(activeCellIndexRecorded);
                    //cannot remove immediately, need to wait till animation is finished
                    final ShadowCircle finalShadowCircleToRemove = shadowCircleToRemove;

                    //adding ShadowCircle to array of circles in current active line to be able
                    // to remove touch listeners later when next move
                    gameActivity.addCircleToActiveLine(activeCell, ShadowCircle.this);

                    int xFinal = Math.round(gameActivity.xFinal(activeCell));

                    ValueAnimator animX = ValueAnimator.ofFloat(eventRawX - radius*2,
                            xFinal);
                    int yFinal = Math.round(gameActivity.yFinal(game.getCurrMove() - 1));
                    ValueAnimator animY = ValueAnimator.ofFloat(eventRawY - radius*2,
                            yFinal);

                    animX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ShadowCircle.this.setX((float) valueAnimator.getAnimatedValue());
                        }
                    });

                    animY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ShadowCircle.this.setY((float) valueAnimator.getAnimatedValue());
                        }
                    });

                    AnimatorSet animSet = new AnimatorSet();
                    animSet.playTogether(animX, animY);
                    animSet.setDuration(500);
                    animSet.setInterpolator(new DecelerateInterpolator());

                    animSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (game.isFilled()) {
                                game.nextMove();
                            }

                            //if another ShadowCircle is in this cell already - remove it from layout
                            if (finalShadowCircleToRemove != null)
                                gameActivity.removeShadowCircleFromBoard(finalShadowCircleToRemove);
                        }
                    });

                    animSet.start();

                    game.setFieldCellState(activeCell, this.getColorId());
//                    Log.d(gameActivity.TAG, "activeCell = " + activeCell + ", colorId = "
//                            + shadowCircle.getColorId());

                } else {
                    parentView.removeView(this);
//                    shadowCircle = null;
                }

                if (gameActivity.getActiveCellView() != null)
//                    gameActivity.setActiveCellViewBackground(R.drawable.field_cell_normal);
                    gameActivity.setActiveCellViewBackground(R.drawable.field_cell);

                activeCell = -1;
                break;
            default:
                return false;
        }
        return true;
    }

}