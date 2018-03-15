package me.gotidea.kamelise.colorguess;

import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

    private final long startTime;
    private long endTime;
//    SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("mm m ss s");
    private final String STRING_FORMAT = "%d min, %d s";
    private GameActivity gameActivity;

    private int[] colorArray;
//    private final int[] colorArray = {
//            Color.parseColor("#fa76e7"),
//            Color.parseColor("#2196f3"),
//            Color.parseColor("#ff8e00"),
//            Color.parseColor("#ca785b"),
//            Color.parseColor("#71ce53"),
//            Color.parseColor("#f12617"),
//            Color.parseColor("#9e9e9e"),
//            Color.parseColor("#ffdc20"),
//            Color.parseColor("#009688"),
//            Color.parseColor("#b610d2")};


    public Game(GameActivity gameActivity, int fieldSize, int numBalls, int maxMoves) {
        this.fieldSize = fieldSize;
        this.maxMoves = maxMoves;
        this.numBalls = numBalls;

        this.gameActivity = gameActivity;

        colorArray = gameActivity.getResources().getIntArray(R.array.colors_array);

        isEnded = false;
        startTime = System.currentTimeMillis();

        initCodedArr();

        activeArray = new int[fieldSize];
        initActiveArr();

        currMove = 1;
    }

    private void initCodedArr() {
        Random random = new Random();

        codedArr = new int[fieldSize];
        colorsMap = new HashMap<>();

//        StringBuilder sb1 = new StringBuilder();
//        sb1.append("colors coded: ");
//        StringBuilder sb2 = new StringBuilder();
//        sb2.append("colors map: ");
        for (int i = 0; i < fieldSize; i++) {
            codedArr[i] = random.nextInt(numBalls);
//            sb1.append(codedArr[i]).append(", ");
            int key = codedArr[i];
            if (colorsMap.containsKey(key)) {
                colorsMap.put(key, colorsMap.get(key) + 1);
            } else {
                colorsMap.put(key, 1);
            }
        }

        Iterator<Map.Entry<Integer, Integer>> it = colorsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> pair = it.next();
//            sb2.append(pair.getKey()).append(": ").append(pair.getValue()).append(", ");
        }
//        Log.d(GameActivity.TAG, sb1.toString());
//        Log.d(GameActivity.TAG, sb2.toString());
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

//        if (filled) {
//            ++currMove;
//            nextMove();
//        }
    }

    public boolean isFieldCellStateFilled(int i) {
        return activeArray[i] != -1;

//        if (filled) {
//            ++currMove;
//            nextMove();
//        }
    }

    public boolean isFilled() {
        if (filled) {
            filled = false;
            return true;
        }
        return false;
    }

    public void nextMove() {
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
        gameActivity.removeTouchListeners();

        ++currMove;
        if (currMove <= maxMoves && placesGuessed < 5) {
            gameActivity.addFieldLine(0);
            initActiveArr();
        } else {
            isEnded = true;
            endTime = System.currentTimeMillis();
            boolean won = false;
            if (placesGuessed == 5)
                won = true;
            gameEnd(won);
        }
    }

    private void gameEnd(boolean won) {
        long time = endTime - startTime;
        long min = TimeUnit.MILLISECONDS.toMinutes(time);
        long sec = TimeUnit.MILLISECONDS.toSeconds(time) % 60;
        String wonOrLost = won ? "You won!" : "You Lost!";
        Toast.makeText(gameActivity.getApplicationContext(),
                wonOrLost + " " + String.format(STRING_FORMAT, min, sec),
                Toast.LENGTH_LONG).show();
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        GameActivity.newGame(GameActivity, fieldSize, numBalls, maxMoves);
//        GameActivity.redraw();
    }

    public int getColorByIndex(int i) {
        return colorArray[i];
    }

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
