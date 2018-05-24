package me.gotidea.kamelise.colorguess;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import me.gotidea.kamelise.colorguess.ui.GameActivity;

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

    private GameActivity gameActivity;

    private int[] colorArray;


    public Game(GameActivity gameActivity, int fieldSize, int numBalls, int maxMoves) {
        this.fieldSize = fieldSize;
        this.maxMoves = maxMoves;
        this.numBalls = numBalls;

        this.gameActivity = gameActivity;

        colorArray = gameActivity.getResources().getIntArray(R.array.colors_array);

        isEnded = false;

        initCodedArr();

        activeArray = new int[fieldSize];
        initActiveArr();

        currMove = 1;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public void setEnded(boolean ended) {
        isEnded = ended;
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
                boolean won = false;
                if (placesGuessed == 5)
                    won = true;
                gameEnd(won);
            }
        }
    }

    private void gameEnd(boolean won) {
        gameActivity.gameEnded(won);
    }

    public int getColorByIndex(int i) {
        return colorArray[i];
    }

    public int getCodedColor(int position) {
        int colorIndex = codedArr[position];
        return colorArray[colorIndex];
    }

    public int getCurrMove() {
        return currMove;
    }

}
