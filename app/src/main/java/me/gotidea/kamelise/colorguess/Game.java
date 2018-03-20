package me.gotidea.kamelise.colorguess;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * Created by kamelise on 7/2/17.
 */
public class Game {

    boolean isEnded;

    public final int numBalls;
    public final int fieldSize;
    public final int maxMoves;

    private int[] codedArr;

    private int[] activeArray;
    private int filledCellsInActiveField;

    private HashMap<Integer, Integer> colorsMap;

    private int currMove;
    private boolean filled = false;

//    private final long startTime;
//    private long endTime;
//    private long pauseTime;
//    private long resumeTime;
    private long totalPauseTime = 0;

    private GameActivity gameActivity;

    private int[] colorArray;


    public Game(GameActivity gameActivity, int fieldSize, int numBalls, int maxMoves) {
        this.fieldSize = fieldSize;
        this.maxMoves = maxMoves;
        this.numBalls = numBalls;

        this.gameActivity = gameActivity;

        colorArray = gameActivity.getResources().getIntArray(R.array.colors_array);

        isEnded = false;
//        startTime = System.currentTimeMillis();
//        Log.d(GameActivity.TAG, "start time is " + startTime);

        initCodedArr();

        activeArray = new int[fieldSize];
        initActiveArr();

        currMove = 1;
    }

    private void initCodedArr() {
        Random random = new Random();

        codedArr = new int[fieldSize];
        colorsMap = new HashMap<>();

        for (int i = 0; i < fieldSize; i++) {
            codedArr[i] = random.nextInt(numBalls);
            int key = codedArr[i];
            if (colorsMap.containsKey(key)) {
                colorsMap.put(key, colorsMap.get(key) + 1);
            } else {
                colorsMap.put(key, 1);
            }
        }
    }

    private void initActiveArr() {
        for (int i = 0; i < fieldSize; i++) {
            activeArray[i] = -1;
        }
        filledCellsInActiveField = 0;
    }

    public void removeFieldCellState(int i) {
        activeArray[i] = -1;
        filledCellsInActiveField--;
    }

    public void setFieldCellState(int i, int colorId) {
        if (activeArray[i] == -1) {
            if (++filledCellsInActiveField == fieldSize)
                filled = true;
        }
        activeArray[i] = colorId;
    }

    public boolean isFieldCellStateFilled(int i) {
        return activeArray[i] != -1;
    }

    public boolean isFilled() {
        if (filled) {
            filled = false;
            return true;
        }
        return false;
    }

    public void nextMove() {
        if (gameActivity.removeTouchListeners()) {
//            Log.d(GameActivity.TAG, "removed touch listeners, here in nextMove");
            int placesGuessed = 0;
            HashMap<Integer, Integer> colorsMap = new HashMap<>();
            colorsMap.putAll(this.colorsMap);
            int colorsGuessed;

            // calculate how many colors and places guessed correctly
            for (int i = 0; i < fieldSize; i++) {
                if (activeArray[i] == codedArr[i]) {
                    placesGuessed++;
                }
                int key = activeArray[i];
                if (colorsMap.containsKey(key) && colorsMap.get(key) > 0) {
                    colorsMap.put(key, colorsMap.get(key) - 1);
                }
            }

            // total colors guessed correctly also include number of places guessed correctly
            int totalColorsNotGuessed = 0;

            Iterator<Map.Entry<Integer, Integer>> it = colorsMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Integer> pair = it.next();
                totalColorsNotGuessed += pair.getValue();
            }
            colorsGuessed = fieldSize - totalColorsNotGuessed - placesGuessed;

            gameActivity.showGuessed(placesGuessed, colorsGuessed, currMove);

            ++currMove;
            if (currMove <= maxMoves && placesGuessed < 5) {
                gameActivity.addFieldLine(0);
                initActiveArr();
            } else {
                isEnded = true;
//                endTime = System.currentTimeMillis();
//                Log.d(GameActivity.TAG, "end time is " + endTime);
                boolean won = false;
                if (placesGuessed == 5)
                    won = true;
                gameEnd(won);
            }
        }
    }

    private void gameEnd(boolean won) {
//        long time = endTime - startTime - totalPauseTime;
//        gameActivity.gameEnded(won, time);
        gameActivity.gameEnded(won);
    }

    public int getColorByIndex(int i) {
        return colorArray[i];
    }

    public int getCodedColor(int position) {
        int colorIndex = codedArr[position];
        return colorArray[colorIndex];
    }

//    public void gameOnPause(boolean pause) {
//        if (pause) {
//            pauseTime = System.currentTimeMillis();
////            Log.d(GameActivity.TAG, "onPause is true, paused at " + pauseTime);
//        } else {
//            resumeTime = System.currentTimeMillis();
//            if (pauseTime != 0)
//                totalPauseTime += (resumeTime - pauseTime);
//            Log.d(GameActivity.TAG, "onPause is false, resumed at " + resumeTime + ", totalPauseTime is " + totalPauseTime);
//            pauseTime = 0;
//            resumeTime = 0;
//        }
//    }

    public boolean isEnded() {
        return isEnded;
    }

    public void setIsEnded(boolean isEnded) {
        this.isEnded = isEnded;
    }

    public int getCurrMove() {
        return currMove;
    }

}
